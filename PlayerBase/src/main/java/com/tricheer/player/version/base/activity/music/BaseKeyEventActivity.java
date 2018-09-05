package com.tricheer.player.version.base.activity.music;

import android.util.Log;
import android.view.KeyEvent;

/**
 * Process {@link KeyEvent}
 *
 * @author Jun.Wang
 */
public abstract class BaseKeyEventActivity extends BaseMusicExtendActionsActivity {
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

    /**
     * @param keyCode {@link com.tricheer.player.engine.Keys.KeyVals}
     */
    public abstract void onGetKeyCode(int keyCode);
}
