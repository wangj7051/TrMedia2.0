package com.tricheer.player.version.base.activity.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.engine.VersionController;
import com.tricheer.player.receiver.ReceiverOperates;

import js.lib.android.media.PlayEnableController;
import js.lib.android.media.PlayState;
import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.video.db.VideoDBManager;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.utils.date.DateFormatUtil;

/**
 * Video Player Base Activity
 *
 * @author Jun.Wang
 */
public abstract class BaseVideoPlayerActivity extends BaseVideoFocusActivity {
    // LOG TAG
    private final String TAG = "BaseVideoPlayerActivity";

    //==========Widget in this Activity==========
    //==========Variable in this Activity==========
    /**
     * Check is seek by user
     */
    private boolean mIsSeekFromUser = false;

    /**
     * Video Play Speed Control
     */
    private String mPlaySpeedFlag = ReceiverOperates.VIDEO_NORMAL;
    private static final int PLAY_STEP_PEROID = 5 * 1000;

    /**
     * Video Screen 4:3 / 16:9 / 21:9
     */
    protected final int SCREEN_4_3 = 1, SCREEN_16_9 = 2, SCREEN_21_9 = 3;
    protected int mResizeMode = SCREEN_16_9;

    /**
     * Light Mode Flag
     */
    private int mLightMode = VideoLightMode.ON;

    protected interface VideoLightMode {
        // Means Light Mode Enable and Show Panel
        int ON = 1;
        // Means Light Mode Enable and Hide Panel
        int OFF = 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        super.init();
        mIsPauseOnNotify = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重置标记
        vvPlayer.setPlayAtBgOnSufaceDestoryed(false);
        // 获取此时目标进度，这是为了在图像重构完成后能够跳转到历史位置
        mTargetAutoSeekProgress = getLastProgress();
        Logs.i(TAG, "onResume() -> [mTargetAutoSeekProgress:" + mTargetAutoSeekProgress);
    }

    @Override
    protected void onHomeKeyClick() {
        super.onHomeKeyClick();
        Logs.i(TAG, "^^ onHomeKeyClick() ^^");
    }

    @Override
    public void finish() {
        super.finish();
        Logs.i(TAG, "^^ finish() ^^");
    }

    @Override
    public void onAudioFocusGain() {
        super.onAudioFocusGain();
        Logs.i(TAG, "^^ onAudioFocusGain() ^^");
        resume();
    }

    @Override
    public void onProgressChanged(String mediaUrl, int progress, int duration) {
        // 如下2种情况，不执行任何操作
        // (1) 未处于正在播放中
        // (2) SeekBar 正在进行手动拖动进度条
        if (!isPlaying() || mIsSeekFromUser) {
            return;
        }

        // 不否允许播放
        if (!PlayEnableController.isPlayEnable()) {
            removePlayRunnable();
            pause();
            return;
        }

        // 视频播放 - {正常模式}
        if (mPlaySpeedFlag == ReceiverOperates.VIDEO_NORMAL) {
            seekBar.setProgress(progress);
            updateSeekTime(progress, duration);

            // 视频播放 - {快进 / 快退 模式}
        } else {
            int targetProgress = progress;
            if (mPlaySpeedFlag == ReceiverOperates.VIDEO_FORWARD) {
                targetProgress = progress + PLAY_STEP_PEROID;
                if (targetProgress >= duration) {
                    mPlaySpeedFlag = ReceiverOperates.VIDEO_NORMAL;
                    targetProgress = progress;
                }
            } else if (mPlaySpeedFlag == ReceiverOperates.VIDEO_BACKWARD) {
                targetProgress = progress - PLAY_STEP_PEROID;
                if (targetProgress <= 0) {
                    mPlaySpeedFlag = ReceiverOperates.VIDEO_NORMAL;
                    targetProgress = progress;
                }
            }

            if (targetProgress != progress) {
                seekTo(targetProgress);
                seekBar.setProgress(targetProgress);
                updateSeekTime(targetProgress, duration);
            } else {
                seekBar.setProgress(progress);
                updateSeekTime(progress, duration);
            }
        }

        // 每秒钟保存一次播放信息
        savePlayInfo();
        // 通知仪表盘信息
        notifyDashboard(PlayState.PLAY);
    }

