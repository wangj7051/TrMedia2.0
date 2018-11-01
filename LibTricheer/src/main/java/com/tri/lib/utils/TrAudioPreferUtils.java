package com.tri.lib.utils;

import js.lib.android.media.player.audio.utils.AudioPreferUtils;

public class TrAudioPreferUtils extends AudioPreferUtils {
    /**
     * Used to flag warning information
     * <p>
     * <p>0 "测试版本" - 不提示无U盘</p>
     * <p>1 "正式版本" - 提示无U盘</p>
     */
    public static int getNoUDiskToastFlag(boolean isSet) {
        final String preferKey = "AUDIO_PLAYER_NO_UDISK_TOAST_FLAG";
        int flag = getInt(preferKey, 1);
        if (isSet) {
            switch (flag) {
                case 1:
                    flag = 0;
                    break;
                case 0:
                    flag = 1;
                    break;
            }
            saveInt(preferKey, flag);
        }
        return flag;
    }
}
