package com.tricheer.player.version.base.activity.music;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;

import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.engine.VersionController;
import com.tricheer.player.engine.db.DBManager;
import com.tricheer.player.receiver.MediaScanReceiver.ScanActives;
import com.tricheer.player.utils.PlayerFileUtils;
import com.tricheer.player.utils.PlayerLogicUtils;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import js.lib.android.media.local.utils.AudioImgUtils;
import js.lib.android.media.local.utils.AudioInfo;
import js.lib.android.media.local.utils.AudioUtils;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * 播放器扩展行动作 - BASE
 *
 * @author Jun.Wang
 */
public abstract class BaseMusicExtendActionsActivity extends BaseMusicCommonActionsActivity {
    // TAG
    private final String TAG = "BaseMusicExtendActionsActivity";

    /**
     * Thread handler
     */
    protected Handler mHandler = new Handler();

    /**
     * 媒体列表集合对象
     */
    protected List<ProMusic> mListPrograms;

    /**
     * AsyncTask - 加载本地媒体
     */
    protected LoadLocalMediasTask mLoadLocalMediasTask;

    /**
     * AsyncTask - 加载外置存储设备媒体
     */
    protected LoadSDCardMediasTask mLoadSDCardMediasTask;

    /**
     * AsyncTask - 加载媒体封面
     */
    protected LoadMediaImageTask mLoadMediaImageTask;

    /**
     * AsyncTask - Merge 数据
     */
    private MergeDatasTask mMergeDatasTask;

    /**
     * 媒体加载监听
     */
    protected interface LoadMediaListener {
        void afterLoaded();
    }

    /**
     * 媒体封面加载监听器
     */
    protected interface LoadImgListener {
        void postRefresh(ProMusic program, boolean isLoadEnd);
    }

    @Override
    protected void onCreate(android.os.Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void onNotifyScanAudios(int flag, List<ProMusic> listPrgrams, Set<String> allSdMountedPaths) {
        super.onNotifyScanAudios(flag, listPrgrams, allSdMountedPaths);
        if (flag == ScanActives.REFRESH) {
            startMergeDatasTask(listPrgrams, false);
        } else if (flag == ScanActives.SYS_SCANED) {
            startMergeDatasTask(listPrgrams, true);
        } else if (flag == ScanActives.CLEAR) {
            refreshPageOnClear(allSdMountedPaths);
        } else {
            refreshOnNotifyLoading(flag);
        }
    }

    /**
     * Refresh Page On Loading
     *
     * @param loadingFlag : {@link ScanActives#START} or {@link ScanActives#END}
     */
    protected void refreshOnNotifyLoading(int loadingFlag) {
    }

    /**
     * Refresh Page On Notify Clear
     */
    protected void refreshPageOnClear(Set<String> allSdMountedPaths) {
        if (VersionController.isCjVersion()) {
            try {
                ArrayList<ProMusic> listMountedProgram = new ArrayList<>();
                for (ProMusic program : mListPrograms) {
                    boolean isProgramMounted = false;
                    for (String mountedPath : allSdMountedPaths) {
                        if (program.mediaUrl.startsWith(mountedPath)) {
                            isProgramMounted = true;
                            break;
                        }
                    }
                    if (isProgramMounted) {
                        listMountedProgram.add(program);
                    }
                }
                mListPrograms = listMountedProgram;
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "refreshPageOnClear()", e);
            }
        } else {
            release();
        }
    }

