package js.lib.android_media.scan.video.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.engine.video.db.VideoDBManager;
import js.lib.android.media.engine.video.utils.VideoInfo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.android.utils.sdcard.SDCardInfo;
import js.lib.android.utils.sdcard.SDCardUtils;

public class VideoScanController extends VideoScanControllerBase {
    //TAG
    private static final String TAG = "VideoScanController";

    /**
     * Context
     */
    private Context mContext;

    /**
     * 挂载的路径 / 未挂载的路径
     */
    private Set<String> mSetPathsMounted, mSetPathsUnMounted;

    /**
     * {@link MediaScanListener}
     */
    private MediaScanListener mMediaScanListener;

    public interface MediaScanListener {
        void onMediaScanningStart();

        void onMediaScanningCancel();

        void onMediaScanningEnd(boolean isHasMedias);

        void onMediaScanningResp(List<ProVideo> listMedias, boolean isOnUiThread);
    }

    /**
     * Set {@link MediaScanListener}
     */
    public void setMediaScanListener(MediaScanListener l) {
        mMediaScanListener = l;
    }

    /**
     * List All mounted paths medias
     */
    @SuppressLint("StaticFieldLeak")
    private static ListMediaTask mListMediaTask;

    public VideoScanController() {
        mSetPathsMounted = new HashSet<>();
        mSetPathsUnMounted = new HashSet<>();
    }

