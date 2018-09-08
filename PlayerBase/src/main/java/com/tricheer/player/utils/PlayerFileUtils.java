package com.tricheer.player.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tricheer.player.engine.VersionController;

import java.util.ArrayList;
import java.util.List;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.JsFileUtils;
import js.lib.android.utils.sdcard.PlayerMp3Utils;

/**
 * Path delegate
 */
interface CjPathDelegate {
    boolean isUDiskPath(String path);

    List<String> getListSuppportPaths();

    List<String> getBlacklistPaths();

    boolean isInBlacklist(String mediaUrl);

    boolean isHasSupportStorage();
}

/**
 * Player File Operate Methods
 *
 * @author Jun.Wang
 */
public class PlayerFileUtils extends JsFileUtils {
    // TAG
    private static final String TAG = "PlayerFileUtils";

    /**
     * FileUtilDelegate Object
     */
    private static CjPathDelegate mDelegate;

    /**
     * Initialize
     *
     * @param context {@link Context}
     */
    public static void init(Context context) {
        JsFileUtils.init(context);
        if (VersionController.isCj2Version()) {
            mDelegate = new Cj2Paths();
        } else if (VersionController.isCjVersion()) {
            mDelegate = new CjPaths();
        }
    }

    /**
     * HAR_LC3110_BAS
     */
    private static final class Cj2Paths implements CjPathDelegate {
        // /storage/sdcard1/Camera2
        // /storage/sdcard1/Camera1
        // External SD card
        static final String SD_EXTERNAL_SDCARD1 = "/storage/sdcard1";
        static final String CAMERA1 = SD_EXTERNAL_SDCARD1 + "/Camera1";
        static final String CAMERA2 = SD_EXTERNAL_SDCARD1 + "/Camera2";

        //"/mnt/media_rw/sdcard1" equals "/storage/sdcard1"
        static final String MEDIA_RW_SD_EXTERNAL_SDCARD1 = "/mnt/media_rw/sdcard1";
        static final String MEDIA_RW_CAMERA1 = MEDIA_RW_SD_EXTERNAL_SDCARD1 + "/Camera1";
        static final String MEDIA_RW_CAMERA2 = MEDIA_RW_SD_EXTERNAL_SDCARD1 + "/Camera2";

        Cj2Paths() {
        }

