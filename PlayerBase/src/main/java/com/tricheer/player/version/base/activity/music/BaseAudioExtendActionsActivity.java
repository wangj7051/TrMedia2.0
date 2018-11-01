package com.tricheer.player.version.base.activity.music;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;

import com.tricheer.player.receiver.MediaScanReceiver;
import com.tricheer.player.utils.PlayerFileUtils;
import com.tricheer.player.utils.PlayerLogicUtils;

import java.io.Serializable;
import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.media.engine.audio.utils.AudioImgUtils;
import js.lib.android.media.player.audio.utils.AudioSortUtils;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.Logs;

/**
 * 播放器扩展行动作 - BASE
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioExtendActionsActivity extends BaseAudioCommonActionsActivity
        implements MediaScanReceiver.AudioScanDelegate {
    // TAG
    private final String TAG = "BaseAudioExtendActions";

    /**
     * Thread handler
     */
    protected Handler mHandler = new Handler();

    /**
     * AsyncTask - 加载本地媒体
     */
    protected LoadLocalMediasTask mLoadLocalMediasTask;

    /**
     * 媒体加载监听
     */
    protected interface LoadMediaListener {
        void afterLoaded(List<ProAudio> listMedias);
    }

    @Override
    protected void onCreate(android.os.Bundle bundle) {
        super.onCreate(bundle);
        MediaScanReceiver.register(this);
    }

    @Override
    public void onMediaScanningStart() {
    }

    @Override
    public void onMediaScanningEnd() {
    }

    @Override
    public void onMediaScanningCancel() {
    }

    @Override
    public void onMediaScanningRefresh(List<ProAudio> listMedias, boolean isOnUiThread) {
    }

//    private void startMergeDataOnClear(Set<String> allSdMountedPaths) {
//        try {
//            List<ProAudio> oldListSrcMedias = getListSrcMedias();
//            List<ProAudio> listMountedProgram = new ArrayList<>();
//            for (ProAudio program : oldListSrcMedias) {
//                boolean isProgramMounted = false;
//                for (String mountedPath : allSdMountedPaths) {
//                    if (program.mediaUrl.startsWith(mountedPath)) {
//                        isProgramMounted = true;
//                        break;
//                    }
//                }
//                if (isProgramMounted) {
//                    listMountedProgram.add(program);
//                }
//            }
//            refreshPageOnClear(listMountedProgram);
//        } catch (Exception e) {
//            Logs.printStackTrace(TAG + "refreshPageOnClear()", e);
//        }
//    }

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
                String storePath = PlayerFileUtils.getMusicPicPath(program.mediaUrl);
                String coverPicFilePath = PlayerLogicUtils.getMediaPicFilePath(program, storePath);
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
    }

    @Override
    protected void onDestroy() {
        MediaScanReceiver.unregister(this);
        super.onDestroy();
    }
}