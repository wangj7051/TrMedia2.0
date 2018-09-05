package com.tricheer.app.receiver;

import java.util.HashMap;
import java.util.Map;

import android.bluetooth.BluetoothHeadsetClient;
import android.content.Intent;
import android.media.AudioManager;

/**
 * 广播 Actions
 * 
 * @author Jun.Wang
 */
public class PlayerReceiverActions implements BaseActions {
	// ### System Power Off/On ###
	public static final String SYS_AUDIO_NOISY = AudioManager.ACTION_AUDIO_BECOMING_NOISY;
	public static final String SYS_BOOT_COMPLETED = Intent.ACTION_BOOT_COMPLETED;

	// ### ACC OFF/ON ###
	public static final String SYS_SHUTDOWN = Intent.ACTION_SHUTDOWN;
	public static final String SELF_BOOT_COMPLETED = "com.tricheer.BOOT_COMPLETED";

	// ### AIOS Operate ###
	public static final String AIOS_OPERATE = "com.tricheer.media.action";

	// ### Open Player ###
	public static final String OPEN_MUSIC_PLAYER = "com.tricheer.player.OPEN_MUSIC_PLAYER";
	public static final String OPEN_VIDEO_PLAYER = "com.tricheer.player.OPEN_VIDEO_PLAYER";

	// ### Exit Player ###
	public static final String EXIT_MUSIC = "com.tricheer.music.ACTION_MUSIC_EXIT";
	public static final String EXIT_VIDEO = "com.tricheer.video.close";
	public static final String OPEN_KUWO_MUSIC = "com.tricheer.kuwo.music.open";
	public static final String OPEN_BT_MUSIC = "com.tricheer.bluetooth.music.open";

	// ### Click FileManager Media to Play ###
	// Parameter1 : "index" - [选中媒体所在数据列表位置]
	// Parameter2 : "fileList" - [媒体路径数据列表]
	public static final String PLAY_MUSIC_BY_FILEMANAGER = "com.tricheer.music.PLAY_FROM_FILEMANAGER";
	// Parameter1 : "index" - [选中媒体所在数据列表位置]
	// Parameter2 : "fileList" - [媒体路径数据列表]
	public static final String PLAY_VIDEO_BY_FILEMANAGER = "com.tricheer.video.PLAY_FROM_FILEMANAGER";

	// ### Click [PLAY/PAUSE] on the Application ICON to Play ###
	// Parameter|Integer : "action"[0准备执行暂停操作 , 1准备执行播放操作]
	// Parameter|String : "path"[当前媒体的绝对路径]
	public static final String PLAY_MUSIC_BY_APPICON_ACT = "com.tricheer.music.PLAY_FROM_LAUNCHER";

	// ### AiSpeech [Search List]/[Send Searched List]/[Play Selected Music]###
	// Parameter1 : "params" – {"title":"谁是大英雄" , "artist"," 张学友"}
	public static final String AIS_SEARCH_MUSIC = "com.aispeech.aios.music.AICTION_SEARCH_BY_TITLE";
	// "musicList" –{ "artist": "张学友", "duration": 0,"id": "620023", "size": 0,
	// "title": "一路上有你","url": ""}
	public static final String AIS_PLAY_SEARCHED_MUSIC = "com.aispeech.aios.music.AICTION_SONG_SELECTED";
	// ### [**NOTIFY**] AIS Searched Music List ###
	// Parameter1 : "musicList" – [搜索到的列表转换的JSON数组]
	// Parameter2 : "keyword" – [搜索关键字]
	public static final String NOTIFY_AIS_SEARCHED_MUSIC_LIST = "com.aispeech.aios.adapter.ACTION_SEARCH_BY_TITLE";

	// ### AiSpeech Common Commands ###
	public static final String AIS_PAUSE = "com.aispeech.aios.music.ACTION_MUSIC_PAUSE";
	public static final String AIS_RESUME = "com.aispeech.aios.music.ACTION_MUSIC_RESUME";
	public static final String AIS_PREV = "com.aispeech.aios.music.ACTION_MUSIC_PREVIOUS";
	public static final String AIS_NEXT = "com.aispeech.aios.music.ACTION_MUSIC_NEXT";

	// ### AiSpeech Operate Music Commands ###
	// (1)Select music from local folder browser
	// (2)Start play selected music
	// Music Mode Set
	public static final String MUSIC_MODE = "com.tricheer.MUSIC.MODE";
	// Music AiSpeech
	public static final String MUSIC_AIS_RANDOM = "com.aispeech.aios.adapter.ACTION_RANDOM";

