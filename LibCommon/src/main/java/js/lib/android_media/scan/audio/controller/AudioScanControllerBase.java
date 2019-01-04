package js.lib.android_media.scan.audio.controller;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.Program;
import js.lib.android.utils.JsFileUtils;
import js.lib.android.utils.Logs;

public class AudioScanControllerBase {
    //TAG
    private static final String TAG = "AudioScanControllerBase";

    /**
     * 支持遍历的路径
     */
    static List<String> mListSupportPaths = new ArrayList<>();

    /**
     * 黑名单列表，在此黑名单列表中的路径不应当被扫描。
     */
    static List<String> mListBlacklistPaths = new ArrayList<>();

    public static void setSupportPaths(List<String> listPaths) {
        if (listPaths == null) {
            mListSupportPaths = listPaths;
        } else {
            mListSupportPaths = new ArrayList<>();
        }
    }

    public static void setBlacklistPaths(List<String> listPaths) {
        if (listPaths == null) {
            mListBlacklistPaths = listPaths;
        } else {
            mListBlacklistPaths = new ArrayList<>();
        }
    }

    protected File renameFileWithSpecialName(File file) {
        if (file != null) {
            String fName = file.getName();
            if (fName.contains("'")) {
                fName = fName.replace("'", "`");
                String fPath = file.getParent() + "/" + fName;
                File targetFile = new File(fPath);
                boolean isRenamed = file.renameTo(targetFile);
                if (isRenamed) {
                    return targetFile;
                }
            }
        }
        return file;
    }

    public void destroy() {
        mListSupportPaths.clear();
        mListBlacklistPaths.clear();
    }

    public static String getCoverBitmapPath(Program media) {
        String coverStorePath = "";
        try {
            @SuppressLint("SdCardPath")
            String parentStorePath = "/sdcard/.media_pics/";//JsFileUtils.getParentPath(media.mediaUrl);
            JsFileUtils.createFolder(parentStorePath);

            //
            coverStorePath = parentStorePath + ".media_audio_pic";
            int respFlag = JsFileUtils.createFolder(coverStorePath);
            Logs.debugI(TAG, "respFlag:" + respFlag);
            return coverStorePath + "/" + media.title + ".png";
        } catch (Exception e) {
            Log.i(TAG, "storeBitmap(Program)");
        }
        return coverStorePath;
    }

    /**
     * 保存Bitmap到SD卡
     *
     * @param filePath  ： 文件路径 ，格式为“.../../example.png”
     * @param bmToStore ： 要执行保存的Bitmap
     */
    void storeBitmap(String filePath, Bitmap bmToStore) {
        if (bmToStore != null) {
            // "/sdcard/" + bitName + ".png"
            FileOutputStream fos = null;
            try {
                //
                File targetF = new File(filePath);
                if (targetF.isDirectory() || targetF.exists()) {
                    return;
                }

                //Compress
                File tmpFile = new File(filePath + "_TEMP");
                if (tmpFile.createNewFile()) {
                    fos = new FileOutputStream(tmpFile);
                    bmToStore.compress(Bitmap.CompressFormat.PNG, 100, fos);

                    //Rename
                    if (tmpFile.renameTo(targetF)) {
                        Log.i(TAG, "storeBitmap --END--");
                    }
                }
            } catch (Throwable e) {
                Logs.printStackTrace(TAG + "storeBitmap()", e);
            } finally {
                try {
                    if (fos != null) {
                        // 刷新数据并将数据转交给操作系统
                        fos.flush();
                        // 强制系统缓冲区与基础设备同步
                        // 将系统缓冲区数据写入到文件
                        fos.getFD().sync();
                        fos.close();
                    }
                } catch (Throwable e) {
                    Logs.printStackTrace(TAG + "storeBitmap()2", e);

                }
            }
        }
    }
}
