package com.tricheer.radio.utils;

import android.util.Log;

import com.yj.lib.radio.engine.BandCategoryEnum;
import com.yj.lib.radio.utils.RadioPreferUtils;

/**
 * Preference storage methods
 *
 * @author Jun.Wang
 */
public class TrRadioPreferUtils extends RadioPreferUtils {
    //TAG
    private static final String TAG = "PreferUtils";

    /**
     * Get collect value
     *
     * @param isSave   true - save; false - not save
     * @param band     {@link BandCategoryEnum#FM} or {@link BandCategoryEnum#AM}
     * @param pageIdx  Page Index. (1)FM(0~5); (2)AM(0~2);
     * @param position Position(0~5)
     * @param freq     Frequency value.
     * @return int
     */
    public static int getCollect(boolean isSave, BandCategoryEnum band, int pageIdx, int position, int freq) {
        final String PREFER_KEY = "com.tricheer.radio.COLLECT" + "_" + band + "." + pageIdx + "." + position;
        if (isSave) {
            saveInt(PREFER_KEY, freq);
        }
        return getInt(PREFER_KEY, -1);
    }

    public static void saveSearchedFreqs(BandCategoryEnum band, int[] freqs) {
        switch (band) {
            case FM:
                saveSearchedFmFreqs(freqs);
                break;
            case AM:
                saveSearchedAmFreqs(freqs);
                break;
        }
    }

    private static void saveSearchedFmFreqs(int[] freqs) {
        final BandCategoryEnum BAND = BandCategoryEnum.FM;
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
        final BandCategoryEnum BAND = BandCategoryEnum.AM;
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
