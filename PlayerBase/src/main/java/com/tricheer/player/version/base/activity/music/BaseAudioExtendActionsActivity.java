package com.tricheer.player.version.base.activity.music;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;

import com.tricheer.player.engine.VersionController;
import com.tricheer.player.receiver.MediaScanReceiver.ScanActives;
import com.tricheer.player.utils.PlayerFileUtils;
import com.tricheer.player.utils.PlayerLogicUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import js.lib.android.media.utils.AudioSortUtils;
import js.lib.android.media.engine.db_audio.AudioDBManager;
import js.lib.android.media.utils.AudioImgUtils;
import js.lib.android.media.audio.utils.AudioInfo;
import js.lib.android.media.audio.utils.AudioUtils;
import js.lib.android.media.bean.ProAudio;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * 播放器扩展行动作 - BASE
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioExtendActionsActivity extends BaseAudioCommonActionsActivity {
    // TAG
    private final String TAG = "BaseMusicExtendActionsActivity";

    /**
     * Thread handler
     */
    protected Handler mHandler = new Handler();

    /**
     * 媒体列表集合对象
     */
    protected List<ProAudio> mListPrograms;

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
        void postRefresh(ProAudio program, boolean isLoadEnd);
    }

    @Override
    protected void onCreate(android.os.Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void onNotifyScanAudios(int flag, List<ProAudio> listPrgrams, Set<String> allSdMountedPaths) {
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
                ArrayList<ProAudio> listMountedProgram = new ArrayList<>();
                for (ProAudio program : mListPrograms) {
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
    private void startMergeDatasTask(List<ProAudio> listScannedAudios, boolean isScaned) {
        CommonUtil.cancelTask(mMergeDatasTask);
        mMergeDatasTask = new MergeDatasTask(listScannedAudios, isScaned);
        mMergeDatasTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class MergeDatasTask extends AsyncTask<Void, Void, Void> {
        private List<ProAudio> mmListScannedAudios;
        private boolean mmIsScaned = false;

        public MergeDatasTask(List<ProAudio> listScannedAudios, boolean isScaned) {
            mmListScannedAudios = listScannedAudios;
            mmIsScaned = isScaned;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (EmptyUtil.isEmpty(mListPrograms)) {
                    mListPrograms = mmListScannedAudios;
                } else {
                    Map<String, ProAudio> mapListedPrograms = new HashMap<String, ProAudio>();
                    for (ProAudio program : mListPrograms) {
                        mapListedPrograms.put(program.mediaUrl, program);
                    }
                    for (ProAudio program : mmListScannedAudios) {
                        if (!mapListedPrograms.containsKey(program.mediaUrl)) {
                            mListPrograms.add(program);
                        }
                    }
                }
                AudioSortUtils.sortByTitle(mListPrograms);
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
    protected void refreshPageOnScan(List<ProAudio> listScannedAudios, boolean isScaned) {
    }

    /**
     * 加载本地媒体
     */
    protected void loadLocalMedias() {
    }

    public class LoadLocalMediasTask extends AsyncTask<Void, Integer, Void> {
        private LoadMediaListener mLoadMediaListener;

        public LoadLocalMediasTask(LoadMediaListener l) {
            mLoadMediaListener = l;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Get List Medias
            mListPrograms = AudioDBManager.instance().getListMusics();
            AudioSortUtils.sortByTitle(mListPrograms);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mLoadMediaListener != null) {
                mLoadMediaListener.afterLoaded();
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
        private LoadMediaListener mLoadMediaListener;
        private List<ProAudio> mmListNewPrograms = new ArrayList<ProAudio>();
        private List<ProAudio> mmListExistPrograms = new ArrayList<ProAudio>();

        public LoadSDCardMediasTask(LoadMediaListener l) {
            mLoadMediaListener = l;
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
            AudioDBManager.instance().insertListMusics(mmListNewPrograms);
            AudioDBManager.instance().updateListMusics(mmListExistPrograms);
            // Sort & Notify load end
            AudioSortUtils.sortByTitle(mListPrograms);
            mHandler.postDelayed(mmLoadSDCardMediasRunnable, 1000);
            return null;
        }

        private void parseMediaInfos(Map<String, AudioInfo> mapMediaInfos) {
            if (!EmptyUtil.isEmpty(mapMediaInfos)) {
                if (EmptyUtil.isEmpty(mListPrograms)) {
                    mListPrograms = AudioDBManager.instance().getListMusics();
                }
                // Remove Multiple
                for (ProAudio program : mListPrograms) {
                    if (isCancelled()) {
                        break;
                    }
                    if (mapMediaInfos.containsKey(program.mediaUrl)) {
                        ProAudio aiProgram = new ProAudio(mapMediaInfos.get(program.mediaUrl));
                        mmListExistPrograms.add(aiProgram);
                        ProAudio.copy(program, aiProgram);
                        mapMediaInfos.remove(program.mediaUrl);
                    }
                }
                // Add remaining Audio
                for (AudioInfo audioInfo : mapMediaInfos.values()) {
                    if (isCancelled()) {
                        break;
                    }
                    ProAudio program = new ProAudio(audioInfo);
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
                        mLoadMediaListener.afterLoaded();
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

        private LoadImgListener mmLoadImgListener;

        public LoadMediaImageTask(LoadImgListener l) {
            mmLoadImgListener = l;
        }

        @Override
        protected String doInBackground(Void... params) {
            // Parameters
            if (EmptyUtil.isEmpty(mListPrograms)) {
                return null;
            }

            int cachedNum = 0;
            for (ProAudio program : mListPrograms) {
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

        private boolean cacheProgramCover(ProAudio program) {
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
                AudioDBManager.instance().updateMediaCoverUrl(program);
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

        private void postResult(final ProAudio program, final boolean isLoadEnd) {
            if (!isCancelled()) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (mmLoadImgListener != null) {
                            mmLoadImgListener.postRefresh(program, isLoadEnd);
                        }
                    }
                });
            }
        }
    }

    /**
     * 获取当前媒体对象
     */
    public ProAudio getCurrProgram() {
        Serializable serialPro = getCurrMedia();
        if (serialPro != null) {
            return (ProAudio) serialPro;
        }
        return null;
    }

    /**
     * EXEC Play Selected Music
     */
    protected void execPlay(String mediaUrl) {
        play(mediaUrl);
    }

    /**
     * EXEC Play Selected Music
     */
    protected void execPlay(int position) {
        play(position);
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
    protected void getMediaInnerCover(ProAudio program) {
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