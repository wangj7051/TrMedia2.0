package com.tricheer.engine.mcu.keys;

/**
 * 按键按压标记
 */
public interface KeyPressFlags {
	/**
	 * 位置状态
	 */
	public static final byte NONE = 0x00;

	/**
	 * 短按
	 */
	public static final byte PRESS_SHORT = 0x01;

	/**
	 * 长按
	 */
	public static final byte PRESS_LONG = 0x02;

	/**
	 * 旋钮
	 */
	public static final byte KNOB = 0x04;
}