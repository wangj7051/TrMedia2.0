package com.tricheer.player.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.utils.PlayerFileUtils;
import com.tricheer.player.utils.PlayerLogicUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.media.engine.audio.utils.AudioInfo;
import js.lib.android.media.engine.video.db.VideoDBManager;
import js.lib.android.media.engine.video.utils.VideoInfo;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.android.utils.sdcard.SDCardInfo;
import js.lib.android.utils.sdcard.SDCardUtils;

/**
 * Media Scan Receiver
 *
 * @author Jun.Wang
 */
public class MediaScanReceiver extends BroadcastReceiver {
    //TAG
    private static String TAG = "MediaScanReceiver";

    /**
     * Start list all medias action
     */
    public static final String ACTION_START_LIST = "com.tricheer.player.START.LIST.ALL_MEDIAS";

    /**
     * 挂载的路径 / 未挂载的路径
     */
    private static Set<String> mSetPathsMounted = new HashSet<>(), mSetPathsUnMounted = new HashSet<>();

    /**
     * List All mounted paths medias
     */
    @SuppressLint("StaticFieldLeak")
    private static ListMediaTask mListMediaTask;

    /**
     * Scanning listener
     */
    private static Set<MediaScanDelegate> mSetScanDelegates = new LinkedHashSet<>();

    interface MediaScanDelegate {
        void onMediaScanningStart();

        void onMediaScanningEnd();

        void onMediaScanningCancel();
    }

    public interface AudioScanDelegate extends MediaScanDelegate {
        void onMediaScanningRefresh(List<ProAudio> listMedias, boolean isOnUiThread);
    }

    public interface VideoScanDelegate extends MediaScanDelegate {
        void onMediaScanningRefresh(List<ProVideo> listMedias, boolean isOnUiThread);
    }

    public static void register(MediaScanDelegate delegate) {
        if (delegate != null) {
            mSetScanDelegates.add(delegate);
        }
    }

    public static void unregister(MediaScanDelegate delegate) {
        if (delegate != null) {
            mSetScanDelegates.remove(delegate);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Action
        String action = intent.getAction();
        Log.i(TAG, "onReceive() -> [action: " + action + "]");
//        Toast.makeText(context, action, Toast.LENGTH_LONG).show();

        // Start list Task
        if (ACTION_START_LIST.equals(action)) {
            refreshMountStatus(context);
            startListMediaTask(context);

//        } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            PlayerAppManager.exitCurrPlayer();

            // SDCard Mounted
        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            refreshMountStatus(context);
            startListMediaTask(context);

            // SDCard UnMounted
        } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
            refreshMountStatus(context);
            CommonUtil.cancelTask(mListMediaTask);
        }
    }

    /**
     * Refresh mount status of SdCard or UDisk.
     */
    public static void refreshMountStatus(Context context) {
        Log.i(TAG, "refreshMountStatus(Context)");
        if (context == null) {
            return;
        }

        // 挂载/未挂载 SDCard
        mSetPathsMounted.clear();
        mSetPathsUnMounted.clear();

        // 获取支持的挂载点
        List<String> listSupportPaths = PlayerFileUtils.getListSuppportPaths();
        // 如果SD支持路径列表为空，那么认为该设备支持所有盘符
        if (EmptyUtil.isEmpty(listSupportPaths)) {
            Log.i(TAG, "-- SUPPORT ALL --");
            HashMap<String, SDCardInfo> mapSDCardInfos = SDCardUtils.getSDCardInfos(context.getApplicationContext(), false);
            for (SDCardInfo temp : mapSDCardInfos.values()) {
                Logs.i(TAG, "refreshMountStatus() -1-> [temp:" + temp.label + "-" + temp.isMounted + "-" + temp.root);
                if (temp.isMounted) {
                    mSetPathsMounted.add(temp.root);
                } else {
                    mSetPathsUnMounted.add(temp.root);
                }
            }
            // 如果SD支持路径列表不为空，那么认为该设备只支持列表所含盘符
        } else {
            Log.i(TAG, "-- SUPPORT FIXED --");
            HashMap<String, SDCardInfo> mapAllSdCards = SDCardUtils.getSDCardInfos(context.getApplicationContext(), false);
            for (SDCardInfo temp : mapAllSdCards.values()) {
                Logs.i(TAG, "refreshMountStatus() -2-> [temp:" + temp.label + "-" + temp.isMounted + "-" + temp.root);
                if (listSupportPaths.contains(temp.root)) {
                    if (temp.isMounted) {
                        mSetPathsMounted.add(temp.root);
                    } else {
                        mSetPathsUnMounted.add(temp.root);
                    }
                }
            }
        }

        Logs.i(TAG, " *** Start ***");
        Logs.i(TAG, "mSetPathsMounted:" + mSetPathsMounted.toString());
        Logs.i(TAG, "mSetPathsUnMounted:" + mSetPathsUnMounted.toString());
        Logs.i(TAG, " ***  End  ***");
    }

