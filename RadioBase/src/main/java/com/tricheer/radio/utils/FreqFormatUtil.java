package com.tricheer.radio.utils;

import com.tricheer.radio.engine.BandInfos;

import java.util.Locale;

/**
 * Radio frequency format methods.
 *
 * @author Jun.Wang
 */
public class FreqFormatUtil {
    public static String getFreqStr(int band, int freq) {
        return (band == BandInfos.BandType.FM) ? getFmFreqStr(freq) : getAmFreqStr(freq);
    }

    public static String getFmFreqStr(int freq) {
        return String.format(Locale.getDefault(), "%1$.1f", (freq / 100d));
    }

    public static String getAmFreqStr(int freq) {
        return String.valueOf(freq);
    }
}
