package com.tricheer.engine.mcu.bean;

import com.tricheer.engine.mcu.MCUConsts;
import com.tricheer.engine.mcu.MCUResCmds;

/**
 * MCU Car Informations Data
 * 
 * @author Jun.Wang
 */
public class MCUCarBodyInfos {

	/**
	 * {@link MCUConsts.HandBrakeStatus}
	 */
	public byte handBrakeStatus;

	/**
	 * {@link MCUResCmds.CAR_BODY_INFOS_INNER_CMD}
	 */
	public byte innerCMD;
}
