package com.tricheer.player.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.tri.lib.utils.SettingsSysUtil;

import java.util.List;

import js.lib.android.media.audio.service.AudioPlayService;
import js.lib.android.media.bean.Program;
import js.lib.android.media.player.PlayMode;
import js.lib.android.media.player.PlayState;
import js.lib.android.media.player.audio.MusicPlayerFactory;
import js.lib.android.utils.Logs;

/**
 * Music Play Service
 *
 * @author Jun.Wang
 */
public class TrPlayService extends AudioPlayService {
    // TAG
    private final String TAG = "MusicPlayService";

    /**
     * {@link LocalBinder} Object
     */
    private LocalBinder mLocalBinder;

    public class LocalBinder extends Binder {
        public TrPlayService getService() {
            return TrPlayService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MusicPlayerFactory.instance().init(MusicPlayerFactory.PlayerType.VLC_PLAYER);
        Logs.i(TAG, "^^ onCreate() ^^");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return getLocalBinder();
    }

    private LocalBinder getLocalBinder() {
        if (mLocalBinder == null) {
            mLocalBinder = new LocalBinder();
        }
        return mLocalBinder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setPlayList(List<? extends Program> listPros) {
        super.setPlayList(listPros);
    }

    @Override
    public List<? extends Program> getListMedias() {
        return super.getListMedias();
    }

    @Override
    public void setPlayPosition(int playPos) {
        super.setPlayPosition(playPos);
    }

    @Override
    public int getTotalCount() {
        return super.getTotalCount();
    }

    @Override
    public int getCurrIdx() {
        return super.getCurrIdx();
    }

    @Override
    public Program getCurrMedia() {
        return super.getCurrMedia();
    }

    @Override
    public String getCurrMediaPath() {
        return super.getCurrMediaPath();
    }

    @Override
    public int getProgress() {
        return super.getProgress();
    }

    @Override
    public int getDuration() {
        return super.getDuration();
    }

    @Override
    public boolean isPlayEnable() {
        return super.isPlayEnable();
    }

    @Override
    public void play() {
        super.play();
    }

    @Override
    public void play(String mediaPath) {
        super.play(mediaPath);
    }

    @Override
    public void play(int pos) {
        super.play(pos);
    }

    @Override
    public void playPrev() {
        super.playPrev();
    }

    @Override
    public void playPrevBySecurity() {
        super.playPrevBySecurity();
    }

    @Override
    public void playNext() {
        super.playNext();
    }

    @Override
    public void playNextBySecurity() {
        super.playNextBySecurity();
    }

    @Override
    public boolean isPlaying() {
        return super.isPlaying();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void pauseByUser() {
        super.pauseByUser();
    }

    @Override
    public boolean isPauseByUser() {
        return super.isPauseByUser();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void resumeByUser() {
        super.resumeByUser();
    }

    @Override
    public void release() {
        super.release();
    }

    @Override
    public void seekTo(int time) {
        super.seekTo(time);
    }

    @Override
    public void saveTargetMediaPath(String mediaPath) {
        super.saveTargetMediaPath(mediaPath);
    }

    @Override
    public String getLastTargetMediaPath() {
        return super.getLastTargetMediaPath();
    }

    @Override
    public String getLastMediaPath() {
        return super.getLastMediaPath();
    }

    @Override
    public long getLastProgress() {
        return super.getLastProgress();
    }

    @Override
    public void savePlayMediaInfos(String mediaPath, int progress) {
        super.savePlayMediaInfos(mediaPath, progress);
    }

    @Override
    public String[] getPlayedMediaInfos() {
        return super.getPlayedMediaInfos();
    }

    @Override
    public void clearPlayedMediaInfos() {
        super.clearPlayedMediaInfos();
    }

    @Override
    public void switchPlayMode(int supportFlag) {
        super.switchPlayMode(supportFlag);
    }

    @Override
    public void setPlayMode(PlayMode mode) {
        super.setPlayMode(mode);
    }

    @Override
    public void onPlayModeChange() {
        super.onPlayModeChange();
    }

    @Override
    public void onPlayStateChanged(PlayState playState) {
        super.onPlayStateChanged(playState);
        // Cache Play State
        cachePlayState(playState);
    }

    @Override
    public void onProgressChanged(String mediaPath, int progress, int duration) {
        super.onProgressChanged(mediaPath, progress, duration);
    }

    @Override
    public void onAudioFocusDuck() {
        Logs.i(TAG, "----$$ onAudioFocusDuck() $$----");
        super.onAudioFocusDuck();
    }

    @Override
    public void onAudioFocusTransient() {
        Logs.i(TAG, "----$$ onAudioFocusTransient() $$----");
        super.onAudioFocusTransient();
    }

    @Override
    public void onAudioFocusGain() {
        Logs.i(TAG, "----$$ onAudioFocusGain() $$----");
        super.onAudioFocusGain();
    }

    @Override
    public void onAudioFocusLoss() {
        Logs.i(TAG, "----$$ onAudioFocusLoss() $$----");
        super.onAudioFocusLoss();
    }

    @Override
    public void onDestroy() {
        Logs.i(TAG, "^^ onDestroy() ^^");
        registerAudioFocus(2);
        release();
        cachePlayState(PlayState.NONE);
        super.onDestroy();
    }

    /**
     * Cache Program Information
     * <p>
     * This method used to set playing status for Screen/Launcher
     */
    public void cachePlayState(PlayState playState) {
        //0-未打开
        //1-打开
        //2-播放
        int flag = 0;
        switch (playState) {
            case NONE:
                break;
            default:
                flag = (playState == PlayState.PLAY || playState == PlayState.PREPARED) ? 2 : 1;
                break;
        }
        SettingsSysUtil.setAudioState(this, flag);
    }
}