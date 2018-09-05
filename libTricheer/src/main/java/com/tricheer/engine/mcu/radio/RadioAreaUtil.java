package com.tricheer.engine.mcu.radio;

import js.lib.android.utils.Logs;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

/**
 * 收音机 [地理区域 及 预置频率]
 * 
 * @author Jun.Wang
 */
public class RadioAreaUtil {
	// TAG
	private static final String TAG = "RadioAreaUtil";

	/**
	 * RadioArea
	 */
	public interface RadioArea {
		public static final int CHINA = 0x00;
		public static final int EUROPE = 0x01;
		public static final int USA = 0x02;
		public static final int JAPAN = 0x03;
		public static final int EASTERN_EUROPE = 0x04;
		public static final int LATIN_AMERICA = 0x05;
		public static final int CHINA_TAIWAN = 0x06;
		public static final int AUSTRILIA = 0x07;
	}

	/**
	 * 获取设置的区域
	 */
	public static int getArea(Context cxt) {
		int areaType = 0;
		try {
			areaType = Settings.System.getInt(cxt.getContentResolver(), "radio_area");
		} catch (SettingNotFoundException e) {
			areaType = RadioArea.CHINA;
			e.printStackTrace();
		}
		Logs.i(TAG, "getAreaType(cxt) -> [areaType:" + areaType + "]");
		return areaType;
	}

	/**
	 * 获取FM预置频率
	 */
	public static int[] getFMPresetFreqs(Context cxt) {
		int[] arrAreas = new int[6];
		int areaType = getArea(cxt);
		switch (areaType) {
		case RadioArea.CHINA:
		case RadioArea.EUROPE:
		case RadioArea.LATIN_AMERICA:
		case RadioArea.CHINA_TAIWAN:
		case RadioArea.AUSTRILIA:
			arrAreas = new int[] { 8750, 9160, 9570, 9980, 10390, 10800 };
			break;
		case RadioArea.USA:
			arrAreas = new int[] { 8750, 9160, 9570, 9970, 10380, 10790 };
			break;
		case RadioArea.JAPAN:
			arrAreas = new int[] { 7600, 7880, 8160, 8440, 8720, 9000 };
			break;
		case RadioArea.EASTERN_EUROPE:
			arrAreas = new int[] { 6500, 6680, 6860, 7040, 7220, 7400 };
			break;
		}
		Logs.i(TAG, "getFMPresetFreqs(cxt) -> [arrAreas:" + arrAreas.toString() + "]");
		return arrAreas;
	}

	/**
	 * 获取AM预置频率
	 */
	public static int[] getAMPresetFreqs(Context cxt) {
		int[] arrAreas = new int[6];
		int areaType = getArea(cxt);
		switch (areaType) {
		case RadioArea.CHINA:
		case RadioArea.EUROPE:
		case RadioArea.CHINA_TAIWAN:
			arrAreas = new int[] { 522, 742, 961, 1181, 1400, 1620 };
			break;
		case RadioArea.USA:
			arrAreas = new int[] { 530, 766, 1002, 1238, 1474, 1710 };
			break;
		case RadioArea.JAPAN:
			arrAreas = new int[] { 522, 743, 965, 1186, 1408, 1629 };
			break;
		case RadioArea.EASTERN_EUROPE:
			arrAreas = new int[] { 522, 742, 961, 1181, 1400, 1620 };
			break;
		case RadioArea.LATIN_AMERICA:
			arrAreas = new int[] { 520, 740, 960, 1180, 1400, 1620 };
			break;
		case RadioArea.AUSTRILIA:
			arrAreas = new int[] { 522, 760, 997, 1236, 1472, 1710 };
			break;
		}
		Logs.i(TAG, "getAMPresetFreqss(cxt) -> [arrAreas:" + arrAreas.toString() + "]");
		return arrAreas;
	}
}
