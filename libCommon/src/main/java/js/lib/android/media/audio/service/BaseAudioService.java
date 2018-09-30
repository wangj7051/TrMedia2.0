package js.lib.android.media.audio.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import js.lib.android.media.audio.IAudioPlayer;

/**
 * Base audio service
 * <p>IAudioPlayer implement class</p>
 *
 * @author Jun.Wang
 */
public class BaseAudioService extends Service {
    // TAG
    private final String TAG = "BaseAudioService";

    /**
     * Music Player Object
     */
    private IAudioPlayer mAudioPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
