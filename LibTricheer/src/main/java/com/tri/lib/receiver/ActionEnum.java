package com.tri.lib.receiver;

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
    PLAY_MUSIC_BY_FILEMANAGER(20,"com.tricheer.music.PLAY_FROM_FILEMANAGER"),
    // Parameter1 : "index" - [选中媒体所在数据列表位置]
    // Parameter2 : "fileList" - [媒体路径数据列表]
    PLAY_VIDEO_BY_FILEMANAGER(21,"com.tricheer.video.PLAY_FROM_FILEMANAGER"),

    /**
     * ### Open Logs ###
     * <p>
     * Parameter1(boolean) : "IS_OPEN"[true,false]
     */
    OPEN_LOGS(30,"com.tricheer.app.OPEN_LOGS"),

    // ENUM - Test Actions
    TEST_OPEN_VIDEO_LIST(100000, "com.tri.test.OPEN_AUDIO"),
    TEST_OPEN_VIDEO(100002, "com.tri.test.OPEN_VIDEO"),
    TEST_OPEN_AUDIO(200000, "com.tri.test.OPEN_AUDIO"),
    TEST_EXIT_PLAYER(999999, "com.tri.test.EXIT_PLAYER");

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

            mMapEnums.put(PLAY_MUSIC_BY_FILEMANAGER.getAction(), PLAY_MUSIC_BY_FILEMANAGER);
            mMapEnums.put(PLAY_VIDEO_BY_FILEMANAGER.getAction(), PLAY_VIDEO_BY_FILEMANAGER);
            mMapEnums.put(OPEN_LOGS.getAction(), OPEN_LOGS);

            // ENUM - Test Actions
            mMapEnums.put(TEST_OPEN_VIDEO_LIST.getAction(), TEST_OPEN_VIDEO_LIST);
            mMapEnums.put(TEST_OPEN_VIDEO.getAction(), TEST_OPEN_VIDEO);
            mMapEnums.put(TEST_OPEN_AUDIO.getAction(), TEST_OPEN_AUDIO);
            mMapEnums.put(TEST_EXIT_PLAYER.getAction(), TEST_EXIT_PLAYER);
        }
        return mMapEnums.get(action);
    }
}
