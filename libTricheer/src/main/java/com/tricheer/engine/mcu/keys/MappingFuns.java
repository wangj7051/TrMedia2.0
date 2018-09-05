package com.tricheer.engine.mcu.keys;

import com.tricheer.engine.mcu.keys.func.CanBoxFuncs;
import com.tricheer.engine.mcu.keys.func.PlayerFuncs;
import com.tricheer.engine.mcu.keys.func.PlayerFuncs.PlayerFuncFlags;
import com.tricheer.engine.mcu.keys.func.RadioFuncs;
import com.tricheer.engine.mcu.keys.func.RadioFuncs.RadioFuncFlags;

/**
 * Vehicle Key Functions
 * 
 * @author Jun.Wang
 */
public class MappingFuns {
	/**
	 * Get Radio Function
	 * 
	 * @param direction
	 *            : 方向键映射值 {@link MappingKeys}
	 * @param pressFlag
	 *            : 按压标记 {@link KeyPressFlags}
	 * @return Integer 功能标记 {@link PlayerFuncFlags}
	 */
	public static int getVideoListFunc(byte direction, byte pressFlag) {
		int funcFlag = -1;
		switch (pressFlag) {
		case KeyPressFlags.PRESS_SHORT:
			funcFlag = PlayerFuncs.getVideoListShortPressFunc(direction);
			break;
		case KeyPressFlags.PRESS_LONG:
			// funcFlag = PlayerFuncs.getVideoListShortPressFunc(direction);
			break;
		}
		return funcFlag;
	}

	/**
	 * Get Radio Function
	 * 
	 * @param direction
	 *            : 方向键映射值 {@link MappingKeys}
	 * @param pressFlag
	 *            : 按压标记 {@link KeyPressFlags}
	 * @return Integer 功能标记 {@link PlayerFuncFlags}
	 */
	public static int getPlayerFunc(byte direction, byte pressFlag) {
		int funcFlag = -1;
		switch (pressFlag) {
		case KeyPressFlags.PRESS_SHORT:
			funcFlag = PlayerFuncs.getShortPressFunc(direction);
			break;
		case KeyPressFlags.PRESS_LONG:
			funcFlag = PlayerFuncs.getLongPressFunc(direction);
			break;
		}
		return funcFlag;
	}

	/**
	 * Get Radio Function
	 * 
	 * @param direction
	 *            : 方向键映射值 {@link MappingKeys}
	 * @param pressFlag
	 *            : 按压标记 {@link KeyPressFlags}
	 * @return Integer 功能标记 {@link RadioFuncFlags}
	 */
	public static int getRadioFunc(byte direction, byte pressFlag) {
		int funcFlag = -1;
		switch (pressFlag) {
		case KeyPressFlags.PRESS_SHORT:
			funcFlag = RadioFuncs.getShortPressFunc(direction);
			break;
		case KeyPressFlags.PRESS_LONG:
			funcFlag = RadioFuncs.getLongPressFunc(direction);
			break;
		case KeyPressFlags.KNOB:
			funcFlag = RadioFuncs.getKnobFunc(direction);
			break;
		}
		return funcFlag;
	}

	public int getCanBoxFunc(byte direction, byte pressFlag) {
		int funcFlag = -1;
		switch (pressFlag) {
		case KeyPressFlags.PRESS_SHORT:
			funcFlag = CanBoxFuncs.getShortPressFunc(direction);
			break;
		case KeyPressFlags.PRESS_LONG:
			funcFlag = CanBoxFuncs.getLongPressFunc(direction);
			break;
		}
		return funcFlag;
	}
}