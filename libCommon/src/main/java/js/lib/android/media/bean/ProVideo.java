package js.lib.android.media.bean;

import android.util.Log;

import js.lib.android.media.video.utils.VideoInfo;
import js.lib.android.utils.Logs;

/**
 * Video Program
 *
 * @author Jun.Wang
 */
public class ProVideo extends Program {
    //TAG
    private static final String TAG = "ProVideo";

    public ProVideo() {
    }

    public ProVideo(VideoInfo video) {
        this.title = video.title;
        titlePinYin = video.titlePinYin;
        this.mediaUrl = video.path;
        mediaDirectory = video.directory;
        mediaDirectoryPinYin = video.directoryPinYin;
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