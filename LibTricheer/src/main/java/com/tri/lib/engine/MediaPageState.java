package com.tri.lib.engine;

import android.util.SparseArray;

public enum MediaPageState {
    RADIO_FOREGROUND(1), RADIO_BACKGROUND(2),
    MUSIC_FOREGROUND(5), MUSIC_BACKGROUND(6),
    BT_MUSIC_FOREGROUND(7), BT_MUSIC_BACKGROUND(8);

    private int mType;

    MediaPageState(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    private static SparseArray<MediaPageState> mSaEnums;

    public static MediaPageState getByType(int type) {
        if (mSaEnums == null) {
            mSaEnums = new SparseArray<>();
            mSaEnums.put(RADIO_FOREGROUND.getType(), RADIO_FOREGROUND);
            mSaEnums.put(RADIO_BACKGROUND.getType(), RADIO_BACKGROUND);
            mSaEnums.put(MUSIC_FOREGROUND.getType(), MUSIC_FOREGROUND);
            mSaEnums.put(MUSIC_BACKGROUND.getType(), MUSIC_BACKGROUND);
            mSaEnums.put(BT_MUSIC_FOREGROUND.getType(), BT_MUSIC_FOREGROUND);
            mSaEnums.put(BT_MUSIC_BACKGROUND.getType(), BT_MUSIC_BACKGROUND);
        }
        return mSaEnums.get(type);
    }
}
