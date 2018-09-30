package com.tricheer.player.version.base.activity.music;

import android.util.Log;
import android.view.KeyEvent;

import com.tricheer.player.engine.Keys;

/**
 * Process {@link KeyEvent}
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioKeyEventActivity extends BaseAudioExtendActionsActivity {
    //TAG
    private final String TAG = "BaseKeyEventActivity";

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (event.getAction()) {
            case KeyEvent.ACTION_UP:
                Log.i(TAG, "dispatchKeyEvent(" + keyCode + ")");
                onGetKeyCode(keyCode);
                break;
        }

        boolean isMenu = (keyCode == Keys.KeyVals.KEYCODE_DPAD_LEFT) || (keyCode == Keys.KeyVals.KEYCODE_DPAD_RIGHT);
        if (isMenu) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    /**
     * @param keyCode {@link com.tricheer.player.engine.Keys.KeyVals}
     */
    public abstract void onGetKeyCode(int keyCode);
}