    /**
     * Start List Medias Task
     */
    public void startListMediaTask(Context context) {
        Logs.i(TAG, "----startListMediaTask()----");
        if (mListMediaTask == null || !mListMediaTask.isScanning()) {
            mContext = context;
            refreshMountStatus(context);
            if (!EmptyUtil.isEmpty(mSetPathsMounted)) {
                Log.i(TAG, "----startListMediaTask()----EXEC----");
                mListMediaTask = new ListMediaTask();
                mListMediaTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    /**
     * List Medias Task
     */
    @SuppressLint("StaticFieldLeak")
    private class ListMediaTask extends AsyncTask<Void, Void, Void> {
        //
        private boolean mmIsScanning = false;

        //
        private boolean mmIsHasVideos;

        //
        private Map<String, ProVideo> mmMapDbVideos;

        //
        private ArrayList<ProVideo> mmListNewVideos;

        //
        private final int NOTIFY_LIMIT = 5;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //START
            mmIsScanning = true;
            mmListNewVideos = new ArrayList<>();
            notifyScanStart();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.i(TAG, "ListMediaTask-> doInBackground(params)");
                // Query DB Medias - Video
                mmMapDbVideos = VideoDBManager.instance().getMapVideos(true, false);

                // List All Medias
                long startTime = System.currentTimeMillis();
                Log.i(TAG, "#### ---- START " + startTime + "---- ####");
                for (String supportPath : mSetPathsMounted) {
                    if (isCancelled()) {
                        break;
                    }
                    Logs.debugI(TAG, "ListMediaTask -> doInBackground ->[supportPath:" + supportPath + "]");
                    listAllMedias(new File(supportPath));
                }
                long endTime = System.currentTimeMillis();
                Log.i(TAG, "#### ---- END " + endTime + "---- ####");
                Log.i(TAG, "#### ---- PERIOD " + (endTime - startTime) + "---- ####");


                //Refresh
                if (mmListNewVideos.size() > 0) {
                    notifyScanRespVideo(mmListNewVideos, false);
                    saveNewVideos();
                }
            } catch (Exception e) {
                Log.i(TAG, "#### ---- END " + e.getMessage() + "---- ####");
                e.printStackTrace();
            }
            return null;
        }

        private void listAllMedias(File pf) {
            //Start list
            if (pf == null || TextUtils.isEmpty(pf.getPath())) {
                Logs.debugI(TAG, "ListMediaTask-> ERROR :: NULL");
                return;
            }
            Logs.debugI(TAG, "ListMediaTask-> listAllMedias(" + pf.getPath() + ")");

            //Don't scan the path in blacklist.
            boolean isInBlacklist = false;
            for (String path : mListBlacklistPaths) {
                if (pf.getPath().startsWith(path)) {
                    isInBlacklist = true;
                    break;
                }
            }
            if (isInBlacklist) {
                Logs.debugI(TAG, "ListMediaTask-> ERROR :: Blacklist");
                return;
            }

            //Loop list files or folders
            try {
                File[] fileArr = pf.listFiles();
                if (fileArr == null) {
                    Logs.i(TAG, "fileArr`s child is NULL.");
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

                // Media Suffix
                String suffix = path.substring(lastIdxOfDot);
                suffix = suffix.toLowerCase();
                // Video
                if (VideoInfo.isSupport(suffix)) {
                    mmIsHasVideos = true;
                    //Rename file
                    cf = renameFileWithSpecialName(cf);
                    String renamedPath = cf.getPath();
                    Logs.debugI(TAG, "Media-VIDEO :: " + renamedPath);

                    //Parse media information
                    if (!mmMapDbVideos.containsKey(renamedPath)) {
                        ProVideo tmpMedia = new ProVideo(renamedPath);
                        mmListNewVideos.add(tmpMedia);

                        //Check media cover picture.
                        String coverPicFilePath = getCoverBitmapPath(tmpMedia);
                        Log.i("VideoCoverCheck", "coverPicFilePath: " + coverPicFilePath);
                        File coverPicFile = new File(coverPicFilePath);
                        if (coverPicFile.exists()) {
                            Log.i("VideoCoverCheck", " [ EXIST ]");
                            tmpMedia.coverUrl = coverPicFilePath;
                            //If cover picture is not exist, try to get it.
                        } else {
                            Log.i("VideoCoverCheck", " [ CREATE NEW ]");
                            Bitmap coverBitmap = tmpMedia.getThumbNail(tmpMedia.mediaUrl, 200, 200, MediaStore.Images.Thumbnails.MINI_KIND);
                            if (coverBitmap == null) {
                                Log.i("VideoCoverCheck", " [ CREATE NEW ] - FAIL - ");
                            } else {
                                Log.i("VideoCoverCheck", " [ CREATE NEW ] - SUCCESSFULLY - ");
                                storeBitmap(coverPicFilePath, coverBitmap);
                                tmpMedia.coverUrl = coverPicFilePath;
                            }
                        }
                    }

                    //Refresh
                    int size = mmListNewVideos.size();
                    if (size > 0 && (size % NOTIFY_LIMIT == 0)) {
                        notifyScanRespVideo(mmListNewVideos, false);
                        saveNewVideos();
                    }
                }
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "parseFileToMedia()", e);
                e.printStackTrace();
            }
        }

        private void saveNewVideos() {
            int count = VideoDBManager.instance().insertListVideos(mmListNewVideos);
            Log.i(TAG, "saveNewVideos() - count:" + count);
            mmListNewVideos.clear();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Logs.i(TAG, "ListMediaTask-> onCancelled()");
            mmIsScanning = false;
            notifyScanCancel();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Logs.i(TAG, "ListMediaTask-> onPostExecute()");
            mmIsScanning = false;
            notifyScanVideoEnd(mmIsHasVideos);
        }

        boolean isScanning() {
            return mmIsScanning;
        }

        List<ProVideo> getNewVideos() {
            return mmListNewVideos;
        }

        private void destroy() {
            mmIsScanning = false;
        }

        private void notifyScanStart() {
            if (mMediaScanListener != null) {
                mMediaScanListener.onMediaScanningStart();
            }
        }

        private void notifyScanCancel() {
            if (mMediaScanListener != null) {
                mMediaScanListener.onMediaScanningCancel();
            }
        }

        private void notifyScanVideoEnd(boolean isHasMedias) {
            if (mMediaScanListener != null) {
                mMediaScanListener.onMediaScanningEnd(isHasMedias);
            }
        }

        private void notifyScanRespVideo(List<ProVideo> listMedias, boolean isOnUiThread) {
            if (mMediaScanListener != null) {
                mMediaScanListener.onMediaScanningResp(listMedias, isOnUiThread);
            }
        }
    }

    /**
     * Refresh mount status of SdCard or UDisk.
     */
    private void refreshMountStatus(Context context) {
        Log.i(TAG, "refreshMountStatus() - [appContext: " + context);
        Context appContext = context.getApplicationContext();

        // 挂载/未挂载 SDCard
        mSetPathsMounted.clear();
        mSetPathsUnMounted.clear();

        // 获取支持的挂载点
        // 如果SD支持路径列表为空，那么认为该设备支持所有盘符
        HashMap<String, SDCardInfo> mapAllSdCards = SDCardUtils.getSDCardInfos(appContext, true);
        if (EmptyUtil.isEmpty(mListSupportPaths)) {
            Log.i(TAG, "-- SUPPORT ALL --");
            for (SDCardInfo temp : mapAllSdCards.values()) {
                Logs.i(TAG, "[temp:" + temp.label + "-" + temp.isMounted + "-" + temp.root);
                if (temp.isMounted) {
                    mSetPathsMounted.add(temp.root);
                } else {
                    mSetPathsUnMounted.add(temp.root);
                }
            }
            // 如果SD支持路径列表不为空，那么认为该设备只支持列表所含盘符
        } else {
            Log.i(TAG, "-- SUPPORT FIXED --");
            for (SDCardInfo temp : mapAllSdCards.values()) {
                if (mListSupportPaths.contains(temp.root)) {
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

    public boolean isMediaScanning() {
        return mListMediaTask != null && mListMediaTask.isScanning();
    }

    public List<ProVideo> getNewVideos() {
        if (mListMediaTask != null) {
            return mListMediaTask.getNewVideos();
        }
        return new ArrayList<>();
    }

    public void destroy() {
        if (mListMediaTask != null) {
            mListMediaTask.cancel(true);
            mListMediaTask.destroy();
            mListMediaTask = null;
        }
        super.destroy();
    }
}
