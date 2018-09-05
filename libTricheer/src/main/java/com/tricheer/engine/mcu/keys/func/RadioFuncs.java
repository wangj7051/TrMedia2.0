package com.tricheer.engine.mcu.keys.func;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

import com.tricheer.engine.mcu.keys.MappingKeys;

/**
 * Radio Functions
 * 
 * @author Jun.Wang
 */
@SuppressLint("UseSparseArrays")
public class RadioFuncs extends BaseFuncs {
	// 短按功能映射
	private static Map<Byte, Integer> mMapShortPressFuncs = new HashMap<Byte, Integer>();
	// 长按功能映射
	private static Map<Byte, Integer> mMapLongPressFuncs = new HashMap<Byte, Integer>();
	// 旋钮功能映射
	private static Map<Byte, Integer> mMapKnobFuncs = new HashMap<Byte, Integer>();

	/**
	 * Radio Function Flags
	 */
	public interface RadioFuncFlags {
		/**
		 * 播放预置的频率列表中的上一个频率
		 */
		public final int PLAY_PREV_FREQ = 10;
		/**
		 * 播放预置的频率列表中的下一个频率
		 */
		public final int PLAY_NEXT_FREQ = 11;

		/**
		 * 搜索并播放上一个可播放的频率
		 */
		public final int SCAN_PLAY_PREV_FREQ = 20;
		/**
		 * 搜索并播放下一个可播放的频率
		 */
		public final int SCAN_PLAY_NEXT_FREQ = 21;

		/**
		 * 步进-
		 */
		public final int STEP_PREV = 30;
		/**
		 * 步进+
		 */
		public final int STEP_NEXT = 31;

		/**
		 * 自动扫描所有波段
		 */
		public final int AUTO_SCAN = 40;

		/**
		 * 波段切换
		 */
		public final int BAND_SWITCH = 50;

		/**
		 * 播放预置列表中的 [第 1/2/3/4/5/6 频率]
		 */
		public final int PLAY_FREQ1 = 60, PLAY_FREQ2 = 61, PLAY_FREQ3 = 62, PLAY_FREQ4 = 63, PLAY_FREQ5 = 64, PLAY_FREQ6 = 65;

		/**
		 * 保存当前频率到 [第1/2/3/4/5/6 位置]
		 */
		public final int SAVE_CURRFREQ_TO_POS1 = 70, SAVE_CURRFREQ_TO_POS2 = 71, SAVE_CURRFREQ_TO_POS3 = 72, SAVE_CURRFREQ_TO_POS4 = 73,
				SAVE_CURRFREQ_TO_POS5 = 74, SAVE_CURRFREQ_TO_POS6 = 75;
	}