    /**
     * 通知仪表盘
     */
    private void notifyDashboard(PlayState state) {
        if (VersionController.isSupportDashboard()) {
            Intent dashboardIntent = new Intent("com.tricheer.player.video_info");
            dashboardIntent.putExtra("path", getCurrMediaPath());
            dashboardIntent.putExtra("status", state);
            dashboardIntent.putExtra("progress", getProgress());
            dashboardIntent.putExtra("position", getPlayPosByMediaUrl(getCurrMediaPath()));
            dashboardIntent.putExtra("total", getTotalCount());
            sendBroadcast(dashboardIntent);
        }
    }

    /**
     * Refresh SeekBar & Play Time
     */
    protected void updateSeekTime(int progress, int duration) {
        Logs.debugI("updateTime", progress + "----" + duration);
        // Set Time Display
        if (tvStartTime != null) {
            tvStartTime.setText(DateFormatUtil.getFormatHHmmss(progress));
            Logs.debugI("updateTime", "StartTime --> " + tvStartTime.getText().toString());
        }
        if (tvEndTime != null) {
            tvEndTime.setText(DateFormatUtil.getFormatHHmmss(duration - progress));
            Logs.debugI("updateTime", "EndTime --> " + tvEndTime.getText().toString() + "\n ");
        }
    }

    @Override
    public void onPlayStateChanged(PlayState playState) {
        super.onPlayStateChanged(playState);
        Logs.i(TAG, " ");
        Logs.i(TAG, "---->>>> playState:[" + playState + "] <<<<----");
        // 以下三种情况不执行状态监听
        // （1） Activity未被销毁的时候
        // （2）Player播放器未获取到
        // （3）播放器被释放
        if (isDestroyed() || vvPlayer == null || isPlayerReleased()) {
            return;
        }

        // 通知仪表盘信息
        notifyDashboard(playState);

        switch (playState) {
            case PLAY:
                updatePlayStatus(1);
                onNotifyPlayState$Play();
                break;
            case PREPARED:
                updatePlayStatus(1);
                onNotifyPlayState$Prepared();
                break;
            case PAUSE:
                updatePlayStatus(2);
                break;
            case COMPLETE:
                updatePlayStatus(2);
                onNotifyPlayState$Complete();
                break;
            case ERROR:
                updatePlayStatus(2);
                onNotifyPlayState$Error();
                break;
            case SEEK_COMPLETED:
                updateSeekTime(getProgress(), getDuration());
                break;
            default:
                updatePlayStatus(2);
                break;
        }
    }

    protected void onNotifyPlayState$Play() {
    }

    protected void onNotifyPlayState$Prepared() {
        seekBar.setMax(getDuration());
        seekToTargetProgress();

        // Refresh Current Media Duration
        Object objParam = vvPlayer.getTag();
        if (objParam != null) {
            ProVideo video = (ProVideo) objParam;
            if (video.duration == 0) {
                video.duration = getDuration();
                VideoDBManager.instance().updateMediaDuration(video);
            }
        }
    }

    protected void onNotifyPlayState$Complete() {
        mPlaySpeedFlag = ReceiverOperates.VIDEO_NORMAL;
        playNext();
    }

    protected void onNotifyPlayState$Error() {
        ProVideo programWithError = null;
        // Play Next
        if (!EmptyUtil.isEmpty(mListPrograms)) {
            mPlaySpeedFlag = ReceiverOperates.VIDEO_NORMAL;
            if (mPlayPos <= mListPrograms.size()) {
                execPlay(mPlayPos);
            } else {
                execPlay(0);
            }
        }
    }

    private void seekToTargetProgress() {
        if (mTargetAutoSeekProgress > 0) {
            seekTo((int) mTargetAutoSeekProgress);
            mTargetAutoSeekProgress = -1;
        }
    }

