package com.yj.audio.version.base.activity.music;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import js.lib.android.media.bean.MediaBase;
import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.media.player.PlayEnableController;
import js.lib.android.media.player.audio.utils.AudioSortUtils;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.Logs;
import js.lib.android_media_scan.IMediaScanService;
import js.lib.android_media_scan.MediaScanService;

/**
 * 播放器扩展行动作 - BASE
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioExtendActionsActivity extends BaseAudioCommonActionsActivity
        implements MediaScanService.AudioScanDelegate {
    // TAG
    private final String TAG = "BaseAudioExtendActions";

    /**
     * Thread handler
     */
    protected Handler mHandler = new Handler();

    /**
     * AsyncTask - 加载本地媒体
     */
    protected LoadLocalMediasTask mLoadLocalMediasTask;

    /**
     * 媒体加载监听
     */
    protected interface LoadMediaListener {
        void afterLoaded(List<ProAudio> listMedias);
    }

    /**
     * Is {@link IMediaScanService} bound?
     */
    private boolean mIsScanServiceBound = false;

    /**
     * {@link IMediaScanService} Object
     */
    private IMediaScanService mMediaScanService;
    private ServiceConnection mScanServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaScanService = IMediaScanService.Stub.asInterface(service);
            registerMediaScanDelegate(true);
            onScanServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    protected abstract void onScanServiceConnected();

    @Override
    protected void onCreate(android.os.Bundle bundle) {
        super.onCreate(bundle);
    }

    /**
     * Load local medias task.
     */
    @SuppressLint("StaticFieldLeak")
    public class LoadLocalMediasTask extends AsyncTask<Void, Integer, Void> {
        private List<ProAudio> mmListMedias;
        private LoadMediaListener mmLoadMediaListener;

        public LoadLocalMediasTask(LoadMediaListener l) {
            mmLoadMediaListener = l;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Get List Medias
            mmListMedias = AudioDBManager.instance().getListMusics();
            AudioSortUtils.sortByTitle(mmListMedias);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mmLoadMediaListener != null) {
                mmLoadMediaListener.afterLoaded(mmListMedias);
            }
        }
    }

    /**
     * 获取当前媒体对象
     */
    public ProAudio getCurrProgram() {
        Serializable serialPro = getCurrMedia();
        if (serialPro != null) {
            return (ProAudio) serialPro;
        }
        return null;
    }

    /**
     * Is Playing Media
     */
    public boolean isPlayingSameMedia(String mediaUrl) {
        boolean isPlayingSameMedia = isPlaying() && TextUtils.equals(getCurrMediaPath(), mediaUrl);
        Log.i(TAG, "isPlayingSameMedia : " + isPlayingSameMedia);
        return isPlayingSameMedia;
    }

    /**
     * EXEC Play previous by user
     */
    public void execPlayPrevByUser() {
        PlayEnableController.pauseByUser(false);
        playPrev();
    }

    /**
     * EXEC Play next by user
     */
    public void execPlayNextByUser() {
        PlayEnableController.pauseByUser(false);
        playNext();
    }

    /**
     * EXEC Play or Pause by user
     */
    public void execPlayOrPauseByUser() {
        Logs.i(TAG, "execPlayOrPause lyy1");
        if (isPlaying()) {
            execPauseByUser();
        } else {
            execResumeByUser();
        }
    }

    /**
     * EXEC pause by user
     */
    public void execPauseByUser() {
        PlayEnableController.pauseByUser(true);
        pause();
    }

    /**
     * EXEC resume by user
     */
    public void execResumeByUser() {
        PlayEnableController.pauseByUser(false);
        resume();
    }

    /**
     * Exec play
     *
     * @param mediaUrl     The media url to play.
     * @param listPrograms The media list to play.
     */
    public void execPlay(String mediaUrl, List<? extends MediaBase> listPrograms) {
        //Set play list.
        setPlayList(listPrograms);
        //Check if execute play "mediaUrl"
        if (isPlayingSameMedia(mediaUrl)) {
            Log.i(TAG, "### The media to play is playing now. ###");
        } else {
            execPlay(mediaUrl);
        }
    }

    /**
     * EXEC Play Selected Music
     */
    protected void execPlay(String mediaUrl) {
        PlayEnableController.pauseByUser(false);
        play(mediaUrl);
    }

    /**
     * EXEC Play Selected Music
     */
    protected void execPlay(int position) {
        play(position);
    }

    @Override
    public void release() {
        super.release();
    }

    /**
     * Cancel All Tasks
     */
    protected void cancelAllTasks() {
        CommonUtil.cancelTask(mLoadLocalMediasTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * MediaScanService bind operate.
     */
    protected void bindScanService(boolean isBind) {
        try {
            Log.i(TAG, "bindScanService(" + isBind + ")");
            if (isBind) {
                if (!mIsScanServiceBound) {
                    Intent bindIntent = new Intent(this, MediaScanService.class);
                    mIsScanServiceBound = bindService(bindIntent, mScanServiceConn, BIND_AUTO_CREATE);
                }
            } else {
                if (mIsScanServiceBound) {
                    registerMediaScanDelegate(false);
                    unbindService(mScanServiceConn);
                    mIsScanServiceBound = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerMediaScanDelegate(boolean isReg) {
        if (mMediaScanService != null) {
            try {
                if (isReg) {
                    mMediaScanService.registerDelegate(this);
                } else {
                    mMediaScanService.unregisterDelegate(this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void startScan() {
        Log.i(TAG, "startScan() -1-");
        if (mMediaScanService != null) {
            try {
                Log.i(TAG, "startScan() -2-");
                mMediaScanService.startScan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isMediaScanning() {
        try {
            return mMediaScanService != null && mMediaScanService.isMediaScanning();
        } catch (Exception e) {
            return false;
        }
    }

    protected void destroyScanService() {
        if (mMediaScanService != null) {
            try {
                mMediaScanService.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMediaScanningStart() {
    }

    @Override
    public void onMediaScanningNew() {
    }

    @Override
    public void onMediaScanningEnd() {
    }

    @Override
    public void onMediaScanningEnd(boolean isHasMedias) {
    }

    @Override
    public void onMediaParseEnd(int type) {
    }

    @Override
    public void onMediaScanningCancel() {
    }

    @Override
    public void onMediaScanningRefresh(List<ProAudio> listMedias, boolean isOnUiThread) {
    }

    @Override
    public IBinder asBinder() {
        return null;
    }
}