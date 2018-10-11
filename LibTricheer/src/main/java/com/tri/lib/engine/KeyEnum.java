package com.tri.lib.engine;

import android.util.SparseArray;

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

    private static SparseArray<KeyEnum> mSaKeys;

    public static KeyEnum getKey(int value) {
        if (mSaKeys == null) {
            mSaKeys = new SparseArray<>();
            mSaKeys.put(KEYCODE_VOLUME_UP.getKeyVal(), KEYCODE_VOLUME_UP);
            mSaKeys.put(KEYCODE_VOLUME_DOWN.getKeyVal(), KEYCODE_VOLUME_DOWN);
            mSaKeys.put(KEYCODE_VOLUME_MUTE.getKeyVal(), KEYCODE_VOLUME_MUTE);

            mSaKeys.put(KEYCODE_RADIO.getKeyVal(), KEYCODE_RADIO);
            mSaKeys.put(KEYCODE_PREV.getKeyVal(), KEYCODE_PREV);
            mSaKeys.put(KEYCODE_NEXT.getKeyVal(), KEYCODE_NEXT);
            mSaKeys.put(KEYCODE_DPAD_LEFT.getKeyVal(), KEYCODE_DPAD_LEFT);
            mSaKeys.put(KEYCODE_DPAD_RIGHT.getKeyVal(), KEYCODE_DPAD_RIGHT);

            mSaKeys.put(KEYCODE_ENTER.getKeyVal(), KEYCODE_ENTER);
            mSaKeys.put(KEYCODE_HOME.getKeyVal(), KEYCODE_HOME);
            mSaKeys.put(KEYCODE_BACK.getKeyVal(), KEYCODE_BACK);
        }
        return mSaKeys.get(value);
    }
}