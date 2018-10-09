package js.lib.android.media.video;

import js.lib.android.media.PlayMode;
import js.lib.android.utils.PreferenceHelper;

/**
 * Media preference
 *
 * @author Jun.Wang
 */
public class VideoPreferUtils extends PreferenceHelper {
    /**
     * Get play mode
     *
     * @param isSet    true - Cache mode value.
     * @param playMode {@link PlayMode}
     * @return js.lib.android.media.PlayMode
     */
    public static PlayMode getPlayMode(boolean isSet, PlayMode playMode) {
        final String PREFER_KEY = "VIDEO_PLAY_MODE";
        if (isSet) {
            saveInt(PREFER_KEY, playMode.getValue());
        }
        int modeVal = getInt(PREFER_KEY, PlayMode.LOOP.getValue());
        return PlayMode.getMode(modeVal);
    }

    /**
     * Get last target mediaUrl to play
     */
    public static String getLastTargetMediaUrl(boolean isSet, String mediaUrl) {
        final String PREFER_KEY_MEDIA_URL = "VIDEO_LAST_TARGET_MEDIA_URL";
        if (isSet) {
            saveString(PREFER_KEY_MEDIA_URL, mediaUrl);
        }
        return getString(PREFER_KEY_MEDIA_URL, "");
    }

    /**
     * Get last played media information
     *
     * @return String[] [0]mediaUrl,[1]progress
     */
    public static String[] getLastPlayedMediaInfo(boolean isSet, String mediaUrl, int progress) {
        final String PREFER_KEY_MEDIA_URL = "VIDEO_LAST_PLAYED_MEDIA_URL";
        final String PREFER_KEY_MEDIA_PROGRESS = "VIDEO_LAST_PLAYED_MEDIA_PROGRESS";

        //Save
        if (isSet) {
            saveString(PREFER_KEY_MEDIA_URL, mediaUrl);
            saveInt(PREFER_KEY_MEDIA_PROGRESS, progress);
        }

        //Get
        String[] mediaInfos = new String[2];
        mediaInfos[0] = getString(PREFER_KEY_MEDIA_URL, "");
        mediaInfos[1] = getInt(PREFER_KEY_MEDIA_PROGRESS, 0).toString();
        return mediaInfos;
    }
}
