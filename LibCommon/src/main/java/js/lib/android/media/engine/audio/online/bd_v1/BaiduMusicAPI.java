package js.lib.android.media.engine.audio.online.bd_v1;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * BaiDu Music API [NOT PUBLISH]
 * 
 * @author Jun.Wang
 */
@SuppressWarnings("deprecation")
public class BaiduMusicAPI {
	// TAG
	private static final String TAG = "BaiduMusicAPI";

	//
	public static String SUPPORT_FORMAT = ".mp3";

	/**
	 * Search music list by Artist or SongTitle
	 */
	public static final String SEARCH_LIST_URL = "http://music.baidu.com/search";

	/**
	 * Search the music by Artist & SongTitle
	 */
	public static final String SEARCH_MUSIC_URL = "http://box.zhangmen.baidu.com/x";

	/**
	 * BaiDu Music Lyric URL
	 * <p>
	 * Format : "http://box.zhangmen.baidu.com/bdlrc/496/49684.lrc"
	 * <p>
	 * if "<lrcid>49684</lrcid> ", then 49684/100 =496.84 取小于等于496.84 的最大整数就是496, then the url is
	 * MUSIC_LYRIC_URL+"/496/49684.lrc"
	 */
	private static final String MUSIC_LYRIC_URL = "http://box.zhangmen.baidu.com/bdlrc";

	/**
	 * Get Search List Parameters
	 */
	public static List<NameValuePair> getSearchListValues(String searchKey) {
		NameValuePair nvPairKey = new BasicNameValuePair("key", searchKey);
		List<NameValuePair> listValues = new ArrayList<NameValuePair>();
		listValues.add(nvPairKey);
		return listValues;
	}

	/**
	 * Get Search Music Parameters
	 */
	public static String getSearchMusicUrl(String title, String artist) {
		String searchUrl = "";
		try {
			if (EmptyUtil.isEmpty(title)) {
				title = "$$";
			} else {
				title = URLEncoder.encode(title, "UTF-8");
			}
			if (EmptyUtil.isEmpty(artist)) {
				artist = "$$";
			} else {
				artist = URLEncoder.encode(artist, "UTF-8");
			}
			searchUrl = (SEARCH_MUSIC_URL + "?op=12&count=1&title=" + title + "$$" + artist + "$$$$");
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "getSearchMusicUrl()", e);
		}
		return searchUrl;
	}

	/**
	 * Get Music Lyric URL
	 */
	public static String getLyricUrl(String strLyricID) {
		String lyricUrl = "";
		try {
			int lyricID = Integer.valueOf(strLyricID);
			if (lyricID > 0) {
				lyricUrl = MUSIC_LYRIC_URL + "/" + (lyricID / 100) + "/" + strLyricID + ".lrc";
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "getLyricUrl()", e);
		}
		return lyricUrl;
	}
}
