package com.tricheer.player.version.base.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tri.lib.utils.PowerManagerUtil;
import com.tricheer.player.receiver.MediaScanReceiver;
import com.tricheer.player.receiver.PlayerReceiver.PlayerReceiverListener;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import js.lib.android.media.bean.MediaBase;
import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.bean.Program;
import js.lib.android.media.bean.ProgramPinyinComparator;
import js.lib.android.media.player.PlayListener;
import js.lib.android.media.player.PlayMode;
import js.lib.android.media.player.PlayState;
import js.lib.android.utils.Logs;

/**
 * Player Base
 *
 * @author Jun.Wang
 */
public abstract class BasePlayerActivity extends BaseFragActivity implements PlayerReceiverListener, PlayListener {
    // TAG
    private static final String TAG = "BasePlayerActivity";

    //==========Widget in this Activity==========
    // Common Widgets
    protected SeekBar seekBar;
    protected TextView tvStartTime, tvEndTime;
    protected ImageView ivPlayPre, ivPlay, ivPlayNext;

    //==========Variable in this Activity==========
    /**
     * Manager Player Power
     */
    private PowerManagerUtil mPlayerPowerManager;

    /**
     * Program Name PinYin Comparator
     */
    protected ProgramPinyinComparator mComparator;

    /**
     * Pause Flag on Notify
     */
    protected boolean mIsPauseOnNotify = false;
    /**
     * Pause Flag on AiSpeech Window On
     */
    protected boolean mIsPauseOnAisOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    /**
     * Initialize
     */
    protected void init() {
        this.mComparator = new ProgramPinyinComparator();
        this.mPlayerPowerManager = new PowerManagerUtil(mContext);
    }

    /**
     * Make Screen ON
     */
    protected void makeScreenOn(boolean isMakeOn) {
        if (mPlayerPowerManager != null) {
            mPlayerPowerManager.keepScreenOn(this, isMakeOn);
        }
    }

    /**
     * Cache Music Picture to Local
     */
    protected void cacheMediaPic(String coverUrl, Bitmap coverBitmap) {
        storeBitmap(coverUrl, coverBitmap);
    }

    /**
     * Cache Music Picture to Local
     */
    protected void cacheMediaPic(final Program program, Bitmap bm, final String storePath) {
        try {
            String bmFilePath = storePath + "/" + URLEncoder.encode(program.mediaUrl, "UTF-8") + ".png";
            storeBitmap(bmFilePath, bm);
        } catch (Throwable e) {
            Logs.printStackTrace(TAG + "cacheMediaPic()", e);
        }
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

                //
                File tmpFile = new File(filePath + "_TEMP");
                tmpFile.createNewFile();
                fos = new FileOutputStream(tmpFile);
                bmToStore.compress(Bitmap.CompressFormat.PNG, 100, fos);

                //
                tmpFile.renameTo(targetF);
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

    protected void notifyScanMedias(boolean isLoading) {
        Intent data = new Intent(MediaScanReceiver.ACTION_START_LIST);
        data.putExtra(MediaScanReceiver.LOADING_FLAG, isLoading);
        sendBroadcast(new Intent(data));
    }

    // {@link PlayerReceiverListener} Implements Method
    @Override
    public void onNotifyScanAudios(MediaScanReceiver.MediaScanActives flag, List<ProAudio> listPrgrams, Set<String> allSdMountedPaths) {
    }

    // {@link PlayerReceiverListener} Implements Method
    @Override
    public void onNotifyScanVideos(MediaScanReceiver.MediaScanActives flag, List<ProVideo> listPrgrams, Set<String> allSdMountedPaths) {
    }

    // {@link IPlayerListener} Implements Method
    @Override
    public void onPlayStateChanged(PlayState playState) {
    }

    // {@link IPlayerListener} Implements Method
    @Override
    public void onProgressChanged(String mediaUrl, int progress, int duration) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void setPlayMode(PlayMode mode) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void onPlayModeChange() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public MediaBase getCurrMedia() {
        return null;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void setPlayList(List<? extends MediaBase> listMedias) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void setPlayPosition(int position) {
    }

    @Override
    public List<? extends MediaBase> getListMedias() {
        return null;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void play() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void playPrev() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void playPrevBySecurity() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void playNext() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void playNextBySecurity() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void pause() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void pauseByUser() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void resume() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void resumeByUser() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void release() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public String getLastMediaPath() {
        return null;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public long getLastProgress() {
        return 0;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public String getCurrMediaPath() {
        return null;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public int getCurrIdx() {
        return 0;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public int getTotalCount() {
        return 0;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public int getProgress() {
        return 0;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public int getDuration() {
        return 0;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public boolean isPlaying() {
        return false;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public boolean isPauseByUser() {
        return false;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void seekTo(int time) {
    }

    // {@link PlayerActionsListener} Implements Method

    @Override
    public String getLastTargetMediaPath() {
        return null;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void saveTargetMediaPath(String mediaPath) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public String[] getPlayedMediaInfos() {
        return null;
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void savePlayMediaInfos(String mediaUrl, int progress) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void clearPlayedMediaInfos() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void setPlayListener(PlayListener l) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void removePlayListener(PlayListener l) {
    }
}