    /**
     * Start MergeDatas Task
     */
    private void startMergeDatasTask(List<ProMusic> listScannedAudios, boolean isScaned) {
        CommonUtil.cancelTask(mMergeDatasTask);
        mMergeDatasTask = new MergeDatasTask(listScannedAudios, isScaned);
        mMergeDatasTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class MergeDatasTask extends AsyncTask<Void, Void, Void> {
        private List<ProMusic> mmListScannedAudios;
        private boolean mmIsScaned = false;

        public MergeDatasTask(List<ProMusic> listScannedAudios, boolean isScaned) {
            mmListScannedAudios = listScannedAudios;
            mmIsScaned = isScaned;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (EmptyUtil.isEmpty(mListPrograms)) {
                    mListPrograms = mmListScannedAudios;
                } else {
                    Map<String, ProMusic> mapListedPrograms = new HashMap<String, ProMusic>();
                    for (ProMusic program : mListPrograms) {
                        mapListedPrograms.put(program.mediaUrl, program);
                    }
                    for (ProMusic program : mmListScannedAudios) {
                        if (!mapListedPrograms.containsKey(program.mediaUrl)) {
                            mListPrograms.add(program);
                        }
                    }
                }
                sortMediaList(mListPrograms);
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "refreshPageOnScan()", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            refreshPageOnScan(mmListScannedAudios, mmIsScaned);
        }
    }

    /**
     * Refresh Medias
     */
    protected void refreshPageOnScan(List<ProMusic> listScannedAudios, boolean isScaned) {
    }

    /**
     * 根据首字母排序列表集合
     */
    protected void sortMediaList(List<ProMusic> listPrograms) {
        // Sort Medias
        if (!EmptyUtil.isEmpty(listPrograms)) {
            for (ProMusic music : listPrograms) {
                try {
                    music.titlePinYin = mCharacterParser.getSelling(music.title);
                    String firstChar = music.titlePinYin.substring(0, 1).toUpperCase();
                    if (firstChar.matches("[A-Z]")) {
                        music.sortLetter = firstChar;
                    } else {
                        music.sortLetter = "#";
                    }
                } catch (Exception e) {
                    Logs.printStackTrace(TAG + "sortMediaList()", e);
                    music.titlePinYin = "";
                    music.sortLetter = "";
                }
            }
            Collections.sort(listPrograms, mComparator);
        }
    }

    /**
     * 加载本地媒体
     */
    protected void loadLocalMedias() {
    }

    public class LoadLocalMediasTask extends AsyncTask<Void, Integer, Void> {
        private WeakReference<LoadMediaListener> mmWeakReference;

        public LoadLocalMediasTask(LoadMediaListener l) {
            mmWeakReference = new WeakReference<LoadMediaListener>(l);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Get List Medias
            mListPrograms = DBManager.getListMusics();
            sortMediaList(mListPrograms);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                mmWeakReference.get().afterLoaded();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下拉刷新媒体
     */
    protected void loadMediasOnPullRefresh() {
    }

    /**
     * 加载外置存储设备媒体
     */
    protected void loadSDCardMedias() {
    }

    protected class LoadSDCardMediasTask extends AsyncTask<Void, Integer, Void> {
        private WeakReference<LoadMediaListener> mmWeakReference;
        private List<ProMusic> mmListNewPrograms = new ArrayList<ProMusic>();
        private List<ProMusic> mmListExistPrograms = new ArrayList<ProMusic>();

        public LoadSDCardMediasTask(LoadMediaListener l) {
            mmWeakReference = new WeakReference<>(l);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mHandler.removeCallbacks(mmLoadSDCardMediasRunnable);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mHandler.removeCallbacks(mmLoadSDCardMediasRunnable);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Refresh & Save Medias
            parseMediaInfos(AudioUtils.queryMapAudioInfos(null));
            DBManager.insertListMusics(mmListNewPrograms);
            DBManager.updateListMusics(mmListExistPrograms);
            // Sort & Notify load end
            sortMediaList(mListPrograms);
            mHandler.postDelayed(mmLoadSDCardMediasRunnable, 1000);
            return null;
        }

        private void parseMediaInfos(Map<String, AudioInfo> mapMediaInfos) {
            if (!EmptyUtil.isEmpty(mapMediaInfos)) {
                if (EmptyUtil.isEmpty(mListPrograms)) {
                    mListPrograms = DBManager.getListMusics();
                }
                // Remove Multiple
                for (ProMusic program : mListPrograms) {
                    if (isCancelled()) {
                        break;
                    }
                    if (mapMediaInfos.containsKey(program.mediaUrl)) {
                        ProMusic aiProgram = new ProMusic(mapMediaInfos.get(program.mediaUrl));
                        mmListExistPrograms.add(aiProgram);
                        ProMusic.copy(program, aiProgram);
                        mapMediaInfos.remove(program.mediaUrl);
                    }
                }
                // Add remaining Audio
                for (AudioInfo audioInfo : mapMediaInfos.values()) {
                    if (isCancelled()) {
                        break;
                    }
                    ProMusic program = new ProMusic(audioInfo);
                    mmListNewPrograms.add(program);
                    mListPrograms.add(program);
                }
            }
        }

        private Runnable mmLoadSDCardMediasRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    if (!isCancelled()) {
                        mmWeakReference.get().afterLoaded();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * 加载媒体封面
     */
    protected void loadMediaImage() {
    }

    protected class LoadMediaImageTask extends AsyncTask<Void, Integer, String> {

        private WeakReference<LoadImgListener> mmWeakReference;

        public LoadMediaImageTask(LoadImgListener l) {
            mmWeakReference = new WeakReference<>(l);
        }

        @Override
        protected String doInBackground(Void... params) {
            // Parameters
            if (EmptyUtil.isEmpty(mListPrograms)) {
                return null;
            }

            int cachedNum = 0;
            for (ProMusic program : mListPrograms) {
                if (!isCancelled() && cacheProgramCover(program)) {
                    cachedNum++;
                    if (cachedNum % 5 == 0) {
                        postResult(program, false);
                    }
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Logs.i(TAG, "LoadMediaImageTask -> ----^^ onPostExecute ^^----");
            postResult(null, true);
        }

        private boolean cacheProgramCover(ProMusic program) {
            try {
                // 已经获取过信息
                // 图片连接为在线图片
                // 以上2种情况不执行信息获取
                if (program.parseInfoFlag == 1 || PlayerLogicUtils.isHttpUrl(program.coverUrl)) {
                    return false;
                }

                // 文件不存在
                // 不是文件
                // 以上两种情况不执行信息获取
                File mediaF = new File(program.mediaUrl);
                if (!mediaF.exists() || !mediaF.isFile()) {
                    return false;
                }

                // 图片已经存在，不执行信息获取
                program.coverUrl = getExistMediaPic(mediaF);
                if (EmptyUtil.isEmpty(program.coverUrl)) {
                    getMediaInnerCover(program);
                    program.parseInfoFlag = 1;
                }
                DBManager.updateMediaCoverUrl(program);
            } catch (Throwable e) {
                Logs.printStackTrace(TAG + "cacheProgramCover()", e);
            }
            return !EmptyUtil.isEmpty(program.coverUrl);
        }

        private String getExistMediaPic(File mediaF) {
            File mediaFolder = mediaF.getParentFile();
            if (mediaFolder.exists()) {
                File imgF = new File(mediaFolder + "/" + PlayerLogicUtils.getMediaPicPath(mediaF.getName(), 1));
                if (imgF.exists()) {
                    return imgF.getPath();
                }
            }
            return "";
        }

        private void postResult(final ProMusic program, final boolean isLoadEnd) {
            if (!isCancelled()) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            mmWeakReference.get().postRefresh(program, isLoadEnd);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    /**
     * 获取当前媒体对象
     */
    public ProMusic getCurrProgram() {
        Serializable serialPro = getCurrMedia();
        if (serialPro != null) {
            return (ProMusic) serialPro;
        }
        return null;
    }

    /**
     * EXEC Play Selected Music
     */
    protected void execPlay(String mediaUrl) {
        execPlay(getPosByMediaUrl(mediaUrl));
    }

    /**
     * EXEC Play Selected Music
     */
    protected void execPlay(int playPos) {
        setPlayPosition(playPos);
        play();
    }

    /**
     * Random Select On Music To Play
     */
    protected void execPlayRandomOne() {
        if (mPlayService != null) {
            mPlayService.playRandomOne();
        }
    }

    public void execSavePlayInfo() {
        if (mPlayService != null) {
            mPlayService.savePlayInfo();
        }
    }

    /**
     * Set Play Position
     */
    public void setPlayPosition(String mediaUrl) {
        setPlayPosition(getPosByMediaUrl(mediaUrl));
    }

    /**
     * Loop and get current play position at list
     */
    protected int getPosOfList(String mediaUrl) {
        if (mPlayService != null) {
            return mPlayService.getPosAtList(mediaUrl);
        }
        return -1;
    }

    /**
     * Loop and get current play position at list
     */
    public int getPosByMediaUrl(String mediaUrl) {
        int selectPos = getPosOfList(mediaUrl);
        return selectPos >= 0 ? selectPos : 0;
    }

    @Override
    public void release() {
        super.release();
        clearPlayInfo();
    }

    /**
     * Release
     */
    protected void clearPlayInfo() {
        mListPrograms = new ArrayList<>();
    }

    /**
     * 获取并存储媒体内置封面图
     */
    protected void getMediaInnerCover(ProMusic program) {
        try {
            Bitmap sysMediaCover = AudioImgUtils.getInnerImg(mContext, program.title, program.sysMediaID, program.albumID, false,
                    -1);
            if (sysMediaCover != null) {
                String coverPicFilePath = PlayerLogicUtils.getMediaPicFilePath(program,
                        PlayerFileUtils.getMusicPicPath(program.mediaUrl));
                cacheMediaPic(coverPicFilePath, sysMediaCover);
                program.coverUrl = coverPicFilePath;
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getMediaInnerCover()", e);
        }
    }

    /**
     * Cancel All Tasks
     */
    protected void cancelAllTasks() {
        CommonUtil.cancelTask(mLoadLocalMediasTask);
        CommonUtil.cancelTask(mMergeDatasTask);
        CommonUtil.cancelTask(mLoadSDCardMediasTask);
        CommonUtil.cancelTask(mLoadMediaImageTask);
    }
}