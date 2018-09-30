package com.tricheer.radio.utils;

import android.util.Log;

import com.tricheer.radio.engine.BandInfos.BandType;

import js.lib.android.utils.PreferenceHelper;

/**
 * Preference storage methods
 *
 * @author Jun.Wang
 */
public class PreferUtils extends PreferenceHelper {
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

    public static void saveSearchedFreqs(int band, int[] freqs) {
        switch (band) {
            case BandType.FM:
                saveSearchedFmFreqs(freqs);
                break;
            case BandType.AM:
                saveSearchedAmFreqs(freqs);
                break;
        }
    }

    private static void saveSearchedFmFreqs(int[] freqs) {
        final int BAND = BandType.FM;
        final int MAX_LOOP = 36;
        //Clear
        int pageIdx = 0, pos = 0;
        for (int idx = 0; idx < MAX_LOOP; idx++) {
            if (idx != 0 && idx % 6 == 0) {
                pageIdx++;
                pos = 0;
            }
            getCollect(true, BAND, pageIdx, pos, -1);
            Log.i(TAG, "FM - CLEAR - [PageIdx:" + pageIdx + " ; " + pos + "] ~ -1");
            pos++;
        }

        //Add
        if (freqs != null) {
            int loop = freqs.length;
            pageIdx = 0;
            pos = 0;
            for (int idx = 0; idx < loop && idx < MAX_LOOP; idx++) {
                if (idx != 0 && idx % 6 == 0) {
                    pageIdx++;
                    pos = 0;
                }
                getCollect(true, BAND, pageIdx, pos, freqs[idx]);
                Log.i(TAG, "FM - ADD - [PageIdx:" + pageIdx + " ; " + pos + "] ~ " + freqs[idx]);
                pos++;
            }
        }
    }

    private static void saveSearchedAmFreqs(int[] freqs) {
        final int BAND = BandType.AM;
        final int MAX_LOOP = 18;
        //Clear
        int pageIdx = 0, pos = 0;
        for (int idx = 0; idx < MAX_LOOP; idx++) {
            if (idx != 0 && idx % 6 == 0) {
                pageIdx++;
                pos = 0;
            }
            Log.i(TAG, "AM - CLEAR - [PageIdx:" + pageIdx + " ; " + pos + "] ~ -1");
            getCollect(true, BAND, pageIdx, pos, -1);
            pos++;
        }

        //Add
        if (freqs != null) {
            int loop = freqs.length;
            pageIdx = 0;
            pos = 0;
            for (int idx = 0; idx < loop && idx < MAX_LOOP; idx++) {
                if (idx != 0 && idx % 6 == 0) {
                    pageIdx++;
                    pos = 0;
                }
                getCollect(true, BAND, pageIdx, pos, freqs[idx]);
                Log.i(TAG, "AM - ADD - [PageIdx:" + pageIdx + " ; " + pos + "] ~ " + freqs[idx]);
                pos++;
            }
        }
    }
}
