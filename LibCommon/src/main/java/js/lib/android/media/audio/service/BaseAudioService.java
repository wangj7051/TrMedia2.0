package js.lib.android.media.audio.service;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import js.lib.android.media.player.PlayListener;
import js.lib.android.media.player.PlayState;
import js.lib.android.media.utils.AudioPreferUtils;

/**
 * Base audio service
 * <p>IAudioPlayer implement class</p>
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioService extends BaseAudioFocusService implements PlayListener {
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

    @Override
    public void onCreate() {
        super.onCreate();
        setAudioFocusListener(this);
        AudioPreferUtils.init(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void setPlayListener(PlayListener l) {
        if (l != null) {
            setAudioFocusListener(l);
            mSetPlayListeners.add(l);
        }
    }

    @Override
    public void removePlayListener(PlayListener l) {
        if (l != null) {
            removeAudioFocusListener(l);
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
        Log.i(TAG, "onAudioFocusDuck()");
    }

    @Override
    public void onAudioFocusTransient() {
        Log.i(TAG, "onAudioFocusTransient()");
    }

    @Override
    public void onAudioFocusGain() {
        Log.i(TAG, "onAudioFocusGain()");
    }

    @Override
    public void onAudioFocusLoss() {
        Log.i(TAG, "onAudioFocusLoss()");
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
        removeAudioFocusListener(this);
        mSetPlayListeners.clear();
        clearAllRunables();
        super.onDestroy();
    }
}
