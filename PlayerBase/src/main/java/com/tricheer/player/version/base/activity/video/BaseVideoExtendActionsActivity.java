package com.tricheer.player.version.base.activity.video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.lib.view.OperateDialog;
import com.tricheer.app.receiver.PlayerReceiverActions;
import com.tricheer.player.R;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.bean.VideoRecordControl;
import com.tricheer.player.engine.VersionController;
import com.tricheer.player.engine.db.DBManager;
import com.tricheer.player.receiver.MediaScanReceiver.ScanActives;
import com.tricheer.player.receiver.PlayerReceiver;
import com.tricheer.player.utils.PlayerFileUtils;
import com.tricheer.player.utils.PlayerLogicUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import js.lib.android.media.local.player.IPlayerListener;
import js.lib.android.media.local.utils.VideoInfo;
import js.lib.android.media.local.utils.VideoUtils;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * 播放器扩展行动作 - BASE
 *
 * @author Jun.Wang
 */
public abstract class BaseVideoExtendActionsActivity extends BaseVideoCommonActionsActivity implements IPlayerListener {
    // TAG
    private final String TAG = "BaseVideoExtendActionsActivity";

    //==========Widget in this Activity==========
    /**
     * 选择关闭或继续行车记录Dialog
     */
    private OperateDialog dialogOpRecord;

    //==========Variable in this Activity==========
    /**
     * Must Be Values Below:
     * <p>
     * {@link MediaStore.Images.Thumbnails#MICRO_KIND}
     * {@link MediaStore.Images.Thumbnails#FULL_SCREEN_KIND}
     * {@link MediaStore.Images.Thumbnails#MINI_KIND}
     */
    private int mThumbMode = MediaStore.Images.Thumbnails.MINI_KIND;
    /**
     * Video ThumbImage Information
     */
    private int mThumbWidth = 200, mThumbHeight = 200;

    /**
     * Is Current Video 1080P Flag
     */
    private boolean mIsCurrVideo1080P = false;
    /**
     * {@link VideoRecordControl}
     */
    private int mRecordControlFlag = VideoRecordControl.RESET;

    /**
     * 是否正在展示黑名单媒体
     */
    private boolean mIsShowingBlacklistMedias = false;

    /**
     * 是否允许后台播放视频
     */
    private boolean mIsCanPlayAtBg = false;

    /**
     * 目标自动Seek进度，当视频加载完成后，可以据此跳转进度
     */
    protected int mTargetAutoSeekProgress = -1;

    /**
     * Load Local Medias Task
     */
    protected LoadLocalMediasTask mLoadLocalMediasTask;
    /**
     * Load SDCard Medias Task
     */
    protected LoadSDCardMediasTask mLoadSDCardMediasTask;
    /**
     * Load Media Images Task
     */
    protected LoadMediaImageTask mLoadMediaImageTask;
    /**
     * Load Selected Medias Task
     */
    protected LoadSelectedMediasTask mLoadSelectedMediasTask;

    /**
     * 媒体加载监听器
     */
    protected interface LoadMediaListener {
        public void afterLoad(String selectMediaUrl);

        public void refreshUI();
    }

    /**
     * 行车记录加载监听器
     */
    protected interface LoadRecordListener {
        public void afterLoad(List<ProVideo> listPrograms);
    }

    /**
     * 媒体封面图片加载监听器
     */
    protected interface LoadImgListner {
        public void afterLoad();
    }

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    /**
     * Configuration Thumb Inch
     *
     * @param thumbImgW
     * @param thumbImgH
     * @param thumbImgMode {@link MediaStore.Images.Thumbnails#MICRO_KIND}
     *                     {@link MediaStore.Images.Thumbnails#FULL_SCREEN_KIND}
     *                     {@link MediaStore.Images.Thumbnails#MINI_KIND}
     */
    protected void configThumbInfo(int thumbImgW, int thumbImgH, int thumbImgMode) {
        mThumbWidth = thumbImgW;
        mThumbHeight = thumbImgH;
        mThumbMode = thumbImgMode;
    }

