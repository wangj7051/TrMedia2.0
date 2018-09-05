package com.tricheer.radio.utils;

import com.tricheer.radio.engine.BandInfos.BandType;

import js.lib.android.utils.PreferenceHelper;

/**
 * Preference storage methods
 *
 * @author Jun.Wang
 */
public class PreferUtils extends PreferenceHelper {
    /**
     * Get last band.
     *
     * @param isSave true - save; false - not save
     * @param band   {@link BandType#FM} or {@link BandType#AM}
     * @return int {@link BandType#FM} or {@link BandType#AM}
     */
    public static int getLastBand(boolean isSave, int band) {
        final String PREFER_KEY = "com.tricheer.radio.LAST_BAND";
        if (isSave) {
            saveInt(PREFER_KEY, band);
        }
        return getInt(PREFER_KEY, BandType.FM);
    }

    /**
     * Get frequency of last played.
     *
     * @param isSave true - save; false - not save
     * @param freq   frequency.
     * @return int -1 means saved nothing.
     */
    public static int getLastFreq(boolean isSave, int band, int freq) {
        final String PREFER_KEY = "com.tricheer.radio.LAST_FREQ" + "_&_BAND=" + band;
        if (isSave) {
            saveInt(PREFER_KEY, freq);
        }
        return getInt(PREFER_KEY, -1);
    }

    /**
     * Get collect value
     *
     * @param isSave   true - save; false - not save
     * @param band     {@link BandType#FM} or {@link BandType#AM}
     * @param pageIdx  Page Index. (1)FM(0~5); (2)AM(0~2);
     * @param position Position(0~5)
     * @param freq     Frequency value.
     * @return int
     */
    public static int getCollect(boolean isSave, int band, int pageIdx, int position, int freq) {
        final String PREFER_KEY = "com.tricheer.radio.COLLECT" + "_" + band + "." + pageIdx + "." + position;
        if (isSave) {
            saveInt(PREFER_KEY, freq);
        }
        return getInt(PREFER_KEY, -1);
    }
}
