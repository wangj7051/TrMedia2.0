package com.tri.lib.utils;

import java.util.HashSet;
import java.util.Set;

import js.lib.android.media.PlayMode;
import js.lib.android.utils.Logs;

/**
 * Player Preference Helper
 *
 * @author Jun.Wang
 */
public class PlayerPreferUtils extends PreferUtils {
    // TAG
    private static final String TAG = "PlayerPreferUtils -> ";

    /**
     * Get Last Music List selected part
     *
     * @param selectPartFlag : 1 my favor; 2 local musics
     */
    public static int getLastMusicListSelectPart(boolean isSet, int selectPartFlag) {
        final String PREFER_KEY = "PLAYER_MUSIC_LIST_SELECT_PART_FLAG";

        if (isSet) {
            saveInt(PREFER_KEY, selectPartFlag);
        }

        return getInt(PREFER_KEY, 2);
    }

    /**
     * Get audio play mode
     *
     * @param isSet    true - Cache mode value.
     * @param playMode {@link PlayMode}
     * @return js.lib.android.media.PlayMode
     */
    public static PlayMode getAudioPlayMode(boolean isSet, PlayMode playMode) {
        final String PREFER_KEY = "AUDIO_PLAY_MODE";
        if (isSet) {
            saveInt(PREFER_KEY, playMode.getValue());
        }
        return PlayMode.getMode(getInt(PREFER_KEY, PlayMode.NONE.getValue()));
    }

    /**
     * Get Last Opened Music Path
     */
    public static String getLastMusicPath(boolean isSet, String path) {
        final String PREFER_KEY = "PLAYER_MUSIC_LAST_PATH";

        if (isSet) {
            saveString(PREFER_KEY, path);
        }

        //
        String storedPath = "/";
        try {
            storedPath = getString(PREFER_KEY, "/");
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getLastMusicPath()", e);
        }

        return storedPath;
    }

    /**
     * Get Last Opened Video Path
     */
    public static String getLastVideoPath(boolean isSet, String path) {
        final String PREFER_KEY = "PLAYER_VIDEO_LAST_PATH";

        if (isSet) {
            saveString(PREFER_KEY, path);
        }

        //
        String storedPath = "/";
        try {
            storedPath = getString(PREFER_KEY, "/");
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getLastVideoPath()", e);
        }

        return storedPath;
    }

    /**
     * Set volume mute mode
     */
    public static boolean getMusicMute(boolean isSet, boolean isMuted) {
        final String PREFER_KEY = "MUSIC_VOL_MUTE";

        if (isSet) {
            saveBoolean(PREFER_KEY, isMuted);
        }

        //
        boolean storedMuted = false;
        try {
            storedMuted = getBoolean(PREFER_KEY, false);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getMusicMute()", e);
        }

        return storedMuted;
    }

    /**
     * Store or get Music Volume
     */
    public static int getMusicVolVal(boolean isSet, int volVal, int maxVolVal) {
        final String PREFER_KEY = "MUSIC_VOL_VALUE";

        if (isSet) {
            saveInt(PREFER_KEY, volVal);
        }

        //
        int storedVol = maxVolVal / 2;
        try {
            storedVol = getInt(PREFER_KEY, maxVolVal / 2);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getMusicVolVal()", e);
        }

        return storedVol;
    }

    /**
     * Store or get Music Volume
     */
    public static int getMusicVolVal(boolean isSet, int volVal) {
        final String PREFER_KEY = "MUSIC_RESUME_VOL_VALUE";

        if (isSet) {
            saveInt(PREFER_KEY, volVal);
        }

        //
        int storedVol = -1;
        try {
            storedVol = getInt(PREFER_KEY, -1);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getMusicVolVal()", e);
        }

        return storedVol;
    }

    /**
     * Store or Get Video Cache Info
     */
    public static String[] getCacheVideoPlayInfo(boolean isSet, String[] cacheInfos) {
        final int ARR_LEN = 3;
        final String PREFER_KEY_VIDEO_URL = "LAST_VIDEO_MEDIA_URL";
        final String PREFER_KEY_VIDEO_SEEK = "LAST_VIDEO_SEEK";
        final String PREFER_KEY_VIDEO_STATUS = "LAST_VIDEO_PLAY_STATUS";

        if (isSet) {
            if (cacheInfos == null || cacheInfos.length == ARR_LEN) {
                saveString(PREFER_KEY_VIDEO_URL, cacheInfos[0]);
                saveString(PREFER_KEY_VIDEO_SEEK, cacheInfos[1]);
                saveString(PREFER_KEY_VIDEO_STATUS, cacheInfos[2]);
            }
        }

        //
        String[] resCacheInfos = new String[]{"", "0", "0"};
        try {
            resCacheInfos[0] = getString(PREFER_KEY_VIDEO_URL, "");
            resCacheInfos[1] = getString(PREFER_KEY_VIDEO_SEEK, "0");
            resCacheInfos[2] = getString(PREFER_KEY_VIDEO_STATUS, "0");
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getCacheVideoPlayInfo()", e);
        }

        return resCacheInfos;
    }

