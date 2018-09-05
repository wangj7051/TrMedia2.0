package com.tricheer.engine.mcu.bean;

import com.tricheer.engine.mcu.MCUConsts;

/**
 * MCU Radio Settings Command
 * 
 * @author Jun.Wang
 */
public class MCURadioSettings {
	/**
	 * {@link MCUConsts.RadioStatus} - ST_*
	 */
	public int stStatus;
	/**
	 * {@link MCUConsts.RadioStatus} - LOC_*
	 */
	public int locStatus;
}
