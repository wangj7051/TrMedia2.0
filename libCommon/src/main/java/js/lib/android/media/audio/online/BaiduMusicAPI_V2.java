package js.lib.android.media.audio.online;

import java.net.URLEncoder;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * BaiDu Music API
 * <p>
 * http://mrasong.com/a/baidu-mp3-api-full/comment-p-1
 * <p>
 * http://mrasong.com/a/baidu-mp3-api
 * 
 * @author Jun.Wang
 */
public class BaiduMusicAPI_V2 {
	//
	private static final String TAG = "BaiduMusicAPI_V2 -> ";
	private static final String SEARCH_LIST_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting?";
	private static final String BAIDU_MUSIC_ROOT_URL = "http://music.baidu.com";
	private static final String SEARCH_MUSIC_URL = BAIDU_MUSIC_ROOT_URL + "/data/music/links?";

	/**
	 * ------Result JSON Format------
	 * <p>
	 * ({ "song": [ { "bitrate_fee": "{\"0\":\"0|0\",\"1\":\"0|0\"}", "yyr_artist": "0", "songname":
	 * "匆匆那年", "artistname": "王菲", "control": "0000000000", "songid": "124380645", "has_mv": "1",
	 * "encrypted_songid": "1507769e5e5095629037aL" }, { "bitrate_fee":
	 * "{\"0\":\"0|0\",\"1\":\"0|0\"}", "yyr_artist": "0", "songname": "匆匆那年法语版", "artistname":
	 * "弗雷德乐队", "control": "0000000000", "songid": "132682972", "has_mv": "0", "encrypted_songid":
	 * "04077e894dc09562101b5L" }, { "bitrate_fee": "{\"0\":\"0|0\",\"1\":\"0|0\"}", "yyr_artist":
	 * "1", "songname": "匆匆那年", "artistname": "微蓝海", "control": "0000000000", "songid": "73970451",
	 * "has_mv": "0", "encrypted_songid": "" }, { "bitrate_fee": "{\"0\":\"0|0\",\"1\":\"0|0\"}",
	 * "yyr_artist": "1", "songname": "匆匆那年", "artistname": "佘礼林", "control": "0000000000",
	 * "songid": "74015369", "has_mv": "0", "encrypted_songid": "" }, { "bitrate_fee":
	 * "{\"0\":\"0|0\",\"1\":\"0|0\"}", "yyr_artist": "1", "songname": "匆匆那年", "artistname": "吴弘远",
	 * "control": "0000000000", "songid": "74026081", "has_mv": "0", "encrypted_songid": "" }, {
	 * "bitrate_fee": "{\"0\":\"0|0\",\"1\":\"0|0\"}", "yyr_artist": "0", "songname": "匆匆那年",
	 * "artistname": "赵小臭", "control": "0000000000", "songid": "257525607", "has_mv": "0",
	 * "encrypted_songid": "7507f598767095642e99eL" }, { "bitrate_fee":
	 * "{\"0\":\"0|0\",\"1\":\"0|0\"}", "yyr_artist": "1", "songname": "匆匆那年", "artistname":
	 * "吾恩_5n_", "control": "0000000000", "songid": "73970629", "has_mv": "0", "encrypted_songid":
	 * "" }, { "bitrate_fee": "{\"0\":\"0|0\",\"1\":\"0|0\"}", "yyr_artist": "1", "songname":
	 * "匆匆那年", "artistname": "玉面小嫣然", "control": "0000000000", "songid": "74006239", "has_mv": "0",
	 * "encrypted_songid": "" }, { "bitrate_fee": "{\"0\":\"0|0\",\"1\":\"0|0\"}", "yyr_artist":
	 * "1", "songname": "匆匆那年日文版", "artistname": "荒木毬菜", "control": "0000000000", "songid":
	 * "74066267", "has_mv": "0", "encrypted_songid": "" }, { "bitrate_fee":
	 * "{\"0\":\"0|0\",\"1\":\"0|0\"}", "yyr_artist": "1", "songname": "匆匆那年", "artistname": "Lau",
	 * "control": "0000000000", "songid": "73968141", "has_mv": "0", "encrypted_songid": "" } ],
	 * "order": "song,album", "error_code": 22000, "album": [ { "albumname": "匆匆那年", "artistpic":
	 * "http:\/\/qukufile2.qianqian.com\/data2\/pic\/124383377\/124383377.jpg", "albumid":
	 * "124380738", "artistname": "王菲" }, { "albumname": "匆匆那年法语版", "artistpic":
	 * "http:\/\/qukufile2.qianqian.com\/data2\/pic\/132682589\/132682589.jpg", "albumid":
	 * "132682970", "artistname": "弗雷德乐队" }, { "albumname": "匆匆那年", "artistpic":
	 * "http:\/\/b.hiphotos.baidu.com\/ting\/pic\/item\/d62a6059252dd42ad69866d2 0 1 3 b 5 b b 5 c 9
	 * e a b 8 3 2 . j p g " , "albumid": "107210349", "artistname": "佚名\/九夜茴
	 * " }, { "albumname": "匆匆那年的你", "artistpic":
	 * "http:\/\/qukufile2.qianqian.com\/data2\/pic\/241236245\/241236245.jpg", "albumid":
	 * "241236319", "artistname": "乐洋" } ] });
	 */
	public static String getSearchListUrl(String searchKey) {
		String searchUrl = SEARCH_LIST_URL;
		try {
			searchUrl += ("from=webapp_music&method=baidu.ting.search.catalogSug&format=json&callback=");
			searchUrl += ("&query=" + URLEncoder.encode(searchKey, "UTF-8"));
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "getSearchListUrl()", e);
		}
		return searchUrl;
	}

	/**
	 * ------Result JSON Format------
	 * <p>
	 * { "errorCode": 22000, "data": { "xcode": "e362dcb85496c0c77994aa2c4fb09932", "songList": [ {
	 * "queryId": "124380645", "songId": 124380645, "songName": "匆匆那年", "artistId": "45561",
	 * "artistName": "王菲", "albumId": 124380738, "albumName": "匆匆那年", "songPicSmall":
	 * "http://musicdata.baidu.com/data2/pic/124383375/124383375.jpg", "songPicBig":
	 * "http://musicdata.baidu.com/data2/pic/124383372/124383372.jpg", "songPicRadio":
	 * "http://musicdata.baidu.com/data2/pic/124383369/124383369.jpg", "lrcLink":
	 * "/data2/lrc/128947919/128947919.lrc", "version": "影视原声", "copyType": 0, "time": 241,
	 * "linkCode": 22000, "songLink":
	 * "http://yinyueshiting.baidu.com/data2/music/136475509/124380645248400320.mp3?xcode=e362dcb85496c0c785429371225e2601"
	 * , "showLink":
	 * "http://yinyueshiting.baidu.com/data2/music/136475509/124380645248400320.mp3?xcode=e362dcb85496c0c785429371225e2601"
	 * , "format": "mp3", "rate": 320, "size": 9642583, "relateStatus": "2", "resourceType": "0",
	 * "source": "web" } ] } }
	 */
	public static String getSearchMusicUrl(int songID) {
		String searchUrl = SEARCH_MUSIC_URL;
		try {
			searchUrl += ("songIds=" + songID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return searchUrl;
	}

	/**
	 * Music Link
	 * 
	 * @param lrcLink
	 *            : Like "/data2/lrc/128947919/128947919.lrc"
	 */
	public static String getLyricUrl(String lrcLink) {
		if (!EmptyUtil.isEmpty(lrcLink)) {
			if (lrcLink.startsWith("http://")) {
				return lrcLink;
			}
			return BAIDU_MUSIC_ROOT_URL + lrcLink;
		}
		return "";
	}
}
