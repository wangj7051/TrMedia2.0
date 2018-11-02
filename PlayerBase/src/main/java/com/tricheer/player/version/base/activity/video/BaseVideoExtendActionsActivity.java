package com.tricheer.player.version.base.activity.video;

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
import js.lib.android.media.engine.scan.IMediaScanService;
import js.lib.android.media.engine.scan.MediaScanService;
import js.lib.android.media.engine.video.db.VideoDBManager;
import js.lib.android.media.player.PlayDelegate;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.utils.CharacterParser;

/**
 * 播放器扩展行动作 - BASE
 *
 * @author Jun.Wang
 */
public abstract class BaseVideoExtendActionsActivity extends BaseVideoCommonActionsActivity
        implements PlayDelegate,
        MediaScanService.VideoScanDelegate {
    // TAG
    private final String TAG = "BaseVideoExtendActions";

    //==========Variable in this Activity==========
    /**
     * 是否正在展示黑名单媒体
     */
    private boolean mIsShowingBlacklistMedias = false;

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
        bindScanService(true);
    }

//    /**
//     * Refresh Page On Notify Clear
//     */
//    protected void refreshPageOnClear(Set<String> allSdMountedPaths) {
//        if (VersionController.isCjVersion()) {
//            try {
//                ArrayList<ProVideo> listMountedProgram = new ArrayList<ProVideo>();
//                for (ProVideo program : mListPrograms) {
//                    boolean isProgramMounted = false;
//                    for (String mountedPath : allSdMountedPaths) {
//                        if (program.mediaUrl.startsWith(mountedPath)) {
//                            isProgramMounted = true;
//                            break;
//                        }
//                    }
//                    if (isProgramMounted) {
//                        listMountedProgram.add(program);
//                    }
//                }
//                mListPrograms = listMountedProgram;
//            } catch (Exception e) {
//                Logs.printStackTrace(TAG + "refreshPageOnClear()", e);
//            }
//        } else {
//            execRelease();
//        }
//    }

    /**
     * Sort List<ProVideo> by first Char
     */
    protected void sortMediaList(List<ProVideo> listPrograms) {
        if (!EmptyUtil.isEmpty(listPrograms)) {
            if (isShowingBlacklistMedias()) {
            } else {
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
                mmWeakReference = new WeakReference<LoadMediaListener>((LoadMediaListener) params[0]);

                // Get List Medias
                Map<String, ProVideo> mapPrograms = VideoDBManager.instance().getMapVideos(true, false);
                mListPrograms = new ArrayList<ProVideo>();
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
        mListPrograms = new ArrayList<ProVideo>();
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
        execPlay(getPlayPosByMediaUrl(mediaUrl));
    }

    /**
     * EXEC Play Select Program
     */
    protected void execPlay(int playPos) {
        // 如下两个条件，不执行播放
        // （1）列表为空
        // （2）索引溢出
        if (EmptyUtil.isEmpty(mListPrograms) || playPos >= mListPrograms.size()) {
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
        vvPlayer.setVisibility(View.INVISIBLE);
        saveTargetMediaPath(toPlayProgram.mediaUrl);
        mTargetAutoSeekProgress = getLastProgress();
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
        execPlay(mPlayPos);
    }

    @Override
    public void playNext() {
        super.playNext();
        execPlay(mPlayPos);
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
     * Play Pause Video
     */
    protected void execPlayOrPause() {
        Logs.i(TAG, "----execPlayOrPause()----");
        if (!EmptyUtil.isEmpty(mListPrograms)) {
            if (isPlaying()) {
                pauseByUser();
            } else {
                resumeByUser();
            }
        }
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
        CommonUtil.cancelTask(mLoadLocalMediasTask);
        super.onDestroy();
    }

    /**
     * MediaScanService bind operate.
     */
    protected void bindScanService(boolean isBind) {
        try {
            if (isBind) {
                Intent bindIntent = new Intent(this, MediaScanService.class);
                bindService(bindIntent, mScanServiceConn, BIND_AUTO_CREATE);
            } else {
                registerMediaScanDelegate(false);
                unbindService(mScanServiceConn);
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

    @Override
    public void onMediaScanningStart() {
    }

    @Override
    public void onMediaScanningEnd() {
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