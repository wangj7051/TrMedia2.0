package com.tricheer.engine.mcu;

import js.lib.android.utils.Logs;

import com.tricheer.engine.mcu.MCUConsts.HandBrakeStatus;
import com.tricheer.engine.mcu.bean.MCUCarBodyInfos;
import com.tricheer.engine.mcu.bean.MCUDirection;
import com.tricheer.engine.mcu.bean.MCURadioInfos;
import com.tricheer.engine.mcu.bean.MCURadioSettings;
import com.tricheer.engine.mcu.bean.MCURoot;

/**
 * MCU Parse Methods
 * 
 * @author Jun.Wang
 */
public class MCUResParser {
	// TAG
	private static final String TAG = "MCUResParser -> ";

	/**
	 * Parse Root Command
	 */
	public static MCURoot parseRoot(byte[] data) {
		MCURoot mcur = new MCURoot();
		try {
			mcur.cmd = data[0];
			mcur.byteArr = new byte[data.length - 1];
			if (data.length > 1) {
				for (int idx = 1; idx < data.length; idx++) {
					mcur.byteArr[idx - 1] = data[idx];
				}
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "parseRoot()", e);
		}
		return mcur;
	}

	/**
	 * Parse Radio Informations
	 * <p>
	 * (bandType / currFreq / autoSearchFlag / currFreqPos / psFlag)
	 */
	public static MCURadioInfos parseRadioInfos(byte[] byteArr) {
		MCURadioInfos mcuris = new MCURadioInfos();
		try {
			mcuris.bandType = byteArr[0];
			mcuris.converCurrFreq(byteArr[1], byteArr[2]);
			mcuris.autoSearchFlag = byteArr[3];
			mcuris.currFreqPos = byteArr[4];
			mcuris.psFlag = byteArr[5];
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "parseRadioInfos()", e);
		}
		return mcuris;
	}

	/**
	 * Parse Radio Settings
	 * <p>
	 * (ST / LOC)
	 */
	public static MCURadioSettings parseRadioSettings(byte[] byteArr) {
		MCURadioSettings mcurss = new MCURadioSettings();
		try {
			mcurss.stStatus = byteArr[0];
			mcurss.locStatus = byteArr[1];
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "parseRadioSettings()", e);
		}
		return mcurss;
	}

	/**
	 * Parse Radio Current Frequency ST Status
	 */
	public static int parseRadioFreqStStatus(byte[] byteArr) {
		int freqSt = 0;
		try {
			freqSt = byteArr[0];
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "parseFreqStStatus()", e);
		}
		return freqSt;
	}

	/**
	 * Parse Directions
	 */
	public static MCUDirection parseDirection(byte[] byteArr) {
		MCUDirection mcud = new MCUDirection();
		try {
			mcud.innerCMD = byteArr[0];
			mcud.mappingKey = byteArr[1];
			mcud.pressFlag = byteArr[2];
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "parseDirection()", e);
		}
		return mcud;
	}

	/**
	 * Parse Car Body Informations Command
	 * <p>
	 * Like "[46, -3, 16, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -15]"
	 */
	public static MCUCarBodyInfos parseCarBodyInfos(byte[] byteArr) {
		MCUCarBodyInfos mcucbis = new MCUCarBodyInfos();
		try {
			mcucbis.innerCMD = byteArr[1];
			mcucbis.handBrakeStatus = byteArr[6];
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "parseCarBodyInfos()", e);
		}
		return mcucbis;
	}

	/**
	 * Parse Car HandBrake Status
	 * <p>
	 * {@link MCUConsts.HandBrakeStatus}
	 */
	public static int parseHandBrakeStatus(byte[] byteArr) {
		try {
			return byteArr[0];
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "parseHandBrakeStatus()", e);
		}
		return HandBrakeStatus.OFF;
	}
}
