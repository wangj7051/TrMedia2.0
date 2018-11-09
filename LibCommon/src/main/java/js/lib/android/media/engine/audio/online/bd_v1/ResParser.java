package js.lib.android.media.engine.audio.online.bd_v1;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.audio.online.ReqUris;
import js.lib.android.utils.Logs;

/**
 * Response Parser
 *
 * @author Jun.Wang
 */
public class ResParser {
    // TAG
    private static final String TAG = "ResParser -> ";

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
    public interface ParseListListener {
        void afterParse(String avatarUrl, String strJsonData);
    }

    /**
     * Parse Music List Listener
     */
    public interface ParseMusicListener {
        void afterParse(String musicWebUrl, String musicLyricWebUrl);
    }

    /**
     * Parse List<ProMusic> to JSON
     */
    public static void parseMusicsToJson(final List<ProAudio> musics, final ParseListListener listener) {
        if (mParseThread != null && mParseThread.isAlive()) {
            return;
        }

        mParseThread = new Thread() {
            @Override
            public void run() {
                super.run();

                JSONArray jaMusicInfos = new JSONArray();
                try {
                    int idx = 0;
                    for (ProAudio music : musics) {
                        JSONObject joMusicInfo = new JSONObject();
                        // joMusicInfo.put("isCloudMusic", false);
                        joMusicInfo.put("duration", 0);
                        joMusicInfo.put("size", 0);
                        jaMusicInfos.put(joMusicInfo);

                        joMusicInfo.put("name", music.title);
                        joMusicInfo.put("artist", music.artist);
                        joMusicInfo.put("cloudUrl", music.mediaUrl);
                        joMusicInfo.put("id", idx++);
                    }
                } catch (Exception e) {
                    Logs.printStackTrace(TAG + "parseMusicsToJson()", e);
                } finally {
                    doAfterParse(listener, "", jaMusicInfos.toString());
                }

                //
                mParseThread = null;
            }
        };
        mParseThread.start();
    }

    /**
     * Parse BaiDu Search Result
     *
     * @param searchFlag : if == 1, means search by title
     *                   <p>
     *                   if == 2, means search by artist
     */
    public static void parseBaiduSearchResult(final String strHtml, final int searchFlag, final ParseListListener listener) {
        if (mParseThread != null && mParseThread.isAlive()) {
            return;
        }

        mParseThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Document rootDoc = Jsoup.parse(strHtml);
                    if (searchFlag == 2) {
                        parseResSearchByArtist(rootDoc, listener);
                    } else if (searchFlag == 1) {
                        parseResSearchByTitle(rootDoc, listener);
                    }
                } catch (Exception e) {
                    Logs.printStackTrace(TAG + "parseBaiduSearchResult()", e);
                } finally {
                }

