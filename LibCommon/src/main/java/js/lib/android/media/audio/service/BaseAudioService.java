package js.lib.android.media.audio.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import js.lib.android.media.player.PlayState;
import js.lib.android.media.utils.AudioPreferUtils;
import js.lib.android.media.player.PlayListener;
import js.lib.android.utils.AudioManagerUtil;
import js.lib.android.utils.Logs;

/**
 * Base audio service
 * <p>IAudioPlayer implement class</p>
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioService extends Service implements PlayListener {
    // TAG
    private final String TAG = "BaseAudioService";

    /**
     * Thread Handler
     */
    private static Handler mHandler = new Handler();

    /**
     * Player State Listener out of service
     */
    private Set<PlayListener> mSetPlayListeners = new HashSet<>();

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
        AudioPreferUtils.init(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Register Audio Focus
     * <p>
     * if==1 : Register audio focus
     * <p>
     * if==2 : Abandon audio focus
     */
    public void registerAudioFocus(int flag) {
        if (flag == 1) {
            int result = AudioManagerUtil.requestMusicGain(this, mAfChangeListener);
            Logs.i(TAG, "registerAudioFocus(" + flag + ") *request AUDIOFOCUS_GAIN* >> [result:" + result);
        } else if (flag == 2) {
            int result = AudioManagerUtil.abandon(this, mAfChangeListener);
            Logs.i(TAG, "registerAudioFocus(" + flag + ") *abandon AudioFocus* >> [result:" + result);
        }
    }

    @Override
    public void setPlayListener(PlayListener l) {
        if (l != null) {
            mSetPlayListeners.add(l);
        }
    }

    @Override
    public void removePlayListener(PlayListener l) {
        if (l != null) {
            mSetPlayListeners.remove(l);
        }
    }

    @Override
    public void onPlayStateChanged(PlayState playState) {
        notifyPlayState(playState);
    }

    protected void notifyPlayState(PlayState playState) {
        for (PlayListener l : mSetPlayListeners) {
            if (l != null) {
                l.onPlayStateChanged(playState);
            }
        }
    }

    @Override
    public void onProgressChanged(String mediaPath, int progress, int duration) {
        for (PlayListener l : mSetPlayListeners) {
            if (l != null) {
                l.onProgressChanged(mediaPath, progress, duration);
            }
        }
    }

    @Override
    public void onPlayModeChange() {
        for (PlayListener l : mSetPlayListeners) {
            if (l != null) {
                l.onPlayModeChange();
            }
        }
    }

    @Override
    public void onAudioFocusDuck() {
        for (PlayListener l : mSetPlayListeners) {
            if (l != null) {
                l.onAudioFocusDuck();
            }
        }
    }

    @Override
    public void onAudioFocusTransient() {
        for (PlayListener l : mSetPlayListeners) {
            if (l != null) {
                l.onAudioFocusTransient();
            }
        }
    }

    @Override
    public void onAudioFocusGain() {
        for (PlayListener l : mSetPlayListeners) {
            if (l != null) {
                l.onAudioFocusGain();
            }
        }
    }

    @Override
    public void onAudioFocusLoss() {
        registerAudioFocus(2);
        for (PlayListener l : mSetPlayListeners) {
            if (l != null) {
                l.onAudioFocusLoss();
            }
        }
    }

    protected void postRunnable(Runnable r) {
        mHandler.post(r);
    }

    protected void postDelayRunnable(Runnable r, int delayTime) {
        mHandler.postDelayed(r, delayTime);
    }

    protected void clearAllRunables() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        clearAllRunables();
        registerAudioFocus(2);
        mSetPlayListeners.clear();
        super.onDestroy();
    }
}