    /**
     * Start List Medias Task
     */
    private void startListMediaTask(Context context) {
        Logs.i(TAG, "----startListMediaTask()----");
        CommonUtil.cancelTask(mListMediaTask);
        if (!EmptyUtil.isEmpty(mSetPathsMounted)) {
            mListMediaTask = new ListMediaTask(context);
            mListMediaTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * List Medias Task
     */
    private static class ListMediaTask extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        private Context mmContext;
        private boolean mmIsScanning = false;

        ListMediaTask(Context context) {
            mmContext = context.getApplicationContext();
        }

        //
        private Map<String, ProAudio> mmMapDbAudios;
        private Map<String, ProVideo> mmMapDbVideos;

        //
        private ArrayList<ProAudio> mmListAllAudios = new ArrayList<>();
        private ArrayList<ProVideo> mmListAllVideos = new ArrayList<>();

        //
        private ArrayList<ProAudio> mmListNewAudios = new ArrayList<>();
        private ArrayList<ProVideo> mmListNewVideos = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            Logs.i(TAG, "ListMediaTask-> onPreExecute()");
            super.onPreExecute();
            notifyScanningStart();

            //
            mmIsScanning = true;
            mmListAllAudios = new ArrayList<>();
            mmListAllVideos = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Logs.i(TAG, "ListMediaTask-> doInBackground(params)");
            // Query DB Medias
            mmMapDbAudios = AudioDBManager.instance().getMapMusics();
            mmMapDbVideos = VideoDBManager.instance().getMapVideos(true, false);

            // List All Medias
            for (String supportPath : mSetPathsMounted) {
                if (isCancelled()) {
                    break;
                }
                Logs.debugI(TAG, "ListMediaTask -> doInBackground ->[supportPath:" + supportPath + "]");
                listAllMedias(new File(supportPath));
            }
            // Save Medias
            if (!isCancelled()) {
                if (mmListNewAudios.size() > 0) {
                    int audioCount = AudioDBManager.instance().insertListMusics(mmListAllAudios);
                    Log.i(TAG, "audioCount: " + audioCount);
                }
                if (mmListNewVideos.size() > 0) {
                    int videoCount = VideoDBManager.instance().insertListVideos(mmListAllVideos);
                    Log.i(TAG, "videoCount: " + videoCount);
                }
            }
            // Refresh
            return null;
        }

        private void listAllMedias(File pf) {
            if (pf == null || TextUtils.isEmpty(pf.getPath())) {
                Logs.debugI(TAG, "ListMediaTask-> ERROR :: NULL");
                return;
            }
            Logs.debugI(TAG, "ListMediaTask-> listAllMedias(" + pf.getPath() + ")");

            //Loop list files or folders
            try {
                File[] fileArr = pf.listFiles();
                if (fileArr == null) {
                    return;
                }

                for (File cf : pf.listFiles()) {
                    if (isCancelled()) {
                        break;
                    }

                    //Don`t list hidden file.
                    if (cf.isHidden()) {
                        continue;
                    }

                    //Directory
                    if (cf.isDirectory()) {
                        listAllMedias(cf);

                        //File
                    } else if (cf.isFile()) {
                        parseFileToMedia(cf);
                    }
                }
            } catch (Exception e) {
                Logs.debugI(TAG, "--pf.listFiles() exception--" + e.getMessage());
            }
        }

        private void parseFileToMedia(File cf) {
            try {
                String path = cf.getPath();
                int lastIdxOfDot = path.lastIndexOf(".");
                if (lastIdxOfDot == -1) {
                    return;
                }

                //Don't scan the path in blacklist.
                if (PlayerFileUtils.isInBlacklist(path)) {
                    return;
                }

                // Media Suffix
                String suffix = path.substring(lastIdxOfDot);
                // Audio
                if (AudioInfo.isSupport(suffix)) {
                    Logs.debugI(TAG, "ListMediaTask -Music-> parseFileToMedia() " + cf.getName() + "----\n" + path);
                    //Rename file
                    cf = renameFileWithSpecialName(cf);
                    String renamedPath = cf.getPath();

                    //Parse media information
                    ProAudio tmpMedia;
                    if (mmMapDbAudios.containsKey(renamedPath)) {
                        tmpMedia = mmMapDbAudios.get(renamedPath);
                    } else {
                        tmpMedia = new ProAudio(mmContext, renamedPath);
                        mmListNewAudios.add(tmpMedia);
                    }

                    if (tmpMedia != null) {
                        //Check media cover picture.
                        String storePath = PlayerFileUtils.getMusicPicPath(tmpMedia.mediaUrl);
                        String coverPicFilePath = PlayerLogicUtils.getMediaPicFilePath(tmpMedia, storePath);
                        File coverPicFile = new File(coverPicFilePath);
                        if (coverPicFile.exists()) {
                            tmpMedia.coverUrl = coverPicFilePath;
                            //If cover picture is not exist, try to get it.
                        } else {
                            Bitmap coverBitmap = tmpMedia.getThumbNail(mmContext, tmpMedia.mediaUrl);
                            if (coverBitmap != null) {
                                storeBitmap(coverPicFilePath, coverBitmap);
                                tmpMedia.coverUrl = coverPicFilePath;
                            }
                        }
                        mmListAllAudios.add(tmpMedia);
                    }

                    //Refresh
                    int newSize = mmListNewAudios.size();
                    if (newSize >= 10) {
                        notifyAudioScanningRefresh(mmListNewAudios, false);
                        mmListNewAudios.clear();
                    }

                    // Video
                } else if (VideoInfo.isSupport(suffix)) {
                    Logs.debugI(TAG, "ListMediaTask -Video-> parseFileToMedia() " + cf.getName() + "----\n" + path);
                    //Rename file
                    cf = renameFileWithSpecialName(cf);
                    String renamedPath = cf.getPath();

                    //Parse media information
                    ProVideo tmpMedia;
                    if (mmMapDbVideos.containsKey(renamedPath)) {
                        tmpMedia = mmMapDbVideos.get(renamedPath);
                    } else {
                        tmpMedia = new ProVideo(mmContext, renamedPath);
                        mmListNewVideos.add(tmpMedia);
                    }

                    if (tmpMedia != null) {
                        //Check media cover picture.
                        String storePath = PlayerFileUtils.getVideoPicPath(tmpMedia.mediaUrl);
                        String coverPicFilePath = PlayerLogicUtils.getMediaPicFilePath(tmpMedia, storePath);
                        Log.i("coverScanReceiver", "coverPicFilePath: " + coverPicFilePath);
                        File coverPicFile = new File(coverPicFilePath);
                        if (coverPicFile.exists()) {
                            tmpMedia.coverUrl = coverPicFilePath;
                            //If cover picture is not exist, try to get it.
                        } else {
                            Bitmap coverBitmap = tmpMedia.getThumbNail(tmpMedia.mediaUrl, 200, 200, MediaStore.Images.Thumbnails.MINI_KIND);
                            if (coverBitmap != null) {
                                storeBitmap(coverPicFilePath, coverBitmap);
                                tmpMedia.coverUrl = coverPicFilePath;
                            }
                        }
                        mmListAllVideos.add(tmpMedia);
                    }

                    //Refresh
                    int newSize = mmListNewVideos.size();
                    if (newSize >= 10) {
                        notifyVideoScanningRefresh(mmListNewVideos, false);
                        mmListNewVideos.clear();
                    }
                }
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "parseFileToMedia()", e);
                e.printStackTrace();
            }
        }

