package com.tri.lib.engine;

import java.util.HashMap;
import java.util.Map;

/**
 * Customized keys
 *
 * @author Jun.Wang
 */
public enum KeyEnum {
    KEYCODE_VOLUME_UP(24), KEYCODE_VOLUME_DOWN(25), KEYCODE_VOLUME_MUTE(164),
    KEYCODE_RADIO(284),
    KEYCODE_PREV(88), KEYCODE_NEXT(87),
    KEYCODE_DPAD_LEFT(21), KEYCODE_DPAD_RIGHT(22),
    KEYCODE_ENTER(66), KEYCODE_HOME(3), KEYCODE_BACK(4);

    private int mValue;

    KeyEnum(int value) {
        mValue = value;
    }

    public int getKeyVal() {
        return mValue;
    }

    private static Map<Integer, KeyEnum> mMapKeys;

    public static KeyEnum getKey(int value) {
        if (mMapKeys == null) {
            mMapKeys = new HashMap<>();
            mMapKeys.put(KEYCODE_VOLUME_UP.getKeyVal(), KEYCODE_VOLUME_UP);
            mMapKeys.put(KEYCODE_VOLUME_DOWN.getKeyVal(), KEYCODE_VOLUME_DOWN);
            mMapKeys.put(KEYCODE_VOLUME_MUTE.getKeyVal(), KEYCODE_VOLUME_MUTE);

            mMapKeys.put(KEYCODE_RADIO.getKeyVal(), KEYCODE_RADIO);
            mMapKeys.put(KEYCODE_PREV.getKeyVal(), KEYCODE_PREV);
            mMapKeys.put(KEYCODE_NEXT.getKeyVal(), KEYCODE_NEXT);
            mMapKeys.put(KEYCODE_DPAD_LEFT.getKeyVal(), KEYCODE_DPAD_LEFT);
            mMapKeys.put(KEYCODE_DPAD_RIGHT.getKeyVal(), KEYCODE_DPAD_RIGHT);

            mMapKeys.put(KEYCODE_ENTER.getKeyVal(), KEYCODE_ENTER);
            mMapKeys.put(KEYCODE_HOME.getKeyVal(), KEYCODE_HOME);
            mMapKeys.put(KEYCODE_BACK.getKeyVal(), KEYCODE_BACK);
        }
        return mMapKeys.get(value);
    }
}