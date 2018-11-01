package com.tricheer.player.version.base.activity.video;

import android.content.Context;
import android.os.Bundle;

import com.tri.lib.utils.TrVideoPreferUtils;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.engine.video.db.VideoDBManager;
import js.lib.android.media.player.PlayEnableController;
import js.lib.android.media.player.PlayMode;
import js.lib.android.media.player.PlayState;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * Video Player Base Activity
 *
 * @author Jun.Wang
 */
public abstract class BaseVideoPlayerActivity extends BaseVideoFocusActivity {
    // LOG TAG
    private final String TAG = "BaseVideoPlayerActivity";

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
            playNextBySecurity();
        } else {
            clearPlayedMediaInfos();
            execPlay(mPlayPos);
        }
    }

    protected void onNotifyPlayState$Error() {
        // Play Next
        if (!EmptyUtil.isEmpty(mListPrograms)) {
            playNextBySecurity();
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
            PlayerAppManager.putCxt(PlayerCxtFlag.VIDEO_PLAYER, cxt);
        } else {
            PlayerAppManager.removeCxt(PlayerCxtFlag.VIDEO_PLAYER);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
