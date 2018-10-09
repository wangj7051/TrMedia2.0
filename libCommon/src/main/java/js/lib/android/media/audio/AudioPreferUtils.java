package js.lib.android.media.audio;

import js.lib.android.media.PlayMode;
import js.lib.android.utils.PreferenceHelper;

/**
 * Audio player preference
 *
 * @author Jun.Wang
 */
public class AudioPreferUtils extends PreferenceHelper {
    // TAG
    private static final String TAG = "MediaPreferUtils";

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
        int modeVal = getInt(PREFER_KEY, PlayMode.LOOP.getValue());
        return PlayMode.getMode(modeVal);
    }

    /**
     * Audio last target mediaUrl to play
     */
    public static String getAudioLastTargetMediaUrl(boolean isSet, String mediaUrl) {
        final String PREFER_KEY_MEDIA_URL = "AUDIO_LAST_TARGET_MEDIA_URL";
        if (isSet) {
            saveString(PREFER_KEY_MEDIA_URL, mediaUrl);
        }
        return getString(PREFER_KEY_MEDIA_URL, "");
    }

    /**
     * Last Played Media Information
     *
     * @return String[] [0]mediaUrl,[1]progress
     */
    public static String[] getAudioLastPlayedMediaInfo(boolean isSet, String mediaUrl, int progress) {
        final String PREFER_KEY_MEDIA_URL = "AUDIO_LAST_PLAYED_MEDIA_URL";
        final String PREFER_KEY_MEDIA_PROGRESS = "AUDIO_LAST_PLAYED_MEDIA_PROGRESS";

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
