package com.tricheer.radio.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class SettingsSysUtil {
    //TAG
    private static final String TAG = "SettingsSysUtil";

    /**
     * Set Radio state
     * <p>0-未打开</p>
     * <p>0-打开</p>
     * <p>0-播放</p>
     */
    public static void setFmState(Context context, int state) {
        try {
            Log.i(TAG, "setFmState(Context," + state + ")");
            Settings.System.putInt(context.getApplicationContext().getContentResolver(), "app_fm_state", state);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
