package com.tricheer.engine.mcu.keys.func;

import java.util.HashMap;
import java.util.Map;

/**
 * CanBox Functions
 * 
 * @author Jun.Wang
 */
public class CanBoxFuncs {
	private static Map<Byte, Integer> mMapShortPressFuncs = new HashMap<Byte, Integer>();
	private static Map<Byte, Integer> mapLongPressFuncs = new HashMap<Byte, Integer>();

	public static int getLongPressFunc(int direction) {
		return -1;
	}

	public static int getShortPressFunc(int direction) {
		return -1;
	}
}