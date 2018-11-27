package js.lib.android.media.player.audio.service;

import android.app.Service;
import android.media.AudioManager;
import android.util.Log;

import java.util.LinkedHashSet;
import java.util.Set;

import js.lib.android.media.engine.IAudioFocusListener;
import js.lib.android.utils.AudioManagerUtil;
import js.lib.android.utils.Logs;

/**
 * Audio focus service
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioFocusService extends Service {
    //TAG
    private final String TAG = "BaseAudioFocusService";

    /**
     * Audio focus flag
     * {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT}
     * or {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK}
     * or {@link AudioManager#AUDIOFOCUS_LOSS}
     * or {@link AudioManager#AUDIOFOCUS_GAIN}
     */
    private int mAudioFocusFlag = 0;

    /**
     * Player State Listener out of service
     */
    private Set<IAudioFocusListener> mSetAudioFocusListeners = new LinkedHashSet<>();

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
                respAudioFocusTransient();

                // 暂时失去AudioFocus，但是可以继续播放，不过要在降低音量。
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                Logs.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK----");
                respAudioFocusDuck();

                // 失去了Audio Focus，并将会持续很长的时间。
                // 这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。
                // 而因为停止播放Audio的时间会很长，如果程序因为这个原因而失去AudioFocus，
                // 最好不要让它再次自动获得AudioFocus而继续播放，不然突然冒出来的声音会让用户感觉莫名其妙，感受很不好。
                // 这里直接放弃AudioFocus，当然也不用再侦听远程播放控制【如下面代码的处理】。
                // 要再次播放，除非用户再在界面上点击开始播放，才重新初始化Media，进行播放
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_LOSS----");
                respAudioFocusLoss();

                // 获得了Audio Focus；
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_GAIN----");
                respAudioFocusGain();
            }
        }
    };

    /**
     * Register Audio Focus
     * <p>
     * if==1 : Register audio focus
     * <p>
     * if==2 : Abandon audio focus
     */
    public void registerAudioFocus(int flag) {
        mAudioFocusFlag = 0;
        switch (flag) {
            case 1:
                int result = AudioManagerUtil.requestMusicGain(this, mAfChangeListener);
                Logs.i(TAG, "registerAudioFocus(" + flag + ") *request AUDIOFOCUS_GAIN* >> [result:" + result);
                if (result == 1) {
                    mAudioFocusFlag = AudioManager.AUDIOFOCUS_GAIN;
                    respAudioFocus(1);
                }
                break;
            case 2:
                result = AudioManagerUtil.abandon(this, mAfChangeListener);
                Logs.i(TAG, "registerAudioFocus(" + flag + ") *abandon AudioFocus* >> [result:" + result);
                if (result == 1) {
                    mAudioFocusFlag = AudioManager.AUDIOFOCUS_LOSS;
                    respAudioFocus(2);
                }
                break;
        }
    }

    public void setAudioFocusListener(IAudioFocusListener l) {
        if (l != null) {
            mSetAudioFocusListeners.add(l);
        }
    }

    public void removeAudioFocusListener(IAudioFocusListener l) {
        if (l != null) {
            mSetAudioFocusListeners.remove(l);
        }
    }

    public void respAudioFocusDuck() {
        for (IAudioFocusListener l : mSetAudioFocusListeners) {
            if (l != null) {
                l.onAudioFocusDuck();
            }
        }
    }

    public void respAudioFocusTransient() {
        for (IAudioFocusListener l : mSetAudioFocusListeners) {
            if (l != null) {
                l.onAudioFocusTransient();
            }
        }
    }

    public void respAudioFocusLoss() {
        registerAudioFocus(2);
        for (IAudioFocusListener l : mSetAudioFocusListeners) {
            if (l != null) {
                l.onAudioFocusLoss();
            }
        }
    }

    public void respAudioFocusGain() {
        for (IAudioFocusListener l : mSetAudioFocusListeners) {
            if (l != null) {
                l.onAudioFocusGain();
            }
        }
    }

    public void respAudioFocus(int flag) {
        for (IAudioFocusListener l : mSetAudioFocusListeners) {
            if (l != null) {
                l.onAudioFocus(flag);
            }
        }
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
    public void onDestroy() {
        registerAudioFocus(2);
        mSetAudioFocusListeners.clear();
        super.onDestroy();
    }
}
