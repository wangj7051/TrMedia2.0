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

/**
 * Video Program
 *
 * @author Jun.Wang
 */
public class ProVideo extends Program {
    //TAG
    private static final String TAG = "ProVideo";

    /**
     * Video width
     */
    public int width;

    /**
     * Video width
     */
    public int height;

    /**
     * Video rotation
     */
    public int rotation;

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

    /**
     * Construct From Media File
     */
    public ProVideo(String mediaPath) {
        //
        File file = new File(mediaPath);
        if (!file.exists()) {
            return;
        }

        //
        String fName = file.getName();
        if (!TextUtils.isEmpty(fName)) {
            int lastIdxOfDot = fName.lastIndexOf(".");
            if (lastIdxOfDot != -1) {
                title = fName.substring(0, lastIdxOfDot);
            }
        }
        titlePinYin = UNKNOWN;

        //
        mediaUrl = mediaPath;
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            this.mediaDirectory = parentFile.getName();
            this.mediaDirectoryPinYin = UNKNOWN;
        }

        //
        duration = 0;
    }

    /**
     * Parse media scale information.
     *
     * @param context {@link Context}
     * @param media   {@link ProVideo}
     */
    public static void parseMediaScaleInfo(Context context, ProVideo media) {
        MediaMetadataRetriever mmr = null;
        try {
            //
            String mediaPath = media.mediaUrl;
            File file = new File(mediaPath);
            if (file.exists()) {
                //
                mmr = new MediaMetadataRetriever();
                mmr.setDataSource(context, Uri.parse(mediaPath));

                // 视频高度
                media.height = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                // 视频宽度
                media.width = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                // 视频旋转方向
                media.rotation = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            }
        } catch (Exception e) {
            Logs.debugI(TAG, "Parse video scale information failure....!!!!");
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