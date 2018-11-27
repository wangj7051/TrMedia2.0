package com.yj.video.version.base.activity.video;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import com.tri.lib.utils.TrVideoPreferUtils;
import com.yj.video.engine.PlayerAppManager;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.engine.video.db.VideoDBManager;
import js.lib.android.media.player.PlayEnableController;
import js.lib.android.media.player.PlayMode;
import js.lib.android.media.player.PlayState;
import js.lib.android.utils.AudioManagerUtil;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * Video Player Base Activity
 *
 * @author Jun.Wang
 */
public abstract class BaseVideoPlayerActivity extends BaseVideoUIActivity {
    // LOG TAG
    private final String TAG = "BaseVideoPlayerActivity";

    /**
     * Audio focus flag
     * {@link android.media.AudioManager#AUDIOFOCUS_LOSS_TRANSIENT}
     * or {@link android.media.AudioManager#AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK}
     * or {@link android.media.AudioManager#AUDIOFOCUS_LOSS}
     * or {@link android.media.AudioManager#AUDIOFOCUS_GAIN}
     */
    private int mAudioFocusFlag = 0;

    /**
     * Listener Audio Focus
     */
    protected AudioManager.OnAudioFocusChangeListener mAfChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            mAudioFocusFlag = focusChange;
            Log.i(TAG, "f* -> {focusChange:[" + focusChange + "]");
            // 暂时失去Audio Focus，并会很快再次获得。必须停止Audio的播放，
            // 但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源；
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                Log.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_LOSS_TRANSIENT----");
                onAudioFocusTransient();

                // 暂时失去AudioFocus，但是可以继续播放，不过要在降低音量。
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                Logs.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK----");
                onAudioFocusDuck();

                // 失去了Audio Focus，并将会持续很长的时间。
                // 这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。
                // 而因为停止播放Audio的时间会很长，如果程序因为这个原因而失去AudioFocus，
                // 最好不要让它再次自动获得AudioFocus而继续播放，不然突然冒出来的声音会让用户感觉莫名其妙，感受很不好。
                // 这里直接放弃AudioFocus，当然也不用再侦听远程播放控制【如下面代码的处理】。
                // 要再次播放，除非用户再在界面上点击开始播放，才重新初始化Media，进行播放
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_LOSS----");
                onAudioFocusLoss();

                // 获得了Audio Focus；
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_GAIN----");
                onAudioFocusGain();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate(Bundle)");
    }

    /**
     * Register Audio Focus
     * <p>
     * if==1 : Register audio focus
     * <p>
     * if==2 : Abandon audio focus
     */
    public void registerAudioFocus(int flag) {
        switch (flag) {
            case 1:
                int result = AudioManagerUtil.requestMusicGain(this, mAfChangeListener);
                Logs.i(TAG, "registerAudioFocus(" + flag + ") *request AUDIOFOCUS_GAIN* >> [result:" + result);
                if (result == 1) {
                    mAudioFocusFlag = AudioManager.AUDIOFOCUS_GAIN;
                }
                break;
            case 2:
                result = AudioManagerUtil.abandon(this, mAfChangeListener);
                mAudioFocusFlag = AudioManager.AUDIOFOCUS_LOSS;
                Logs.i(TAG, "registerAudioFocus(" + flag + ") *abandon AudioFocus* >> [result:" + result);
                break;
        }
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

    protected abstract void onNotifyPlayState$Play();

    protected abstract void updateSeekTime(int progress, int duration);

    protected void onNotifyPlayState$Prepared() {
        seekBar.setMax(getDuration());
        if (mTargetAutoSeekProgress > 0 && mTargetAutoSeekProgress < seekBar.getMax()) {
            seekTo((int) mTargetAutoSeekProgress);
            mTargetAutoSeekProgress = -1;
        }

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
        PlayMode storePlayMode = TrVideoPreferUtils.getPlayMode(false, PlayMode.LOOP);
        if (storePlayMode == PlayMode.LOOP) {
            playNext();
        } else {
            clearPlayedMediaInfos();
            execPlay(mPlayPos);
        }
    }

    protected void onNotifyPlayState$Error() {
        // Play Next
        if (!EmptyUtil.isEmpty(mListPrograms)) {
            playNext();
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

    /**
     * Set Player Flag
     */
    protected void setCurrPlayer(boolean isInit, Context cxt) {
        if (isInit) {
            PlayerAppManager.putCxt(PlayerAppManager.PlayerCxtFlag.VIDEO_PLAYER, cxt);
        } else {
            PlayerAppManager.removeCxt(PlayerAppManager.PlayerCxtFlag.VIDEO_PLAYER);
        }
    }

    @Override
    public void onAudioFocusGain() {
    }

    @Override
    public void onAudioFocusTransient() {
    }

    @Override
    public void onAudioFocusDuck() {
    }

    @Override
    public void onAudioFocusLoss() {
        registerAudioFocus(2);
    }

    @Override
    public void onAudioFocus(int flag) {
    }

    @Override
    public void play() {
        super.play();
        registerAudioFocus(1);
    }

    /**
     * Is audio focus registered
     *
     * @return true-registered; false-unregistered or loss.
     */
    public boolean isAudioFocusRegistered() {
        return (mAudioFocusFlag == AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
