package com.yj.video.version.base.activity.video;

import android.util.Log;
import android.view.KeyEvent;

import com.tri.lib.engine.KeyEnum;

/**
 * Process {@link KeyEvent}
 *
 * @author Jun.Wang
 */
public abstract class BaseVideoKeyEventActivity extends BaseVideoExtendActionsActivity {
    //TAG
    private final String TAG = "BaseKeyEventActivity";

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        KeyEnum key = KeyEnum.getKey(event.getKeyCode());
        switch (event.getAction()) {
            case KeyEvent.ACTION_UP:
                Log.i(TAG, "dispatchKeyEvent(" + key + ")");
                onGetKeyCode(key);
                break;
        }

        boolean isMenu = (key == KeyEnum.KEYCODE_DPAD_LEFT)
                || (key == KeyEnum.KEYCODE_DPAD_RIGHT)
                || (key == KeyEnum.KEYCODE_ENTER);
        if (isMenu) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    /**
     * @param key {@link KeyEnum}
     */
    public abstract void onGetKeyCode(KeyEnum key);
}
