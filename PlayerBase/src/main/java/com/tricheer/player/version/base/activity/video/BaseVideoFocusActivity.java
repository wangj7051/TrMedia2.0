package com.tricheer.player.version.base.activity.video;

import android.media.AudioManager;
import android.os.Bundle;

import js.lib.android.utils.AudioManagerUtil;
import js.lib.android.utils.Logs;

public abstract class BaseVideoFocusActivity extends BaseVideoKeyEventActivity {
    //TAG
    private static final String TAG = "BaseVideoFocusActivity";

    /**
     * Listener Audio Focus
     */
    protected AudioManager.OnAudioFocusChangeListener mAfChangeListener = new AudioManager.OnAudioFocusChangeListener() {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume();
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
            int result = AudioManagerUtil.requestMusicGain(mContext, mAfChangeListener);
            Logs.i(TAG, "registerAudioFocus(" + flag + ") *request AUDIOFOCUS_GAIN* >> [result:" + result);
        } else if (flag == 2) {
            int result = AudioManagerUtil.abandon(mContext, mAfChangeListener);
            Logs.i(TAG, "registerAudioFocus(" + flag + ") *abandon AudioFocus* >> [result:" + result);
        }
    }

    @Override
    public void onAudioFocusGain() {
        if (isForeground()) {
            resumeByUser();
        }
    }

    @Override
    public void onAudioFocusTransient() {
        pause();
    }

    @Override
    public void onAudioFocusDuck() {
    }

    @Override
    public void onAudioFocusLoss() {
        registerAudioFocus(2);
        pauseByUser();
    }

    @Override
    public void play() {
        super.play();
        registerAudioFocus(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
