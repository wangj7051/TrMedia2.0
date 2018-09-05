package com.tricheer.engine.mcu.radio;

import js.lib.utils.DigitUtil;

/**
 * Radio Logic Methods
 * 
 * @author Jun.Wang
 */
public class RadioUtil {

	/**
	 * Is FM Band
	 */
	public static boolean isFMBand(int bandType) {
		switch (bandType) {
		case BandType.BAND_FM1:
		case BandType.BAND_FM2:
		case BandType.BAND_FM3:
			return true;
		}
		return false;
	}

	/**
	 * Is AM Band
	 */
	public static boolean isAMBand(int bandType) {
		switch (bandType) {
		case BandType.BAND_AM1:
		case BandType.BAND_AM2:
			return true;
		}
		return false;
	}

	/**
	 * Get Band Unit
	 * <p>
	 * AM1/AM2 - FM1/FM2/FM3
	 */
	public static String getBandUnit1(int bandType) {
		switch (bandType) {
		case BandType.BAND_AM1:
		case BandType.BAND_AM2:
			return BandUnit.UNIT_AM;
		case BandType.BAND_FM1:
		case BandType.BAND_FM2:
		case BandType.BAND_FM3:
			return BandUnit.UNIT_FM;
		}
		return "";
	}

	/**
	 * Get Band Unit
	 * <p>
	 * AM - FM
	 */
	public static String getBandUnit2(int bandType) {
		switch (bandType) {
		case BandType.AM:
			return BandUnit.UNIT_AM;
		case BandType.FM:
			return BandUnit.UNIT_FM;
		}
		return "";
	}

	/**
	 * Parse Frequency to Double
	 */
	public static double getFreq(int freq) {
		return ((double) freq) / 100;
	}

	/**
	 * Get Formatted FM Frequency
	 * <p>
	 * 10800 -> 108.00
	 */
	public static String getFormattedFmFreq(int freq) {
		return DigitUtil.format(getFreq(freq), DigitUtil.FORMAT_11);
	}

	/**
	 * Get Formatted Current Frequency
	 */
	public static String getFormattedFreq(int bandType, int freq) {
		if (isFMBand(bandType)) {
			return getFormattedFmFreq(freq);
		} else if (isAMBand(bandType)) {
			return String.valueOf(String.valueOf(freq));
		}
		return "";
	}

	/**
	 * Get Formatted FM Frequency
	 * <p>
	 * 10800 -> 108.0
	 */
	public static String getFormattedFmFreq2(int freq) {
		return DigitUtil.format(getFreq(freq), DigitUtil.FORMAT_12);
	}
}
