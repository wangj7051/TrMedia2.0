package com.tricheer.engine.mcu.keys;

/**
 * Vehicle Mapping Keys
 * 
 * @author Jun.Wang
 */
public interface MappingKeys {
	public final byte KEY_VOLUME_UP = 0x01;
	public final byte KEY_VOLUME_DOWN = 0x02;
	public final byte KEY_TURN_RIGHT = 0x03;// （下一曲）
	public final byte KEY_TURN_LEFT = 0x04;// （上一曲）
	public final byte KEY_SRC_OR_MUTE = 0x07;
	public final byte KEY_SPEECH = 0x08;
	public final byte KEY_PICK_UP = 0x09;
	public final byte KEY_HANG_UP = 0x0A;
	public final byte KEY_AUDIO = 0x0B;
	public final byte KEY_GPS = 0x0C;
	public final byte KEY_FAST_FORWARD = 0x0D;
	public final byte KEY_FAST_REWIND = 0x0E;
	public final byte KEY_RELEASE_FORWARD_REWIND = 0x0F;

	public final byte KEY_MIC_OFF = 0x11;
	public final byte KEY_MIC_ON = 0x12;
	public final byte KEY_TURN_UP = 0x13;
	public final byte KEY_TURN_DOWN = 0x14;
	public final byte KEY_RETURN_BACK = 0x15;
	public final byte KEY_ENTER = 0x16;
	public final byte KEY_PRIVATE_PHONE = 0x17;
	public final byte KEY_SPEAKER_PHONE = 0x18;
	public final byte KEY_MENU_OR_MUTE = 0x19;
	public final byte KEY_TURN_DOWN_LEFT = 0x1A;
	public final byte KEY_TURN_LEFT_UP = 0x1B;
	public final byte KEY_TURN_RIGHT_DOWN = 0x1C;
	public final byte KEY_TURN_UP_RIGHT = 0x1D;
	public final byte KEY_MODE = 0x1E;

	public final byte KEY_BT_PHONE = 0x31;
	public final byte KEY_MUTE_REDEF = 0x32;
	public final byte KEY_SCREEN_OFF = 0x33;
	public final byte KEY_MENU = 0x34;
	public final byte KEY_RIGHT_CAMERA = 0x36;
	public final byte KEY_MIC = 0x37;
	public final byte KEY_RADIO = 0x38;// 在收音机打开的情况下Band切换
	public final byte KEY_MEDIA = 0x3A;
	public final byte KEY_POWER = 0x3B;
	public final byte KEY_MUTE_OR_SPEECH = 0x3C;
	public final byte KEY_HANGUP_OR_MUTE = 0x3F;

	public final byte KEY_HANGUP_OR_RETURN = 0x40;
	public final byte KEY_SPEECH_HOLD = 0x41;
	public final byte KEY_PICKUP_HOLD = 0x42;
	public final byte KEY_HANGUP_OR_RETURN_HOLD = 0x43;
	public final byte KEY_CH_FLD_INCREASE = 0x45;
	public final byte KEY_CH_FLD_DECREASE = 0x46;
	public final byte KEY_MEM = 0x47;
	public final byte KEY_MENU_UP = 0x48;
	public final byte KEY_MENU_DOWN = 0x49;
	public final byte KEY_LIST = 0x4A;
	public final byte KEY_PAGE_SW = 0x4B;
	public final byte KEY_DIS_MODE = 0x4C;
	public final byte K_PRESET_UP = 0x4D;
	public final byte K_PRESET_DN = 0x4E;
	public final byte KEY_DISP = 0x4F;

	public final byte KEY_PAGE = 0x50;
	public final byte KEY_CRUISE_MEM = 0x51;
	public final byte KEY_LIMIT_MEM = 0x52;

	public final byte KEY_APS = (byte) 0x80;
	public final byte KEY_CONFIG = (byte) 0x81;
	public final byte KEY_SCAN = (byte) 0x82;// SCAN 功能，当前映射为自动搜索功能
	public final byte KEY_MUTE_OR_POWER = (byte) 0x83;
	public final byte KEY_NUM_1 = (byte) 0x84;
	public final byte KEY_NUM_2 = (byte) 0x85;
	public final byte KEY_NUM_3 = (byte) 0x86;
	public final byte KEY_NUM_4 = (byte) 0x87;
	public final byte KEY_NUM_5 = (byte) 0x88;
	public final byte KEY_NUM_6 = (byte) 0x89;
	public final byte KEY_EJECT = (byte) 0x8A;
	public final byte KEY_SEL = (byte) 0x8B;
	public final byte KEY_TONE = (byte) 0x8C;
	public final byte KEY_CDC_OR_AUX = (byte) 0x8D;
	public final byte KEY_MUTE_OR_CALL = (byte) 0x8E;
	public final byte KEY_PLAY_OR_PAUSE = (byte) 0x8F;

	public final byte KEY_INFO = (byte) 0x90;
	public final byte KEY_TIME = (byte) 0x91;
	public final byte KEY_FAV = (byte) 0x92;
	public final byte KEY_AS = (byte) 0x93;
	public final byte KEY_HOME = (byte) 0x94;
	public final byte KEY_CD = (byte) 0x95;
	public final byte KEY_LOAD = (byte) 0x96;
	public final byte KEY_AUX = (byte) 0x97;
	public final byte KEY_DEST = (byte) 0x98;
	public final byte KEY_PRT = (byte) 0x99;
	public final byte KEY_SOURCE_BT = (byte) 0x9A;
	public final byte KEY_FR = (byte) 0x9B;
	public final byte KEY_FF = (byte) 0x9C;
	public final byte KEY_ILL_DEC = (byte) 0x9D;
	public final byte KEY_ILL_INC = (byte) 0x9E;
	public final byte KEY_MAP_ZOOM_OUT = (byte) 0x9F;

	public final byte KEY_MAP_ZOOM_IN = (byte) 0xA0;
	public final byte KEY_SPEAK_OFF = (byte) 0xA1;
	public final byte KEY_SPEAK_ON = (byte) 0xA2;
	public final byte KEY_AIR = (byte) 0xA3;
	public final byte KEY_CAR = (byte) 0xA4;
	public final byte KEY_SETUP = (byte) 0xA6;
	public final byte KEY_RESET = (byte) 0xA7;
}