	// ### AiSpeech Operate Video Commands ###
	// Video Pause/Resume/Previous/Next
	public static final String VIDEO_PAUSE = "com.tricheer.video.PAUSE";
	public static final String VIDEO_RESUME = "com.tricheer.video.PLAY";
	public static final String VIDEO_PREV = "com.tricheer.VIDEO.PLAY_PRE";
	public static final String VIDEO_NEXT = "com.tricheer.VIDEO.PLAY_NEXT";
	// Video Play Speed
	public static final String VIDEO_PLAY_NORMAL = "com.tricheer.video.PLAY_NORMAL";
	public static final String VIDEO_PLAY_FORWARD = "com.tricheer.video.FAST_FORWARD";
	public static final String VIDEO_PLAY_BACKWARD = "com.tricheer.video.FAST_REWIND";
	// Video Screen Size
	// Parameter1 : "VIEW_TYPE" – ["4to3","16to9","21to9","bigger","smaller"]
	public static final String VIDEO_RESIZE = "com.tricheer.video.VIEW_CHENGED";
	public static final String VIDEO_FULL = "com.tricheer.video.FULL_SCREEN";
	// Video Show/Close PlayList
	public static final String VIDEO_SHOW_LIST = "com.tricheer.show.VIDEO_LIST";
	public static final String VIDEO_CLOSE_LIST = "com.tricheer.close.VIDEO_LIST";

	// ### BlueTooth Commands ###
	// BT Call Connect State Changed
	// Parameter1(Integer) : intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
	// - [BluetoothProfile.STATE_CONNECTED , BluetoothProfile.STATE_DISCONNECTED]
	public static final String BTCALL_SYS_CONN_STATE_CHANGED = BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED;
	// BT Call State Changed
	// Parameter1(BluetoothHeadsetClientCall) :
	// intent.getParcelableExtra(BluetoothHeadsetClient.EXTRA_CALL);
	// - getState()~[BluetoothHeadsetClientCall.CALL_STATE_TERMINATED]
	public static final String BTCALL_SYS_CHANGED = BluetoothHeadsetClient.ACTION_CALL_CHANGED;
	// BT Call IDLE
	public static final String BTCALL_IDLE_IN = "action.bluetooth.TRICHEER_INCOMING_IDLE";
	public static final String BTCALL_IDLE_OUT = "action.bluetooth.TRICHEER_OUTGOING_IDLE";
	// BT Calling
	// Parameter1 : "number" - [要拨打的电话号码]
	public static final String BT_RINGING_TRICHEER_DIAL = "action.intent.TRICHEER_PHONE_DIAL";
	public static final String BT_RINGING_IN_SPECIAL = "com.tricheer.bluetoothcarkit.INCOMING_RINGING_FOR_PLAYER";
	public static final String BT_RINGING_IN = "action.bluetooth.TRICHEER_INCOMING_RINGING";
	public static final String BT_RINGING_OUT = "action.bluetooth.TRICHEER_OUTGOING_RINGING";

	// ### Record State ###
	// Parameter|boolean : "state"[true,false]
	public static final String RECORD_STATECHANGE = "action.camera.STATE_CHANGE";

	// ### Cloud E-Dog ###
	public static final String E_DOG_PLAY_START = "com.tricheer.detector.playsound.start";
	public static final String E_DOG_PLAY_END = "com.tricheer.detector.playsound.end";

	// ### Screen ###
	// Screen Status
	// 点一下，屏不亮，关屏
	public static final String SCREEN_OFF = "com.tricheer.screen_on";
	// 点两下，屏不亮，睡眠
	public static final String SCREEN_SLEEP = "com.tricheer.screen_off";
	// 点三下，屏亮
	public static final String SCREEN_ON = "com.tricheer.screen_sleep";
	// Screen Saver
	public static final String SCREEN_SAVER_EXIT = "action.maskapp.EXIT";

	// ### System Temperature Mode ###
	public static final String SYS_TEMP_LOW = "com.tricheer.hightemp1";
	public static final String SYS_TEMP_HIGH = "com.tricheer.lowtemp1";

	// ### Stop All Medias ###
	// Parameter1(String) : "processname" ~ [程序包名]
	// 接到该广播表示，第三方音频打开了，此时要退出播放器，类似于播放器失去了声音焦点
	public static final String STOP_ALL_MEDIA = "com.tricheer.STOP_ALL_MEDIA";

	// ### [**NOTIFY**] Video Width ###
	// Parameter1(int) : "size" ~ [分辨率值，大于等于1000认为是1080P]
	public static final String NOTIFY_VIDEO_SIZE = "action.player.VIDEO_SIZE";

	// ### [**NOTIFY**] Configuration RECORD ###
	// Parameter1(boolean) : "start" –[true,false]
	public static final String NOTIFY_CONFIG_RECORD = "action.camera.ACTION_CONFIG_RECORD";

