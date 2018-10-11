package com.tri.lib.radio.utils;

import com.tri.lib.radio.engine.BandCategoryEnum;

import js.lib.android.utils.PreferenceHelper;

/**
 * Preference storage methods
 *
 * @author Jun.Wang
 */
public class RadioPreferUtils extends PreferenceHelper {
    //TAG
    private static final String TAG = "PreferUtils";

    public static boolean isFirstOpen() {
        final String PREFER_KEY = "com.tricheer.radio.FIST_OPEN_FLAG";
        boolean isOpened = getBoolean(PREFER_KEY, false);
        if (!isOpened) {
            saveBoolean(PREFER_KEY, true);
        }
        return !isOpened;
    }

    /**
     * Get last band.
     *
     * @param isSave true - save; false - not save
     * @param band   {@link BandCategoryEnum#FM} or {@link BandCategoryEnum#AM}
     * @return int {@link BandCategoryEnum#FM} or {@link BandCategoryEnum#AM}
     */
    public static BandCategoryEnum getLastBand(boolean isSave, BandCategoryEnum band) {
        final String PREFER_KEY = "com.tricheer.radio.LAST_BAND";
        if (isSave && band != null) {
            saveInt(PREFER_KEY, band.getVal());
        }
        int bandVal = getInt(PREFER_KEY, -1);
        BandCategoryEnum resBand = BandCategoryEnum.get(bandVal);
        return (resBand == BandCategoryEnum.NONE) ? BandCategoryEnum.FM : resBand;
    }

    /**
     * Get frequency of last played.
     *
     * @param isSave true - save; false - not save
     * @param freq   frequency.
     * @return int -1 means saved nothing.
     */
    public static int getLastFreq(boolean isSave, BandCategoryEnum band, int freq) {
        final String PREFER_KEY = "com.tricheer.radio.LAST_FREQ" + "_&_BAND=" + band;
        if (isSave) {
            saveInt(PREFER_KEY, freq);
        }
        return getInt(PREFER_KEY, -1);
    }
}
