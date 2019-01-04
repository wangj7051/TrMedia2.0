package com.tri.lib.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.tri.lib.engine.MediaTypeEnum;

/**
 * {@link Settings} util
 */
public class SettingsGlobalUtil {
    //TAG
    private static final String TAG = "SettingsSysUtil";

    /**
     * Get BT call state
     */
    public static int getBtCallState(Context context) {
        try {
            return Settings.Global.getInt(context.getContentResolver(), "bt_call_status", -1);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
        return -1;
    }
}
