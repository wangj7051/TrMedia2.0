package js.lib.android.media.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import js.lib.android.media.engine.video.utils.VideoInfo;
import js.lib.android.utils.CommonUtil;
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
    public ProVideo(Context context, String mediaPath) {
        MediaMetadataRetriever mmr = null;
        try {
            //
            File file = new File(mediaPath);
            if (!file.exists()) {
                return;
            }

            //
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, Uri.parse(mediaPath));

            //
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if (title == null) {
                String fName = file.getName();
                if (!TextUtils.isEmpty(fName)) {
                    int lastIdxOfDot = fName.lastIndexOf(".");
                    if (lastIdxOfDot != -1) {
                        title = fName.substring(0, lastIdxOfDot);
                    }
                }
            }
            titlePinYin = CharacterParser.getPingYin(title);

            //
            mediaUrl = mediaPath;
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                this.mediaDirectory = parentFile.getName();
                this.mediaDirectoryPinYin = CharacterParser.getPingYin(this.mediaDirectory);
            }

            //
            String strDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (TextUtils.isDigitsOnly(strDuration)) {
                duration = Integer.parseInt(strDuration);
            }
        } catch (Exception e) {
            Logs.debugI(TAG, "Parse video failure....!!!!");
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    /**
     * Get video thumbnail
     */
    public Bitmap getThumbNail(String mediaPath, int width, int height, int kind) {
        return CommonUtil.getVideoThumbnail(mediaPath, width, height, kind);
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