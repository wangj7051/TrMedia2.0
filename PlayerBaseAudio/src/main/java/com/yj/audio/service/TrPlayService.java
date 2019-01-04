package com.yj.audio.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.List;

import js.lib.android.media.bean.MediaBase;
import js.lib.android.media.player.PlayMode;
import js.lib.android.media.player.PlayState;
import js.lib.android.media.player.audio.service.AudioPlayService;
import js.lib.android.utils.Logs;

/**
 * Music Play Service
 *
 * @author Jun.Wang
 */
public class TrPlayService extends AudioPlayService {
    // TAG
    private final String TAG = "TrPlayService";

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
    public void setPlayList(List<? extends MediaBase> mediasToPlay) {
        super.setPlayList(mediasToPlay);
    }

    @Override
    public List<? extends MediaBase> getListMedias() {
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
    public MediaBase getCurrMedia() {
        return super.getCurrMedia();
    }

    @Override
    public String getCurrMediaPath() {
        return super.getCurrMediaPath();
    }

    @Override
    public long getProgress() {
        return super.getProgress();
    }

    @Override
    public long getDuration() {
        return super.getDuration();
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
    public void playNext() {
        super.playNext();
    }

    @Override
    public void playRandom() {
        super.playRandom();
    }

    @Override
    public void playAuto() {
        super.playAuto();
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
    public void resume() {
        super.resume();
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
    public void savePlayMediaInfo(String mediaPath, int progress) {
        super.savePlayMediaInfo(mediaPath, progress);
    }

    @Override
    public String[] getPlayedMediaInfo() {
        return super.getPlayedMediaInfo();
    }

    @Override
    public void clearPlayedMediaInfo() {
        super.clearPlayedMediaInfo();
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
    public void onAudioFocus(int flag) {
    }

    @Override
    public void onDestroy() {
        Logs.i(TAG, "^^ onDestroy() ^^");
        registerAudioFocus(2);
        release();
        super.onDestroy();
    }
}