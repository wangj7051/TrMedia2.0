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

            // ENUM - Test Actions
            mMapEnums.put(TEST_OPEN_VIDEO_LIST.getAction(), TEST_OPEN_VIDEO_LIST);
            mMapEnums.put(TEST_OPEN_VIDEO.getAction(), TEST_OPEN_VIDEO);
            mMapEnums.put(TEST_OPEN_AUDIO.getAction(), TEST_OPEN_AUDIO);
            mMapEnums.put(TEST_EXIT_PLAYER.getAction(), TEST_EXIT_PLAYER);
        }
        return mMapEnums.get(action);
    }
}
