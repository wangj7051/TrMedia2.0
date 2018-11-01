package com.tri.lib.utils;

import js.lib.android.media.player.video.utils.VideoPreferUtils;

public class TrVideoPreferUtils extends VideoPreferUtils {
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

    /**
     * Used to flag warning information
     * <p>
     * <p>0 "测试版本" - 不提示无U盘</p>
     * <p>1 "正式版本" - 提示无U盘</p>
     */
    public static int getNoUDiskToastFlag(boolean isSet) {
        final String preferKey = "VIDEO_PLAYER_NO_UDISK_TOAST_FLAG";
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
