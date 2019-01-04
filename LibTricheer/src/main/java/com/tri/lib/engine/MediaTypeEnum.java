package com.tri.lib.engine;

import android.util.SparseArray;

public enum MediaTypeEnum {
    RADIO_1ST(1), RADIO_2ND(2), MUSIC(3), BT_MUSIC(4);

    private int mType;

    MediaTypeEnum(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    private static SparseArray<MediaTypeEnum> mSaEnums;

    public static MediaTypeEnum getByType(int type) {
        if (mSaEnums == null) {
            mSaEnums = new SparseArray<>();
            mSaEnums.put(RADIO_1ST.getType(), RADIO_1ST);
            mSaEnums.put(RADIO_2ND.getType(), RADIO_2ND);
            mSaEnums.put(MUSIC.getType(), MUSIC);
            mSaEnums.put(BT_MUSIC.getType(), BT_MUSIC);
        }
        return mSaEnums.get(type);
    }
}
