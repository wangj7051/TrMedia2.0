package com.tricheer.player.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class SettingsSysUtil {
    //TAG
    private static final String TAG = "SettingsSysUtil";

    /**
     * Set music state
     * <p>0-未打开</p>
     * <p>1-打开</p>
     * <p>2-播放</p>
     */
    public static void setMusicState(Context context, int state) {
        try {
            Log.i(TAG, "setMusicState(Context," + state + ")");
            Settings.System.putInt(context.getApplicationContext().getContentResolver(), "app_music_state", state);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    /**
     * Set Radio state
     * <p>0-未打开</p>
     * <p>1-打开</p>
     * <p>2-播放</p>
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
