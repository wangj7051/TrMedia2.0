package com.yj.audio.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.yj.audio.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.Program;
import js.lib.android.media.engine.MediaUtils;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * Player Logic Methods
 *
 * @author Jun.Wang
 */
public class PlayerLogicUtils {
    // Tag
    private static final String TAG = "PlayerLogicUtils";

    /**
     * Get PicFile Path
     *
     * @param picType : 1 Music Picture
     *                <p>
     *                2 Video Picture
     */
    public static String getMediaPicPath(String mediaName, int picType) {
        String picFilePath = "";
        try {
            String picName = URLEncoder.encode(mediaName, "UTF-8") + ".png";
            if (picType == 1) {
                picFilePath = PlayerFileUtils.getMusicPicPath(mediaName) + "/" + picName;
            } else if (picType == 2) {
                picFilePath = PlayerFileUtils.getVideoPicPath(mediaName) + "/" + picName;
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getMediaPicPath()", e);
        }
        return picFilePath;
    }

    /**
     * Get Media Cover Image File Path
     */
    public static String getMediaPicFilePath(final Program program, final String storePath) throws UnsupportedEncodingException {
//        if (!TextUtils.isEmpty(storePath) && storePath.endsWith("/")) {
//            return storePath + URLEncoder.encode(program.title, "UTF-8") + ".png";
//        }
//        return storePath + "/" + URLEncoder.encode(program.title, "UTF-8") + ".png";

        if (!TextUtils.isEmpty(storePath) && storePath.endsWith("/")) {
            return storePath + URLEncoder.encode(program.title, "UTF-8") + ".png";
        }
        return storePath + "/" + URLEncoder.encode(program.title, "UTF-8") + ".png";
    }

    /**
     * Set media cover Image
     */
    public static void setMediaCover(ImageView ivCover, ProAudio program) {
        if (EmptyUtil.isEmpty(program.coverUrl)) {
            ivCover.setImageResource(R.drawable.bg_cover_music_udisk);
        } else {
            ivCover.setImageURI(Uri.parse(program.coverUrl));
        }
    }

    /**
     * Return String or UnKnow
     */
    public static String getUnKnowOnNull(Context cxt, String str) {
        if (EmptyUtil.isEmpty(str)) {
            return cxt.getString(R.string.unknow);
        }
        return str;
    }

    /**
     * Toast Play
     * Error
     */
    public static void toastPlayError(Context cxt, String mediaTitle) {
        String errorMsg = String.format(cxt.getString(R.string.play_error), mediaTitle);
        Logs.i(TAG, errorMsg);
//        Toast.makeText(cxt, errorMsg, Toast.LENGTH_LONG).show();
    }

    /**
     * Get Media Title
     */
    public static String getMediaTitle(Context context, int position, Program program, boolean isContainSuffix) {
        String title = "";
        try {
            if (position >= 0) {
                title = position + ". ";
            }
            title += getUnKnowOnNull(context, program.title);
            if (isContainSuffix) {
                title += MediaUtils.getSuffix(program.mediaUrl);
            }
        } catch (Exception e) {
            title = "";
        }
        return title;
    }
}
