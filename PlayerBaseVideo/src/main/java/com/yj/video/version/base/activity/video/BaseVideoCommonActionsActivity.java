package com.yj.video.version.base.activity.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.tri.lib.utils.TrVideoPreferUtils;
import com.yj.video.version.base.activity.BasePlayerActivity;

import java.util.List;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.player.PlayEnableController;
import js.lib.android.media.player.PlayMode;
import js.lib.android.media.player.video.IVideoPlayer;
import js.lib.android.media.player.video.utils.VideoPreferUtils;
import js.lib.android.utils.Logs;

/**
 * 播放器统一动作 - BASE
 * <p>
 * (1) 实现了"自定义统一行为"
 * <p>
 * (2) 实现了"注册的服务及广播"的统一行为
 *
 * @author Jun.Wang
 */
public abstract class BaseVideoCommonActionsActivity extends BasePlayerActivity {
    // TAG
    private final String TAG = "BaseVideoCommonActionsActivity";

    /**
     * Thread handler
     */
    protected Handler mHandler = new Handler();

    /**
     * 视频播放器对象
     */
    protected IVideoPlayer vvPlayer;

    /**
     * To Play Programs
     */
    protected List<ProVideo> mListPrograms;
    /**
     * Current Play Position
     */
    protected int mPlayPos = 0;

    /**
     * FLAG - 播放器是否被释放了
     */
    private boolean mIsPlayerReleased = true;

    /**
     * FLAG - 是否在收到广播Intent.ACTION_SCREEN_OFF后暂停
     */
    protected boolean mIsPauseOnScreenOff = false;

    /**
     * Screen On/OFF
     * <p>
     * (1) Intent.ACTION_SCREEN_ON / Intent.ACTION_SCREEN_OFF
     */
    private BroadcastReceiver mScreenStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    /**
     * 播放器是否被释放
     */
    protected boolean isPlayerReleased() {
        return mIsPlayerReleased;
    }

    @Override
    public void switchPlayMode(int supportFlag) {
        PlayMode storePlayMode = VideoPreferUtils.getPlayMode(false, PlayMode.LOOP);
        if (storePlayMode == null) {
            return;
        }
        switch (supportFlag) {
            case 51: {
                switch (storePlayMode) {
                    case SINGLE:
                        VideoPreferUtils.getPlayMode(true, PlayMode.RANDOM);
                        break;
                    case RANDOM:
                        VideoPreferUtils.getPlayMode(true, PlayMode.LOOP);
                        break;
                    case LOOP:
                        VideoPreferUtils.getPlayMode(true, PlayMode.ORDER);
                        break;
                    case ORDER:
                        VideoPreferUtils.getPlayMode(true, PlayMode.SINGLE);
                        break;
                    default:
                        VideoPreferUtils.getPlayMode(true, PlayMode.LOOP);
                        break;
                }
            }
            break;
            case 52: {
                switch (storePlayMode) {
                    case SINGLE:
                        VideoPreferUtils.getPlayMode(true, PlayMode.RANDOM);
                        break;
                    case RANDOM:
                        VideoPreferUtils.getPlayMode(true, PlayMode.LOOP);
                        break;
                    case LOOP:
                        VideoPreferUtils.getPlayMode(true, PlayMode.SINGLE);
                        break;
                    default:
                        VideoPreferUtils.getPlayMode(true, PlayMode.LOOP);
                        break;
                }
            }
            break;
            case 53: {
                switch (storePlayMode) {
                    case SINGLE:
                        VideoPreferUtils.getPlayMode(true, PlayMode.LOOP);
                        break;
                    case LOOP:
                        VideoPreferUtils.getPlayMode(true, PlayMode.SINGLE);
                        break;
                    default:
                        VideoPreferUtils.getPlayMode(true, PlayMode.LOOP);
                        break;
                }
            }
            break;
        }
        onPlayModeChange();
    }

    @Override
    public void play() {
        Logs.i(TAG, "^^ play() ^^");
        mIsPlayerReleased = false;
        if (vvPlayer != null) {
            vvPlayer.playMedia();
            vvPlayer.requestFocus();
        }
    }

    @Override
    public void play(String mediaPath) {
    }

    @Override
    public void play(int pos) {
    }

