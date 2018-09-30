package js.lib.android.media.video.bean;

import android.util.Log;

import js.lib.android.utils.Logs;
import js.lib.bean.BaseBean;

/**
 * Video Program
 *
 * @author Jun.Wang
 */
public class ProVideo extends BaseBean {
    //TAG
    private static final String TAG = "ProVideo";

    /**
     * Serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * Program ID
     */
    public int id;

    /**
     * Program Title
     */
    public String title = "";
    /**
     * Spelling
     */
    public String titlePinYin = "";

    /**
     * Media Play URL
     * <p>e.g. "/sdcard/媒体/test.mp4"</p>
     */
    public String mediaUrl = "";
    /**
     * Media Play URL
     * <p>e.g. mediaUrl="/sdcard/媒体/test.mp3" ; mediaDirectory="媒体"</p>
     */
    public String mediaDirectory = "";
    /**
     * Media Play URL
     * <p>e.g. mediaUrl="/sdcard/媒体/test.mp3" ; mediaDirectory="媒体" ; mediaDirectoryPinYin="meiti"</p>
     */
    public String mediaDirectoryPinYin = "";

    /**
     * Sort Letter
     * <p>If sort by collects or audio name, the first char of title.</p>
     * <p>If sort by folder, the first char of folder.</p>
     * <p>If sort by artist, the first char of artist.</p>
     * <p>If sort by album, the first char of album.</p>
     */
    public String sortLetter = "";

    /**
     * Cover Image URL
     */
    public String coverUrl = "";

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
     * Is This Program Come from net
     * <p>1 ~ from net</p>
     * <p>0 ~ local</p>
     */
    public int source = 0;

    /**
     * Records create/update Time
     */
    public long createTime, updateTime = 0;

    public ProVideo() {
    }

    public ProVideo(VideoInfo video) {
        this.title = video.displayName;
        this.mediaUrl = video.path;
        this.duration = video.duration;
    }

    public ProVideo(String fPath) {
        try {
            this.mediaUrl = fPath;
            this.title = fPath.substring(fPath.lastIndexOf("/") + 1);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "ProVideo()", e);
            this.mediaUrl = "";
            this.title = "";
        }
    }

    /**
     * Construct From Media File
     */
    public ProVideo(String mediaUrl, String title) {
        this.mediaUrl = mediaUrl;
        this.title = title;
    }

    /**
     * Copy srcPro`s DATA to targetPro
     */
    public static void copy(ProVideo targetPro, ProVideo srcPro) {
        try {
            targetPro.title = srcPro.title;
            targetPro.mediaUrl = srcPro.mediaUrl;
            targetPro.duration = srcPro.duration;
        } catch (Exception e) {
            Log.i(TAG, "copy(targetPro,srcPro)");
        }
    }
}