	// ### [**NOTIFY**] AiSpeech To Broadcast String ###
	// Parameter1(String): "NOTIFY_PLAY_STR" –[需要语音播报的字符串]
	public static final String NOTIFY_AIS_PLAY_STR = "com.notify.ais.play.str";

	// ### [**NOTIFY**] Media Player Opened ###
	// Parameter1(String): "PLAYER_FLAG" –[MUSIC_PLAYER / VIDEO_PLAYER]
	public static final String MEDIA_PLAYER_OPEN = "com.tricheer.media.player.open";

	private static Map<String, Integer> mMapActionTypes = new HashMap<String, Integer>();
	static {
		// ### System Power Off/On ###
		mMapActionTypes.put(SYS_AUDIO_NOISY, PlayerReceiverActionIdxs.SYS_AUDIO_NOISY);
		mMapActionTypes.put(SYS_BOOT_COMPLETED, PlayerReceiverActionIdxs.SYS_BOOT_COMPLETED);

		// ### ACC OFF/ON ###
		mMapActionTypes.put(SYS_SHUTDOWN, PlayerReceiverActionIdxs.SYS_SHUTDOWN);
		mMapActionTypes.put(SELF_BOOT_COMPLETED, PlayerReceiverActionIdxs.SELF_BOOT_COMPLETED);

		// ### AIS Window Status ###
		mMapActionTypes.put(AIS_STATUS, PlayerReceiverActionIdxs.AIS_STATUS);
		// ### AIOS Operate ###
		mMapActionTypes.put(AIOS_OPERATE, PlayerReceiverActionIdxs.AIOS_OPERATE);

		// ### Open Player ###
		mMapActionTypes.put(OPEN_MUSIC_PLAYER, PlayerReceiverActionIdxs.OPEN_MUSIC_PLAYER);
		mMapActionTypes.put(OPEN_VIDEO_PLAYER, PlayerReceiverActionIdxs.OPEN_VIDEO_PLAYER);

		// ### Exit Player ###
		mMapActionTypes.put(EXIT_MUSIC, PlayerReceiverActionIdxs.EXIT_MUSIC);
		mMapActionTypes.put(EXIT_VIDEO, PlayerReceiverActionIdxs.EXIT_VIDEO);
		mMapActionTypes.put(OPEN_KUWO_MUSIC, PlayerReceiverActionIdxs.OPEN_KUWO_MUSIC);
		mMapActionTypes.put(OPEN_BT_MUSIC, PlayerReceiverActionIdxs.OPEN_BT_MUSIC);

		// ### Click FileManager Media to Play ###
		mMapActionTypes.put(PLAY_MUSIC_BY_FILEMANAGER, PlayerReceiverActionIdxs.PLAY_MUSIC_BY_FILEMANAGER);
		mMapActionTypes.put(PLAY_VIDEO_BY_FILEMANAGER, PlayerReceiverActionIdxs.PLAY_VIDEO_BY_FILEMANAGER);

		// ### Click [PLAY/PAUSE] on the Application ICON to Play ###
		mMapActionTypes.put(PLAY_MUSIC_BY_APPICON_ACT, PlayerReceiverActionIdxs.PLAY_MUSIC_BY_APPICON_ACT);

		// ### AiSpeech [Search List]/[Send Searched List]/[Play Selected Music] ###
		mMapActionTypes.put(AIS_SEARCH_MUSIC, PlayerReceiverActionIdxs.AIS_SEARCH_MUSIC);
		mMapActionTypes.put(AIS_PLAY_SEARCHED_MUSIC, PlayerReceiverActionIdxs.AIS_PLAY_SEARCHED_MUSIC);

		// ### AiSpeech Common Commands ###
		mMapActionTypes.put(AIS_PAUSE, PlayerReceiverActionIdxs.AIS_PAUSE);
		mMapActionTypes.put(AIS_RESUME, PlayerReceiverActionIdxs.AIS_RESUME);
		mMapActionTypes.put(AIS_PREV, PlayerReceiverActionIdxs.AIS_PREV);
		mMapActionTypes.put(AIS_NEXT, PlayerReceiverActionIdxs.AIS_NEXT);

		// ### AiSpeech Operate Music Commands ###
		mMapActionTypes.put(MUSIC_MODE, PlayerReceiverActionIdxs.MUSIC_MODE);
		mMapActionTypes.put(MUSIC_AIS_RANDOM, PlayerReceiverActionIdxs.MUSIC_AIS_RANDOM);

		// ### AiSpeech Operate Video Commands ###
		// Video Pause/Resume/Previous/Next
		mMapActionTypes.put(VIDEO_PAUSE, PlayerReceiverActionIdxs.VIDEO_PAUSE);
		mMapActionTypes.put(VIDEO_RESUME, PlayerReceiverActionIdxs.VIDEO_RESUME);
		mMapActionTypes.put(VIDEO_PREV, PlayerReceiverActionIdxs.VIDEO_PREV);
		mMapActionTypes.put(VIDEO_NEXT, PlayerReceiverActionIdxs.VIDEO_NEXT);
		// Video Play Speed
		mMapActionTypes.put(VIDEO_PLAY_NORMAL, PlayerReceiverActionIdxs.VIDEO_PLAY_NORMAL);
		mMapActionTypes.put(VIDEO_PLAY_FORWARD, PlayerReceiverActionIdxs.VIDEO_PLAY_FORWARD);
		mMapActionTypes.put(VIDEO_PLAY_BACKWARD, PlayerReceiverActionIdxs.VIDEO_PLAY_BACKWARD);
		// Video Screen Size
		mMapActionTypes.put(VIDEO_RESIZE, PlayerReceiverActionIdxs.VIDEO_RESIZE);
		mMapActionTypes.put(VIDEO_FULL, PlayerReceiverActionIdxs.VIDEO_FULL);
		// Video Show/Close PlayList
		mMapActionTypes.put(VIDEO_SHOW_LIST, PlayerReceiverActionIdxs.VIDEO_SHOW_LIST);
		mMapActionTypes.put(VIDEO_CLOSE_LIST, PlayerReceiverActionIdxs.VIDEO_CLOSE_LIST);

		// ### BlueTooth Commands ###
		// BT Connect Status
		mMapActionTypes.put(BTCALL_SYS_CONN_STATE_CHANGED, PlayerReceiverActionIdxs.BTCALL_SYS_CONN_STATE_CHANGED);
		// BT Call IDLE
		mMapActionTypes.put(BTCALL_SYS_CHANGED, PlayerReceiverActionIdxs.BTCALL_SYS_CHANGED);
		mMapActionTypes.put(BTCALL_IDLE_IN, PlayerReceiverActionIdxs.BTCALL_IDLE_IN);
		mMapActionTypes.put(BTCALL_IDLE_OUT, PlayerReceiverActionIdxs.BTCALL_IDLE_OUT);
		// BT Calling
		mMapActionTypes.put(BT_RINGING_TRICHEER_DIAL, PlayerReceiverActionIdxs.BT_RINGING_TRICHEER_DIAL);
		mMapActionTypes.put(BT_RINGING_IN_SPECIAL, PlayerReceiverActionIdxs.BT_RINGING_IN_SPECIAL);
		mMapActionTypes.put(BT_RINGING_IN, PlayerReceiverActionIdxs.BT_RINGING_IN);
		mMapActionTypes.put(BT_RINGING_OUT, PlayerReceiverActionIdxs.BT_RINGING_OUT);

		// ### Record State ###
		mMapActionTypes.put(RECORD_STATECHANGE, PlayerReceiverActionIdxs.RECORD_STATECHANGE);

		// ### Cloud E-Dog ###
		mMapActionTypes.put(E_DOG_PLAY_START, PlayerReceiverActionIdxs.E_DOG_PLAY_START);
		mMapActionTypes.put(E_DOG_PLAY_END, PlayerReceiverActionIdxs.E_DOG_PLAY_END);

		// ### Screen Status ###
		// Screen Status
		mMapActionTypes.put(SCREEN_ON, PlayerReceiverActionIdxs.SCREEN_ON);
		mMapActionTypes.put(SCREEN_OFF, PlayerReceiverActionIdxs.SCREEN_OFF);
		mMapActionTypes.put(SCREEN_SLEEP, PlayerReceiverActionIdxs.SCREEN_SLEEP);
		// Screen Saver
		mMapActionTypes.put(SCREEN_SAVER_EXIT, PlayerReceiverActionIdxs.SCREEN_SAVER_EXIT);

		// ### System Temperature Mode ###
		mMapActionTypes.put(SYS_TEMP_HIGH, PlayerReceiverActionIdxs.SYS_TEMP_HIGH);
		mMapActionTypes.put(SYS_TEMP_LOW, PlayerReceiverActionIdxs.SYS_TEMP_LOW);

		// ### Open Logs ###
		mMapActionTypes.put(OPEN_LOGS, PlayerReceiverActionIdxs.OPEN_LOGS);
	}

	/**
	 * 获取Action索引
	 * 
	 * @param action
	 *            : Action字符串
	 */
	public static int getActionIdx(String action) {
		Integer type = mMapActionTypes.get(action);
		if (type != null) {
			return type;
		}
		return -1;
	}
}