    @Override
    public void onNotifyScanVideos(int flag, List<ProVideo> listPrgrams, Set<String> allSdMountedPaths) {
        super.onNotifyScanVideos(flag, listPrgrams, allSdMountedPaths);
        if (flag == ScanActives.REFRESH) {
            refreshPageOnScan(listPrgrams, false);
        } else if (flag == ScanActives.SYS_SCANED) {
            refreshPageOnScan(listPrgrams, true);
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
     * Refresh Local Medias
     */
    protected void refreshPageOnScan(List<ProVideo> listScannedVideos, boolean isScaned) {
        try {
            if (EmptyUtil.isEmpty(mListPrograms)) {
                mListPrograms = listScannedVideos;
            } else {
                Map<String, ProVideo> mapListedPrograms = new HashMap<String, ProVideo>();
                for (ProVideo program : mListPrograms) {
                    mapListedPrograms.put(program.mediaUrl, program);
                }
                for (ProVideo program : listScannedVideos) {
                    if (!mapListedPrograms.containsKey(program.mediaUrl)) {
                        mListPrograms.add(program);
                    }
                }
            }
            sortMediaList(mListPrograms);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "refreshPageOnScan()", e);
        }
    }

    /**
     * Refresh Page On Notify Clear
     */
    protected void refreshPageOnClear(Set<String> allSdMountedPaths) {
        if (VersionController.isCjVersion()) {
            try {
                ArrayList<ProVideo> listMountedProgram = new ArrayList<ProVideo>();
                for (ProVideo program : mListPrograms) {
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
            execRelease();
        }
    }

    /**
     * Sort List<ProVideo> by first Char
     */
    protected void sortMediaList(List<ProVideo> listPrograms) {
        if (!EmptyUtil.isEmpty(listPrograms)) {
            if (isShowingBlacklistMedias()) {
            } else {
                for (ProVideo program : mListPrograms) {
                    try {
                        program.titlePinYin = mCharacterParser.getSelling(program.title);
                        String firstChar = program.titlePinYin.substring(0, 1).toUpperCase();
                        if (firstChar.matches("[A-Z]")) {
                            program.sortLetter = firstChar;
                        } else {
                            program.sortLetter = "#";
                        }
                    } catch (Exception e) {
                        Logs.printStackTrace(TAG + "sortMediaList()", e);
                        program.titlePinYin = "";
                        program.sortLetter = "";
                    }
                }
                Collections.sort(mListPrograms, mComparator);
            }
        }
    }

    /**
     * Load Played Medias From Database
     */
    protected void loadLocalMedias() {
    }

    public class LoadLocalMediasTask extends AsyncTask<Object, Integer, Void> {
        private WeakReference<LoadMediaListener> mmWeakReference;

        @Override
        protected Void doInBackground(Object... params) {
            try {
                // Parameters
                mmWeakReference = new WeakReference<LoadMediaListener>((LoadMediaListener) params[0]);

                // Get List Medias
                Map<String, ProVideo> mapPrograms = DBManager.getMapVideos(true, false);
                mListPrograms = new ArrayList<ProVideo>();
                if (mapPrograms != null) {
                    mListPrograms.addAll(mapPrograms.values());
                }

                // Sort
                sortMediaList(mListPrograms);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (!isCancelled()) {
                    mmWeakReference.get().afterLoad("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load Media From SDCard.
     */
    protected void loadMediasOnPullRefresh() {
    }

    /**
     * Load Media From SDCard.
     */
    protected void loadSDCardMedias() {
    }

    public class LoadSDCardMediasTask extends AsyncTask<Void, Integer, Void> {
        private List<ProVideo> mmListNewPrograms = new ArrayList<ProVideo>();
        private List<ProVideo> mmListExistPrograms = new ArrayList<ProVideo>();
        private WeakReference<LoadMediaListener> mmWeakReference;
        private List<String> mmListSelectPaths;

        public LoadSDCardMediasTask(List<String> listSelectPaths, LoadMediaListener l) {
            this.mmListSelectPaths = listSelectPaths;
            mmWeakReference = new WeakReference<LoadMediaListener>(l);
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
            parseMediaInfos(VideoUtils.queryMapVideoInfos(mmListSelectPaths));
            DBManager.insertListVideos(mmListNewPrograms);
            DBManager.updateListVideos(mmListExistPrograms);
            // Sort & Notify load end
            sortMediaList(mListPrograms);
            mHandler.postDelayed(mmLoadSDCardMediasRunnable, 1000);
            return null;
        }

        private void parseMediaInfos(Map<String, VideoInfo> mapMediaInfos) {
            if (!EmptyUtil.isEmpty(mapMediaInfos)) {
                if (EmptyUtil.isEmpty(mListPrograms)) {
                    mListPrograms = DBManager.getListVideos(true, false);
                }
                // Remove Multiple
                for (ProVideo program : mListPrograms) {
                    if (isCancelled()) {
                        break;
                    }
                    if (mapMediaInfos.containsKey(program.mediaUrl)) {
                        ProVideo aiProgram = new ProVideo(mapMediaInfos.get(program.mediaUrl));
                        mmListExistPrograms.add(aiProgram);
                        ProVideo.copy(program, aiProgram);
                        mapMediaInfos.remove(program.mediaUrl);
                    }
                }
                // Add remaining Audio
                for (VideoInfo mediaInfo : mapMediaInfos.values()) {
                    if (isCancelled()) {
                        break;
                    }
                    ProVideo program = new ProVideo(mediaInfo);
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
                        mmWeakReference.get().afterLoad("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Load Video Cover Image
     */
    protected void loadMediaImage() {
    }

    public class LoadMediaImageTask extends AsyncTask<Object, Integer, Void> {
        private WeakReference<LoadImgListner> mmWeakReference;

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground(Object... params) {
            // Parameters
            List<ProVideo> listMedias = null;
            if (params[0] != null) {
                listMedias = (List<ProVideo>) params[0];
            }

            if (params[1] != null) {
                mmWeakReference = new WeakReference<LoadImgListner>((LoadImgListner) params[1]);
            }

            //
            if (listMedias != null) {
                for (Iterator<ProVideo> it = listMedias.iterator(); it.hasNext(); ) {
                    if (isCancelled()) {
                        break;
                    } else {
                        try {
                            ProVideo program = it.next();
                            File picF = new File(PlayerLogicUtils.getMediaPicPath(program.mediaUrl, 2));
                            if (!picF.exists()) {
                                cacheMediaPic(program, getThumbnail(program.mediaUrl),
                                        PlayerFileUtils.getVideoPicPath(program.mediaUrl));
                                if (picF.exists()) {
                                    runOnUiThread(mmPostResultRunnable);
                                }
                            }
                        } catch (Exception e) {
                            Logs.printStackTrace(TAG + "LoadMediaImageTask -> doInBackground()", e);
                        }
                    }
                }
            }
            return null;
        }

        private Bitmap getThumbnail(String mediaUrl) {
            return CommonUtil.getVideoThumbnail(mediaUrl, mThumbWidth, mThumbHeight, mThumbMode);
        }

        private Runnable mmPostResultRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    if (!isCancelled()) {
                        mmWeakReference.get().afterLoad();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Load Selected Medias
     */
    public class LoadSelectedMediasTask extends AsyncTask<Object, Integer, Void> {

        private WeakReference<LoadMediaListener> mmWeakReference;
        private String mmSelectMediaUrl;
        private List<String> mmListSelectPaths;

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground(Object... params) {
            // Sleep thread for loading wait
            sleepForLoading();
            // Parameters
            int playPos = (Integer) params[0];
            mmListSelectPaths = (List<String>) params[1];
            if (params[2] != null) {
                mmWeakReference = new WeakReference<LoadMediaListener>((LoadMediaListener) params[2]);
            }
            mmSelectMediaUrl = mmListSelectPaths.get(playPos);

            // Post Results
            mIsShowingBlacklistMedias = PlayerFileUtils.isInBlacklist(mmSelectMediaUrl);
            if (isShowingBlacklistMedias()) {
                postDrivingRecordsByPaths(mmListSelectPaths);
                postDrivingRecordsByScanInfo(mmListSelectPaths);
            } else {
                postVideos(mmListSelectPaths);
            }
            return null;
        }

        /**
         * 这个是为了展示加载动作，而让线程延迟了500ms.
         */
        private void sleepForLoading() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void postVideos(List<String> listSelectPaths) {
            ArrayList<ProVideo> listPrograms = new ArrayList<ProVideo>();
            if (!EmptyUtil.isEmpty(listSelectPaths)) {
                Map<String, ProVideo> mapVideos = DBManager.getMapVideos(true, false);
                List<String> listToParsePaths = new ArrayList<String>();

                // Parse and Filter
                for (String path : listSelectPaths) {
                    if (isCancelled()) {
                        break;
                    } else {
                        ProVideo program = mapVideos.get(path);
                        if (program == null) {
                            listToParsePaths.add(path);
                        } else {
                            listPrograms.add(program);
                        }
                    }
                }

                // Parse filtered paths
                if (!EmptyUtil.isEmpty(listToParsePaths)) {
                    List<VideoInfo> listVideoInfos = VideoUtils.queryListVideoInfos(listSelectPaths);
                    Map<String, ProVideo> mapPrograms = parseMediaInfos(listVideoInfos, null);
                    listPrograms.addAll(mapPrograms.values());
                    DBManager.insertListVideos(new ArrayList<ProVideo>(mapPrograms.values()));
                }
            }
            postResults(listPrograms);
        }

        private void postDrivingRecordsByPaths(List<String> listSelectPaths) {
            ArrayList<ProVideo> listMedias = new ArrayList<ProVideo>();
            // 倒序
            for (String selectPath : listSelectPaths) {
                if (isCancelled()) {
                    break;
                } else {
                    File recordFile = new File(selectPath);
                    if (recordFile.exists()) {
                        listMedias.add(new ProVideo(recordFile.getPath()));
                    }
                }
            }
            postResults(listMedias);
        }

        private void postResults(ArrayList<ProVideo> listMedias) {
            if (isCancelled()) {
                return;
            }
            // Sort
            mListPrograms = listMedias;
            if (!isShowingBlacklistMedias()) {
                sortMediaList(mListPrograms);
            }
            // Post data to UI thread
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        mmWeakReference.get().afterLoad(mmSelectMediaUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void postDrivingRecordsByScanInfo(List<String> listSelectPaths) {
            if (EmptyUtil.isEmpty(listSelectPaths)) {
                return;
            }
            Map<String, VideoInfo> mapInfos = VideoUtils.queryMapVideoInfos(listSelectPaths);
            ArrayList<ProVideo> listMedias = new ArrayList<ProVideo>();
            for (String selectPath : listSelectPaths) {
                if (isCancelled()) {
                    break;
                } else {
                    File recordFile = new File(selectPath);
                    if (recordFile.exists()) {
                        VideoInfo videoInfo = mapInfos.get(recordFile.getPath());
                        if (videoInfo == null) {
                            listMedias.add(new ProVideo(recordFile.getPath()));
                        } else {
                            listMedias.add(new ProVideo(videoInfo));
                        }
                    }
                }
            }
            // Post data and refresh UI
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        mmWeakReference.get().refreshUI();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private Map<String, ProVideo> parseMediaInfos(List<VideoInfo> listVideoInfos, final LoadMediaListener l) {
        final Map<String, ProVideo> mapPrograms = new HashMap<String, ProVideo>();
        for (VideoInfo video : listVideoInfos) {
            ProVideo program = new ProVideo(video);
            mapPrograms.put(program.mediaUrl, program);
        }
        return mapPrograms;
    }

    /**
     * Get Music Objects from Music Info Array
     */
    protected List<ProVideo> parseVideoInfos(List<String> listPaths) {
        List<ProVideo> listPrograms = new ArrayList<ProVideo>();
        List<VideoInfo> listInfos = VideoUtils.queryListVideoInfos(listPaths);
        for (VideoInfo info : listInfos) {
            ProVideo video = new ProVideo(info);
            listPrograms.add(video);
        }

        return listPrograms;
    }

    /**
     * Get Music Objects from Music Info Array
     */
    protected void parseProgramInfos() {
        mListPrograms = new ArrayList<ProVideo>();
        Map<String, ProVideo> mapStoredVideos = DBManager.getMapVideos(true, false);
        List<VideoInfo> listInfos = VideoUtils.queryListVideoInfos(null);
        for (VideoInfo info : listInfos) {
            try {
                // Just Add Parsed Program
                ProVideo program = mapStoredVideos.get(info.path);
                if (program != null) {
                    mListPrograms.add(program);
                } else {
                    program = new ProVideo(info);
                    mListPrograms.add(program);
                }
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "parseProgramInfos()", e);
            }
        }
    }

    /**
     * Release
     */
    protected void clearPlayInfo() {
        mListPrograms = new ArrayList<ProVideo>();
        if (tvStartTime != null) {
            tvStartTime.setText("00:00");
        }
        if (tvEndTime != null) {
            tvEndTime.setText("00:00");
        }
        if (seekBar != null) {
            seekBar.setEnabled(false);
            seekBar.setMax(0);
            seekBar.setProgress(0);
        }
    }

    /**
     * 是否正在展示黑名单媒体文件
     */
    protected boolean isShowingBlacklistMedias() {
        return mIsShowingBlacklistMedias;
    }

    /**
     * AccOff 时，是否记录缓存播放信息
     */
    @Override
    public boolean isCacheOnAccOff() {
        Logs.i(TAG, " ");
        Logs.i(TAG, "isCacheOnAccOff() -> [isBgOnHomeKeyClick():" + isHomeClicked());
        Logs.i(TAG, "isCacheOnAccOff() -> [mIsPauseOnNotify:" + mIsPauseOnNotify);
        Logs.i(TAG, "isCacheOnAccOff() -> [mIsShowingBlacklistMedias:" + mIsShowingBlacklistMedias);
        Logs.i(TAG, " ");
        return !(mIsPauseOnNotify || mIsShowingBlacklistMedias || isHomeClicked());
    }

    /**
     * 启动播放视频
     */
    private void startPlay(boolean isMakeScreeenOn) {
        play();
        makeScreenOn(isMakeScreeenOn);
        Logs.i(TAG, "++++VideoView Start and Make Screen ON++++");
    }

    /**
     * EXEC Play Select Program
     */
    protected void execPlay(String mediaUrl) {
        execPlay(getPlayPosByMediaUrl(mediaUrl));
    }

    /**
     * EXEC Play Select Program
     */
    protected void execPlay(int playPos) {
        // 如下两个条件，不执行播放
        // （1）列表为空
        // （2）索引溢出
        if (EmptyUtil.isEmpty(mListPrograms) || playPos >= mListPrograms.size()) {
            return;
        }

        // 获取并检测当前位置节目是否可以播放
        Logs.i(TAG, "----execPlay(" + playPos + ")----");
        mPlayPos = playPos;
        ProVideo toPlayProgram = mListPrograms.get(playPos);
        if (toPlayProgram == null || EmptyUtil.isEmpty(toPlayProgram.mediaUrl)) {
            return;
        }

        // 执行播放
        vvPlayer.setVisibility(View.INVISIBLE);
        saveTargetMediaUrl(toPlayProgram.mediaUrl);
        mTargetAutoSeekProgress = getLastProgress();
        vvPlayer.setTag(toPlayProgram);
        vvPlayer.setMediaPath(toPlayProgram.mediaUrl);
        startPlay(true);
        vvPlayer.setVisibility(View.VISIBLE);

        // 设置相关信息
        resetSeekBar();

        // 是否处理视频分辨率
        if (VersionController.isProcessVideoResolution()) {
            doProcessResolutionOnPlay(mContext, toPlayProgram.mediaUrl, false);
        }
    }

    @Override
    public void playPrev() {
        super.playPrev();
        execPlay(mPlayPos);
    }

    @Override
    public void playNext() {
        super.playNext();
        execPlay(mPlayPos);
    }

    private void resetSeekBar() {
        try {
            Logs.i(TAG, "----resetSeekBar()----");
            if (seekBar != null) {
                seekBar.setEnabled(true);
                seekBar.setMax(mListPrograms.get(mPlayPos).duration);
                seekBar.setProgress(0);
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "resetSeekBar()", e);
        }
    }

    /**
     * Stop Video Play
     */
    protected void execRelease() {
        Logs.i(TAG, "----execRelease()----");
        release();
        if (!isDestroyed()) {
            clearPlayInfo();
        }
    }

    /**
     * Play Pause Video
     */
    protected void execPlayOrPause() {
        Logs.i(TAG, "----execPlayOrPause()----");
        if (!EmptyUtil.isEmpty(mListPrograms)) {
            if (isPlaying()) {
                pauseByUser();
            } else {
                resumeByUser();
            }
        }
    }

    /**
     * Save Play Information
     */
    protected void savePlayInfo() {
        if (isPlaying() && getProgress() > 0) {
            String currMediaUrl = vvPlayer.getMediaPath();
            if (!EmptyUtil.isEmpty(currMediaUrl)) {
                Logs.debugI(TAG, "savePlayInfo() -> [progress:" + getProgress());
                savePlayMediaInfos(currMediaUrl, getProgress());
            }
        }
    }

    /**
     * Get Media Play Position by Media URL
     */
    protected int getPlayPosByMediaUrl(String mediaUrl) {
        int playPos = -1;

        for (int idx = 0; idx < mListPrograms.size(); idx++) {
            ProVideo program = mListPrograms.get(idx);
            if (program.mediaUrl.equals(mediaUrl)) {
                playPos = idx;
                break;
            }
        }

        if (playPos < 0) {
            playPos = 0;
            clearPlayedMediaInfos();
        }

        return playPos;
    }

    protected ProVideo getCurrProgram() {
        try {
            String currMediaUrl = getPath();
            for (ProVideo program : mListPrograms) {
                if (program.mediaUrl.equals(currMediaUrl)) {
                    return program;
                }
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getCurrProgram()", e);
        }
        return null;
    }

    protected ViewGroup.LayoutParams getLPs() {
        if (vvPlayer != null) {
            return vvPlayer.getLayoutParams();
        }
        return null;
    }

    protected void setLPs(ViewGroup.LayoutParams lps) {
        if (vvPlayer != null) {
            vvPlayer.setLayoutParams(lps);
        }
    }

    /**
     * Broadcast Media Resolution
     */
    protected void doProcessResolutionOnPlay(final Context cxt, final String mediaUrl, final boolean isOnDestroy) {
        try {
            // onDestroy
            if (isOnDestroy) {
                mIsCurrVideo1080P = false;
                doBroadCastVideoResolution(0, isOnDestroy);
                // Playing
            } else {
                int[] videoLens = CommonUtil.getVideoResolutions(cxt, mediaUrl);
                int videoH = (videoLens[1] == 0) ? 1080 : videoLens[1];
                doBroadCastVideoResolution(videoH, isOnDestroy);

                mIsCurrVideo1080P = (videoH == 1080);
                if (mRecordControlFlag == VideoRecordControl.RESET) {
                    Logs.i(TAG, "doProcessResolutionOnPlay ----Reset---");
                    showDialogOn1080P();
                } else if (mRecordControlFlag == VideoRecordControl.CLOSE_ON_1080P_PLAYING) {
                    doBroadcastStartRecord(!mIsCurrVideo1080P);
                }
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "doProcessResolutionOnPlay()", e);
        }
    }

    /**
     * Broadcast Video Resolution
     */
    protected void doBroadCastVideoResolution(int size, boolean isOnDestroy) {
        Logs.i(TAG, "doBroadCastVideoResolution -> [VideoResolution:" + size + " , isOnDestroy:" + isOnDestroy + "]");
        Intent resolutionIntent = new Intent(PlayerReceiverActions.NOTIFY_VIDEO_SIZE);
        resolutionIntent.putExtra("size", size);
        sendBroadcast(resolutionIntent);
    }

    /**
     * Broadcast Start Record
     */
    protected void doBroadcastStartRecord(boolean isStart) {
        Intent closeRecordIntent = new Intent(PlayerReceiverActions.NOTIFY_CONFIG_RECORD);
        closeRecordIntent.putExtra("start", isStart);
        sendBroadcast(closeRecordIntent);
        Logs.i(TAG, "doBroadcastCloseRecord -> action.camera.ACTION_CONFIG_RECORD[start:" + isStart + "]");
    }

    /**
     * Show Dialog when Receiver Notify [Record Start]
     */
    protected void showDialogRecordStart() {
        Logs.i(TAG, "showDialogRecordStart() ----Start----");
        if (mRecordControlFlag == VideoRecordControl.RESET && isPlaying()) {
            showDialogOn1080P();
        }
    }

    /**
     * Show Dialog On 1080P Video Playing
     */
    private void showDialogOn1080P() {
        Logs.i(TAG, "showDialogOn1080P() -> [mIsCurrVideo1080P:" + mIsCurrVideo1080P);
        showDialogOn1080P(mIsCurrVideo1080P);
    }

    /**
     * Open Dialog
     */
    protected void showDialogOn1080P(boolean isShow) {
        if (dialogOpRecord == null) {
            dialogOpRecord = new OperateDialog(mContext, R.layout.zpt_lv8918_slb_v_dialog_operates);
            dialogOpRecord.setJustToast(false);
            dialogOpRecord.setCancelable(false);
            dialogOpRecord.setCanceledOnTouchOutside(false);
            dialogOpRecord.setMessage(R.string.playing_1080p_on_record);
            dialogOpRecord.setOperate1OnClick(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialogOpRecord.dismiss();
                    mRecordControlFlag = VideoRecordControl.CLOSE_ON_1080P_PLAYING;
                    doBroadcastStartRecord(!mIsCurrVideo1080P);
                }
            });
            dialogOpRecord.setOperate2OnClick(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialogOpRecord.dismiss();
                    mRecordControlFlag = VideoRecordControl.CONTINUE_ON_1080P_PLAYING;
                }
            });
        }

        // Show
        if (isShow) {
            Logs.i(TAG, "showDialogOn1080P(true) -> [isRecordStart:" + PlayerReceiver.isRecordStart + "; mIs1080PVideo:"
                    + mIsCurrVideo1080P);
            if (PlayerReceiver.isRecordStart && !dialogOpRecord.isShowing()) {
                dialogOpRecord.show(false);
            }
            // Dismiss
        } else {
            if (dialogOpRecord.isShowing()) {
                dialogOpRecord.dismiss();
            }
        }
    }

    /**
     * Resume Record
     */
    protected void resumeRecord() {
        if (mRecordControlFlag == VideoRecordControl.CLOSE_ON_1080P_PLAYING) {
            mRecordControlFlag = VideoRecordControl.RESET;
            doBroadcastStartRecord(true);
        }
    }

    /**
     * 设置是否允许后台播放视频
     */
    protected void setCanPlayAtBg(boolean isCanPlayAtBg) {
        this.mIsCanPlayAtBg = isCanPlayAtBg;
    }

    /**
     * 是否允许后台播放视频
     */
    protected boolean isCanPlayAtBg() {
        return mIsCanPlayAtBg;
    }

    /**
     * Cancel All Tasks
     */
    protected void cancelAllTasks() {
        CommonUtil.cancelTask(mLoadLocalMediasTask);
        CommonUtil.cancelTask(mLoadSDCardMediasTask);
        CommonUtil.cancelTask(mLoadSelectedMediasTask);
        CommonUtil.cancelTask(mLoadMediaImageTask);
    }
}