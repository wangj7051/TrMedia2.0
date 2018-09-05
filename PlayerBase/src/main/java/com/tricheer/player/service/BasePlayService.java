package com.tricheer.player.service;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import js.lib.android.media.local.player.IPlayerListener;
import js.lib.android.media.local.player.IPlayerState;
import js.lib.android.utils.AudioManagerUtil;
import js.lib.android.utils.Logs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Handler;
import android.os.IBinder;

import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.engine.PlayEnableFlag;
import com.tricheer.player.engine.PlayerActionsListener;
import com.tricheer.player.engine.PlayerConsts.PlayMode;
import com.tricheer.player.engine.VersionController;
import com.tricheer.player.receiver.PlayerBaseReceiver.PlayerReceiverListener;
import com.tricheer.player.utils.PlayerLogicUtils;
import com.tricheer.player.utils.PlayerPreferUtils;

/**
 * Base Play Service
 *
 * @author Jun.Wang
 */
public abstract class BasePlayService extends Service implements PlayerReceiverListener, PlayerActionsListener {
    private final String TAG = "BasePlayerService";

    /**
     * Context
     */
    protected Context mContext;

    /**
     * Thread Handler
     */
    protected Handler mHandler = new Handler();

    /**
     * Player State Listener out of service
     */
    private Set<PlayerActionsListener> mSetPlayerListeners = new HashSet<PlayerActionsListener>();

    /**
     * Service 是否销毁了
     */
    protected boolean mIsDestoryed = false;

    /**
     * Pause Flag on Notify
     */
    protected boolean mIsPauseOnNotify = false;
    /**
     * Pause Flag on AiSpeech Window On
     */
    protected boolean mIsPauseOnAisOpen = false;
    /**
     * Pause Flag on BlueTooth Dialing
     */
    protected boolean mIsPauseOnBtDialing = false;
    /**
     * Pause Flag on E-Dog Start
     */
    protected boolean mIsPauseOnEDogStart = false;

    /**
     * Is System Down
     */
    private boolean mIsSystemDown = false;

    /**
     * Listener Audio Focus
     */
    protected OnAudioFocusChangeListener mAfChangeListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            Logs.i(TAG, "f* -> {focusChange:[" + focusChange + "]");
            // 暂时失去Audio Focus，并会很快再次获得。必须停止Audio的播放，
            // 但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源；
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                Logs.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_LOSS_TRANSIENT----");
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
                Logs.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_LOSS----");
                onAudioFocusLoss();

                // 获得了Audio Focus；
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Logs.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_GAIN----");
                onAudioFocusGain();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 通知播放器状态
     */
    protected void notifyPlayState(int playState) {
        for (IPlayerListener l : mSetPlayerListeners) {
            if (l != null) {
                l.onNotifyPlayState(playState);
            }
        }
    }

    /**
     * 通知仪表盘
     */
    private void notifyDashboard(int status) {
        if (VersionController.isSupportDashboard()) {
            Intent dashboardIntent = new Intent("com.tricheer.player.music_info");
            dashboardIntent.putExtra("path", getPath());
            dashboardIntent.putExtra("status", status);
            dashboardIntent.putExtra("progress", getProgress());
            dashboardIntent.putExtra("position", getPosition());
            dashboardIntent.putExtra("total", getTotalCount());
            dashboardIntent.putExtra("playMode", PlayerPreferUtils.getMusicPlayMode(false, PlayMode.LOOP));
            sendBroadcast(dashboardIntent);
        }
    }

    /**
     * Register Audio Focus
     * <p>
     * if==1 : Register audio focus
     * <p>
     * if==2 : Abandon audio focus
     */
    protected int registerAudioFocus(int flag) {
        int result = -1;
        if (flag == 1) {
            result = AudioManagerUtil.requestMusicGain(mContext, mAfChangeListener);
            Logs.i(TAG, "registerAudioFocus(" + flag + ") *request AUDIOFOCUS_GAIN* >> [result:" + result);
        } else if (flag == 2) {
            result = AudioManagerUtil.abandon(mContext, mAfChangeListener);
            Logs.i(TAG, "registerAudioFocus(" + flag + ") *abandon AudioFocus* >> [result:" + result);
        }
        return result;
    }

    /**
     * System Down Status
     */
    public void setSystemDown() {
        mIsSystemDown = true;
    }

    /**
     * System Up Status
     */
    public void setSystemUp() {
        mIsSystemDown = false;
    }

    /**
     * Get System Down Status
     */
    protected boolean isSystemDown() {
        return mIsSystemDown;
    }

