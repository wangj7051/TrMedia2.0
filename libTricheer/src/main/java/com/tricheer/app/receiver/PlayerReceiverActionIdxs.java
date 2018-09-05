package com.tricheer.app.receiver;

/**
 * Receiver Actions Index
 * 
 * @author Jun.Wang
 */
public class PlayerReceiverActionIdxs {
	/**
	 * System Power Off
	 * <p>
	 * {@link PlayerReceiverActions#SYS_AUDIO_NOISY} Index
	 * </p>
	 */
	public static final int SYS_AUDIO_NOISY = 1;
	/**
	 * System Power On
	 * <p>
	 * {@link PlayerReceiverActions#SYS_BOOT_COMPLETED} Index
	 * </p>
	 */
	public static final int SYS_BOOT_COMPLETED = 2;

	/**
	 * ACC OFF
	 * <p>
	 * {@link PlayerReceiverActions#SYS_SHUTDOWN} Index
	 * </p>
	 */
	public static final int SYS_SHUTDOWN = 101;
	/**
	 * ACC ON
	 * <p>
	 * {@link PlayerReceiverActions#SELF_BOOT_COMPLETED} Index
	 * </p>
	 */
	public static final int SELF_BOOT_COMPLETED = 102;

	/**
	 * AIS Window Status
	 * <p>
	 * {@link PlayerReceiverActions#AIS_STATUS} Index
	 * </p>
	 * <p>
	 * Parameter|String : "SmallWindow" - ["Open","Close"]
	 * </p>
	 */
	public static final int AIS_STATUS = 203;
	/**
	 * AIOS Operate
	 * <p>
	 * {@link PlayerReceiverActions#AIOS_OPERATE} Index
	 * </p>
	 */
	public static final int AIOS_OPERATE = 202;

	/**
	 * Open Player - MUSIC
	 * <p>
	 * {@link PlayerReceiverActions#OPEN_MUSIC_PLAYER} Index
	 * </p>
	 */
	public static final int OPEN_MUSIC_PLAYER = 301;
	/**
	 * Open Player - VIDEO
	 * <p>
	 * {@link PlayerReceiverActions#OPEN_VIDEO_PLAYER} Index
	 * </p>
	 */
	public static final int OPEN_VIDEO_PLAYER = 302;
	/**
	 * Exit Player - MUSIC
	 * <p>
	 * {@link PlayerReceiverActions#EXIT_MUSIC} Index
	 * </p>
	 */
	public static final int EXIT_MUSIC = 401;
	/**
	 * Exit Player - VIDEO
	 * <p>
	 * {@link PlayerReceiverActions#EXIT_VIDEO} Index
	 * </p>
	 */
	public static final int EXIT_VIDEO = 402;
	/**
	 * Exit Player - by Open KUWO MUSIC
	 * <p>
	 * {@link PlayerReceiverActions#OPEN_KUWO_MUSIC} Index
	 * </p>
	 */
	public static final int OPEN_KUWO_MUSIC = 404;
	/**
	 * Exit Player - by Open BlueTooth MUSIC
	 * <p>
	 * {@link PlayerReceiverActions#OPEN_BT_MUSIC} Index
	 * </p>
	 */
	public static final int OPEN_BT_MUSIC = 405;

	/**
	 * Click FileManager Media to Play MUSIC
	 * <p>
	 * {@link PlayerReceiverActions#PLAY_MUSIC_BY_FILEMANAGER} Index
	 * </p>
	 * <p>
	 * Parameter1|int : “index” - [选中媒体所在数据列表位置]
	 * </p>
	 * <p>
	 * Parameter2|List<{@link String}> : “fileList” - [媒体路径数据列表]
	 * </p>
	 */
	public static final int PLAY_MUSIC_BY_FILEMANAGER = 501;
	/**
	 * Click FileManager Media to Play VIDEO
	 * <p>
	 * {@link PlayerReceiverActions#PLAY_VIDEO_BY_FILEMANAGER} Index
	 * </p>
	 * <p>
	 * Parameter1|int : “index” - [选中媒体所在数据列表位置]
	 * </p>
	 * <p>
	 * Parameter2|List<{@link String}> : “fileList” - [媒体路径数据列表]
	 * </p>
	 */
	public static final int PLAY_VIDEO_BY_FILEMANAGER = 502;

	/**
	 * ### Click [PLAY/PAUSE] on the Application ICON to Play Music ###
	 * <p>
	 * {@link PlayerReceiverActions#PLAY_MUSIC_BY_APPICON_ACT} Index
	 * </p>
	 * <p>
	 * Parameter|Integer : "action"[0准备执行暂停操作 , 1准备执行播放操作]
	 * </p>
	 * <p>
	 * Parameter|String : "path"[当前媒体的绝对路径]
	 * </p>
	 */
	public static final int PLAY_MUSIC_BY_APPICON_ACT = 601;
	/**
	 * ### AiSpeech [Search List]/[Send Searched List]/[Play Selected Music]###
	 * <p>
	 * {@link PlayerReceiverActions#AIS_SEARCH_MUSIC} Index
	 * </p>
	 * <p>
	 * Parameter1 : “params” – {“title”:”谁是大英雄” , “artist”,” 张学友”}
	 * </p>
	 */
	public static final int AIS_SEARCH_MUSIC = 701;
	/**
	 * ### AiSpeech [Play Selected Music]###
	 * <p>
	 * {@link PlayerReceiverActions#AIS_PLAY_SEARCHED_MUSIC} Index
	 * </p>
	 * <p>
	 * Parameter1 : “musicList” –{ "artist": "张学友", "duration": 0,"id": "620023", "size": 0,"title":
	 * "一路上有你","url": ""}
	 * </p>
	 */
	public static final int AIS_PLAY_SEARCHED_MUSIC = 702;

