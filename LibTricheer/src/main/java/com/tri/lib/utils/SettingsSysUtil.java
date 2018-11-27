package com.tri.lib.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

/**
 * {@link Settings} util
 */
public class SettingsSysUtil {
    //TAG
    private static final String TAG = "SettingsSysUtil";

    /**
     * Set music state
     * <p>0-未打开</p>
     * <p>1-打开</p>
     * <p>2-播放</p>
     */
    public static void setAudioState(Context context, int state) {
        try {
            Log.i(TAG, "setAudioState(Context," + state + ")");
            Settings.System.putInt(context.getApplicationContext().getContentResolver(), "app_music_state", state);

            //
            int storedFlag = Settings.System.getInt(context.getContentResolver(), "app_music_state");
            Log.i(TAG, "AudioState: " + storedFlag);
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

    /**
     * Get theme value
     *
     * @return int-0 默认主题 ; 1 苹果主题
     */
    public static int getThemeVal(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "theme_setting", 0);
    }

    /**
     * Set player remember flag.
     */
    public static void setRememberFlag(Context context, boolean isRemember) {
        try {
            Log.i(TAG, "setRememberFlag(Context," + isRemember + ")");
            String flag = isRemember ? "music" : "";
            Settings.System.putString(context.getContentResolver(), "remembered_application", flag);

            //
            String storedFlag = Settings.System.getString(context.getContentResolver(), "remembered_application");
            Log.i(TAG, "RememberFlag: " + storedFlag);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
