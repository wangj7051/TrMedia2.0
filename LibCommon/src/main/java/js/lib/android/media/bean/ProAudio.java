package js.lib.android.media.bean;

import android.util.Log;

import js.lib.android.media.audio.utils.AudioInfo;

/**
 * Audio Program
 *
 * @author Jun.Wang
 */
public class ProAudio extends Program {
    //TAG
    private static final String TAG = "ProAudio";

    /**
     * System Database Media ID
     */
    public long sysMediaID = 0;

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
     * Words of a song
     */
    public String lyric = "";

    /**
     * Is Parsed By MP3File
     * <p>
     * 0 尚未获取信息
     * <p>
     * 1 已经获取过信息
     */
    public int parseInfoFlag = 0;

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
        artistPinYin = info.artistPinYin;
        this.albumID = info.albumID;
        this.album = info.album;
        albumPinYin = info.albumPinYin;
        this.mediaUrl = info.path;
        mediaDirectory = info.directory;
        mediaDirectoryPinYin = info.directoryPinYin;
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
