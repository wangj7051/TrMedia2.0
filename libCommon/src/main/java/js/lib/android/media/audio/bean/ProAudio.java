package js.lib.android.media.audio.bean;

import android.util.Log;

import js.lib.bean.BaseBean;

/**
 * Audio Program
 *
 * @author Jun.Wang
 */
public class ProAudio extends BaseBean {
    //TAG
    private static final String TAG = "ProAudio";

    /**
     * Serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * Program ID
     */
    public int id;

    /**
     * System Database Media ID
     */
    public long sysMediaID = 0;

    /**
     * Program Title
     */
    public String title = "";
    public String titlePinYin = "";

    /**
     * Album ID
     */
    public long albumID = 0;
    /**
     * Album Name
     */
    public String album = "";
    /**
     * If album="专辑", albumPinYin="zhuanji"
     */
    public String albumPinYin = "";

    /**
     * Artist
     */
    public String artist = "";
    /**
     * If album="艺术家", albumPinYin="yishujia"
     */
    public String artistPinYin = "";

    /**
     * Media Play URL
     * <p>e.g. "/sdcard/音乐/test.mp3"</p>
     */
    public String mediaUrl = "";
    /**
     * Media Play URL
     * <p>e.g. mediaUrl="/sdcard/音乐/test.mp3" ; mediaDirectory="音乐"</p>
     */
    public String mediaDirectory = "";
    /**
     * Media Play URL
     * <p>e.g. mediaUrl="/sdcard/音乐/test.mp3" ; mediaDirectory="音乐" ; mediaDirectoryPinYin="yinyue"</p>
     */
    public String mediaDirectoryPinYin = "";

    /**
     * Program Duration
     */
    public int duration = 0;

    /**
     * Is This Program Collect
     * <p>
     * if == 1 , yes
     * <p>
     * if == 0, not
     */
    public int isCollected = 0;

    /**
     * Cover Image URL
     */
    public String coverUrl = "";

    /**
     * Words of a song
     */
    public String lyric = "";

    /**
     * Is This Program Come from net
     * <p>1 ~ from net</p>
     * <p>0 ~ local</p>
     */
    public int source = 0;

    /**
     * Records create/update Time
     */
    public long createTime, updateTime = 0;

    /**
     * Sort Letter
     * <p>If sort by collects or audio name, the first char of title.</p>
     * <p>If sort by folder, the first char of folder.</p>
     * <p>If sort by artist, the first char of artist.</p>
     * <p>If sort by album, the first char of album.</p>
     */
    public String sortLetter = "";

    /**
     * Empty Construct
     */
    public ProAudio() {
    }

    /**
     * Construct {@link ProAudio} From {@link AudioInfo}
     */
    public ProAudio(AudioInfo info) {
        this.sysMediaID = info.sysMediaID;
        this.title = info.title;
        this.titlePinYin = info.titlePinYin;
        this.artist = info.artist;
        this.albumID = info.albumID;
        this.album = info.album;
        this.mediaUrl = info.path;
        this.duration = info.duration;
    }

    /**
     * Construct ProMusic From Media File
     */
    public ProAudio(String mediaUrl, String title) {
        this.mediaUrl = mediaUrl;
        this.title = title;
    }

    /**
     * Copy srcPro`s DATA to targetPro
     */
    public static void copy(ProAudio targetPro, ProAudio srcPro) {
        try {
            targetPro.sysMediaID = srcPro.sysMediaID;
            targetPro.title = srcPro.title;
            targetPro.titlePinYin = srcPro.titlePinYin;
            targetPro.artist = srcPro.artist;
            targetPro.albumID = srcPro.albumID;
            targetPro.album = srcPro.album;
            targetPro.mediaUrl = srcPro.mediaUrl;
            targetPro.duration = srcPro.duration;
        } catch (Exception e) {
            Log.i(TAG, "copy(targetPro,srcPro)");
        }
    }
}
