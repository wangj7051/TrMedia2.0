package com.yj.video.version.base.activity.video;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.engine.video.db.VideoDBManager;
import js.lib.android.media.player.PlayDelegate;
import js.lib.android.media.player.PlayEnableController;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.android_media_scan.IMediaScanService;
import js.lib.android_media_scan.MediaScanService;
import js.lib.utils.CharacterParser;

/**
 * 播放器扩展行动作 - BASE
 *
 * @author Jun.Wang
 */
public abstract class BaseVideoExtendActionsActivity extends BaseVideoCommonActionsActivity
        implements PlayDelegate, MediaScanService.VideoScanDelegate {
    // TAG
    private final String TAG = "BaseVideoExtendActions";

    //==========Variable in this Activity==========
    /**
     * 是否正在展示黑名单媒体
     */
    private boolean mIsShowingBlacklistMedias = false;

    /**
     * 安全线程 - 上一个/下一个
     * <p>
     * 防高频点击线程
     */
    private Runnable mPlayPrevSecRunnable, mPlayNextSecRunnable;

    /**
     * 是否允许后台播放视频
     */
    private boolean mIsCanPlayAtBg = false;

    /**
     * 目标自动Seek进度，当视频加载完成后，可以据此跳转进度
     */
    protected long mTargetAutoSeekProgress = -1;

    /**
     * Load Local Medias Task
     */
    protected LoadLocalMediasTask mLoadLocalMediasTask;

    /**
     * 媒体加载监听器
     */
    protected interface LoadMediaListener {
        void afterLoad(String selectMediaUrl);

        void refreshUI();
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
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    /**
     * Sort List<ProVideo> by first Char
     */
    protected void sortMediaList(List<ProVideo> listPrograms) {
        if (!EmptyUtil.isEmpty(listPrograms)) {
            for (ProVideo program : mListPrograms) {
                try {
                    program.titlePinYin = CharacterParser.getPingYin(program.title);
                    String firstChar = program.titlePinYin.substring(0, 1).toUpperCase();
                    if (firstChar.matches("[A-Z]")) {
                        program.sortLetter = firstChar;
                    } else {
                        program.sortLetter = "#";
                    }
                } catch (Exception e) {
                    Logs.printStackTrace(TAG + "sortMediaList()", e);
                    program.titlePinYin = "";
                    program.sortLetter = "";
                }
            }
            Collections.sort(mListPrograms, mComparator);
        }
    }

    /**
     * Load Played Medias From Database
     */
    protected void loadLocalMedias() {
    }

    @SuppressLint("StaticFieldLeak")
    public class LoadLocalMediasTask extends AsyncTask<Object, Integer, Void> {
        private WeakReference<LoadMediaListener> mmWeakReference;

        @Override
        protected Void doInBackground(Object... params) {
            try {
                // Parameters
                mmWeakReference = new WeakReference<>((LoadMediaListener) params[0]);

                // Get List Medias
                Map<String, ProVideo> mapPrograms = VideoDBManager.instance().getMapVideos(true, false);
                mListPrograms = new ArrayList<>();
                if (mapPrograms != null) {
                    mListPrograms.addAll(mapPrograms.values());
                }

                // Sort
                sortMediaList(mListPrograms);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (!isCancelled()) {
                    mmWeakReference.get().afterLoad("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Release
     */
    protected void clearPlayInfo() {
        mListPrograms = new ArrayList<>();
        if (tvStartTime != null) {
            tvStartTime.setText("00:00");
        }
        if (tvEndTime != null) {
            tvEndTime.setText("00:00");
        }
        if (seekBar != null) {
            seekBar.setEnabled(false);
            seekBar.setMax(0);
            seekBar.setProgress(0);
        }
    }

    /**
     * 是否正在展示黑名单媒体文件
     */
    protected boolean isShowingBlacklistMedias() {
        return mIsShowingBlacklistMedias;
    }

    /**
     * 启动播放视频
     */
    private void startPlay(boolean isMakeScreeenOn) {
        play();
        makeScreenOn(isMakeScreeenOn);
        Logs.i(TAG, "++++VideoView Start and Make Screen ON++++");
    }

    /**
     * EXEC Play Select Program
     */
    protected void execPlay(String mediaUrl) {
        PlayEnableController.pauseByUser(false);
        execPlay(getPlayPosByMediaUrl(mediaUrl));
    }

    /**
     * EXEC Play Select Program
     */
    protected void execPlay(int playPos) {
        // 如下两个条件，不执行播放
        // （0）播放器对象为空
        // （1）列表为空
        // （2）索引溢出
        if (vvPlayer == null || EmptyUtil.isEmpty(mListPrograms) || playPos >= mListPrograms.size()) {
            return;
        }

        // 获取并检测当前位置节目是否可以播放
        Logs.i(TAG, "----execPlay(" + playPos + ")----");
        mPlayPos = playPos;
        ProVideo toPlayProgram = mListPrograms.get(playPos);
        if (toPlayProgram == null || EmptyUtil.isEmpty(toPlayProgram.mediaUrl)) {
            return;
        }

        // 执行播放
        saveTargetMediaPath(toPlayProgram.mediaUrl);
        mTargetAutoSeekProgress = getLastProgress();

        vvPlayer.setVisibility(View.INVISIBLE);
        vvPlayer.setTag(toPlayProgram);
        vvPlayer.setMediaPath(toPlayProgram.mediaUrl);
        startPlay(true);
        vvPlayer.setVisibility(View.VISIBLE);

        // 设置相关信息
        resetSeekBar();
    }

    @Override
    public void playPrev() {
        super.playPrev();
        if (mPlayPrevSecRunnable == null) {
            mPlayPrevSecRunnable = new Runnable() {

                @Override
                public void run() {
                    Logs.i(TAG, "playPrevBySecurity() -> ^^ mPlayPrevSecRunnable ^^");
                    execPlay(mPlayPos);
                }
            };
        }
        mHandler.postDelayed(mPlayPrevSecRunnable, 500);
    }

    @Override
    public void playNext() {
        super.playNext();
        removePlayRunnable();
        if (mPlayNextSecRunnable == null) {
            mPlayNextSecRunnable = new Runnable() {

                @Override
                public void run() {
                    Logs.i(TAG, "playPrevBySecurity() -> ^^ mPlayNextSecRunnable ^^");
                    execPlay(mPlayPos);
                }
            };
        }
        mHandler.postDelayed(mPlayNextSecRunnable, 500);
    }

    @Override
    public void pause() {
        super.pause();
    }

    private void resetSeekBar() {
        try {
            Logs.i(TAG, "----resetSeekBar()----");
            if (seekBar != null) {
                seekBar.setEnabled(true);
                seekBar.setMax(mListPrograms.get(mPlayPos).duration);
                seekBar.setProgress(0);
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "resetSeekBar()", e);
        }
    }

    /**
     * Stop Video Play
     */
    protected void execRelease() {
        Logs.i(TAG, "----execRelease()----");
        release();
        if (!isDestroyed()) {
            clearPlayInfo();
        }
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
     * Play Pause Video
     */
    protected void execPlayOrPauseByUser() {
        Logs.i(TAG, "----execPlayOrPause()----");
        if (!EmptyUtil.isEmpty(mListPrograms)) {
            if (isPlaying()) {
                execPauseByUser();
            } else {
                execResumeByUser();
            }
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
     * Save Play Information
     */
    protected void savePlayInfo() {
        if (isPlaying() && getProgress() > 0) {
            String currMediaUrl = vvPlayer.getMediaPath();
            if (!EmptyUtil.isEmpty(currMediaUrl)) {
                Logs.debugI(TAG, "savePlayInfo() -> [progress:" + getProgress());
                savePlayMediaInfos(currMediaUrl, getProgress());
            }
        }
    }

    /**
     * Get Media Play Position by Media URL
     */
    protected int getPlayPosByMediaUrl(String mediaUrl) {
        int playPos = -1;

        for (int idx = 0; idx < mListPrograms.size(); idx++) {
            ProVideo program = mListPrograms.get(idx);
            if (program.mediaUrl.equals(mediaUrl)) {
                playPos = idx;
                break;
            }
        }

        if (playPos < 0) {
            playPos = 0;
            clearPlayedMediaInfos();
        }

        return playPos;
    }

    protected ProVideo getCurrProgram() {
        try {
            String currMediaUrl = getCurrMediaPath();
            for (ProVideo program : mListPrograms) {
                if (program.mediaUrl.equals(currMediaUrl)) {
                    return program;
                }
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getCurrProgram()", e);
        }
        return null;
    }

    protected ViewGroup.LayoutParams getLPs() {
        if (vvPlayer != null) {
            return vvPlayer.getLayoutParams();
        }
        return null;
    }

    protected void setLPs(ViewGroup.LayoutParams lps) {
        if (vvPlayer != null) {
            vvPlayer.setLayoutParams(lps);
        }
    }

    /**
     * 设置是否允许后台播放视频
     */
    protected void setCanPlayAtBg(boolean isCanPlayAtBg) {
        this.mIsCanPlayAtBg = isCanPlayAtBg;
    }

    /**
     * 是否允许后台播放视频
     */
    protected boolean isCanPlayAtBg() {
        return mIsCanPlayAtBg;
    }

    @Override
    protected void onDestroy() {
        removePlayRunnable();
        CommonUtil.cancelTask(mLoadLocalMediasTask);
        super.onDestroy();
    }

    /**
     * Remove play runnable
     */
    public void removePlayRunnable() {
        if (mPlayPrevSecRunnable != null) {
            mHandler.removeCallbacks(mPlayPrevSecRunnable);
        }
        if (mPlayNextSecRunnable != null) {
            mHandler.removeCallbacks(mPlayNextSecRunnable);
        }
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
            } catch (RemoteException e) {
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
                mMediaScanService = null;
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
    public void onMediaScanningRefresh(List<ProVideo> listMedias, boolean isOnUiThread) {
    }

    @Override
    public IBinder asBinder() {
        return null;
    }
}