package js.lib.android.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * System Volume Common Methods
 * 
 * @author Jun.Wang
 * 
 */
public class SysVolUtils {
	private static AudioManager mAudioManager;

	public static AudioManager getAudioMananger(Context cxt) {
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) cxt.getSystemService(Context.AUDIO_SERVICE);
		}
		return mAudioManager;
	}

	/**
	 * Get System Music Value
	 */
	public static int getMusicVolVal(Context cxt, boolean isGetMax) {
		if (isGetMax) {
			return getAudioMananger(cxt).getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		}
		return getAudioMananger(cxt).getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	/**
	 * Set System Music Value
	 */
	public static void setMusicVolVal(Context cxt, int volVal) {
		setMusicVolVal(cxt, volVal, AudioManager.FLAG_PLAY_SOUND);
	}

	/**
	 * Set System Music Value
	 */
	public static void setMusicVolVal(Context cxt, int volVal, int flags) {
		getAudioMananger(cxt).setStreamVolume(AudioManager.STREAM_MUSIC, volVal, flags);
	}
}