        /**
         * 是否是U盘路径
         */
        @Override
        public boolean isUDiskPath(String path) {
            try {
                if (path.startsWith(SD_EXTERNAL_SDCARD1) || TextUtils.equals(path, SD_EXTERNAL_SDCARD1)) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public List<String> getListSuppportPaths() {
            return new ArrayList<String>();
        }

        @Override
        public List<String> getBlacklistPaths() {
            List<String> listFilterPaths = new ArrayList<String>();
            listFilterPaths.add(CAMERA1);
            listFilterPaths.add(MEDIA_RW_CAMERA1);
            listFilterPaths.add(CAMERA2);
            listFilterPaths.add(MEDIA_RW_CAMERA2);
            return listFilterPaths;
        }

        @Override
        public boolean isInBlacklist(String mediaUrl) {
            try {
                boolean isInBlacklist = mediaUrl.startsWith(CAMERA1) || mediaUrl.startsWith(MEDIA_RW_CAMERA1)
                        || mediaUrl.startsWith(CAMERA2) || mediaUrl.startsWith(MEDIA_RW_CAMERA2);
                Log.i(TAG, "isInBlacklist : " + isInBlacklist);
                return isInBlacklist;
            } catch (Exception e) {
                Log.i(TAG, "isInBlacklist(" + mediaUrl + ") > " + e.getMessage());
                return false;
            }
        }

        @Override
        public boolean isHasSupportStorage() {
            return (PlayerMp3Utils.getAllExterSdcardPath().size() > 0);
        }
    }

    /**
     * OLA_LC8909_IND || OLA_LC8909_INS || OLA_LC8939_INC
     */
    private static final class CjPaths implements CjPathDelegate {
        // Inner SD card
        static String SD_INNER = "";
        static String AIOS_CACHE = "";

        // External SD card
        static final String SD_EXTERNAL_SDCARD1 = "/storage/sdcard1";
        static final String DVR = SD_EXTERNAL_SDCARD1 + "/DVR";
        static final String AMAPAUTO_SDCARD1 = SD_EXTERNAL_SDCARD1 + "/amapauto";

        // USB UDISK
        static final String SD_PATH_UDISK = "/storage/udisk";
        static final String AMAPAUTO_UDISK = SD_PATH_UDISK + "/amapauto";

        CjPaths() {
            try {
                // Inner Storage
                SD_INNER = Environment.getExternalStorageDirectory().getPath();
                AIOS_CACHE = SD_INNER + "/aios-eng";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 是否是U盘路径
         */
        @Override
        public boolean isUDiskPath(String path) {
            try {
                return path.startsWith(SD_PATH_UDISK) || TextUtils.equals(path, SD_PATH_UDISK);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public List<String> getListSuppportPaths() {
            List<String> listSupportPaths = new ArrayList<String>();
            if (!EmptyUtil.isEmpty(SD_INNER)) {// Inner SDCard
                listSupportPaths.add(SD_INNER);
            }
            listSupportPaths.add(SD_EXTERNAL_SDCARD1);// TCard
            listSupportPaths.add(SD_PATH_UDISK);// UDisk
            return listSupportPaths;
        }

        @Override
        public List<String> getBlacklistPaths() {
            List<String> listFilterPaths = new ArrayList<String>();
            listFilterPaths.add(DVR);
            listFilterPaths.add(AMAPAUTO_SDCARD1);
            listFilterPaths.add(AMAPAUTO_UDISK);
            listFilterPaths.add(AIOS_CACHE);
            return listFilterPaths;
        }

        @Override
        public boolean isInBlacklist(String mediaUrl) {
            try {
                return mediaUrl.startsWith(CjPaths.DVR) || mediaUrl.startsWith(CjPaths.AMAPAUTO_SDCARD1)
                        || mediaUrl.startsWith(CjPaths.AMAPAUTO_UDISK) || mediaUrl.startsWith(CjPaths.AIOS_CACHE);
            } catch (Exception e) {
                Log.i(TAG, "isInBlacklist(" + mediaUrl + ") > " + e.getMessage());
                return false;
            }
        }

        @Override
        public boolean isHasSupportStorage() {
            return true;
        }
    }

    /**
     * Get Database Path
     */
    public static String getDBPath() {
        String storePath = getRootPath() + "/db";
        createFolder(storePath);
        return getPath(storePath, "");
    }

    /**
     * Get Music Picture Store Path
     */
    public static String getMusicPicPath(String mediaUrl) {
        String storePath = getParentPath(mediaUrl) + ".music_pic";
        // String storePath = SUPPORT_PATH_0 + "/PlayerCache/Music/Pic";
        createFolder(storePath);
        return getPath(storePath, "");
    }

    /**
     * Get Video Picture Store Path
     */
    public static String getVideoPicPath(String mediaUrl) {
        String storePath = getParentPath(mediaUrl) + ".video_pic";
        createFolder(storePath);
        return getPath(storePath, "");
    }

    /**
     * 是否是U盘路径
     */
    public static boolean isUDiskPath(String path) {
        return mDelegate != null && mDelegate.isUDiskPath(path);
    }

    /**
     * Media Scan Support Paths
     */
    public static List<String> getListSuppportPaths() {
        if (mDelegate != null) {
            return mDelegate.getListSuppportPaths();
        }
        return new ArrayList<String>();
    }

    /**
     * Paths at blacklist
     */
    public static List<String> getBlacklistPaths() {
        if (mDelegate != null) {
            return mDelegate.getBlacklistPaths();
        }
        return new ArrayList<String>();
    }

    /**
     * Is Media in the blacklist
     */
    public static boolean isInBlacklist(String mediaUrl) {
        return mDelegate != null && mDelegate.isInBlacklist(mediaUrl);
    }

    public static boolean isHasSupportStorage() {
        return mDelegate != null && mDelegate.isHasSupportStorage();
    }
}