    /**
     * 蓝牙通话是否正在进行中
     */
    public boolean isBtCalling() {
        return PlayerLogicUtils.isBtCalling(mContext);
    }

    // {@link PlayerReceiverListener} Implements Method
    @Override
    public void onPlayFromFolder(int playPos, List<String> listPlayPaths) {
    }

    // {@link PlayerReceiverListener} Implements Method
    @Override
    public void onPlayFromFolder(Intent data) {
    }

    // {@link PlayerReceiverListener} Implements Method
    @Override
    public void onNotifyOperate(String opFlag) {
    }

    // {@link PlayerReceiverListener} Implements Method
    @Override
    public boolean isCacheOnAccOff() {
        return false;
    }

    // {@link PlayerReceiverListener} Implements Method
    @Override
    public void onNotifySearchMediaList(String title, String artist) {
    }

    // {@link PlayerReceiverListener} Implements Method
    @Override
    public void onNotifyPlaySearchedMusic(ProMusic program) {
    }

    // {@link PlayerReceiverListener} Implements Method
    @Override
    public void onNotifyPlayMedia(String path) {
    }

    // {@link PlayerReceiverListener} Implements Method
    @Override
    public void onNotifyScanAudios(int flag, List<ProMusic> listPrgrams, Set<String> allSdMountedPaths) {
    }

    // {@link PlayerReceiverListener} Implements Method
    @Override
    public void onNotifyScanVideos(int flag, List<ProVideo> listPrgrams, Set<String> allSdMountedPaths) {
    }

    // {@link IPlayerListener} Implements Method
    @Override
    public void onNotifyPlayState(int playState) {
        // 通知仪表盘信息
        notifyDashboard(playState);
        // Notify Play State
        notifyPlayState(playState);
    }

    // {@link IPlayerListener} Implements Method
    @Override
    public void onProgressChange(String mediaUrl, int progress, int duration, boolean isPerSecond) {
        // 通知仪表盘信息
        if (isPerSecond) {
            notifyDashboard(IPlayerState.PLAY);
        }
        for (PlayerActionsListener l : mSetPlayerListeners) {
            if (l != null) {
                l.onProgressChange(mediaUrl, progress, duration, isPerSecond);
            }
        }
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void setPlayMode(int mode) {
    }

    @Override
    public void onPlayModeChange() {
        for (PlayerActionsListener l : mSetPlayerListeners) {
            if (l != null) {
                l.onPlayModeChange();
            }
        }
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public Serializable getCurrMedia() {
        return null;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public PlayEnableFlag getPlayEnableFlag() {
        return null;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void setPlayList(List<?> listMedias) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void setPlayPosition(int position) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public boolean isPlayEnable() {
        return false;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void removePlayRunnable() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void play() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void playPrev() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void playPrevBySecurity() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void playNext() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void playNextBySecurity() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void pause() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void pauseByUser() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void resume() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void resumeByUser() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void release() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public String getLastPath() {
        return null;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public int getLastProgress() {
        return 0;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public String getPath() {
        return null;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public int getPosition() {
        return 0;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public int getTotalCount() {
        return 0;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public int getProgress() {
        return 0;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public int getDuration() {
        return 0;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public boolean isPlaying() {
        return false;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public boolean isPauseByUser() {
        return false;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void seekTo(int time) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void adjustVol(int flag) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public String getLastTargetMediaUrl() {
        return null;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void saveTargetMediaUrl(String mediaUrl) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public String[] getPlayedMediaInfos() {
        return null;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void savePlayMediaInfos(String mediaUrl, int progress) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void clearPlayedMediaInfos() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void setPlayerActionsListener(PlayerActionsListener l) {
        if (l != null) {
            mSetPlayerListeners.add(l);
        }
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void removePlayerActionsListener(PlayerActionsListener l) {
        if (l != null) {
            mSetPlayerListeners.remove(l);
        }
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void onAudioFocusDuck() {
        for (PlayerActionsListener l : mSetPlayerListeners) {
            if (l != null) {
                l.onAudioFocusDuck();
            }
        }
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void onAudioFocusTransient() {
        for (PlayerActionsListener l : mSetPlayerListeners) {
            if (l != null) {
                l.onAudioFocusTransient();
            }
        }
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void onAudioFocusLoss() {
        for (PlayerActionsListener l : mSetPlayerListeners) {
            if (l != null) {
                l.onAudioFocusLoss();
            }
        }
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void onAudioFocusGain() {
        for (PlayerActionsListener l : mSetPlayerListeners) {
            if (l != null) {
                l.onAudioFocusGain();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSetPlayerListeners.clear();
        mHandler.removeCallbacksAndMessages(null);
    }
}
