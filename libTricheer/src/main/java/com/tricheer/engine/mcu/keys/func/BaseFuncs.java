package com.tricheer.engine.mcu.keys.func;

import java.util.Map;

/**
 * Base Functions
 * 
 * @author Jun.Wang
 */
public class BaseFuncs {
	/**
	 * Get ShortPress Function
	 */
	protected static int getShortPressFunc(Map<Byte, Integer> mapShortPressFuncs, byte direction) {
		Integer funFlag = mapShortPressFuncs.get(direction);
		return (funFlag == null) ? 0 : funFlag;
	}

	/**
	 * Get LongPress Function
	 */
	protected static int getLongPressFunc(Map<Byte, Integer> mapLongPressFuncs, byte direction) {
		Integer funFlag = mapLongPressFuncs.get(direction);
		return (funFlag == null) ? 0 : funFlag;
	}

	/**
	 * Get Knob Function
	 */
	protected static int getKnobFunc(Map<Byte, Integer> mapKnobPressFuncs, byte direction) {
		Integer funFlag = mapKnobPressFuncs.get(direction);
		return (funFlag == null) ? 0 : funFlag;
	}
}
