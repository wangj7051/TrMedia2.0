package com.tricheer.radio.activity;

import android.util.Log;
import android.view.KeyEvent;

import com.tri.lib.engine.KeyEnum;

/**
 * Process {@link KeyEvent}
 *
 * @author Jun.Wang
 */
public abstract class BaseKeyEventActivity extends BaseRadioImplActivity {
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

        boolean isMenu = (keyCode == KeyEnum.KEYCODE_DPAD_LEFT.getKeyVal()) || (keyCode == KeyEnum.KEYCODE_DPAD_RIGHT.getKeyVal());
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
