package com.tri.lib.utils;

import js.lib.android.utils.PreferenceHelper;

/**
 * Player Preference Helper
 *
 * @author Jun.Wang
 */
public class PreferUtils extends PreferenceHelper {
    // TAG
    //private static final String TAG = "PreferUtils";

    /**
     * Used to flag warning information
     * <p>
     * <p>1 “本次同意”---应用退出后，下次进入时继续提示，选择后进入应用</p>
     * <p>2 “本次不再提示”---没有熄火，则不再提示，选择后进入应用</p>
     */
    public static int getVideoWarningFlag(boolean isSet, int flag) {
        final String preferKey = "PLAYER_VIDEO_WARNING_FLAG";
        if (isSet) {
            saveInt(preferKey, flag);
        }
        return getInt(preferKey, 1);
    }
}
