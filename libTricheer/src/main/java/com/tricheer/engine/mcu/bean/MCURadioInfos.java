package com.tricheer.engine.mcu.bean;

import js.lib.utils.HexUtils;

import com.tricheer.engine.mcu.MCUConsts;
import com.tricheer.engine.mcu.radio.BandType;
import com.tricheer.engine.mcu.radio.RadioStatus;

/**
 * MCU Radio Informations
 * 
 * @author Jun.Wang
 */
public class MCURadioInfos {
	/**
	 * Radio Open Status
	 * <p>
	 * Radio Opened : {@link MCUConsts.RadioStatus} - RADIO_*
	 */
	public int openStatus = RadioStatus.RADIO_OPENED;

	/**
	 * Band Type, {@link MCUConsts.BandType}
	 */
	public int bandType = BandType.NONE;

	/**
	 * Like 10800 , means 108.00 MHz
	 */
	public int currFreq;

	/**
	 * Auto Search Flag
	 * <p>
	 * Start {@link MCUConsts.RadioStatus} - SEARCH_*
	 */
	public int autoSearchFlag = -1;

	/**
	 * Current Frequency Position at List
	 */
	public int currFreqPos = -1;

	/**
	 * PS Flag
	 * <p>
	 * Start {@link MCUConsts.RadioStatus} - PS_*
	 */
	public int psFlag = -1;

	/**
	 * Convert High & Low Position data to Frequency
	 */
	public void converCurrFreq(byte byteFreqHighPos, byte byteFreqLowPos) {
		String hexStr = HexUtils.bytesToHexString(new byte[] { byteFreqHighPos, byteFreqLowPos });
		currFreq = HexUtils.parse16HexStrToInt(hexStr);
	}
}
