package js.lib.android.media.engine.mediabtn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Media button broadcast receiver.
 * <p>1. Register {@link MediaBtnReceiver} with action:"android.intent.action.MEDIA_BUTTON" in your 'AndroidManifest.xml'</p>
 * <p>2. Register in where you want use media button {@link android.media.AudioManager#registerMediaButtonEventReceiver}</p>
 *
 * @author Jun.Wang
 */
public class MediaBtnReceiver extends BroadcastReceiver {
    //TAG
    private static final String TAG = "MediaBtnReceiver";

    /**
     * {@link MediaBtnListener}
     */
    private static MediaBtnListener mMediaBtnListener;

    public interface MediaBtnListener {
        void onGotMediaKeyCode(KeyEvent event);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action: " + action);

        if (Intent.ACTION_MEDIA_BUTTON.equals(action)) {
            KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (keyEvent != null) {
                Log.i(TAG, "keycode: " + keyEvent.getKeyCode());
                if (mMediaBtnListener != null) {
                    mMediaBtnListener.onGotMediaKeyCode(keyEvent);
                }
            }
        }
    }

    public static void setListener(MediaBtnListener l) {
        mMediaBtnListener = l;
    }
}