    /**
     * Update Play Status
     * <p>
     * if==1 : play
     * <p>
     * if==2 : pause
     */
    protected void updatePlayStatus(int flag) {
    }

    /**
     * SeekBar Seek Event
     */
    public class SeekOnChange implements SeekBar.OnSeekBarChangeListener {

        int mmProgress = 0;

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mmProgress = progress;
            mIsSeekFromUser = fromUser;
            if (mIsSeekFromUser) {
                mPlaySpeedFlag = ReceiverOperates.VIDEO_NORMAL;
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mIsSeekFromUser) {
                mIsSeekFromUser = false;
                seekTo(mmProgress);
            }
        }
    }

    /**
     * SeekBar Touch Event
     */
    public class SeekOnTouch implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Logs.i(TAG, "seekBar -> SeekOnTouch -> ACTION_DOWN");
                    setLightMode(VideoLightMode.ON);
                    break;
                case MotionEvent.ACTION_UP:
                    Logs.i(TAG, "seekBar -> SeekOnTouch -> ACTION_UP");
                    resetLightMode();
                    break;
            }
            return false;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (!isPlaying() && CommonUtil.isRunningBackground(mContext, getPackageName())) {
            PlayerAppManager.exitCurrPlayer();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        trimMemory(level);
        super.onTrimMemory(level);
    }

    private void trimMemory(int level) {
        Logs.i(TAG, "trimMemory(" + level + ")");
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            getImageLoader().clearMemoryCache();
            getImageLoader().destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause
        pause();
        // 如果允许后台播放
        if (isCanPlayAtBg()) {
            if (PlayEnableController.isPlayEnable()) {
                // 获取此时目标进度，这是为了在图像重构完成后能够跳转到历史位置
                mTargetAutoSeekProgress = getLastProgress();
                vvPlayer.setPlayAtBgOnSufaceDestoryed(true);
            } else {
                vvPlayer.setPlayAtBgOnSufaceDestoryed(false);
            }
        }
    }

    @Override
    protected void onIDestroy() {
        super.onIDestroy();
        // 释放播放器
        execRelease();
        // 取消屏常亮
        makeScreenOn(false);
    }

    /**
     * Set Player Flag
     */
    protected void setCurrPlayer(boolean isInit, Context cxt) {
        if (isInit) {
            PlayerAppManager.putCxt(PlayerCxtFlag.VIDEO_PLAYER, cxt);
        } else {
            PlayerAppManager.removeCxt(PlayerCxtFlag.VIDEO_PLAYER);
        }
    }

    //>>>---------------------------------<<<
    // >>>【开灯模式&&关灯模式设置=====Start】<<<
    //>>>---------------------------------<<<

    /**
     * Is Light Mode On
     */
    protected boolean isLightOn() {
        if (VersionController.isCanTurnOffLight()) {
            return mLightMode == VideoLightMode.ON;
        }
        return true;
    }

    /**
     * Set Light Mode
     *
     * @param lightMode : {@link VideoLightMode}
     */
    protected void setLightMode(int lightMode) {
        if (VersionController.isCanTurnOffLight()) {
            mHandler.removeCallbacks(mLightModeOffRunnable);
            this.mLightMode = lightMode;
        }
    }

    /**
     * Switch Light Mode
     */
    protected void switchLightMode() {
        if (VersionController.isCanTurnOffLight()) {
            if (mLightMode == VideoLightMode.ON) {
                setLightMode(VideoLightMode.OFF);
            } else if (mLightMode == VideoLightMode.OFF) {
                setLightMode(VideoLightMode.ON);
                resetLightMode();
            }
        }
    }

    /**
     * Reset Light Mode
     */
    protected void resetLightMode() {
        if (VersionController.isCanTurnOffLight()) {
            mHandler.removeCallbacks(mLightModeOffRunnable);
            mHandler.postDelayed(mLightModeOffRunnable, 5 * 1000);
        }
    }

    private Runnable mLightModeOffRunnable = new Runnable() {

        @Override
        public void run() {
            setLightMode(VideoLightMode.OFF);
        }
    };
}