	/**
	 * ### AiSpeech Common Commands - PAUSE ###
	 * <p>
	 * {@link PlayerReceiverActions#AIS_PAUSE} Index
	 * </p>
	 */
	public static final int AIS_PAUSE = 801;
	/**
	 * ### AiSpeech Common Commands - RESUME ###
	 * <p>
	 * {@link PlayerReceiverActions#AIS_RESUME} Index
	 * </p>
	 */
	public static final int AIS_RESUME = 802;
	/**
	 * ### AiSpeech Common Commands - PREV ###
	 * <p>
	 * {@link PlayerReceiverActions#AIS_PREV} Index
	 * </p>
	 */
	public static final int AIS_PREV = 803;
	/**
	 * ### AiSpeech Common Commands - NEXT ###
	 * <p>
	 * {@link PlayerReceiverActions#AIS_NEXT} Index
	 * </p>
	 */
	public static final int AIS_NEXT = 804;

	// ### AiSpeech Operate Music Commands ###
	// (1)Select music from local folder browser
	// (2)Start play selected music
	// Music Mode Set
	public static final int MUSIC_MODE = 901;
	// Music AiSpeech
	public static final int MUSIC_AIS_RANDOM = 902;

	// ### AiSpeech Operate Video Commands ###
	// Video Pause/Resume/Previous/Next
	public static final int VIDEO_PAUSE = 1001;
	public static final int VIDEO_RESUME = 1002;
	public static final int VIDEO_PREV = 1003;
	public static final int VIDEO_NEXT = 1004;
	// Video Play Speed
	public static final int VIDEO_PLAY_NORMAL = 1101;
	public static final int VIDEO_PLAY_FORWARD = 1102;
	public static final int VIDEO_PLAY_BACKWARD = 1103;
	// Video Screen Size
	// Parameter1 : "VIEW_TYPE" – ["4to3","16to9","21to9","bigger","smaller"]
	public static final int VIDEO_RESIZE = 1201;
	public static final int VIDEO_FULL = 1202;
	// Video Show/Close PlayList
	public static final int VIDEO_SHOW_LIST = 1301;
	public static final int VIDEO_CLOSE_LIST = 1302;

	// ### BlueTooth Commands ###
	// BT Call Connect State Changed
	// Parameter1(Integer) : intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
	// - [BluetoothProfile.STATE_CONNECTED , BluetoothProfile.STATE_DISCONNECTED]
	public static final int BTCALL_SYS_CONN_STATE_CHANGED = 1501;
	// BT Call State Changed
	// Parameter1(BluetoothHeadsetClientCall) :
	// intent.getParcelableExtra(BluetoothHeadsetClient.EXTRA_CALL);
	// - getState()~[BluetoothHeadsetClientCall.CALL_STATE_TERMINATED]
	public static final int BTCALL_SYS_CHANGED = 1502;
	// BT Call IDLE
	public static final int BTCALL_IDLE_IN = 1503;
	public static final int BTCALL_IDLE_OUT = 1504;
	// BT Calling
	// Parameter1 : "number" - [要拨打的电话号码]
	public static final int BT_RINGING_TRICHEER_DIAL = 1505;
	public static final int BT_RINGING_IN_SPECIAL = 1506;
	public static final int BT_RINGING_IN = 1507;
	public static final int BT_RINGING_OUT = 1508;

	// ### Record State ###
	// Parameter|boolean : "state"[true,false]
	public static final int RECORD_STATECHANGE = 1601;

	// ### Cloud E-Dog ###
	public static final int E_DOG_PLAY_START = 1701;
	public static final int E_DOG_PLAY_END = 1702;

	// ### Screen ###
	// Screen Status
	// 点一下，屏不亮，关屏
	public static final int SCREEN_OFF = 1801;
	// 点两下，屏不亮，睡眠
	public static final int SCREEN_SLEEP = 1802;
	// 点三下，屏亮
	public static final int SCREEN_ON = 1803;
	// Screen Saver
	public static final int SCREEN_SAVER_EXIT = 1804;

	// ### System Temperature Mode ###
	public static final int SYS_TEMP_LOW = 1901;
	public static final int SYS_TEMP_HIGH = 1902;

	// ### Open Logs ###
	// Parameter|boolean : "IS_OPEN"[true,false]
	// adb shell am broadcast -a com.tricheer.app.OPEN_LOGS --ez "IS_OPEN" true
	public static final int OPEN_LOGS = 2001;
}
