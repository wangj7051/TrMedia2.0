package com.tricheer.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.tricheer.app.receiver.PlayerReceiverActions;
import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.engine.PlayerType;
import com.tricheer.player.engine.VersionController;
import com.tricheer.player.engine.db.DBManager;
import com.tricheer.player.receiver.PlayerBaseReceiver.PlayerReceiverListener;
import com.tricheer.player.utils.PlayerFileUtils;
import com.tricheer.player.utils.PlayerPreferUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import js.lib.android.media.local.utils.AudioInfo;
import js.lib.android.media.local.utils.AudioUtils;
import js.lib.android.media.local.utils.VideoInfo;
import js.lib.android.media.local.utils.VideoUtils;
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
    // TAG
    private static final String TAG = "MediaScanReceiver";

    //
    private static Context mContext;
    private static Handler mHandler = new Handler();

    /**
     * System Down Flag
     */
    private static boolean mIsSystemDown = false;

    /**
     * Start list all medias action
     */
    public static final String ACTION_START_LIST = "com.tricheer.player.START.LIST.ALL_MEDIAS",
            LOADING_FLAG = "IS_LOADING_SHOWING";

    // Audio
    private static ArrayList<ProMusic> mListMusics = new ArrayList<ProMusic>(), mListNewMusics = new ArrayList<ProMusic>();
    private static ArrayList<String> mListToSysScanAudios = new ArrayList<String>();

    // Video
    private static ArrayList<ProVideo> mlistVideos = new ArrayList<ProVideo>(), mListNewVideos = new ArrayList<ProVideo>();
    private static List<String> mListToSysScanVideos = new ArrayList<String>();

    /**
     * 支持的挂载点信息
     */
    private static Map<String, SDCardInfo> mMapSupportSDCards = new HashMap<String, SDCardInfo>();

    /**
     * 挂载的路径 / 未挂载的路径
     */
    private static Set<String> mSetPathsMounted = new HashSet<String>(), mSetPathsUnMounted = new HashSet<String>();

    /**
     * 已挂载 / 未挂载
     * <p>
     * (1) ("init()" || "收到ACTION_START_LIST")，更新
     * <p>
     * (2) 收到[Intent.ACTION_MEDIA_MOUNTED 或 Intent.ACTION_MEDIA_UNMOUNTED]时， 且("新挂载"||"新移除") &&
     * "设备支持该挂载点"，更新
     */
    private static boolean mIsMounted = false, mIsUnMounted = false;

    /**
     * List All mounted paths medias
     */
    private static ListMediaTask mListMediaTask;

    /**
     * Scan Active
     */
    public interface ScanActives {
        int START = 1;
        int END = 2;
        int TASK_CANCEL = 4;
        int REFRESH = 5;
        int SYS_SCANED = 6;
        int CLEAR = 7;
    }

    /**
     * 在应用启动时候必须调用此方法初始化
     */
    public static void init(Context context) {
        mContext = context;
        refreshMountStatus();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Action
        String action = intent.getAction();
        Log.i(TAG, "onReceive() -> [action:" + action);

        // System Down/Up
        if (PlayerReceiverActions.SYSTEM_DOWN.equals(action)) {
            mIsSystemDown = true;
        } else if (PlayerReceiverActions.SYSTEM_UP.equals(action)) {
            mIsSystemDown = false;
            refreshMountStatus();

            // After 80s , System will drop down UDisk
        } else if (PlayerReceiverActions.SYSTEM_POWER_DISCONN.equals(action)) {
            PlayerPreferUtils.isLastPowerDisconned(true, true);
            PlayerAppManager.exitCurrPlayer();

            // Start list Task
        } else if (ACTION_START_LIST.equals(action)) {
            refreshMountStatus();
            startListMediaTask();

        } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

            // SDCard Mounted
        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            refreshMountStatus();
            if (mIsMounted) {
                mIsMounted = false;
                startListMediaTask();
            }

            // SDCard UnMounted
        } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
            refreshMountStatus();
            if (mIsUnMounted) {
                mIsUnMounted = false;
                CommonUtil.cancelTask(mListMediaTask);
                // If System Down ,just stop clear
                if (!mIsSystemDown) {
                    clearPlayCacheInfos();
                    for (String supportPath : mSetPathsUnMounted) {
                        DBManager.deleteMusics(supportPath);
                        DBManager.deleteVideos(supportPath);
                    }
                }
                // Refresh
                notifyAudiosRefresh(ScanActives.CLEAR);
                notifyVideosRefresh(ScanActives.CLEAR);
            }

            //
        } else if ("android.os.storage.action.VOLUME_STATE_CHANGED".equals(action)) {
        }
    }

    /**
     * Start List Medias Task
     */
    private void startListMediaTask() {
        Logs.i(TAG, "----startListMediaTask()----");
        CommonUtil.cancelTask(mListMediaTask);
        if (!EmptyUtil.isEmpty(mSetPathsMounted)) {
            mListMediaTask = new ListMediaTask();
            mListMediaTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * List Medias Task
     */
    private static class ListMediaTask extends AsyncTask<Void, Void, Void> {
        private Map<String, ProMusic> mmMapDBMusics;
        private Map<String, AudioInfo> mmMapSDCardAudios;
        private Map<String, ProVideo> mmMapDBVideos;
        private Map<String, VideoInfo> mmMapSDCardVideos;

        long insertMusicCount = 0;
        int insertVideoCount = 0;

        @Override
        protected void onPreExecute() {
            Logs.i(TAG, "ListMediaTask-> onPreExecute()");
            super.onPreExecute();
            // Notify Start
            notifyAudiosRefresh(ScanActives.START);
            notifyVideosRefresh(ScanActives.START);
            // Reset Audio
            mListMusics.clear();
            mListNewMusics.clear();
            mListToSysScanAudios.clear();
            // Reset Video
            mlistVideos.clear();
            mListNewVideos.clear();
            mListToSysScanVideos.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Logs.i(TAG, "ListMediaTask-> doInBackground(params)");
            // Query DB Medias
            mmMapDBMusics = DBManager.getMapMusics();
            mmMapDBVideos = DBManager.getMapVideos(true, false);
            // Query SDCard Medias
            mmMapSDCardAudios = AudioUtils.queryMapAudioInfos(null);
            mmMapSDCardVideos = VideoUtils.queryMapVideoInfos(null);

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
                insertMusicCount = DBManager.insertListMusics(mListNewMusics);
                insertVideoCount = DBManager.insertListVideos(mListNewVideos);
            }
            // Refresh
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Logs.i(TAG, "ListMediaTask-> onCancelled()");
            notifyAudiosRefresh(ScanActives.TASK_CANCEL);
            notifyVideosRefresh(ScanActives.TASK_CANCEL);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Logs.i(TAG, "ListMediaTask-> onPostExecute()");
            if (!isCancelled()) {
                // Notify END
                notifyAudiosRefresh(ScanActives.END);
                notifyVideosRefresh(ScanActives.END);
                // Notify REFRESH
                notifyAudiosRefresh(ScanActives.REFRESH);
                notifyVideosRefresh(ScanActives.REFRESH);
                // Start System Scanner
                startScanMusics();
                startScanVideos();
            }
        }

        private void listAllMedias(File pf) {
            if (pf == null || TextUtils.isEmpty(pf.getPath())) {
                Logs.debugI(TAG, "ListMediaTask-> ERROR :: NULL");
                return;
            }
            Logs.debugI(TAG, "ListMediaTask-> listAllMedias(" + pf.getPath() + ")");

            //Loop list files or folders
            try {
                File[] fArrs = pf.listFiles();
                if (fArrs == null) {
                    return;
                }

                for (File cf : pf.listFiles()) {
                    if (isCancelled()) {
                        break;
                    }

                    // Loop List
                    if (cf.isDirectory() && !cf.isHidden()) {
                        listAllMedias(cf);
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
                Logs.debugI(TAG, "ListMediaTask-> parseFileToMedia(" + path + ")");
                int lastIdxOfDot = path.lastIndexOf(".");
                if (lastIdxOfDot == -1) {
                    return;
                }

                // Media Suffix
                String suffix = path.substring(lastIdxOfDot);
                // Get Music
                if (AudioInfo.isSupport(suffix) && !PlayerFileUtils.isInBlacklist(path)) {
                    Logs.debugI(TAG, "ListMediaTask -Music-> parseFileToMedia() " + cf.getName() + "----\n" + path);
                    renameFileWithSpecialName(cf);
                    if (mmMapDBMusics.containsKey(path)) {
                        mListMusics.add(mmMapDBMusics.get(path));
                    } else if (mmMapSDCardAudios.containsKey(path)) {
                        ProMusic program = new ProMusic(mmMapSDCardAudios.get(path));
                        mListNewMusics.add(program);
                        mListMusics.add(program);
                    } else {
                        ProMusic program = new ProMusic(path, PlayerFileUtils.getFileName(cf, false));
                        mListNewMusics.add(program);
                        mListMusics.add(program);
                        mListToSysScanAudios.add(path);
                    }

                    // Get Video
                } else if (VideoInfo.isSupport(suffix) && !PlayerFileUtils.isInBlacklist(path)) {
                    Logs.debugI(TAG, "ListMediaTask -Video-> parseFileToMedia() " + cf.getName() + "----\n" + path);
                    renameFileWithSpecialName(cf);
                    if (mmMapDBVideos.containsKey(path)) {
                        mlistVideos.add(mmMapDBVideos.get(path));
                    } else if (mmMapSDCardVideos.containsKey(path)) {
                        ProVideo program = new ProVideo(mmMapSDCardVideos.get(path));
                        mListNewVideos.add(program);
                        mlistVideos.add(program);
                    } else {
                        ProVideo program = new ProVideo(path, cf.getName());
                        mListNewVideos.add(program);
                        mlistVideos.add(program);
                        mListToSysScanVideos.add(path);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logs.printStackTrace(TAG + "parseFileToMedia()", e);
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
                        file = targetFile;
                    }
                }
            }
            return file;
        }
    }

    /**
     * Scan Musics to System DataBase
     */
    private static void startScanMusics() {
        if (mListToSysScanAudios.size() <= 0) {
            return;
        }

        // Scan Audio
        final Object[] objArrAudioPaths = mListToSysScanAudios.toArray();
        if (objArrAudioPaths != null && objArrAudioPaths.length > 0) {
            Logs.i(TAG, "startScanMusics() ----|> START <|----");
            String[] toScanArr = new String[objArrAudioPaths.length];
            for (int idx = 0; idx < objArrAudioPaths.length; idx++) {
                toScanArr[idx] = (String) objArrAudioPaths[idx];
            }

            AudioUtils.scanAudios(mContext, toScanArr, new OnScanCompletedListener() {

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    mHandler.removeCallbacks(mScannedCompletedRunnable);
                    if (uri != null) {
                        mHandler.postDelayed(mScannedCompletedRunnable, 1000);
                    }
                }

                private Runnable mScannedCompletedRunnable = new Runnable() {

                    @Override
                    public void run() {
                        ArrayList<ProMusic> listToSaveMusics = new ArrayList<ProMusic>();
                        List<AudioInfo> listAudioInfos = AudioUtils.queryListAudioInfos(mListToSysScanAudios);
                        for (AudioInfo audio : listAudioInfos) {
                            if (AudioUtils.isExist(audio.path)) {
                                listToSaveMusics.add(new ProMusic(audio));
                            }
                        }
                        if (!EmptyUtil.isEmpty(listToSaveMusics)) {
                            DBManager.updateListMusics(listToSaveMusics);
                        }
                        notifyAudiosRefresh(ScanActives.SYS_SCANED);
                        Logs.i(TAG, "startScanMusics() ----|> END <|----");
                    }
                };
            });
        }
    }

    /**
     * Scan Videos to System DataBase
     */
    private static void startScanVideos() {
        if (mListToSysScanVideos.size() <= 0) {
            return;
        }

        // Scan Audio
        final Object[] objArrVideoPaths = mListToSysScanVideos.toArray();
        if (objArrVideoPaths != null && objArrVideoPaths.length > 0) {
            Logs.i(TAG, "startScanVideos() ----|> START <|----");
            String[] toScanArr = new String[objArrVideoPaths.length];
            for (int idx = 0; idx < objArrVideoPaths.length; idx++) {
                toScanArr[idx] = (String) objArrVideoPaths[idx];
            }

            VideoUtils.scanVideos(mContext, toScanArr, new OnScanCompletedListener() {

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    mHandler.removeCallbacks(mScannedCompletedRunnable);
                    if (uri != null) {
                        mHandler.postDelayed(mScannedCompletedRunnable, 1000);
                    }
                }

                private Runnable mScannedCompletedRunnable = new Runnable() {

                    @Override
                    public void run() {
                        ArrayList<ProVideo> listToSaveVideos = new ArrayList<ProVideo>();
                        List<VideoInfo> listVideoInfos = VideoUtils.queryListVideoInfos(mListToSysScanVideos);
                        for (VideoInfo video : listVideoInfos) {
                            if (AudioUtils.isExist(video.path)) {
                                listToSaveVideos.add(new ProVideo(video));
                            }
                        }
                        if (!EmptyUtil.isEmpty(listToSaveVideos)) {
                            DBManager.updateListVideos(listToSaveVideos);
                        }
                        notifyVideosRefresh(ScanActives.SYS_SCANED);
                        Logs.i(TAG, "startScanVideos() ----|> End <|----");
                    }
                };
            });
        }
    }

    /**
     * Broadcast Refresh Musics
     */
    private static void notifyAudiosRefresh(int flag) {
        Logs.i(TAG, "notifyAudiosRefresh(" + flag + ") -> [AuidoSize:" + mListMusics.size());
        // Notify Player
        if (PlayerType.isMusic()) {
            PlayerReceiverListener player = PlayerAppManager.getCurrPlayer();
            if (player != null) {
                player.onNotifyScanAudios(flag, mListMusics, mSetPathsMounted);
            }
        }
        // Notify List
        Context cxtList = PlayerAppManager.getCxt(PlayerCxtFlag.MUSIC_LIST);
        if (cxtList != null) {
            if (cxtList instanceof PlayerReceiverListener) {
                ((PlayerReceiverListener) cxtList).onNotifyScanAudios(flag, mListMusics, mSetPathsMounted);
            }
        }
    }

    /**
     * Broadcast Refresh Videos
     */
    private static void notifyVideosRefresh(int flag) {
        Logs.i(TAG, "notifyVideosRefresh(" + flag + ") -> [VideoSize:" + mlistVideos.size());
        // Notify Player
        if (PlayerType.isVideo()) {
            PlayerReceiverListener player = PlayerAppManager.getCurrPlayer();
            if (player != null) {
                player.onNotifyScanVideos(flag, mlistVideos, mSetPathsMounted);
            }
        }
        // Notify List
        Context cxtList = PlayerAppManager.getCxt(PlayerCxtFlag.VIDEO_LIST);
        if (cxtList != null) {
            if (cxtList instanceof PlayerReceiverListener) {
                ((PlayerReceiverListener) cxtList).onNotifyScanVideos(flag, mlistVideos, mSetPathsMounted);
            }
        }
    }

    /**
     * Get Mounted SDCard Paths On Receive Action:[Intent.ACTION_MEDIA_MOUNTED]
     */
    public static void refreshMountStatus() {
        Log.i(TAG, "refreshMountStatus()");
        // 挂载/未挂载 SDCard
        mSetPathsMounted.clear();
        mSetPathsUnMounted.clear();
        // 重置挂载/未挂载标记
        mIsMounted = false;
        mIsUnMounted = false;

        // 挂载状态发生改变
        Set<String> setMountStatusChangedPaths = new HashSet<String>();
        Set<String> setUnMountStatusChangedPaths = new HashSet<String>();

        // 获取支持的挂载点
        List<String> listSupportPaths = PlayerFileUtils.getListSuppportPaths();
        // 如果SD支持路径列表为空，那么认为该设备支持所有盘符
        if (EmptyUtil.isEmpty(listSupportPaths)) {
            Log.i(TAG, "refreshMountStatus() - support ALL -");
            HashMap<String, SDCardInfo> mapSDCardInfos = SDCardUtils.getSDCardInfos(mContext);
            for (SDCardInfo temp : mapSDCardInfos.values()) {
                Logs.i(TAG, "refreshMountStatus() -1-> [temp:" + temp.label + "-" + temp.isMounted + "-" + temp.root);
                mIsMounted = true;
                mMapSupportSDCards.put(temp.root, temp);
                // 已挂载
//                if (temp.isMounted) {
                mSetPathsMounted.add(temp.root);
                // 未挂载
//                } else {
//                mSetPathsUnMounted.add(temp.root);
//                }
            }
            Logs.i(TAG, "refreshMountStatus() -1-> [mMapSupportSDCards:" + mMapSupportSDCards.toString());

            // 如果SD支持路径列表不为空，那么认为该设备只支持列表所含盘符
        } else if (EmptyUtil.isEmpty(mMapSupportSDCards)) {
            Log.i(TAG, "refreshMountStatus() - support FIXED -");
            HashMap<String, SDCardInfo> mapSDCardInfos = SDCardUtils.getSDCardInfos(mContext);
            for (SDCardInfo sdcardInfo : mapSDCardInfos.values()) {
                if (listSupportPaths.contains(sdcardInfo.root)) {
                    mMapSupportSDCards.put(sdcardInfo.root, sdcardInfo);
                }
            }
            Logs.i(TAG, "refreshMountStatus() -2-> [mMapSupportSDCards:" + mMapSupportSDCards.toString());

            // 遍历获取SDCard状态
            for (SDCardInfo temp : mapSDCardInfos.values()) {
                Logs.i("temp", temp.root + " : " + temp.isMounted);
                // 检测支持的SD状态
                SDCardInfo supportSDCardInfo = mMapSupportSDCards.get(temp.root);
                if (supportSDCardInfo != null) {
                    // 状态不一致，表示该SDCard的Mount状态有改变
                    if (supportSDCardInfo.isMounted != temp.isMounted) {
                        if (temp.isMounted) {
                            setMountStatusChangedPaths.add(temp.root);
                        } else {
                            setUnMountStatusChangedPaths.add(temp.root);
                        }
                        supportSDCardInfo.isMounted = temp.isMounted;
                    }
                    // 已挂载
                    if (temp.isMounted) {
                        mSetPathsMounted.add(temp.root);
                        // 未挂载
                    } else {
                        mSetPathsUnMounted.add(temp.root);
                    }
                }
            }

            // 设置挂载/未挂载标记
            mIsMounted = setMountStatusChangedPaths.size() > 0 ? true : false;
            mIsUnMounted = setUnMountStatusChangedPaths.size() > 0 ? true : false;
        }

        Logs.i(TAG, " *** Start ***");
        Logs.i(TAG, "mIsMounted:" + mIsMounted);
        Logs.i(TAG, "mIsUnMounted:" + mIsUnMounted);
        Logs.i(TAG, "mSetPathsMounted:" + mSetPathsMounted.toString());
        Logs.i(TAG, "mSetPathsUnMounted:" + mSetPathsUnMounted.toString());
        Logs.i(TAG, " ***  End  ***");
    }

    /**
     * Clear Played Media Information
     */
    private void clearPlayCacheInfos() {
        if (VersionController.isCjVersion()) {
            return;
        }
        // Clear Music Informations
        String lastMediaUrl = PlayerPreferUtils.getLastTargetMediaUrl(false, PlayerCxtFlag.MUSIC_PLAYER, "");
        if (!EmptyUtil.isEmpty(lastMediaUrl)) {
            for (String root : mSetPathsUnMounted) {
                try {
                    if (lastMediaUrl.startsWith(root)) {
                        PlayerPreferUtils.getLastTargetMediaUrl(true, PlayerCxtFlag.MUSIC_PLAYER, "");
                        PlayerPreferUtils.getLastPlayedMediaInfo(true, PlayerCxtFlag.MUSIC_PLAYER, "", 0);
                        break;
                    }
                } catch (Exception e) {
                    Logs.printStackTrace(TAG + "clearPlayCacheInfos()", e);
                }
            }
        }

        // Clear Video Informations
        lastMediaUrl = PlayerPreferUtils.getLastTargetMediaUrl(false, PlayerCxtFlag.VIDEO_PLAYER, "");
        if (!EmptyUtil.isEmpty(lastMediaUrl)) {
            for (String root : mSetPathsUnMounted) {
                try {
                    if (lastMediaUrl.startsWith(root)) {
                        PlayerPreferUtils.getLastTargetMediaUrl(true, PlayerCxtFlag.VIDEO_PLAYER, "");
                        PlayerPreferUtils.getLastPlayedMediaInfo(true, PlayerCxtFlag.VIDEO_PLAYER, "", 0);
                        break;
                    }
                } catch (Exception e) {
                    Logs.printStackTrace(TAG + "clearPlayCacheInfos()", e);
                }
            }
        }
    }
}
