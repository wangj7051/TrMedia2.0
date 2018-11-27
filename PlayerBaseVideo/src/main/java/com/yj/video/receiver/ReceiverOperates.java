package com.yj.video.receiver;

/**
 * Receiver Operates
 * 
 * @author Jun.Wang
 */
public class ReceiverOperates {
	// ----Common Command----
	public static final String PAUSE = "PAUSE";
	public static final String RESUME = "RESUME";
	public static final String NEXT = "NEXT";
	public static final String PREVIOUS = "PREVIOUS";

	public static final String PAUSE_ON_E_DOG_START = "PAUSE_ON_E_DOG_START";
	public static final String RESUME_ON_E_DOG_END = "RESUME_ON_E_DOG_END";

	public static final String RECORD_STATE_START = "RECORD_STATE_START";
	public static final String RECORD_STATE_END = "RECORD_STATE_END";

	public static final String PREV_PAGE = "PREV_PAGE";
	public static final String NEXT_PAGE = "NEXT_PAGE";

	// ----Music Command----
	public static final String MUSIC_RANDOM = "MUSIC_RANDOM";
	public static final String MUSIC_MODE_SIGLE = "MUSIC_MODE_SIGLE";
	public static final String MUSIC_MODE_RANDOM = "MUSIC_MODE_RANDOM";
	public static final String MUSIC_MODE_LOOP = "MUSIC_MODE_LOOP";
	public static final String MUSIC_MODE_ORDER = "MUSIC_MODE_ORDER";

	// ----Video Command----
	public static final String VIDEO_FORWARD = "VIDEO_FORWARD";
	public static final String VIDEO_BACKWARD = "VIDEO_BACKWARD";
	public static final String VIDEO_NORMAL = "VIDEO_NORMAL";

	public static final String VIDEO_SCREEN_4_3 = "VIDEO_SCREEN_4_3";
	public static final String VIDEO_SCREEN_16_9 = "VIDEO_SCREEN_16_9";
	public static final String VIDEO_SCREEN_21_9 = "VIDEO_SCREEN_21_9";
	public static final String VIDEO_SCREEN_FULL = "VIDEO_SCREEN_FULL";
	public static final String VIDEO_SCREEN_BIGGER = "VIDEO_SCREEN_BIGGER";
	public static final String VIDEO_SCREEN_SMALLER = "VIDEO_SCREEN_SMALLER";
	public static final String SYS_TEMP_HIGH = "SYS_TEMP_HIGH";

	public static final String VIDEO_PAUSE_ON_AIS_ON = "VIDEO_PAUSE_ON_AIS_ON";
	public static final String VIDEO_RESUME_ON_AIS_EXIT = "VIDEO_RESUME_ON_AIS_EXIT";

	public static final String VIDEO_PAUSE_ON_SCREEN_OFF = "VIDEO_PAUSE_ON_SCREEN_OFF";
	public static final String VIDEO_RESUME_ON_SCREEN_ON = "VIDEO_RESUME_ON_SCREEN_ON";
	public static final String VIDEO_RESUME_ON_MASKAPP_EXIT = "VIDEO_RESUME_ON_MASKAPP_EXIT";

	// ----BlueTooth Commands----
	public static final String BTCALL_RUNING = "BTCALL_RUNING";
	public static final String BTCALL_END = "BT_DIALED";

	// ----AIS Commands----
	public static final String AIS_OPEN = "AIS_OPEN";
	public static final String AIS_EXIT = "AIS_EXIT";
}