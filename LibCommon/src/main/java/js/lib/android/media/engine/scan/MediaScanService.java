package js.lib.android.media.engine.scan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.media.engine.audio.utils.AudioInfo;
import js.lib.android.media.engine.video.db.VideoDBManager;
import js.lib.android.media.engine.video.utils.VideoInfo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.android.utils.sdcard.SDCardInfo;
import js.lib.android.utils.sdcard.SDCardUtils;

/**
 * Media scan service.
 * <p>This service should start when the system boot completed.</p>
 *
 * @author Jun.Wang
 */
public class MediaScanService extends BaseScanService {
    //TAG
    private static final String TAG = "MediaScanService";

    /**
     * {@link Context}
     */
    private Context mContext;

    /**
     * 挂载的路径 / 未挂载的路径
     */
    private Set<String> mSetPathsMounted, mSetPathsUnMounted;

    /**
     * List All mounted paths medias
     */
    @SuppressLint("StaticFieldLeak")
    private static ListMediaTask mListMediaTask;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        Log.i(TAG, "init()");
        mContext = this;
        mSetPathsMounted = new HashSet<>();
        mSetPathsUnMounted = new HashSet<>();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends IMediaScanService.Stub {

        @Override
        public void startScan() {
            Log.i(TAG, "MyBinder->startScan()");
            refreshMountStatus(mContext);
            startListMediaTask(mContext);
        }

        @Override
        public void destroy() {
            Log.i(TAG, "MyBinder->destroy()");
            cancelListMediaTask();
            stopSelf();
        }

        @Override
        public boolean isMediaScanning() {
            return mListMediaTask != null && mListMediaTask.isScanning();
        }

        @Override
        public void registerDelegate(IMediaScanDelegate delegate) {
            if (delegate != null) {
                Log.i(TAG, "MyBinder->registerDelegate(" + delegate.toString() + ")");
                register(delegate);
            }
        }

        @Override
        public void unregisterDelegate(IMediaScanDelegate delegate) {
            if (delegate != null) {
                Log.i(TAG, "MyBinder->unregister(" + delegate.toString() + ")");
                unregister(delegate);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String param = intent.getStringExtra(PARAM_SCAN);
            Log.i(TAG, "param : " + param);
            if (PARAM_SCAN_VAL_START.equals(param)) {
                refreshMountStatus(this);
                startListMediaTask(this);
            } else if (PARAM_SCAN_VAL_CANCEL.equals(param)) {
                refreshMountStatus(this);
                cancelListMediaTask();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Start List Medias Task
     */
    private void startListMediaTask(Context context) {
        Logs.i(TAG, "----startListMediaTask()----");
        if (mListMediaTask == null || !mListMediaTask.isScanning()) {
            if (!EmptyUtil.isEmpty(mSetPathsMounted)) {
                mListMediaTask = new ListMediaTask(context);
                mListMediaTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    /**
     * Cancel list media task.
     */
    private void cancelListMediaTask() {
        if (mListMediaTask != null) {
            mListMediaTask.cancel(true);
            mListMediaTask.destroy();
            mListMediaTask = null;
        }
    }

    /**
     * List Medias Task
     */
    @SuppressLint("StaticFieldLeak")
    private class ListMediaTask extends AsyncTask<Void, Void, Void> {
        //
        private Context mmContext;
        private boolean mmIsScanning = false;

        //
        private Map<String, ProAudio> mmMapDbAudios;
        private Map<String, ProVideo> mmMapDbVideos;

        //
        private ArrayList<ProAudio> mmListNewAudios = new ArrayList<>();
        private ArrayList<ProVideo> mmListNewVideos = new ArrayList<>();

        ListMediaTask(Context context) {
            mmContext = context.getApplicationContext();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //START
            mmIsScanning = true;
            notifyScanningStart();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Logs.i(TAG, "ListMediaTask-> doInBackground(params)");
            // Query DB Medias
            mmMapDbAudios = AudioDBManager.instance().getMapMusics();
            mmMapDbVideos = VideoDBManager.instance().getMapVideos(true, false);

            // List All Medias
            Log.i(TAG, "#### ---- START " + System.currentTimeMillis() + "---- ####");
            for (String supportPath : mSetPathsMounted) {
                if (isCancelled()) {
                    break;
                }
                Logs.debugI(TAG, "ListMediaTask -> doInBackground ->[supportPath:" + supportPath + "]");
                listAllMedias(new File(supportPath));
            }
            Log.i(TAG, "#### ---- END " + System.currentTimeMillis() + "---- ####");

            //
            saveDeltaAudios();
            saveDeltaVideos();
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
                        String coverPicFilePath = getCoverBitmapPath(tmpMedia, 0);
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
                    }

                    //Refresh
                    if (mmListNewAudios.size() >= 10) {
                        saveDeltaAudios();
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
                        String coverPicFilePath = getCoverBitmapPath(tmpMedia, 1);
                        Log.i("coverScanReceiver", "coverPicFilePath: " + coverPicFilePath);
                        File coverPicFile = new File(coverPicFilePath);
                        if (coverPicFile.exists()) {
                            Log.i("coverScanReceiver", " [ EXIST ]");
                            tmpMedia.coverUrl = coverPicFilePath;
                            //If cover picture is not exist, try to get it.
                        } else {
                            Log.i("coverScanReceiver", " [ CREATE NEW ]");
                            Bitmap coverBitmap = tmpMedia.getThumbNail(tmpMedia.mediaUrl, 200, 200, MediaStore.Images.Thumbnails.MINI_KIND);
                            if (coverBitmap == null) {
                                Log.i("coverScanReceiver", " [ CREATE NEW ] - FAIL - ");
                            } else {
                                Log.i("coverScanReceiver", " [ CREATE NEW ] - SUCCESSFULLY - ");
                                storeBitmap(coverPicFilePath, coverBitmap);
                                tmpMedia.coverUrl = coverPicFilePath;
                            }
                        }
                    }

                    //Refresh
                    if (mmListNewVideos.size() >= 10) {
                        saveDeltaVideos();
                    }
                }
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "parseFileToMedia()", e);
                e.printStackTrace();
            }
        }

        private void saveDeltaAudios() {
            if (mmListNewAudios.size() > 0) {
                int count = AudioDBManager.instance().insertListMusics(mmListNewAudios);
                Log.i(TAG, "count:" + count);
                notifyAudioScanningRefresh(mmListNewAudios, false);
                mmListNewAudios.clear();
            }
        }

        private void saveDeltaVideos() {
            if (mmListNewVideos.size() > 0) {
                int count = VideoDBManager.instance().insertListVideos(mmListNewVideos);
                Log.i(TAG, "count:" + count);
                notifyVideoScanningRefresh(mmListNewVideos, false);
                mmListNewVideos.clear();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Logs.i(TAG, "ListMediaTask-> onCancelled()");
            destroy();
            notifyScanningCancel();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Logs.i(TAG, "ListMediaTask-> onPostExecute()");
            destroy();
            notifyScanningEnd();
        }

        boolean isScanning() {
            return mmIsScanning;
        }

        private void destroy() {
            mmContext = null;
            mmIsScanning = false;
        }
    }

    /**
     * Refresh mount status of SdCard or UDisk.
     */
    public void refreshMountStatus(Context context) {
        Log.i(TAG, "refreshMountStatus(Context)");
        if (context == null) {
            return;
        }

        // 挂载/未挂载 SDCard
        mSetPathsMounted.clear();
        mSetPathsUnMounted.clear();

        // 获取支持的挂载点
        // 如果SD支持路径列表为空，那么认为该设备支持所有盘符
        HashMap<String, SDCardInfo> mapAllSdCards = SDCardUtils.getSDCardInfos(context.getApplicationContext(), true);
        if (EmptyUtil.isEmpty(mListSupportPaths)) {
            Log.i(TAG, "-- SUPPORT ALL --");
            for (SDCardInfo temp : mapAllSdCards.values()) {
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

    private void startParseMediaTask() {
    }

    /**
     * List Medias Task
     */
    @SuppressLint("StaticFieldLeak")
    private class MediaParseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        cancelListMediaTask();
        super.onDestroy();
    }
}
