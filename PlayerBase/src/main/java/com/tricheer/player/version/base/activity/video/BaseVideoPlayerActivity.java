package com.tricheer.player.version.base.activity.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.tricheer.engine.mcu.MCUConsts.HandBrakeStatus;
import com.tricheer.player.R;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.engine.PlayEnableFlag;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.engine.VersionController;
import com.tricheer.player.engine.db.DBManager;
import com.tricheer.player.receiver.ReceiverOperates;
import com.tricheer.player.utils.PlayerLogicUtils;
import com.tricheer.player.version.base.view.PopMediaListView.PlayMediaListListener;
import com.tricheer.player.version.base.view.PromptDialog;
import com.tricheer.player.version.base.view.PromptDialog.PromptListener;

import java.util.Timer;
import java.util.TimerTask;

import js.lib.android.media.local.player.IPlayerState;
import js.lib.android.utils.AudioManagerUtil;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.utils.date.DateFormatUtil;

/**
 * Video Player Base Activity
 *
 * @author Jun.Wang
 */
public abstract class BaseVideoPlayerActivity extends BaseKeyEventActivity implements PlayMediaListListener {
    // LOG TAG
    private final String TAG = "BaseVideoPlayerActivity";

    /**
     * ==========Widget in this Activity==========
     */
    /**
     * ScreenSize Set
     */
    protected ImageView ivScreenSize;
    /**
     * Brake PopWindow
     */
    private PopupWindow pwHandBrakePrompt;

    /**
     * ==========Variable in this Activity==========
     */
    /**
     * Check is seek by user
     */
    private boolean mIsSeekFromUser = false;

    /**
     * Register Audio Focus Timer
     */
    private Timer mRegAudioFocusTimer;

