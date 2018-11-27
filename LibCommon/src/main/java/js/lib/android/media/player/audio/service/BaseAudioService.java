package js.lib.android.media.player.audio.service;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import js.lib.android.media.player.PlayDelegate;
import js.lib.android.media.player.PlayState;
import js.lib.android.media.player.audio.utils.AudioPreferUtils;

/**
 * Base audio service
 * <p>IAudioPlayer implement class</p>
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioService extends BaseAudioFocusService implements PlayDelegate {
    // TAG
    private final String TAG = "BaseAudioService";

    /**
     * Thread Handler
     */
    private static Handler mHandler = new Handler();

    /**
     * Player State Listener out of service
     */
    private Set<PlayDelegate> mSetPlayDelegates = new HashSet<>();

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
    public void setPlayListener(PlayDelegate l) {
        if (l != null) {
            mSetPlayDelegates.add(l);
            setAudioFocusListener(l);
        }
    }

    @Override
    public void removePlayListener(PlayDelegate l) {
        if (l != null) {
            mSetPlayDelegates.remove(l);
            removeAudioFocusListener(l);
        }
    }

    @Override
    public void onPlayStateChanged(PlayState playState) {
        notifyPlayState(playState);
    }

    protected void notifyPlayState(PlayState playState) {
        for (PlayDelegate l : mSetPlayDelegates) {
            if (l != null) {
                l.onPlayStateChanged(playState);
            }
        }
    }

    @Override
    public void onProgressChanged(String mediaPath, int progress, int duration) {
        for (PlayDelegate l : mSetPlayDelegates) {
            if (l != null) {
                l.onProgressChanged(mediaPath, progress, duration);
            }
        }
    }

    @Override
    public void onPlayModeChange() {
        for (PlayDelegate l : mSetPlayDelegates) {
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

    @Override
    public void onAudioFocus(int flag) {
        Log.i(TAG, "onAudioFocus(" + flag + ")");
    }

    protected void postRunnable(Runnable r) {
        mHandler.post(r);
    }

    protected void postDelayRunnable(Runnable r, int delayTime) {
        mHandler.postDelayed(r, delayTime);
    }

    protected void clearAllRunnable() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        removeAudioFocusListener(this);
        mSetPlayDelegates.clear();
        clearAllRunnable();
        super.onDestroy();
    }
}
