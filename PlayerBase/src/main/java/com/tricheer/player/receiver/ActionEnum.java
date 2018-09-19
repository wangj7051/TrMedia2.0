package com.tricheer.player.receiver;

import java.util.HashMap;
import java.util.Map;

public enum ActionEnum {
    // ENUM - ACC Actions
    ACC_OFF(0, "tricheer.intent.action.ACC_OFF"),
    ACC_OFF_TRUE(1, "tricheer.intent.action.SLEEP"),
    ACC_ON(2, "tricheer.intent.action.ACC_ON");

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

        }
        return mMapEnums.get(action);
    }
}
