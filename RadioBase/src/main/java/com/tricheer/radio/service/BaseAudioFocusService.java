package com.tricheer.radio.service;

import android.app.Service;
import android.media.AudioManager;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import js.lib.android.utils.AudioManagerUtil;
import js.lib.android.utils.Logs;

public abstract class BaseAudioFocusService extends Service {
    //TAG
    private final String TAG = "BaseAudioFocusService";

    /**
     * Player State Listener out of service
     */
    private Set<AudioFocusListener> mSetPlayerListeners = new HashSet<AudioFocusListener>();

    public interface AudioFocusListener {
        /**
         * 暂时失去AudioFocus，但是可以继续播放，不过要在降低音量。
         */
        void onAudioFocusDuck();

        /**
         * 暂时失去Audio Focus，并会很快再次获得。必须停止Audio的播放， 但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源；
         */
        void onAudioFocusTransient();

        /**
         * 失去了Audio Focus，并将会持续很长的时间。 这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。
         * 而因为停止播放Audio的时间会很长，如果程序因为这个原因而失去AudioFocus，
         * 最好不要让它再次自动获得AudioFocus而继续播放，不然突然冒出来的声音会让用户感觉莫名其妙，感受很不好。
         * 这里直接放弃AudioFocus，当然也不用再侦听远程播放控制【如下面代码的处理】。 要再次播放，除非用户再在界面上点击开始播放，才重新初始化Media，进行播放
         */
        void onAudioFocusLoss();

        /**
         * 获得了Audio Focus；
         */
        void onAudioFocusGain();
    }

    /**
     * Listener Audio Focus
     */
    protected AudioManager.OnAudioFocusChangeListener mAfChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
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

    /**
     * Register Audio Focus
     * <p>
     * if==1 : Register audio focus
     * <p>
     * if==2 : Abandon audio focus
     */
    public int registerAudioFocus(int flag) {
        int result = -1;
        if (flag == 1) {
            result = AudioManagerUtil.requestMusicGain(this, mAfChangeListener);
            Logs.i(TAG, "registerAudioFocus(" + flag + ") *request AUDIOFOCUS_GAIN* >> [result:" + result);
        } else if (flag == 2) {
            result = AudioManagerUtil.abandon(this, mAfChangeListener);
            Logs.i(TAG, "registerAudioFocus(" + flag + ") *abandon AudioFocus* >> [result:" + result);
        }
        return result;
    }

    public void setAudioFocusListener(AudioFocusListener l) {
        if (l != null) {
            mSetPlayerListeners.add(l);
        }
    }

    public void removeAudioFocusListener(AudioFocusListener l) {
        if (l != null) {
            mSetPlayerListeners.remove(l);
        }
    }

    public void onAudioFocusDuck() {
        for (AudioFocusListener l : mSetPlayerListeners) {
            if (l != null) {
                l.onAudioFocusDuck();
            }
        }
    }

    public void onAudioFocusTransient() {
        for (AudioFocusListener l : mSetPlayerListeners) {
            if (l != null) {
                l.onAudioFocusTransient();
            }
        }
    }

    public void onAudioFocusLoss() {
        for (AudioFocusListener l : mSetPlayerListeners) {
            if (l != null) {
                l.onAudioFocusLoss();
            }
        }
    }

    public void onAudioFocusGain() {
        for (AudioFocusListener l : mSetPlayerListeners) {
            if (l != null) {
                l.onAudioFocusGain();
            }
        }
    }
}
