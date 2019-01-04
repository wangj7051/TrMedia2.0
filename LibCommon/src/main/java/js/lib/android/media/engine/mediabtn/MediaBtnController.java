package js.lib.android.media.engine.mediabtn;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class MediaBtnController {
    //TAG
    private String TAG = "MediaBtnController";

    /**
     * {@link Context}
     */
//    private Context mContext;

    /**
     * Class ComponentName
     */
    private ComponentName mComponentName;

    /**
     * {@link AudioManager}
     */
    private AudioManager mAudioManager;


    public MediaBtnController(Context context) {
        //
//        mContext = context;
        mComponentName = new ComponentName(context.getPackageName(), MediaBtnReceiver.class.getName());
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void register() {
        unregister();
        Log.i(TAG, "register()");
        if (mAudioManager != null) {
            mAudioManager.registerMediaButtonEventReceiver(mComponentName);
        }
    }

    public void unregister() {
        Log.i(TAG, "unregister()");
        if (mAudioManager != null) {
            mAudioManager.unregisterMediaButtonEventReceiver(mComponentName);
        }
    }
}
