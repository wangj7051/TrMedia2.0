package com.tri.lib.radio.engine;

import android.util.SparseArray;

/**
 * Band category
 *
 * @author Jun.Wang
 */
public enum BandCategoryEnum {
    FM(0, "MHz"),
    AM(1, "KHz"),
    NONE(-1, "");

    private int mVal;
    private String mUnit;

    BandCategoryEnum(int val, String unit) {
        mVal = val;
        mUnit = unit;
    }

    public int getVal() {
        return mVal;
    }

    public String getUnit() {
        return mUnit;
    }

    private static SparseArray<BandCategoryEnum> mSaEnums;

    public static BandCategoryEnum get(int val) {
        if (mSaEnums == null) {
            mSaEnums = new SparseArray<>();
            mSaEnums.put(FM.getVal(), FM);
            mSaEnums.put(AM.getVal(), AM);
        }
        BandCategoryEnum res = mSaEnums.get(val);
        return res == null ? NONE : res;
    }
}
