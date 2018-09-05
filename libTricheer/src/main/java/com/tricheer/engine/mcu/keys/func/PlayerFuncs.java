package com.tricheer.engine.mcu.keys.func;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

import com.tricheer.engine.mcu.keys.MappingKeys;

/**
 * Player Functions
 * 
 * @author Jun.Wang
 */
@SuppressLint("UseSparseArrays")
public class PlayerFuncs extends BaseFuncs {
	// 短按功能映射
	private static Map<Byte, Integer> mMapShortPressFuncs = new HashMap<Byte, Integer>();
	// 长按功能映射
	private static Map<Byte, Integer> mMapLongPressFuncs = new HashMap<Byte, Integer>();

	// Video List
	private static Map<Byte, Integer> mMapVideoListShortFuncs = new HashMap<Byte, Integer>();

	/**
	 * Player Function Flags
	 */
	public interface PlayerFuncFlags {
		/**
		 * 播放上一个
		 */
		public final int PLAY_PREV_MEDIA = 1;
		/**
		 * 播放下一个
		 */
		public final int PLAY_NEXT_MEDIA = 2;
		/**
		 * 播放暂停切换
		 */
		public final int PLAY_OR_PAUSE = 3;

		/**
		 * 播放模式切换
		 * <p>
		 * 音乐播放器中 随机/顺序/单曲/循环 切换
		 */
		public final int PLAY_MODE_SWITCH = 4;

		/**
		 * 选中列表上一个位置
		 */
		public final int LIST_PREV_POS = 5;
		/**
		 * 选中列表下一个位置
		 */
		public final int LIST_NEXT_POS = 6;
		/**
		 * 播放列表当前位置
		 */
		public final int LIST_CURR_POS_PLAY = 7;
	}

	static {
		// KEY_TURN_UP
		mMapShortPressFuncs.put(MappingKeys.KEY_TURN_UP, PlayerFuncFlags.PLAY_PREV_MEDIA);
		mMapVideoListShortFuncs.put(MappingKeys.KEY_TURN_UP, PlayerFuncFlags.LIST_PREV_POS);
		// KEY_TURN_DOWN
		mMapShortPressFuncs.put(MappingKeys.KEY_TURN_DOWN, PlayerFuncFlags.PLAY_NEXT_MEDIA);
		mMapVideoListShortFuncs.put(MappingKeys.KEY_TURN_DOWN, PlayerFuncFlags.LIST_NEXT_POS);

		// K_PRESET_UP
		mMapShortPressFuncs.put(MappingKeys.K_PRESET_UP, PlayerFuncFlags.PLAY_PREV_MEDIA);
		mMapVideoListShortFuncs.put(MappingKeys.K_PRESET_UP, PlayerFuncFlags.LIST_PREV_POS);
		// K_PRESET_DN
		mMapShortPressFuncs.put(MappingKeys.K_PRESET_DN, PlayerFuncFlags.PLAY_NEXT_MEDIA);
		mMapVideoListShortFuncs.put(MappingKeys.K_PRESET_DN, PlayerFuncFlags.LIST_NEXT_POS);

		// KEY_TURN_RIGHT
		mMapShortPressFuncs.put(MappingKeys.KEY_TURN_LEFT, PlayerFuncFlags.PLAY_PREV_MEDIA);
		mMapVideoListShortFuncs.put(MappingKeys.KEY_TURN_LEFT, PlayerFuncFlags.LIST_PREV_POS);
		// KEY_TURN_RIGHT
		mMapShortPressFuncs.put(MappingKeys.KEY_TURN_RIGHT, PlayerFuncFlags.PLAY_NEXT_MEDIA);
		mMapVideoListShortFuncs.put(MappingKeys.KEY_TURN_RIGHT, PlayerFuncFlags.LIST_NEXT_POS);

		// KEY_PLAY_OR_PAUSE
		mMapShortPressFuncs.put(MappingKeys.KEY_PLAY_OR_PAUSE, PlayerFuncFlags.PLAY_OR_PAUSE);
		// KEY_ENTER
		mMapShortPressFuncs.put(MappingKeys.KEY_ENTER, PlayerFuncFlags.PLAY_OR_PAUSE);
		mMapVideoListShortFuncs.put(MappingKeys.KEY_ENTER, PlayerFuncFlags.LIST_CURR_POS_PLAY);

		// KEY_PRT
		mMapShortPressFuncs.put(MappingKeys.KEY_PRT, PlayerFuncFlags.PLAY_MODE_SWITCH);
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
	 * Get Video List ShortPress Function
	 */
	public static int getVideoListShortPressFunc(byte direction) {
		return getShortPressFunc(mMapVideoListShortFuncs, direction);
	}
}