        private File renameFileWithSpecialName(File file) {
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

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Logs.i(TAG, "ListMediaTask-> onCancelled()");
            //
            mmContext = null;
            mmIsScanning = false;
            notifyScanningCancel();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Logs.i(TAG, "ListMediaTask-> onPostExecute()");
            //
            mmContext = null;
            mmIsScanning = false;
            notifyScanningEnd();
        }

        //Notify scanning start.
        private void notifyScanningStart() {
            for (MediaScanDelegate delegate : mSetScanDelegates) {
                delegate.onMediaScanningStart();
            }
        }

        //Audio - Notify scanning refresh.
        private void notifyAudioScanningRefresh(final List<ProAudio> listMedias, boolean isOnUiThread) {
            for (MediaScanDelegate delegate : mSetScanDelegates) {
                if (delegate instanceof AudioScanDelegate) {
                    ((AudioScanDelegate) delegate).onMediaScanningRefresh(listMedias, isOnUiThread);
                }
            }
        }

        //Video - Notify scanning refresh.
        private void notifyVideoScanningRefresh(final List<ProVideo> listMedias, boolean isOnUiThread) {
            for (MediaScanDelegate delegate : mSetScanDelegates) {
                if (delegate instanceof VideoScanDelegate) {
                    ((VideoScanDelegate) delegate).onMediaScanningRefresh(listMedias, isOnUiThread);
                }
            }
        }

        //Notify scanning start.
        private void notifyScanningEnd() {
            for (MediaScanDelegate delegate : mSetScanDelegates) {
                delegate.onMediaScanningEnd();
            }
        }

        //Notify scanning cancel.
        private void notifyScanningCancel() {
            for (MediaScanDelegate delegate : mSetScanDelegates) {
                delegate.onMediaScanningCancel();
            }
        }

        boolean isScanning() {
            return mmIsScanning;
        }
    }

    public static boolean isMediaScanning() {
        return mListMediaTask != null && mListMediaTask.isScanning();
    }

    /**
     * 保存Bitmap到SD卡
     *
     * @param filePath  ： 文件路径 ，格式为“.../../example.png”
     * @param bmToStore ： 要执行保存的Bitmap
     */
    private static void storeBitmap(String filePath, Bitmap bmToStore) {
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
