package com.yj.audio.version.base.activity.music;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yj.audio.engine.PlayerAppManager;
import com.yj.audio.receiver.PlayerReceiver.PlayerReceiverListener;
import com.yj.audio.service.TrPlayService;
import com.yj.audio.service.TrPlayService.LocalBinder;
import com.yj.audio.version.base.activity.BaseFragActivity;

import java.util.List;

import js.lib.android.media.bean.MediaBase;
import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.player.PlayDelegate;
import js.lib.android.media.player.PlayMode;
import js.lib.android.media.player.PlayState;
import js.lib.android.media.player.audio.utils.AudioPreferUtils;
import js.lib.android.utils.CommonUtil;
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
public abstract class BaseCommonActionsActivity extends BaseFragActivity
        implements PlayerReceiverListener, PlayDelegate {
    // TAG
    private final String TAG = "audio_base_common";

    /**
     * Music Play Service
     */
    protected TrPlayService mPlayService;
    protected ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mPlayService = ((LocalBinder) binder).getService();
            if (mPlayService != null) {
                onAudioServiceConnChanged(mPlayService);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // mPlayService = null;
            Log.i(TAG, "mServiceConnection-> onServiceDisconnected");
            onAudioServiceConnChanged(null);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    /**
     * 当MusicPlayService绑定成功
     */
    protected void onAudioServiceConnChanged(Service service) {
    }

    /**
     * Bind/Unbind Create/Destroy service
     */
    protected void bindAndCreatePlayService(int... flags) {
        try {
            Intent serviceIntent = new Intent(this, TrPlayService.class);
            for (int flag : flags) {
                switch (flag) {
                    case 1:
                        startService(serviceIntent);
                        break;
                    case 2:
                        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);
                        break;
                    case 3:
                        if (mPlayService != null) {
                            unbindService(mServiceConnection);
                        }
                    case 4:
                        if (mPlayService != null) {
                            stopService(serviceIntent);
                        }
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "bindAndCreatePlayService :: Exception - " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayFromFolder(int playPos, List<String> listPlayPaths) {
    }

    @Override
    public void setPlayListener(PlayDelegate delegate) {
        if (mPlayService != null) {
            mPlayService.setPlayListener(delegate);
        }
    }

    @Override
    public void removePlayListener(PlayDelegate delegate) {
        if (mPlayService != null) {
            mPlayService.removePlayListener(delegate);
        }
    }

    @Override
    public void setListSrcMedias(List<? extends MediaBase> listSrcMedias) {
        if (mPlayService != null) {
            mPlayService.setListSrcMedias(listSrcMedias);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProAudio> getListSrcMedias() {
        Logs.i(TAG, "^^ getListMedias() ^^");
        if (mPlayService != null) {
            List<?> list = mPlayService.getListSrcMedias();
            if (list != null) {
                return (List<ProAudio>) list;
            }
        }
        return null;
    }

    @Override
    public void setPlayList(List<? extends MediaBase> mediasToPlay) {
        Logs.i(TAG, "^^ setPlayList() ^^");
        if (mPlayService != null) {
            mPlayService.setPlayList(mediasToPlay);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProAudio> getListMedias() {
        Logs.i(TAG, "^^ getListMedias() ^^");
        if (mPlayService != null) {
            List<?> list = mPlayService.getListMedias();
            if (list != null) {
                return (List<ProAudio>) list;
            }
        }
        return null;
    }

    @Override
    public void setPlayPosition(int playPos) {
        Logs.i(TAG, "^^ setPlayPosition(" + playPos + ") ^^");
        if (mPlayService != null) {
            mPlayService.setPlayPosition(playPos);
        }
    }

    @Override
    public int getTotalCount() {
        if (mPlayService != null) {
            return mPlayService.getTotalCount();
        }
        return 0;
    }

    @Override
    public int getCurrIdx() {
        if (mPlayService != null) {
            return mPlayService.getCurrIdx();
        }
        return 0;
    }

    @Override
    public MediaBase getCurrMedia() {
        if (mPlayService != null) {
            return mPlayService.getCurrMedia();
        }
        return null;
    }

    @Override
    public String getCurrMediaPath() {
        if (mPlayService != null) {
            return mPlayService.getCurrMediaPath();
        }
        return "";
    }

    @Override
    public long getProgress() {
        if (mPlayService != null) {
            return mPlayService.getProgress();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (mPlayService != null) {
            return mPlayService.getDuration();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return mPlayService != null && mPlayService.isPlaying();
    }

    @Override
    public void play() {
        Logs.i(TAG, "^^ play() ^^");
        if (mPlayService != null) {
            mPlayService.play();
        }
    }

    @Override
    public void play(String mediaPath) {
        Logs.i(TAG, "^^ play(" + mediaPath + ") ^^");
        if (mPlayService != null) {
            mPlayService.play(mediaPath);
        }
    }

    @Override
    public void play(int pos) {
        Logs.i(TAG, "^^ play(" + pos + ") ^^");
        if (mPlayService != null) {
            mPlayService.play(pos);
        }
    }

    @Override
    public void playPrev() {
        Logs.i(TAG, "^^ playPrev() ^^");
        if (mPlayService != null) {
            mPlayService.playPrev();
        }
    }

    @Override
    public void playNext() {
        Logs.i(TAG, "^^ playNext() ^^");
        if (mPlayService != null) {
            mPlayService.playNext();
        }
    }

    public void playRandom() {
        Logs.i(TAG, "^^ playRandom() ^^");
        if (mPlayService != null) {
            mPlayService.playRandom();
        }
    }

    @Override
    public void pause() {
        Logs.i(TAG, "^^ pause() ^^");
        if (mPlayService != null) {
            mPlayService.pause();
        }
    }

    @Override
    public void resume() {
        Logs.i(TAG, "^^ resume() ^^");
        if (mPlayService != null) {
            mPlayService.resume();
        }
    }

    @Override
    public void release() {
        Logs.i(TAG, "^^ release() ^^");
        if (mPlayService != null) {
            mPlayService.release();
        }
    }

    @Override
    public void seekTo(int msec) {
        Logs.i(TAG, "^^ seekTo(" + msec + ") ^^");
        if (mPlayService != null) {
            mPlayService.seekTo(msec);
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
    }

    @Override
    public void switchPlayMode(int supportFlag) {
        Logs.i(TAG, "^^ switchPlayMode(" + supportFlag + ") ^^");
        if (mPlayService != null) {
            mPlayService.switchPlayMode(supportFlag);
        }
    }

    @Override
    public void setPlayMode(PlayMode mode) {
        Logs.i(TAG, "^^ setPlayMode(" + mode + ") ^^");
        if (mPlayService != null) {
            mPlayService.setPlayMode(mode);
        }
    }

    @Override
    public PlayMode getPlayMode() {
        if (mPlayService != null) {
            return mPlayService.getPlayMode();
        }
        return AudioPreferUtils.getPlayMode(false, PlayMode.LOOP);
    }

    @Override
    public void onPlayModeChange() {
    }

    @Override
    public void saveTargetMediaPath(String mediaPath) {
    }

    @Override
    public String getLastTargetMediaPath() {
        return null;
    }

    @Override
    public String getLastMediaPath() {
        if (mPlayService != null) {
            return mPlayService.getLastMediaPath();
        }
        return "";
    }

    @Override
    public long getLastProgress() {
        if (mPlayService != null) {
            return mPlayService.getLastProgress();
        }
        return 0;
    }

    @Override
    public void savePlayMediaInfo(String mediaPath, int progress) {
    }

    @Override
    public String[] getPlayedMediaInfo() {
        return new String[0];
    }

    @Override
    public void clearPlayedMediaInfo() {
    }

    @Override
    public void onPlayStateChanged(PlayState playState) {
    }

    @Override
    public void onProgressChanged(String mediaPath, int progress, int duration) {
    }

    /**
     * Register Audio Focus
     * <p>if==1 : Register audio focus</p>
     * <p>if==2 : Abandon audio focus</p>
     */
    public void registerAudioFocus(int flag) {
        if (mPlayService != null) {
            mPlayService.registerAudioFocus(flag);
        }
    }

    /**
     * Audio focus register state check.
     */
    public boolean isAudioFocusGained() {
        boolean isAudioFocusRegistered = mPlayService != null && mPlayService.isAudioFocusGained();
        Log.i(TAG, "isAudioFocusRegistered: " + isAudioFocusRegistered);
        return isAudioFocusRegistered;
    }

    @Override
    public void onAudioFocusDuck() {
    }

    @Override
    public void onAudioFocusTransient() {
    }

    @Override
    public void onAudioFocusGain() {
    }

    @Override
    public void onAudioFocusLoss() {
    }

    @Override
    public void onAudioFocus(int flag) {
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (!isPlaying() && CommonUtil.isRunningBackground(this, getPackageName())) {
            PlayerAppManager.exitCurrPlayer(true);
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
        } else if (!isPlaying() && isHomeClicked()) {
            PlayerAppManager.exitCurrPlayer(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}