    /**
     * Is Music has showed guide gesture?
     */
    public static int isMusicGuided(boolean isSet) {
        final String PREFER_KEY = "GUIDE_FLAG_MUSIC";

        if (isSet) {
            saveInt(PREFER_KEY, 1);
        }

        //
        int isGudied = 0;
        try {
            isGudied = getInt(PREFER_KEY, 0);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "isMusicGuided()", e);
        }

        return isGudied;
    }

    /**
     * Is Video has showed guide gesture?
     */
    public static int isVideGuided(boolean isSet) {
        final String PREFER_KEY = "GUIDE_FLAG_VIDEO";

        if (isSet) {
            saveInt(PREFER_KEY, 1);
        }

        //
        int isGudied = 0;
        try {
            isGudied = getInt(PREFER_KEY, 0);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "isVideGuided()", e);
        }

        return isGudied;
    }

    /**
     * Is Radio has showed guide gesture?
     */
    public static int isRadioGuided(boolean isSet) {
        final String PREFER_KEY = "GUIDE_FLAG_RADIO";

        if (isSet) {
            saveInt(PREFER_KEY, 1);
        }

        //
        int isGudied = 0;
        try {
            isGudied = getInt(PREFER_KEY, 0);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "isRadioGuided()", e);
        }

        return isGudied;
    }

    /**
     * Get PlayerFlag that playing Before Shutdown
     */
    public static int getPlayerFlagOfShutdown(boolean isSet, int playerFlag) {
        final String PREFER_KEY = "PLAYER_FLAG_OF_SHUT_DOWN";

        if (isSet) {
            saveInt(PREFER_KEY, playerFlag);
        }

        //
        try {
            playerFlag = getInt(PREFER_KEY, -1);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getPlayerFlagOfShutdown()", e);
        }

        return playerFlag;
    }

    /**
     * Last Target MediaUrl to play
     */
    public static String getLastTargetMediaUrl(boolean isSet, int playerFlag, String mediaUrl) {
        final String PREFER_KEY_MEDIA_URL = playerFlag + "_LAST_TARGET_MEDIA_URL";
        if (isSet) {
            saveString(PREFER_KEY_MEDIA_URL, mediaUrl);
        }
        return getString(PREFER_KEY_MEDIA_URL, "");
    }

    /**
     * Last Played Media Information
     *
     * @return String[] : [0] mediaUrl,[1]progress
     */
    public static String[] getLastPlayedMediaInfo(boolean isSet, int playerFlag, String mediaUrl, int progress) {
        final String PREFER_KEY_MEDIA_URL = playerFlag + "_LAST_PLAYED_MEDIA_URL";
        final String PREFER_KEY_MEDIA_PROGRESS = playerFlag + "_LAST_PLAYED_MEDIA_PROGRESS";
        if (isSet) {
            saveString(PREFER_KEY_MEDIA_URL, mediaUrl);
            saveInt(PREFER_KEY_MEDIA_PROGRESS, progress);
        }

        String[] mediaInfos = new String[2];
        mediaInfos[0] = getString(PREFER_KEY_MEDIA_URL, "");
        mediaInfos[1] = getInt(PREFER_KEY_MEDIA_PROGRESS, 0).toString();
        return mediaInfos;
    }

    /**
     * Get Listed Audio Set
     */
    public static Set<String> getListedAudioSet(boolean isSet, Set<String> setAudios) {
        final String preferKey = "PLAYER_LISTED_AUDIO_SET";
        if (isSet) {
            saveStringSet(preferKey, setAudios);
        }
        return getStringSet(preferKey, new HashSet<String>());
    }

    /**
     * Get Listed Video Set
     */
    public static Set<String> getListedVideoSet(boolean isSet, Set<String> setVideo) {
        final String preferKey = "PLAYER_LISTED_VIDEO_SET";
        if (isSet) {
            saveStringSet(preferKey, setVideo);
        }
        return getStringSet(preferKey, new HashSet<String>());
    }

    /**
     * 上一次是否设备彻底休眠了，即是否收到了广播 "android.hardware.input.action.POWER_DISCONNECTED"
     */
    public static boolean isLastPowerDisconned(boolean isSet, boolean isDisconned) {
        final String PREFER_KEY = "IS_LAST_POWER_DISCONN";
        if (isSet) {
            saveBoolean(PREFER_KEY, isDisconned);
        }
        boolean isLastPowerDisconned = getBoolean(PREFER_KEY, false);
        if (!isSet && isLastPowerDisconned) {
            saveBoolean(PREFER_KEY, false);
        }
        return isLastPowerDisconned;
    }

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
