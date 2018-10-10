package com.tricheer.radio.activity;

import android.util.Log;
import android.view.KeyEvent;

import com.tricheer.radio.engine.Keys;

/**
 * Process {@link KeyEvent}
 *
 * @author Jun.Wang
 */
public abstract class BaseKeyEventActivity extends BaseFmLogicActivity {
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

    protected void onGetKeyCode(int keyCode) {
        Log.i(TAG, "onGetKeyCode(" + keyCode + ")");
    }
}
