package com.tricheer.player.version.base.activity.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.engine.PlayEnableFlag;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.utils.PlayerPreferUtils;
import com.tricheer.player.version.base.activity.BasePlayerActivity;

import java.util.List;

import js.lib.android.media.video.player_native.IVideoPlayer;
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
     * 安全线程 - 上一个/下一个
     * <p>
     * 防高频点击线程
     */
    private Runnable mPlayPrevSecRunnable, mPlayNextSecRunnable;

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
    public PlayEnableFlag getPlayEnableFlag() {
        PlayEnableFlag pef = new PlayEnableFlag();
        pef.pauseByUser(mIsPauseOnNotify);
        pef.pauseByScreenOff(mIsPauseOnScreenOff);
        pef.pauseByAiosOpen(mIsPauseOnAisOpen);
        pef.complete();
        return pef;
    }

    @Override
    public boolean isPlayEnable() {
        Logs.i(TAG, "^^ isPlayEnable() ^^");
        PlayEnableFlag pef = getPlayEnableFlag();
        pef.print();
        return pef.isPlayEnable();
    }

    @Override
    public void removePlayRunnable() {
        if (mPlayPrevSecRunnable != null) {
            mHandler.removeCallbacks(mPlayPrevSecRunnable);
        }
        if (mPlayNextSecRunnable != null) {
            mHandler.removeCallbacks(mPlayNextSecRunnable);
        }
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
    public void playPrev() {
        Logs.i(TAG, "^^ playPrev() ^^");
        if (isPlayEnable()) {
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
    public void playPrevBySecurity() {
        Logs.i(TAG, "^^ playPrevBySecurity() ^^");
        removePlayRunnable();
        if (mPlayPrevSecRunnable == null) {
            mPlayPrevSecRunnable = new Runnable() {

                @Override
                public void run() {
                    Logs.i(TAG, "playPrevBySecurity() -> ^^ mPlayPrevSecRunnable ^^");
                    playPrev();
                }
            };
        }
        mHandler.postDelayed(mPlayPrevSecRunnable, 500);
    }

    @Override
    public void playNext() {
        Logs.i(TAG, "^^ playNext() ^^");
        if (isPlayEnable()) {
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
    public void playNextBySecurity() {
        Logs.i(TAG, "^^ playNextBySecurity() ^^");
        removePlayRunnable();
        if (mPlayNextSecRunnable == null) {
            mPlayNextSecRunnable = new Runnable() {

                @Override
                public void run() {
                    Logs.i(TAG, "playPrevBySecurity() -> ^^ mPlayNextSecRunnable ^^");
                    playNext();
                }
            };
        }
        mHandler.postDelayed(mPlayNextSecRunnable, 500);
    }

    @Override
    public void pause() {
        Logs.i(TAG, "^^ pause() ^^");
        if (vvPlayer != null) {
            vvPlayer.pauseMedia();
        }
    }

    @Override
    public void pauseByUser() {
        Logs.i(TAG, "^^ pauseByUser() ^^");
        mIsPauseOnNotify = true;
        removePlayRunnable();
        pause();
    }

    @Override
    public void resume() {
        Logs.i(TAG, "^^ resume() ^^");
        if (isPlayEnable()) {
            if (!isPlaying()) {
                play();
                makeScreenOn(true);
            }
        }
    }

    @Override
    public void resumeByUser() {
        Logs.i(TAG, "^^ resumeByUser() ^^");
        mIsPauseOnNotify = false;
        removePlayRunnable();
        resume();
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
    public String getLastPath() {
        return getLastTargetMediaUrl();
    }

    @Override
    public int getLastProgress() {
        int lastProgress = 0;
        try {
            String lastTargetMediaUrl = getLastPath();
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
    public String getPath() {
        if (vvPlayer != null) {
            return vvPlayer.getMediaPath();
        }
        return "";
    }

    @Override
    public int getPosition() {
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
    public String getLastTargetMediaUrl() {
        return PlayerPreferUtils.getLastTargetMediaUrl(false, PlayerCxtFlag.VIDEO_PLAYER, "");
    }

    @Override
    public void saveTargetMediaUrl(String mediaUrl) {
        PlayerPreferUtils.getLastTargetMediaUrl(true, PlayerCxtFlag.VIDEO_PLAYER, mediaUrl);
    }

    @Override
    public String[] getPlayedMediaInfos() {
        return PlayerPreferUtils.getLastPlayedMediaInfo(false, PlayerCxtFlag.VIDEO_PLAYER, "", 0);
    }

    @Override
    public void savePlayMediaInfos(String mediaUrl, int progress) {
        PlayerPreferUtils.getLastPlayedMediaInfo(true, PlayerCxtFlag.VIDEO_PLAYER, mediaUrl, progress);
        Logs.debugI("SavePlayInfo", "savePlayMediaInfos() -> [mediaUrl:" + mediaUrl + " ; progress:" + progress);
    }

    @Override
    public void clearPlayedMediaInfos() {
        PlayerPreferUtils.getLastPlayedMediaInfo(true, PlayerCxtFlag.VIDEO_PLAYER, "", 0);
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