	static {
		// KEY_TURN_UP
		mMapShortPressFuncs.put(MappingKeys.KEY_TURN_UP, RadioFuncFlags.PLAY_PREV_FREQ);
		mMapLongPressFuncs.put(MappingKeys.KEY_TURN_UP, RadioFuncFlags.SCAN_PLAY_PREV_FREQ);
		// KEY_TURN_DOWN
		mMapShortPressFuncs.put(MappingKeys.KEY_TURN_DOWN, RadioFuncFlags.PLAY_NEXT_FREQ);
		mMapLongPressFuncs.put(MappingKeys.KEY_TURN_DOWN, RadioFuncFlags.SCAN_PLAY_NEXT_FREQ);

		// K_PRESET_UP
		mMapShortPressFuncs.put(MappingKeys.K_PRESET_UP, RadioFuncFlags.PLAY_PREV_FREQ);
		mMapLongPressFuncs.put(MappingKeys.K_PRESET_UP, RadioFuncFlags.SCAN_PLAY_PREV_FREQ);
		// K_PRESET_DN
		mMapShortPressFuncs.put(MappingKeys.K_PRESET_DN, RadioFuncFlags.PLAY_NEXT_FREQ);
		mMapLongPressFuncs.put(MappingKeys.K_PRESET_DN, RadioFuncFlags.SCAN_PLAY_NEXT_FREQ);

		// KEY_CH_FLD_INCREASE
		mMapShortPressFuncs.put(MappingKeys.KEY_CH_FLD_INCREASE, RadioFuncFlags.PLAY_PREV_FREQ);
		mMapLongPressFuncs.put(MappingKeys.KEY_CH_FLD_INCREASE, RadioFuncFlags.SCAN_PLAY_PREV_FREQ);
		// KEY_CH_FLD_DECREASE
		mMapShortPressFuncs.put(MappingKeys.KEY_CH_FLD_DECREASE, RadioFuncFlags.PLAY_NEXT_FREQ);
		mMapLongPressFuncs.put(MappingKeys.KEY_CH_FLD_DECREASE, RadioFuncFlags.SCAN_PLAY_NEXT_FREQ);

		// KEY_TURN_LEFT
		mMapShortPressFuncs.put(MappingKeys.KEY_TURN_LEFT, RadioFuncFlags.SCAN_PLAY_PREV_FREQ);
		mMapLongPressFuncs.put(MappingKeys.KEY_TURN_LEFT, RadioFuncFlags.SCAN_PLAY_PREV_FREQ);
		mMapKnobFuncs.put(MappingKeys.KEY_TURN_LEFT, RadioFuncFlags.STEP_PREV);
		// KEY_TURN_RIGHT
		mMapShortPressFuncs.put(MappingKeys.KEY_TURN_RIGHT, RadioFuncFlags.SCAN_PLAY_NEXT_FREQ);
		mMapLongPressFuncs.put(MappingKeys.KEY_TURN_RIGHT, RadioFuncFlags.SCAN_PLAY_NEXT_FREQ);
		mMapKnobFuncs.put(MappingKeys.KEY_TURN_RIGHT, RadioFuncFlags.STEP_NEXT);

		// KEY_APS
		mMapShortPressFuncs.put(MappingKeys.KEY_APS, RadioFuncFlags.AUTO_SCAN);
		// KEY_AS
		mMapShortPressFuncs.put(MappingKeys.KEY_AS, RadioFuncFlags.AUTO_SCAN);
		// KEY_SCAN
		mMapShortPressFuncs.put(MappingKeys.KEY_SCAN, RadioFuncFlags.AUTO_SCAN);

		// KEY_RADIO
		mMapShortPressFuncs.put(MappingKeys.KEY_RADIO, RadioFuncFlags.BAND_SWITCH);

		// KEY_NUM_1
		mMapShortPressFuncs.put(MappingKeys.KEY_NUM_1, RadioFuncFlags.PLAY_FREQ1);
		mMapLongPressFuncs.put(MappingKeys.KEY_NUM_1, RadioFuncFlags.SAVE_CURRFREQ_TO_POS1);
		// KEY_NUM_2
		mMapShortPressFuncs.put(MappingKeys.KEY_NUM_2, RadioFuncFlags.PLAY_FREQ2);
		mMapLongPressFuncs.put(MappingKeys.KEY_NUM_2, RadioFuncFlags.SAVE_CURRFREQ_TO_POS2);
		// KEY_NUM_3
		mMapShortPressFuncs.put(MappingKeys.KEY_NUM_3, RadioFuncFlags.PLAY_FREQ3);
		mMapLongPressFuncs.put(MappingKeys.KEY_NUM_3, RadioFuncFlags.SAVE_CURRFREQ_TO_POS3);
		// KEY_NUM_4
		mMapShortPressFuncs.put(MappingKeys.KEY_NUM_4, RadioFuncFlags.PLAY_FREQ4);
		mMapLongPressFuncs.put(MappingKeys.KEY_NUM_4, RadioFuncFlags.SAVE_CURRFREQ_TO_POS4);
		// KEY_NUM_5
		mMapShortPressFuncs.put(MappingKeys.KEY_NUM_5, RadioFuncFlags.PLAY_FREQ5);
		mMapLongPressFuncs.put(MappingKeys.KEY_NUM_5, RadioFuncFlags.SAVE_CURRFREQ_TO_POS5);
		// KEY_NUM_6
		mMapShortPressFuncs.put(MappingKeys.KEY_NUM_6, RadioFuncFlags.PLAY_FREQ6);
		mMapLongPressFuncs.put(MappingKeys.KEY_NUM_6, RadioFuncFlags.SAVE_CURRFREQ_TO_POS6);
	}

	/**
	 * Get ShortPress Function
	 */
	public static int getShortPressFunc(byte direction) {
		return getShortPressFunc(mMapShortPressFuncs, direction);
	}

	/**
	 * Get LongPress Function
	 */
	public static int getLongPressFunc(byte direction) {
		return getLongPressFunc(mMapLongPressFuncs, direction);
	}

	/**
	 * Get Knob Function
	 */
	public static int getKnobFunc(byte direction) {
		return getKnobFunc(mMapKnobFuncs, direction);
	}
}
