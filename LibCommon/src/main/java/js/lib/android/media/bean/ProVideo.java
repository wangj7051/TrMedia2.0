package js.lib.android.media.bean;

import android.util.Log;

import java.io.File;

import js.lib.android.media.engine.video.utils.VideoInfo;
import js.lib.android.utils.Logs;
import js.lib.utils.CharacterParser;

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
        this.titlePinYin = video.titlePinYin;
        this.mediaUrl = video.path;
        this.mediaDirectory = video.directory;
        this.mediaDirectoryPinYin = video.directoryPinYin;
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
        this.title = title;
        this.titlePinYin = CharacterParser.getPingYin(title);

        //
        this.mediaUrl = mediaUrl;
        File file = new File(mediaUrl);
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            this.mediaDirectory = parentFile.getName();
            this.mediaDirectoryPinYin = CharacterParser.getPingYin(this.mediaDirectory);
        }
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