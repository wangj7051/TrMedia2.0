package js.lib.android.media.engine.mediabtn;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;

public class MediaBtnController {
    private Context mContext;
    private ComponentName mComponentName;
    private AudioManager mAudioManager;

    public MediaBtnController(Context context) {
        mComponentName = new ComponentName(context.getPackageName(), MediaBtnReceiver.class.getName());
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void register() {
        unregister();
        if (mAudioManager != null) {
            mAudioManager.registerMediaButtonEventReceiver(mComponentName);
        }
    }

    public void unregister() {
        if (mAudioManager != null) {
            mAudioManager.unregisterMediaButtonEventReceiver(mComponentName);
        }
    }
}
