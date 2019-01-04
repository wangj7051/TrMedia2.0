package com.tri.lib.receiver;

import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public enum ActionEnum {
    // ENUM - ACC Actions
    ACC_OFF(0, "tricheer.intent.action.ACC_OFF"),
    ACC_OFF_TRUE(1, "tricheer.intent.action.SLEEP"),
    ACC_ON(2, "tricheer.intent.action.ACC_ON"),

    // ENUM - Car reverse Actions
    REVERSE_ON(10, "com.tricheer.carback.on"),
    REVERSE_OFF(11, "com.tricheer.carback.off"),

    // ### Click FileManager Media to Play ###
    // Parameter1 : "index" - [选中媒体所在数据列表位置]
    // Parameter2 : "fileList" - [媒体路径数据列表]
    PLAY_MUSIC_BY_FILEMANAGER(90, "com.tricheer.music.PLAY_FROM_FILEMANAGER"),
    // Parameter1 : "index" - [选中媒体所在数据列表位置]
    // Parameter2 : "fileList" - [媒体路径数据列表]
    PLAY_VIDEO_BY_FILEMANAGER(91, "com.tricheer.video.PLAY_FROM_FILEMANAGER"),

    //Media Switch
    MEDIA_SWITCH_BY_SOURCE(100, "tricheer.intent.action.SWC_SOURCE_CHANGE"),
    MEDIA_SWITCH_BY_MEDIA(100, "tricheer.intent.action.MEDIA_SWITCH"),

    //Voice assistant
    MEDIA_EXIT_AUDIO(110, "com.tricheer.audio.EXIT"),
    MEDIA_AUDIO_OPEN_AND_PLAY(112, "com.tricheer.audio.OPEN_PLAY"),
    MEDIA_EXIT_VIDEO(111, "com.tricheer.video.EXIT"),
    MEDIA_PLAY_PREV(112, "com.tricheer.MEDIA_PREV"),
    MEDIA_PLAY_NEXT(113, "com.tricheer.MEDIA_NEXT"),
    MEDIA_PLAY(114, "com.tricheer.MEDIA_PLAY"),
    MEDIA_PAUSE(115, "com.tricheer.MEDIA_STOP"),
    //parameter "new_freq" fm-108.0 /am-1620
    MEDIA_RADIO_SET_FREQ(115, "com.tricheer.radio.SET_FREQ"),

    // System Broadcast
    BOOT_COMPLETED(10000, Intent.ACTION_BOOT_COMPLETED),
    SCREEN_ON(11000, Intent.ACTION_SCREEN_ON),
    SCREEN_OFF(11001, Intent.ACTION_SCREEN_OFF),

    /**
     * ### {@link js.lib.android.utils.Logs} enable switch ###
     */
    OPEN_LOGS(30, "com.yj.app.OPEN_LOGS");

    private int mIdx;
    private String mAction;

    ActionEnum(int idx, String action) {
        mIdx = idx;
        mAction = action;
    }

    public int getIdx() {
        return mIdx;
    }

    public String getAction() {
        return mAction;
    }

    private static Map<String, ActionEnum> mMapEnums = null;

    public static ActionEnum getByAction(String action) {
        if (mMapEnums == null) {
            mMapEnums = new HashMap<>();
            // ENUM - ACC Actions
            mMapEnums.put(ACC_OFF.getAction(), ACC_OFF);
            mMapEnums.put(ACC_OFF_TRUE.getAction(), ACC_OFF_TRUE);
            mMapEnums.put(ACC_ON.getAction(), ACC_ON);

            // ENUM - Car reverse Actions
            mMapEnums.put(REVERSE_ON.getAction(), REVERSE_ON);
            mMapEnums.put(REVERSE_OFF.getAction(), REVERSE_OFF);

            // ENUM - Open by file manager
            mMapEnums.put(PLAY_MUSIC_BY_FILEMANAGER.getAction(), PLAY_MUSIC_BY_FILEMANAGER);
            mMapEnums.put(PLAY_VIDEO_BY_FILEMANAGER.getAction(), PLAY_VIDEO_BY_FILEMANAGER);

            // ENUM - Media Switch
            mMapEnums.put(MEDIA_SWITCH_BY_SOURCE.getAction(), MEDIA_SWITCH_BY_SOURCE);
            mMapEnums.put(MEDIA_SWITCH_BY_MEDIA.getAction(), MEDIA_SWITCH_BY_MEDIA);

            //Voice assistant
            mMapEnums.put(MEDIA_EXIT_AUDIO.getAction(), MEDIA_EXIT_AUDIO);
            mMapEnums.put(MEDIA_AUDIO_OPEN_AND_PLAY.getAction(), MEDIA_AUDIO_OPEN_AND_PLAY);
            mMapEnums.put(MEDIA_EXIT_VIDEO.getAction(), MEDIA_EXIT_VIDEO);
            mMapEnums.put(MEDIA_PLAY_PREV.getAction(), MEDIA_PLAY_PREV);
            mMapEnums.put(MEDIA_PLAY_NEXT.getAction(), MEDIA_PLAY_NEXT);
            mMapEnums.put(MEDIA_PLAY.getAction(), MEDIA_PLAY);
            mMapEnums.put(MEDIA_PAUSE.getAction(), MEDIA_PAUSE);
            //parameter "new_freq" fm-108.0 /am-1620
            mMapEnums.put(MEDIA_RADIO_SET_FREQ.getAction(), MEDIA_RADIO_SET_FREQ);

            // ENUM - System broadcast
            // Boot
            mMapEnums.put(OPEN_LOGS.getAction(), OPEN_LOGS);

            // ENUM - Open Logs
            mMapEnums.put(BOOT_COMPLETED.getAction(), BOOT_COMPLETED);
        }
        return mMapEnums.get(action);
    }
}