    /**
     * Video Play Speed Control
     */
    private String mPlaySpeedFlag = ReceiverOperates.VIDEO_NORMAL;
    private final int PLAY_STEP_PEROID = 5 * 1000;

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
        public int ON = 1;
        // Means Light Mode Enable and Hide Panel
        public int OFF = 2;
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
        if (VersionController.isJzVersion()) {
            resumeRecord();
        }
    }

    @Override
    public void onAudioFocusDuck() {
        Logs.i(TAG, "^^ onAudioFocusDuck() ^^");
        // execAdjustVol(1);
    }

    @Override
    public void onAudioFocusTransient() {
        Logs.i(TAG, "^^ onAudioFocusTransient() ^^");
        if (VersionController.isJzVersion()) {
            adjustVol(1);
        } else if (VersionController.isCjVersion()) {
            removePlayRunnable();
            if (isBtCalling()) {
                CommonUtil.cancelTimer(mRegAudioFocusTimer);
            }
            pause();
        }
    }

    @Override
    public void onAudioFocusLoss() {
        Logs.i(TAG, "^^ onAudioFocusLoss() ^^");
        Logs.i(TAG, "thisObj: " + this.toString());
        finish();
        Logs.i(TAG, "----$$ Exit on Audio Focus Loss $$----");
        PlayerAppManager.exitCurrPlayer();
    }

    @Override
    public void finish() {
        super.finish();
        Logs.i(TAG, "^^ finish() ^^");
    }

    @Override
    public void onAudioFocusGain() {
        Logs.i(TAG, "^^ onAudioFocusGain() ^^");
        adjustVol(2);
        resume();
    }

    @Override
    public void onPlayFixedPos(int pos) {
    }

    @Override
    protected void onStopAllMedia() {
        super.onStopAllMedia();
        PlayerAppManager.exitCurrPlayer();
    }

    @Override
    public void onNotifyOperate(String opFlag) {
        Logs.i(TAG, "onNotifyOperate(" + opFlag + ")");
        // Common
        if (ReceiverOperates.PAUSE.equals(opFlag)) {
            mIsPauseOnNotify = true;
            pause();
        } else if (ReceiverOperates.RESUME.equals(opFlag)) {
            mIsPauseOnNotify = false;
            resume();
        } else if (ReceiverOperates.NEXT.equals(opFlag)) {
            resumeByUser();
            playNext();
        } else if (ReceiverOperates.PREVIOUS.equals(opFlag)) {
            resumeByUser();
            playPrev();

            // AIOS
        } else if (ReceiverOperates.AIS_OPEN.equals(opFlag)) {
            if (VersionController.isJzVersion()) {
                mIsPauseOnAisOpen = true;
                pause();
            }
        } else if (ReceiverOperates.AIS_EXIT.equals(opFlag)) {
            if (VersionController.isJzVersion()) {
                mIsPauseOnAisOpen = false;
                resume();
            }

            // BlueTooth Call
        } else if (ReceiverOperates.BTCALL_RUNING.equals(opFlag)) {
            removePlayRunnable();
            CommonUtil.cancelTimer(mRegAudioFocusTimer);
            pause();
        } else if (ReceiverOperates.BTCALL_END.equals(opFlag)) {
            resume();

            // Screen
        } else if (ReceiverOperates.VIDEO_PAUSE_ON_SCREEN_OFF.equals(opFlag)) {
            mIsPauseOnScreenOff = true;
            pause();
        } else if (ReceiverOperates.VIDEO_RESUME_ON_SCREEN_ON.equals(opFlag)
                || ReceiverOperates.VIDEO_RESUME_ON_MASKAPP_EXIT.equals(opFlag)) {
            mIsPauseOnScreenOff = false;
            resume();

            // E-Dog
        } else if (ReceiverOperates.PAUSE_ON_E_DOG_START.equals(opFlag)) {
            adjustVol(1);
        } else if (ReceiverOperates.RESUME_ON_E_DOG_END.equals(opFlag)) {
            adjustVol(2);

            // Video Screen Resize
        } else if (ReceiverOperates.VIDEO_SCREEN_21_9.equals(opFlag) || ReceiverOperates.VIDEO_SCREEN_FULL.equals(opFlag)
                || ReceiverOperates.VIDEO_SCREEN_16_9.equals(opFlag) || ReceiverOperates.VIDEO_SCREEN_4_3.equals(opFlag)
                || ReceiverOperates.VIDEO_SCREEN_BIGGER.equals(opFlag) || ReceiverOperates.VIDEO_SCREEN_SMALLER.equals(opFlag)) {
            execResizeScreen(opFlag);
            // Video Speed Mode
        } else if (ReceiverOperates.VIDEO_FORWARD.equals(opFlag) || ReceiverOperates.VIDEO_BACKWARD.equals(opFlag)
                || ReceiverOperates.VIDEO_NORMAL.equals(opFlag)) {
            mPlaySpeedFlag = opFlag;

            // System High Temperature
        } else if (ReceiverOperates.RESUME_ON_E_DOG_END.equals(opFlag)) {
            showHignTemperatureDialog(1);

            // Record
        } else if (ReceiverOperates.RECORD_STATE_START.equals(opFlag)) {
            showDialogRecordStart();
        } else if (ReceiverOperates.RECORD_STATE_END.equals(opFlag)) {
            // showDialogOn1080P(false);
        }
    }

    /**
     * 高温报警Dialog
     * <p>
     * 1 高温模式, 0 低温模式
     */
    protected void showHignTemperatureDialog(int tempMode) {
        if (tempMode == 1) {
            registerAudioFocus(2);
            execRelease();

            // Show Dialog
            final int msgResID = R.string.ais_device_temp_too_high;
            PromptDialog highTempDialog = new PromptDialog(mContext, new PromptListener() {

                @Override
                public void afterPromptDialogOpened() {
                    PlayerLogicUtils.notifyAisPlayStr(mContext, mContext.getString(msgResID));
                }

                @Override
                public void afterPrompDialogSureDismissed() {
                    PlayerAppManager.exitCurrPlayer();
                }
            });
            highTempDialog.showDialog(msgResID);
        }
    }

    /**
     * Is Playing Media
     */
    protected boolean isPlayingSameMedia(String mediaUrl) {
        try {
            return mediaUrl.equals(getPath());
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "isPlayingSameMedia()", e);
            return false;
        }
    }

    /**
     * Resize Screen
     */
    protected void execResizeScreen(String resizeFlag) {
    }

    @Override
    public void onProgressChange(String mediaUrl, int progress, int duration, boolean isPerSecond) {
        // 如下2种情况，不执行任何操作
        // (1) 未处于正在播放中
        // (2) SeekBar 正在进行手动拖动进度条
        if (!isPlaying() || mIsSeekFromUser) {
            return;
        }

        // 不否允许播放
        PlayEnableFlag pef = getPlayEnableFlag();
        if (!pef.isPlayEnable()) {
            removePlayRunnable();
            if (pef.isBtCalling()) {
                CommonUtil.cancelTimer(mRegAudioFocusTimer);
            }
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
        notifyDashboard(IPlayerState.PLAY);
    }

    /**
     * 通知仪表盘
     */
    private void notifyDashboard(int status) {
        if (VersionController.isSupportDashboard()) {
            Intent dashboardIntent = new Intent("com.tricheer.player.video_info");
            dashboardIntent.putExtra("path", getPath());
            dashboardIntent.putExtra("status", status);
            dashboardIntent.putExtra("progress", getProgress());
            dashboardIntent.putExtra("position", getPlayPosByMediaUrl(getPath()));
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
    public void onNotifyPlayState(int playState) {
        super.onNotifyPlayState(playState);
        PlayerLogicUtils.printPlayState(TAG, IPlayerState.getStateDesc(playState));
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
            case IPlayerState.PLAY:
                updatePlayStatus(1);
                onNotifyPlayState$Play();
                break;
            case IPlayerState.PREPARED:
                updatePlayStatus(1);
                onNotifyPlayState$Prepared();
                break;
            case IPlayerState.PAUSE:
                updatePlayStatus(2);
                break;
            case IPlayerState.COMPLETE:
                updatePlayStatus(2);
                onNotifyPlayState$Complete();
                break;
            case IPlayerState.ERROR:
                updatePlayStatus(2);
                onNotifyPlayState$Error();
                break;
            case IPlayerState.SEEK_COMPLETED:
                updateSeekTime(getProgress(), getDuration());
                break;
            default:
                updatePlayStatus(2);
                break;
        }
    }

    protected void onNotifyPlayState$Play() {
        startRegisterAudioFocusTimer();
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
                refreshMediaList(true);
                DBManager.updateMediaDuration(video);
            }
        }
    }

    protected void onNotifyPlayState$Complete() {
        mPlaySpeedFlag = ReceiverOperates.VIDEO_NORMAL;
        playNext();
    }

    protected void onNotifyPlayState$Error() {
        ProVideo programWithError = null;
        if (!EmptyUtil.isEmpty(mListPrograms)) {
            programWithError = mListPrograms.get(mPlayPos);
            if (programWithError != null) {
                programWithError.isCauseError = 1;
                // DBManager.updateProgramInfo(programWithError);
                mListPrograms.remove(programWithError);
            }
            // Toast Play Error
            PlayerLogicUtils.toastPlayError(mContext, programWithError.title);
        }

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
            seekTo(mTargetAutoSeekProgress);
            mTargetAutoSeekProgress = -1;
        }
    }

    /**
     * Refresh Media List
     */
    protected void refreshMediaList(boolean isJustRefresh) {
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
            if (isPlayEnable()) {
                // 获取此时目标进度，这是为了在图像重构完成后能够跳转到历史位置
                mTargetAutoSeekProgress = getLastProgress();
                vvPlayer.setPlayAtBgOnSufaceDestoryed(true);
            } else {
                vvPlayer.setPlayAtBgOnSufaceDestoryed(false);
            }
        }

        // 关闭弹出框
        dismissBrakePropmt();
    }

    @Override
    protected void onIDestroy() {
        super.onIDestroy();
        // 释放播放器
        execRelease();
        // 取消屏常亮
        makeScreenOn(false);

        // 关闭弹出框
        dismissBrakePropmt();
        showDialogOn1080P(false);

        // 更新分辨率，并恢复行车记录
        doBroadCastVideoResolution(0, true);
        resumeRecord();

        // 取消计时器
        CommonUtil.cancelTimer(mRegAudioFocusTimer);

        // 取消 注册的声音焦点
        registerAudioFocus(2);
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

    /**
     * 开始启动注册声音焦点线程
     * <p>
     * 为了保证与其他音频操作的互斥操作，必须要不停的注册声音焦点，直到成功为止
     */
    private void startRegisterAudioFocusTimer() {
        CommonUtil.cancelTimer(mRegAudioFocusTimer);
        mRegAudioFocusTimer = new Timer();
        mRegAudioFocusTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (isPlaying()) {
                    if (AudioManagerUtil.isAudioFocusRegistered(registerAudioFocus(1))) {
                        Logs.i(TAG, "----^^ On Audio Focus Register Successfully ^^----");
                        runOnUiThread(mmCancelAudioFocusTimer);
                    } else {
                        Logs.i(TAG, "----^^ On Audio Focus Register Failure ^^----");
                    }
                }
            }

            private Runnable mmCancelAudioFocusTimer = new Runnable() {

                @Override
                public void run() {
                    CommonUtil.cancelTimer(mRegAudioFocusTimer);
                }
            };
        }, 1000, 1000);
    }

    /**
     * >>>---------------------------------<<<
     * <p>
     * >>>【开灯模式&&关灯模式设置=====Start】<<<
     * <p>
     * >>>---------------------------------<<<
     */
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

    /**
     * >>>----------------------<<<
     * <p>
     * >>> 【手刹提示=====Start】 <<<
     * <p>
     * >>>----------------------<<<
     */
    /**
     * Show HandBrake PopWindow by HandBrakeStatus
     */
    protected void showHandBrakePrompt(int brakeStatus, View v) {
        if (PlayerLogicUtils.isHandBrakeEnable(getContentResolver())) {
            if (brakeStatus == HandBrakeStatus.OFF) {
                showHandBrakePrompt(v);
            } else if (brakeStatus == HandBrakeStatus.ON) {
                dismissBrakePropmt();
            }
        }
    }

    @SuppressLint("InflateParams")
    private void showHandBrakePrompt(View v) {
        try {
            if (pwHandBrakePrompt == null) {
                View vHandBrakePrompt = getLayoutInflater().inflate(R.layout.lct_lm8917_zbk_v_video_handbrake_prompt, null);
                pwHandBrakePrompt = new PopupWindow(mContext);
                pwHandBrakePrompt.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                pwHandBrakePrompt.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                pwHandBrakePrompt.setBackgroundDrawable(mContext.getDrawable(android.R.color.transparent));
                pwHandBrakePrompt.setContentView(vHandBrakePrompt);
                pwHandBrakePrompt.setOutsideTouchable(false);
            }
            if (!pwHandBrakePrompt.isShowing()) {
                pwHandBrakePrompt.showAtLocation(v, Gravity.CENTER, 0, 0);
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "showHandBrakePrompt()", e);
        }
    }

    /**
     * Dismiss HandBrake PopWindow
     */
    protected void dismissBrakePropmt() {
        try {
            if (pwHandBrakePrompt != null && pwHandBrakePrompt.isShowing()) {
                pwHandBrakePrompt.dismiss();
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "dismissBrakePropmt()", e);
        }
    }

    /**
     * Resume HandBrake PopWindown By HandBrakeStatus
     */
    protected void resumeHandBrakeStatus(View v) {
        dismissBrakePropmt();
        showHandBrakePrompt(mController.getHandBrakeOperateStatus(), v);
    }
}
