package com.tricheer.radio.utils;

import com.yj.lib.radio.engine.BandCategoryEnum;

import java.util.Locale;

/**
 * Radio frequency format methods.
 *
 * @author Jun.Wang
 */
public class FreqFormatUtil {
    public static String getFreqStr(BandCategoryEnum band, int freq) {
        return (band == BandCategoryEnum.FM) ? getFmFreqStr(freq) : getAmFreqStr(freq);
    }

    public static String getFmFreqStr(int freq) {
        return String.format(Locale.getDefault(), "%1$.1f", (freq / 100d));
    }

    public static String getAmFreqStr(int freq) {
        return String.valueOf(freq);
    }
}
