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
        switch (event.getAction()) {
            case KeyEvent.ACTION_UP:
                int keyCode = event.getKeyCode();
                Log.i(TAG, "dispatchKeyEvent(" + keyCode + ")");
                onGetKeyCode(keyCode);
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    protected void onGetKeyCode(int keyCode) {
        Log.i(TAG, "onGetKeyCode(" + keyCode + ")");
        switch (keyCode) {
            case Keys.KeyVals.KEYCODE_VOLUME_UP:
                break;
            case Keys.KeyVals.KEYCODE_VOLUME_DOWN:
                break;
            case Keys.KeyVals.KEYCODE_VOLUME_MUTE:
                break;

            case Keys.KeyVals.KEYCODE_RADIO:
                execSwitchBand();
                break;

            case Keys.KeyVals.KEYCODE_PREV:
                scanAndPlayPrev();
                break;
            case Keys.KeyVals.KEYCODE_NEXT:
                scanAndPlayNext();
                break;

            case Keys.KeyVals.KEYCODE_DPAD_LEFT:
                stepPrev();
                break;
            case Keys.KeyVals.KEYCODE_DPAD_RIGHT:
                stepNext();
                break;

            case Keys.KeyVals.KEYCODE_ENTER:
                break;
            case Keys.KeyVals.KEYCODE_HOME:
                break;
            case Keys.KeyVals.KEYCODE_BACK:
                break;
        }
    }
}