package js.lib.android.media.local.utils;

import java.util.HashSet;
import java.util.Set;

import js.lib.bean.BaseBean;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * SDCard Audio Information Bean
 * 
 * @author Jun.Wang
 */
public class AudioInfo extends BaseBean {

	/**
	 * Serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Support Suffixes
	 * <p>
	 * like Set<".mp3"> ...
	 */
	private static Set<String> mSetSuffixs = new HashSet<String>();
	static {
		mSetSuffixs.add(".aac");
		mSetSuffixs.add(".mp3");
	}

	/**
	 * MIME
	 */
	public static final String[] MIME = { "video/*" };

	/**
	 * Query System Media URI
	 */
	public static final Uri QUERY_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	/**
	 * Query System Media QUERY_PROJECTION
	 */
	public static final String[] QUERY_PROJECTION = new String[] { AudioInfo._SYS_MEDIA_ID, AudioInfo._MIME_TYPE,
			AudioInfo._DISPLAY_NAME, AudioInfo._TITLE, AudioInfo._ARTIST, AudioInfo._ALBUM, AudioInfo._ALBUM_ID, AudioInfo._PATH,
			AudioInfo._DURATION };

	/**
	 * Media ID from System Database
	 */
	public int sysMediaID = 0;
	public static final String _SYS_MEDIA_ID = MediaStore.Audio.Media._ID;

	/**
	 * MIME_TYPE
	 */
	public String mimeType = "";
	public static final String _MIME_TYPE = MediaStore.Audio.Media.MIME_TYPE;

	/**
	 * Display Name
	 */
	public String displayName = "";
	public static final String _DISPLAY_NAME = MediaStore.Audio.Media.DISPLAY_NAME;

	/**
	 * Title
	 */
	public String title = "", titlePinYin = "";
	public static final String _TITLE = MediaStore.Audio.Media.TITLE;

	/**
	 * Title
	 */
	public String artist = "";
	public static final String _ARTIST = MediaStore.Audio.Media.ARTIST;

	/**
	 * Album ID
	 */
	public long albumID = 0;
	public static final String _ALBUM_ID = MediaStore.Audio.Media.ALBUM_ID;

	/**
	 * Album
	 */
	public String album = "";
	public static final String _ALBUM = MediaStore.Audio.Media.ALBUM;

	/**
	 * Path
	 */
	public String path = "";
	public static final String _PATH = MediaStore.Audio.Media.DATA;

	/**
	 * Duration
	 */
	public int duration = 0;
	public static final String _DURATION = MediaStore.Audio.Media.DURATION;

	/**
	 * Is Support Files
	 */
	public static boolean isSupport(String suffix) {
		return mSetSuffixs.contains(suffix);
	}

	/**
	 * Set Support Media Types
	 */
	public static void setSupportMedias(boolean isSupportAll) {
		if (isSupportAll) {
			// mSetSuffixs.add(".aac");
			mSetSuffixs.add(".m4a");
			mSetSuffixs.add(".mid");
			// mSetSuffixs.add(".mp3");
			mSetSuffixs.add(".wav");
			mSetSuffixs.add(".flac");
			mSetSuffixs.add(".wma");
		}
	}
}