                //
                mParseThread = null;
            }
        };
        mParseThread.start();
    }

    private static void parseResSearchByTitle(Document rootDoc, final ParseListListener listener) {
        JSONArray jaMusicInfos = new JSONArray();
        String avatarUrl = "";

        try {
            // Artist Information
            try {
                Element eleArtistDiv = rootDoc.getElementById("target_album");
                Elements eleImgs = eleArtistDiv.getElementsByTag("img");
                avatarUrl = eleImgs.first().attr("org_src").trim();
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "parseResSearchByTitle()", e);
            }

            // Music List Information
            Elements eleTitles = rootDoc.getElementsByClass("song-title");
            Elements eleArtists = rootDoc.getElementsByClass("singer");

            int loop = eleArtists.size();
            for (int idx = 0; idx < loop; idx++) {
                try {
                    JSONObject joMusicInfo = new JSONObject();
                    // joMusicInfo.put("isCloudMusic", true);
                    joMusicInfo.put("duration", 0);
                    joMusicInfo.put("size", 0);
                    joMusicInfo.put("cloudUrl", ReqUris.DEFAULT_MEDIA_URL);
                    jaMusicInfos.put(joMusicInfo);

                    String title = eleTitles.get(idx).select("a[data-songdata]").first().attr("title");
                    joMusicInfo.put("name", title);

                    String songIDJson = eleTitles.get(idx).select("a[data-songdata]").first().attr("data-songdata");
                    JSONObject joSongID = new JSONObject(songIDJson);
                    joMusicInfo.put("id", joSongID.optString("id", "0"));

                    String singer = eleArtists.get(idx).getElementsByTag("a").first().text();
                    joMusicInfo.put("artist", singer);
                } catch (Exception e) {
                    Logs.printStackTrace(TAG + "parseResSearchByTitle()2", e);
                }
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "parseResSearchByTitle()3", e);
        } finally {
            doAfterParse(listener, avatarUrl, jaMusicInfos.toString());
        }
    }

    private static void parseResSearchByArtist(Document rootDoc, final ParseListListener listener) {
        JSONArray jaMusicInfos = new JSONArray();
        String avatarUrl = "";

        try {
            // Artist Information
            Element eleArtistDiv = rootDoc.getElementById("target_artist");
            Elements eleAs = eleArtistDiv.getElementsByTag("a");

            int loop = eleAs.size();
            for (int idx = 0; idx < loop; idx++) {
                try {
                    Element eleA = eleAs.get(idx);
                    if ("avatar cover-img".equalsIgnoreCase(eleA.attr("class"))) {
                        avatarUrl = eleA.getElementsByTag("img").first().attr("src").trim();
                        if (!TextUtils.isEmpty(avatarUrl)) {
                            break;
                        }
                    }
                } catch (Exception e) {

                }
            }

            // Music List Information
            Elements eleTitles = rootDoc.getElementsByClass("song-title");
            Elements eleArtists = rootDoc.getElementsByClass("singer");

            loop = eleArtists.size();
            for (int idx = 0; idx < loop; idx++) {
                try {
                    JSONObject joMusicInfo = new JSONObject();
                    // joMusicInfo.put("isCloudMusic", true);
                    joMusicInfo.put("duration", 0);
                    joMusicInfo.put("size", 0);
                    joMusicInfo.put("cloudUrl", ReqUris.DEFAULT_MEDIA_URL);
                    jaMusicInfos.put(joMusicInfo);

                    String title = eleTitles.get(idx).select("a[data-songdata]").first().attr("title");
                    joMusicInfo.put("name", title);

                    String singer = eleArtists.get(idx).select("a[href]").first().select("em").first().text();
                    joMusicInfo.put("artist", singer);

                    String songIDJson = eleTitles.get(idx).select("a[data-songdata]").first().attr("data-songdata");
                    JSONObject joSongID = new JSONObject(songIDJson);
                    joMusicInfo.put("id", joSongID.optString("id", "0"));
                } catch (Exception e) {
                    Logs.printStackTrace(TAG + "parseResSearchByArtist()1", e);
                }
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "parseResSearchByArtist()2", e);
        } finally {
            doAfterParse(listener, avatarUrl, jaMusicInfos.toString());
        }
    }

    private static void doAfterParse(final ParseListListener listener, final String avatarUrl, final String strJsonData) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                listener.afterParse(avatarUrl, strJsonData);
            }
        });
    }

    /**
     * Parse BaiDu Searched Music
     */
    public static void parseBaiduSearchedMusic(final String strXml, final ParseMusicListener listener) {
        if (mParseThread != null && mParseThread.isAlive()) {
            return;
        }

        mParseThread = new Thread() {
            @Override
            public void run() {
                super.run();

                InputStream inStream = null;
                try {
                    inStream = new ByteArrayInputStream(strXml.getBytes());
                    XmlPullParser xpParser = Xml.newPullParser();
                    xpParser.setInput(inStream, "UTF-8");

                    //
                    boolean isGotMusicInfo = false;
                    int arrLen = 5;

                    String[] musicInfo = null;
                    for (int event = xpParser.getEventType(); event != XmlPullParser.END_DOCUMENT; event = xpParser.next()) {
                        if (event == XmlPullParser.START_TAG) {
                            if (isGotMusicInfo) {
                                break;
                            }

                            if ("url".equals(xpParser.getName())) {
                                if (musicInfo == null) {
                                    musicInfo = new String[arrLen];
                                }
                            } else if ("encode".equals(xpParser.getName())) {
                                if (musicInfo != null) {
                                    musicInfo[0] = xpParser.nextText();
                                }
                            } else if ("decode".equals(xpParser.getName())) {
                                if (musicInfo != null) {
                                    String decode = xpParser.nextText();
                                    String musicFName = decode.substring(0, decode.indexOf("?xcode"));
                                    if (musicFName.endsWith(BaiduMusicAPI.SUPPORT_FORMAT)) {
                                        musicInfo[1] = decode;
                                    }
                                }
                            } else if ("lrcid".equals(xpParser.getName())) {
                                if (musicInfo != null) {
                                    musicInfo[2] = BaiduMusicAPI.getLyricUrl(xpParser.nextText());
                                }
                            }
                        } else if (event == XmlPullParser.END_TAG) {
                            if ("url".equals(xpParser.getName())) {
                                if (musicInfo != null) {
                                    if (TextUtils.isEmpty(musicInfo[0]) || TextUtils.isEmpty(musicInfo[1])) {
                                        musicInfo = null;
                                        isGotMusicInfo = false;
                                    } else {
                                        isGotMusicInfo = true;
                                    }
                                }
                            }
                        }
                    }

                    //
                    doAfterParse(listener, musicInfo);
                } catch (Exception e) {
                    Logs.printStackTrace(TAG + "parseBaiduSearchedMusic()1", e);
                    doAfterParse(listener, null);
                } finally {
                    try {
                        if (inStream != null) {
                            inStream.close();
                        }
                    } catch (Exception e2) {
                        Logs.printStackTrace(TAG + "parseBaiduSearchedMusic()2", e2);
                    }
                }
            }
        };
        mParseThread.start();
    }

    /**
     * Notify After Parse Music XML information
     *
     * @param musicInfo : [0] encode ; [1] decode ; [2] lrcid
     */
    private static void doAfterParse(final ParseMusicListener listener, final String[] musicInfo) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    String encode = musicInfo[0];
                    String musicWebUrl = encode.substring(0, encode.lastIndexOf("/") + 1) + musicInfo[1];
                    String musicLyricWebUrl = musicInfo[2];
                    listener.afterParse(musicWebUrl, musicLyricWebUrl);
                } catch (Exception e) {
                    Logs.printStackTrace(TAG + "doAfterParse()", e);
                    listener.afterParse("", "");
                }
            }
        });
    }
}
