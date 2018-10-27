package com.tricheer.player.version.base.activity.music;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;

import com.tricheer.player.receiver.MediaScanReceiver.MediaScanActives;
import com.tricheer.player.utils.PlayerFileUtils;
import com.tricheer.player.utils.PlayerLogicUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.media.engine.audio.utils.AudioImgUtils;
import js.lib.android.media.player.audio.utils.AudioSortUtils;
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
    private MergeDataTask mMergeDataTask;

    /**
     * 媒体加载监听
     */
    protected interface LoadMediaListener {
        void afterLoaded(List<ProAudio> listMedias);
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
    public void onNotifyScanAudios(MediaScanActives flag, List<ProAudio> listPrgrams, Set<String> allSdMountedPaths) {
        super.onNotifyScanAudios(flag, listPrgrams, allSdMountedPaths);
        switch (flag) {
            case REFRESH:
                startMergeDataOnScanning(listPrgrams, false);
                break;
            case SYS_SCANNED:
                startMergeDataOnScanning(listPrgrams, true);
                break;
            case CLEAR:
                startMergeDataOnClear(allSdMountedPaths);
                break;
            default:
                refreshOnNotifyLoading(flag);
                break;
        }
    }

    /**
     * 开始同步扫描数据到当前列表
     */
    private void startMergeDataOnScanning(List<ProAudio> listScannedAudios, boolean isScanned) {
        CommonUtil.cancelTask(mMergeDataTask);
        mMergeDataTask = new MergeDataTask(listScannedAudios, isScanned);
        mMergeDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class MergeDataTask extends AsyncTask<Void, Void, List<ProAudio>> {
        private List<ProAudio> mmListScannedAudios;
        private boolean mmIsScanned;

        MergeDataTask(List<ProAudio> listScannedAudios, boolean isScanned) {
            mmListScannedAudios = listScannedAudios;
            mmIsScanned = isScanned;
        }

        @Override
        protected List<ProAudio> doInBackground(Void... params) {
            List<ProAudio> listSrcMedias = getListSrcMedias();
            try {
                if (EmptyUtil.isEmpty(listSrcMedias)) {
                    listSrcMedias = mmListScannedAudios;
                } else {
                    Map<String, ProAudio> mapListedPrograms = new HashMap<String, ProAudio>();
                    for (ProAudio program : listSrcMedias) {
                        mapListedPrograms.put(program.mediaUrl, program);
                    }
                    for (ProAudio program : mmListScannedAudios) {
                        if (!mapListedPrograms.containsKey(program.mediaUrl)) {
                            listSrcMedias.add(program);
                        }
                    }
                }
                AudioSortUtils.sortByTitle(listSrcMedias);
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "refreshPageOnScan()", e);
            }
            return listSrcMedias;
        }

        @Override
        protected void onPostExecute(List<ProAudio> listMedias) {
            super.onPostExecute(listMedias);
            refreshPageOnScanning(listMedias, mmIsScanned);
        }
    }

    /**
     * 当扫描过程中或扫描结束时,刷新页面
     *
     * @param listSrcMedias 新的媒体源数据
     * @param isScanned     新的媒体文件是否扫描到系统媒体数据库
     */
    protected void refreshPageOnScanning(List<ProAudio> listSrcMedias, boolean isScanned) {
    }

    private void startMergeDataOnClear(Set<String> allSdMountedPaths) {
        try {
            List<ProAudio> oldListSrcMedias = getListSrcMedias();
            List<ProAudio> listMountedProgram = new ArrayList<>();
            for (ProAudio program : oldListSrcMedias) {
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
            refreshPageOnClear(listMountedProgram);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "refreshPageOnClear()", e);
        }
    }

    /**
     * Refresh Page On Notify Clear
     */
    protected void refreshPageOnClear(List<ProAudio> listMedias) {
    }

    /**
     * Refresh Page On Loading
     *
     * @param flag : {@link MediaScanActives#START} or {@link MediaScanActives#END}
     */
    protected void refreshOnNotifyLoading(MediaScanActives flag) {
    }

    /**
     * 加载本地媒体
     */
    protected void loadLocalMedias() {
    }

    public class LoadLocalMediasTask extends AsyncTask<Void, Integer, Void> {
        private List<ProAudio> mmListMedias;
        private LoadMediaListener mmLoadMediaListener;

        public LoadLocalMediasTask(LoadMediaListener l) {
            mmLoadMediaListener = l;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Get List Medias
            mmListMedias = AudioDBManager.instance().getListMusics();
            AudioSortUtils.sortByTitle(mmListMedias);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mmLoadMediaListener != null) {
                mmLoadMediaListener.afterLoaded(mmListMedias);
            }
        }
    }

    /**
     * 加载外置存储设备媒体
     */
    protected void loadSDCardMedias() {
    }

    protected class LoadSDCardMediasTask extends AsyncTask<Void, Integer, Void> {
        private List<ProAudio> mmListMedias;
        private LoadMediaListener mmLoadMediaListener;
        private List<ProAudio> mmListNewPrograms = new ArrayList<ProAudio>();
        private List<ProAudio> mmListExistPrograms = new ArrayList<ProAudio>();

        public LoadSDCardMediasTask(LoadMediaListener l) {
            mmLoadMediaListener = l;
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
            mmListMedias = AudioDBManager.instance().getListMusics();
            AudioDBManager.instance().insertListMusics(mmListNewPrograms);
            AudioDBManager.instance().updateListMusics(mmListExistPrograms);
            // Sort & Notify load end
            AudioSortUtils.sortByTitle(mmListMedias);
            mHandler.postDelayed(mmLoadSDCardMediasRunnable, 1000);
            return null;
        }

        private Runnable mmLoadSDCardMediasRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    if (!isCancelled()) {
                        mmLoadMediaListener.afterLoaded(mmListMedias);
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
            List<ProAudio> listSrcMedias = getListSrcMedias();
            if (EmptyUtil.isEmpty(listSrcMedias)) {
                return null;
            }

            int cachedNum = 0;
            for (ProAudio program : listSrcMedias) {
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
        CommonUtil.cancelTask(mMergeDataTask);
        CommonUtil.cancelTask(mLoadSDCardMediasTask);
        CommonUtil.cancelTask(mLoadMediaImageTask);
    }
}