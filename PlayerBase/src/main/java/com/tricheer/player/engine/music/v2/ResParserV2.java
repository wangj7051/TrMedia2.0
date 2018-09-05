package com.tricheer.player.engine.music.v2;

import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bd.BaiduMusicAPI_V2;
import js.lib.android.utils.Logs;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;

import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.bean.ProMusicBaidu;
import com.tricheer.player.engine.VersionController;
import com.tricheer.player.engine.music.ReqUris;

/**
 * Response Parser
 * 
 * @author Jun.Wang
 */
public class ResParserV2 {
	// TAG
	private static final String TAG = "ResParserV2 -> ";

	/**
	 * Parse Thread
	 */
	private static Thread mParseThread;

	/**
	 * Thread Handler
	 */
	private static Handler mHandler = new Handler();

	/**
	 * Parse Music List Listener
	 */
	public interface ParseBDMediasListener {
		public void afterParse(String strJsonList);
	}

	/**
	 * Parse Music List Listener
	 */
	public interface ParseSearchInfoListener {
		public void afterParse(List<ProMusic> listMusics);
	}

	/**
	 * Parse Music List Listener
	 */
	public interface ParseBDMediaListener {
		public void afterParse(ProMusic program);
	}

	/**
	 * Parse List<ProMusic> to JSON
	 */
	public static void parseMusicsToJson(final List<ProMusic> musics, final ParseBDMediasListener l) {
		if (mParseThread != null && mParseThread.isAlive()) {
			return;
		}

		mParseThread = new Thread() {
			@Override
			public void run() {
				super.run();

				JSONArray jaMedias = new JSONArray();
				try {
					int idx = 0;
					for (ProMusic music : musics) {
						JSONObject joMedia = new JSONObject();
						joMedia.put("duration", 0);
						joMedia.put("size", 0);
						jaMedias.put(joMedia);

						//
						if (VersionController.isAiosV2()) {
							joMedia.put("id", idx++);
							joMedia.put("url", music.mediaUrl);
							joMedia.put("title", music.title);
							joMedia.put("artist", music.artist);
						} else {
							joMedia.put("id", idx++);
							joMedia.put("cloudUrl", music.mediaUrl);
							joMedia.put("name", music.title);
							joMedia.put("artist", music.artist);
						}
					}
				} catch (Throwable e) {
					Logs.printStackTrace(TAG + "parseMusicsToJson()", e);
				} finally {
					doAfterGotMediaList(jaMedias.toString(), l);
				}

				//
				mParseThread = null;
			}
		};
		mParseThread.start();
	}

	/**
	 * Parse Media List
	 */
	public static void parseMediaList(final String strRes, final ParseBDMediasListener l) {
		if (mParseThread != null && mParseThread.isAlive()) {
			return;
		}

		mParseThread = new Thread() {
			@Override
			public void run() {
				super.run();

				//
				String strJsonList = "";
				JSONArray jaMedias = new JSONArray();

				try {
					int start = strRes.indexOf("{");
					int end = strRes.lastIndexOf("}") + 1;
					JSONObject joRes = new JSONObject(strRes.substring(start, end));
					JSONArray jaResSongs = joRes.optJSONArray("song");

					//
					int loop = jaResSongs.length();
					for (int idx = 0; idx < loop; idx++) {
						JSONObject joMedia = new JSONObject();
						joMedia.put("duration", 0);
						joMedia.put("size", 0);
						jaMedias.put(joMedia);

						//
						JSONObject joResSong = jaResSongs.optJSONObject(idx);
						if (VersionController.isAiosV2()) {
							joMedia.put("id", joResSong.optInt("songid", -1));
							joMedia.put("url", ReqUris.DEFAULT_MEDIA_URL);
							joMedia.put("title", joResSong.optString("songname", ""));
							joMedia.put("artist", joResSong.optString("artistname", ""));
						} else {
							joMedia.put("id", joResSong.optInt("songid", -1));
							joMedia.put("cloudUrl", ReqUris.DEFAULT_MEDIA_URL);
							joMedia.put("name", joResSong.optString("songname", ""));
							joMedia.put("artist", joResSong.optString("artistname", ""));
						}
					}
					strJsonList = jaMedias.toString();
				} catch (Throwable e) {
					Logs.printStackTrace(TAG + "parseMediaList()", e);
					strJsonList = "";
				} finally {
					doAfterGotMediaList(strJsonList, l);
				}

				//
				mParseThread = null;
			}
		};
		mParseThread.start();
	}

