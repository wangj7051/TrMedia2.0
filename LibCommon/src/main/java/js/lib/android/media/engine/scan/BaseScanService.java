package js.lib.android.media.engine.scan;

import android.app.Service;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.bean.Program;
import js.lib.android.utils.JsFileUtils;
import js.lib.android.utils.Logs;


/**
 * Scan service base.
 * <p>Supply callback.</p>
 *
 * @author Jun.Wang
 */
public abstract class BaseScanService extends Service {
    //TAG
    private static final String TAG = "BaseScanService";

    /**
     * 支持遍历的路径
     */
    protected static List<String> mListSupportPaths = new ArrayList<>();

    /**
     * 黑名单列表，在此黑名单列表中的路径不应当被扫描。
     */
    protected static List<String> mListBlacklistPaths = new ArrayList<>();

    //
    public static final String PARAM_SCAN = "PARAM_SCAN";
    public static final String PARAM_SCAN_VAL_START = "START_LIST_ALL_MEDIAS";
    public static final String PARAM_SCAN_VAL_CANCEL = "CANCEL_LIST_ALL_MEDIAS";

    /**
     * Scanning listener
     */
    private Set<IMediaScanDelegate> mSetScanDelegates = new LinkedHashSet<>();

    /**
     * Audio scan delegate.
     */
    public interface AudioScanDelegate extends IMediaScanDelegate {
        void onMediaScanningRefresh(List<ProAudio> listMedias, boolean isOnUiThread);
    }

    /**
     * Video scan delegate.
     */
    public interface VideoScanDelegate extends IMediaScanDelegate {
        void onMediaScanningRefresh(List<ProVideo> listMedias, boolean isOnUiThread);
    }

    public void register(IMediaScanDelegate delegate) {
        if (delegate != null) {
            mSetScanDelegates.add(delegate);
        }
    }

    public void unregister(IMediaScanDelegate delegate) {
        if (delegate != null) {
            mSetScanDelegates.remove(delegate);
        }
    }

    /**
     * Notify scanning start.
     */
    protected void notifyScanningStart() {
        try {
            for (IMediaScanDelegate delegate : mSetScanDelegates) {
                delegate.onMediaScanningStart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify scanning end.
     */
    protected void notifyScanningEnd() {
        try {
            for (IMediaScanDelegate delegate : mSetScanDelegates) {
                delegate.onMediaScanningEnd();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify scanning cancel.
     */
    protected void notifyScanningCancel() {
        try {
            for (IMediaScanDelegate delegate : mSetScanDelegates) {
                delegate.onMediaScanningCancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Audio - Notify scanning refresh.
     *
     * @param listMedias   Delta audio list.
     * @param isOnUiThread If callback is in UI thread.
     */
    protected void notifyAudioScanningRefresh(final List<ProAudio> listMedias, boolean isOnUiThread) {
        for (IMediaScanDelegate delegate : mSetScanDelegates) {
            if (delegate instanceof AudioScanDelegate) {
                ((AudioScanDelegate) delegate).onMediaScanningRefresh(listMedias, isOnUiThread);
            }
        }
    }

    /**
     * Video - Notify scanning refresh.
     *
     * @param listMedias   Delta video list.
     * @param isOnUiThread If callback is in UI thread.
     */
    protected void notifyVideoScanningRefresh(final List<ProVideo> listMedias, boolean isOnUiThread) {
        for (IMediaScanDelegate delegate : mSetScanDelegates) {
            if (delegate instanceof VideoScanDelegate) {
                ((VideoScanDelegate) delegate).onMediaScanningRefresh(listMedias, isOnUiThread);
            }
        }
    }

    public static String getCoverBitmapPath(Program media, int flag) {
        String coverStorePath = "";
        try {
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
    protected void storeBitmap(String filePath, Bitmap bmToStore) {
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
}
