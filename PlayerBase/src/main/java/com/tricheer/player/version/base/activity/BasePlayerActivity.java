package com.tricheer.player.version.base.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tri.lib.utils.PowerManagerUtil;
import com.tricheer.player.receiver.PlayerReceiver.PlayerReceiverListener;

import java.util.List;

import js.lib.android.media.bean.MediaBase;
import js.lib.android.media.bean.ProgramPinyinComparator;
import js.lib.android.media.player.PlayDelegate;
import js.lib.android.media.player.PlayMode;
import js.lib.android.media.player.PlayState;

/**
 * Player Base
 *
 * @author Jun.Wang
 */
public abstract class BasePlayerActivity extends BaseFragActivity implements PlayerReceiverListener, PlayDelegate {
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

    @Override
    public void setListSrcMedias(List<? extends MediaBase> listSrcMedias) {
    }

    @Override
    public List<? extends MediaBase> getListSrcMedias() {
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
    public void playNext() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void pause() {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void resume() {
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
    public void setPlayListener(PlayDelegate l) {
    }

    // {@link PlayerActionsListener} Implements Method
    @Override
    public void removePlayListener(PlayDelegate l) {
    }
}