    @Override
    public void playPrev() {
        Logs.i(TAG, "^^ playPrev() ^^");
        if (PlayEnableController.isPlayEnable()) {
            try {
                mPlayPos--;
                if (mPlayPos < 0) {
                    mPlayPos = mListPrograms.size() - 1;
                }
                clearPlayedMediaInfos();
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "execPlayPre()", e);
            }
        }
    }

    @Override
    public void playNext() {
        Logs.i(TAG, "^^ playNext() ^^");
        if (PlayEnableController.isPlayEnable()) {
            try {
                mPlayPos++;
                if (mPlayPos >= mListPrograms.size()) {
                    mPlayPos = 0;
                }
                clearPlayedMediaInfos();
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "execPlayNext()", e);
            }
        }
    }

    @Override
    public void pause() {
        Logs.i(TAG, "^^ pause() ^^");
        if (vvPlayer != null) {
            vvPlayer.pauseMedia();
        }
    }

    @Override
    public void resume() {
        Logs.i(TAG, "^^ resume() ^^");
        if (PlayEnableController.isPlayEnable()) {
            if (!isPlaying()) {
                play();
                makeScreenOn(true);
            }
        }
    }

    @Override
    public void release() {
        Logs.i(TAG, "^^ release() ^^");
        if (vvPlayer != null) {
            mIsPlayerReleased = true;
            vvPlayer.clearFocus();
            vvPlayer.releaseMedia();
            vvPlayer = null;
        }
    }

    @Override
    public String getLastMediaPath() {
        return getLastTargetMediaPath();
    }

    @Override
    public long getLastProgress() {
        int lastProgress = 0;
        try {
            String lastTargetMediaUrl = getLastMediaPath();
            Logs.i(TAG, "getLastMediaProgress() -> [lastTargetMediaUrl:" + lastTargetMediaUrl);
            String[] mediaInfos = getPlayedMediaInfos();
            if (mediaInfos != null) {
                Logs.i(TAG, "getLastMediaProgress() -> [lastPlayedMediaUrl:" + mediaInfos[0]);
                if (mediaInfos[0].equals(lastTargetMediaUrl)) {
                    lastProgress = Integer.valueOf(mediaInfos[1]);
                } else {
                    clearPlayedMediaInfos();
                }
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getLastMediaProgress()", e);
        }
        Logs.i(TAG, "getLastMediaProgress() -> [last Progress:" + lastProgress);
        return lastProgress;
    }

    @Override
    public String getCurrMediaPath() {
        if (vvPlayer != null) {
            return vvPlayer.getMediaPath();
        }
        return "";
    }

    @Override
    public int getCurrIdx() {
        return mPlayPos;
    }

    @Override
    public int getTotalCount() {
        if (mListPrograms != null) {
            return mListPrograms.size();
        }
        return 0;
    }

    @Override
    public int getProgress() {
        if (vvPlayer != null) {
            return vvPlayer.getMediaProgress();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (vvPlayer != null) {
            return vvPlayer.getMediaDuration();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return vvPlayer != null && vvPlayer.isMediaPlaying();
    }

    @Override
    public void seekTo(int msec) {
        if (vvPlayer != null) {
            vvPlayer.seekTo(msec);
        }
    }

    @Override
    public String getLastTargetMediaPath() {
        return TrVideoPreferUtils.getLastTargetMediaUrl(false, "");
    }

    @Override
    public void saveTargetMediaPath(String mediaPath) {
        TrVideoPreferUtils.getLastTargetMediaUrl(true, mediaPath);
    }

    @Override
    public String[] getPlayedMediaInfos() {
        return TrVideoPreferUtils.getLastPlayedMediaInfo(false, "", 0);
    }

    @Override
    public void savePlayMediaInfos(String mediaUrl, int progress) {
        TrVideoPreferUtils.getLastPlayedMediaInfo(true, mediaUrl, progress);
        Logs.debugI("SavePlayInfo", "savePlayMediaInfos() -> [mediaUrl:" + mediaUrl + " ; progress:" + progress);
    }

    @Override
    public void clearPlayedMediaInfos() {
        TrVideoPreferUtils.getLastPlayedMediaInfo(true, "", 0);
    }

    /**
     * 注册屏状态广播
     */
    protected void registerScreenStatusReceiver(boolean isReg) {
        try {
            if (isReg) {
                // Screen Status
                IntentFilter ifScreenStatus = new IntentFilter();
                ifScreenStatus.addAction(Intent.ACTION_SCREEN_ON);
                ifScreenStatus.addAction(Intent.ACTION_SCREEN_OFF);
                registerReceiver(mScreenStatusReceiver, ifScreenStatus);
            } else {
                unregisterReceiver(mScreenStatusReceiver);
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "registerScreenStatusReceiver()", e);
        }
    }
}