	/**
	 * Post Media List to UIThread
	 */
	private static void doAfterGotMediaList(final String strJsonMedias, final ParseBDMediasListener l) {
		if (l != null) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					l.afterParse(strJsonMedias);
				}
			});
		}
	}

	/**
	 * Parse Media List
	 */
	public static void parseSearchValues(final String strJsonInfos, final ParseSearchInfoListener l) {
		if (mParseThread != null && mParseThread.isAlive()) {
			return;
		}

		mParseThread = new Thread() {
			@Override
			public void run() {
				super.run();

				//
				List<ProMusic> listMusicInfos = new ArrayList<ProMusic>();
				try {
					JSONArray jaInfos = new JSONArray(strJsonInfos);
					//
					int loop = jaInfos.length();
					for (int idx = 0; idx < loop; idx++) {
						JSONObject joInfo = jaInfos.optJSONObject(idx);
						if (VersionController.isAiosV2()) {
							ProMusic music = new ProMusic();
							music.id = joInfo.optInt("id", -1);
							music.title = joInfo.optString("title", "");
							music.artist = joInfo.optString("artist", "");
							listMusicInfos.add(music);
						} else {
							ProMusic music = new ProMusic();
							music.id = joInfo.optInt("id", -1);
							music.title = joInfo.optString("name", "");
							music.artist = joInfo.optString("artist", "");
							listMusicInfos.add(music);
						}
					}
				} catch (Throwable e) {
					Logs.printStackTrace(TAG + "parseSearchValues()", e);
				} finally {
					doAfterGotMusicList(listMusicInfos, l);
				}

				//
				mParseThread = null;
			}
		};
		mParseThread.start();
	}

	/**
	 * Post Music List to UIThread
	 */
	private static void doAfterGotMusicList(final List<ProMusic> listMusicInfos, final ParseSearchInfoListener l) {
		if (l != null) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					l.afterParse(listMusicInfos);
				}
			});
		}
	}

	/**
	 * Parse Media
	 */
	public static void parseMedia(final String strRes, final ParseBDMediaListener l) {
		if (mParseThread != null && mParseThread.isAlive()) {
			return;
		}

		mParseThread = new Thread() {
			@Override
			public void run() {
				super.run();

				//
				ProMusicBaidu program = null;
				try {
					JSONObject joRes = new JSONObject(strRes);
					JSONObject joData = joRes.optJSONObject("data");
					String xCode = joData.optString("xcode", "");
					JSONArray jaSongs = joData.optJSONArray("songList");

					//
					JSONObject joSong = jaSongs.optJSONObject(0);

					// String format = joSong.optString("format", "");
					program = new ProMusicBaidu();
					program.id = joSong.optInt("songId", -1);
					program.title = joSong.optString("songName", "");
					program.artist = joSong.optString("artistName", "");
					program.albumName = joSong.optString("albumName", "");

					// "/data2/lrc/128947919/128947919.lrc"
					program.lyric = BaiduMusicAPI_V2.getLyricUrl(joSong.optString("lrcLink", ""));

					// Music Pictures
					program.songPicRadio = joSong.optString("songPicRadio", "");
					program.songPicSmall = joSong.optString("songPicSmall", "");
					program.songPicBig = joSong.optString("songPicBig", "");
					program.coverUrl = program.songPicBig;

					// Media URL
					program.songLink = joSong.optString("songLink", "");
					program.showLink = joSong.optString("showLink", "");

					int end = program.songLink.indexOf("xcode");
					program.mediaUrl = program.songLink.substring(0, end) + "xcode=" + xCode;
				} catch (Throwable e) {
					Logs.printStackTrace(TAG + "parseMedia()", e);
					program = null;
				} finally {
					doAfterGotMedia(program, l);
					mParseThread = null;
				}
			}
		};
		mParseThread.start();
	}

	/**
	 * Post Media to UIThread
	 */
	private static void doAfterGotMedia(final ProMusic program, final ParseBDMediaListener l) {
		if (l != null) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					l.afterParse(program);
				}
			});
		}
	}
}