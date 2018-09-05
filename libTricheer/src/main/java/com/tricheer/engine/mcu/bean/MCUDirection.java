package com.tricheer.engine.mcu.bean;

import com.tricheer.engine.mcu.MCUResCmds;
import com.tricheer.engine.mcu.keys.KeyPressFlags;
import com.tricheer.engine.mcu.keys.MappingKeys;

/**
 * MCU Direction
 * 
 * @author Jun.Wang
 */
public class MCUDirection {
	/**
	 * {@link MCUResCmds}
	 */
	public byte innerCMD; // = MCUResCmds.DIRECTION_INNER_CMD

	/**
	 * {@link MappingKeys}
	 */
	public byte mappingKey = -1;

	/**
	 * {@link KeyPressFlags}
	 */
	public byte pressFlag = 0;
}
