package js.lib.android_media_scan.parse_controller;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import js.lib.android.media.bean.Program;
import js.lib.android.utils.JsFileUtils;
import js.lib.android.utils.Logs;

public class ParseMediaController {
    //TAG
    private static final String TAG = "ParseAudioController";

    /**
     * {@link ParseMediaDelegate} object
     */
    private ParseMediaDelegate mParseMediaDelegate;

    public interface ParseMediaDelegate {
        void onParseEnd();
    }

    void setParseMediaDelegate(ParseMediaDelegate delegate) {
        mParseMediaDelegate = delegate;
    }

    void notifyParseEnd() {
        if (mParseMediaDelegate != null) {
            mParseMediaDelegate.onParseEnd();
        }
    }

    public static String getCoverBitmapPath(Program media, int flag) {
        String coverStorePath = "";
        try {
            @SuppressLint("SdCardPath")
            String parentStorePath = "/sdcard/.media_pics/";//JsFileUtils.getParentPath(media.mediaUrl);
            JsFileUtils.createFolder(parentStorePath);

            //
            switch (flag) {
                case 0:
                    coverStorePath = parentStorePath + ".media_audio_pic";
                    break;
                case 1:
                    coverStorePath = parentStorePath + ".media_video_pic";
                    break;
            }
            int respFlag = JsFileUtils.createFolder(coverStorePath);
            Log.i(TAG, "respFlag:" + respFlag);
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
