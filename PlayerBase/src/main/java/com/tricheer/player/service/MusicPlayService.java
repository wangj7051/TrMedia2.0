package com.tricheer.player.service;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.engine.PlayerConsts;
import com.tricheer.player.utils.PlayerLogicUtils;
import com.tricheer.player.utils.PlayerPreferUtils;

import java.io.Serializable;
import java.util.List;

import js.lib.android.media.PlayEnableController;
import js.lib.android.media.PlayMode;
import js.lib.android.media.PlayState;
import js.lib.android.media.audio.AudioPreferUtils;
import js.lib.android.media.audio.IAudioPlayer;
import js.lib.android.media.audio.MusicPlayerFactory;
import js.lib.android.media.audio.db.AudioDBManager;
import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.Program;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.JsFileUtils;
import js.lib.android.utils.Logs;

/**
 * Music Play Service
 *
 * @author Jun.Wang
 */
public class MusicPlayService extends BasePlayService {
    // TAG
    private final String TAG = "MusicPlayService";

    /**
     * 是否在第一个媒体加载完成后执行停止播放？
     * <p>
     * "顺序播放" 模式下，播放完成最后一个音频后，跳转到第一个媒体，停止播放
     */
    private boolean mIsPauseOnFirstLoaded = false;

    /**
     * 是否在播放器初始化的时，产生了异常错误
     * <p>
     * {@link PlayState#ERROR_PLAYER_INIT}
     */
    private boolean mIsPlayerInitError = false;

    /**
     * Play Next/Previous Media Flag
     */
    private boolean mIsPlayNext = false, mIsPlayPrev = false;

    /**
     * Program list that to play
     */
    private List<ProAudio> mListPrograms;
    /**
     * Current Play Position
     */
    private int mPlayPos = 0;

    /**
     * Music Player Object
     */
    private IAudioPlayer mAudioPlayer;

    /**
     * Play Previous/Next Security Runnable
     */
    private Runnable mPlayPrevSecRunnable, mPlayNextSecRunnable;

    /**
     * Get Service Object
     */
    public class LocalBinder extends Binder {
        public MusicPlayService getService() {
            return MusicPlayService.this;
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
        return new LocalBinder();
    }

    @Override
    public void onAudioFocusTransient() {
        super.onAudioFocusTransient();
        Logs.i(TAG, "----$$ onAudioFocusTransient() $$----");
        removePlayRunnable();
        pause();
    }

    @Override
    public void onAudioFocusLoss() {
        super.onAudioFocusLoss();
        Logs.i(TAG, "----$$ onAudioFocusLoss() $$----");
        pauseByUser();
    }

    @Override
    public void onAudioFocusGain() {
        super.onAudioFocusGain();
        Logs.i(TAG, "----$$ onAudioFocusGain() $$----");
        resumeByUser();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            boolean isJustOpenService = intent.getBooleanExtra(PlayerConsts.IS_JUST_OPEN_SERVICE, false);
            if (isJustOpenService) {
                setCurrPlayer(true, this);
                playOnStart();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Play On Start
     */
    private void playOnStart() {
        List<ProAudio> listMeidas = AudioDBManager.instance().getListMusics();
        if (!EmptyUtil.isEmpty(listMeidas)) {
            setPlayList(listMeidas);
            // Last Play Position
            String lastMediaUrl = getLastMediaPath();
            if (!EmptyUtil.isEmpty(lastMediaUrl)) {
                setPlayPosition(getPosAtList(lastMediaUrl));
            } else {
                setPlayPosition(0);
            }
            playFixedMedia(getCurrPosUrl());
        }
    }

    /**
     * 被用户暂停
     */
    public void pauseByUser() {
        PlayEnableController.pauseByUser(true);
        removePlayRunnable();
        pause();
    }

    /**
     * 被用户恢复播放
     * <p>
     * 指用户点击恢复或等同于此动作的操作
     */
    public void resumeByUser() {
        removePlayRunnable();
        if (!EmptyUtil.isEmpty(mListPrograms)) {
            if (mAudioPlayer == null) {
                playFixedMedia(getLastMediaPath());
            } else {
                resume();
            }
        }
    }

    /**
     * Notify Play State
     *
     * @param playState {@link PlayState}
     */
    @Override
    public void onPlayStateChanged(PlayState playState) {
        // 阻止继续执行
        // (1) 已经执行了Service销毁
        if (mIsDestoryed) {
            return;
        }

        // Service正常运行中
        super.onPlayStateChanged(playState);
        // Print LOGS
        Logs.i(TAG, " ");
        Logs.i(TAG, "---->>>> playState:[" + playState + "] <<<<----");
        // Cache Play State
        cachePlayState(playState);
        // Process By Play State
        switch (playState) {
            case PREPARED:
                onNotifyPlayState$Prepared();
                break;
            case COMPLETE:
                clearPlayedMediaInfos();
                playAuto();
                break;
            case ERROR:
            case ERROR_FILE_NOT_EXIST:
                onNotifyPlayState$Error();
                break;
            case ERROR_PLAYER_INIT:
                mIsPlayerInitError = true;
                onNotifyPlayState$Error();
                break;
        }
    }

    private void onNotifyPlayState$Prepared() {
        mIsPlayNext = false;
        mIsPlayPrev = false;
        if (mIsPauseOnFirstLoaded) {
            mIsPauseOnFirstLoaded = false;
            release();
        } else {
            long lastProgress = getLastProgress();
            if (lastProgress > 0) {
                seekTo((int) lastProgress);
            }
        }
    }

    private void onNotifyPlayState$Error() {
        try {
            mListPrograms.remove(mPlayPos);
            if (mIsPlayPrev) {
                mIsPlayPrev = false;
                mPlayPos--;
            } else if (mIsPlayNext) {
                mIsPlayNext = false;
            }
            if (!EmptyUtil.isEmpty(mListPrograms)) {
                int listSize = mListPrograms.size();
                if (mPlayPos >= listSize) {
                    mPlayPos = 0;
                } else if (mPlayPos < 0) {
                    mPlayPos = listSize - 1;
                }
                play(mPlayPos);
            }
            notifyPlayState(PlayState.REFRESH_ON_ERROR);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "playOnError()", e);
        }
    }

    /**
     * Get media position at list
     */
    public int getPosAtList(String mediaUrl) {
        int pos = -1;
        if (!EmptyUtil.isEmpty(mListPrograms)) {
            int loop = mListPrograms.size();
            for (int idx = 0; idx < loop; idx++) {
                ProAudio pro = mListPrograms.get(idx);
                if (pro.mediaUrl.equals(mediaUrl)) {
                    pos = idx;
                    break;
                }
            }
        } else {
            pos = -1;
        }
        return pos;
    }

    /**
     * Get Play List
     */
    public List<ProAudio> getPlayList() {
        return mListPrograms;
    }

    public void clear() {
        if (mAudioPlayer != null) {
            mAudioPlayer.setMediaPath("");
        }
    }

    /**
     * Play By Media Path
     */
    private void play(String path) {
        int playPos = getPosAtList(path);
        play((playPos >= 0 ? playPos : 0));
    }

    /**
     * Play the select position music
     */
    public void play(int pos) {
        mPlayPos = pos;
        play();
    }

    /**
     * 播放指定位置媒体
     * <p>
     * 调用此方法的时候一定要确定已经设置了播放位置
     */
    private void playFixedMedia(String mediaUrl) {
        if (JsFileUtils.isFileExist(mediaUrl)) {
            saveTargetMediaPath(mediaUrl);
            if (mAudioPlayer == null || mIsPlayerInitError) {
                release();
                mAudioPlayer = MusicPlayerFactory.instance().create(this, mediaUrl, this);
                onPlayStateChanged(PlayState.REFRESH_UI);
                startPlay("");
            } else {
                startPlay(mediaUrl);
            }
        } else {
            onPlayStateChanged(PlayState.ERROR_FILE_NOT_EXIST);
        }
    }

    /**
     * If you want play Music, this is the finally method that must be execute.
     */
    private void startPlay(String mediaUrl) {
        if (PlayEnableController.isPlayEnable()) {
            registerAudioFocus(1);
            if (EmptyUtil.isEmpty(mediaUrl)) {
                mAudioPlayer.playMedia();
            } else {
                mAudioPlayer.playMedia(mediaUrl);
            }
        }
    }

    /**
     * AUTO Select to play
     */
    public void playAuto() {
        Logs.i(TAG, "^^ playAuto() ^^");
        PlayMode storePlayMode = PlayerPreferUtils.getAudioPlayMode(false, PlayMode.LOOP);
        // 如果是播放模式是“顺序模式”，并且已经播放完毕了最后一个，那么下面的动作是在跳转到第一个媒体后，停止播放
        if (storePlayMode == PlayMode.ORDER) {
            try {
                if (mPlayPos >= (mListPrograms.size() - 1)) {
                    mIsPauseOnFirstLoaded = true;
                }
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "playAuto()", e);
            }
        }

        // 如果不是单曲循环，获取下一个播放位置
        if (storePlayMode != PlayMode.SINGLE) {
            setPlayPosByMode(1);
        }
        play();
    }

    /**
     * Random Select One to play
     */
    public void playRandomOne() {
        Logs.i(TAG, "----playRandomOne()----");
        setRandomPos();
        play();
    }

    /**
     * Set Play position by Play Mode
     *
     * @param flag : 1 means play next
     *             <p>
     *             2 means play previous
     */
    private void setPlayPosByMode(int flag) {
        try {
            PlayMode storePlayMode = PlayerPreferUtils.getAudioPlayMode(false, PlayMode.LOOP);
            // MODE : RANDOM
            if (storePlayMode == PlayMode.RANDOM) {
                setRandomPos();
                // MODE : SINGLE/LOOP/ORDER
            } else {
                if (flag == 1) {
                    mPlayPos++;
                    if (mPlayPos >= mListPrograms.size()) {
                        mPlayPos = 0;
                    }
                } else if (flag == 2) {
                    mPlayPos--;
                    if (mPlayPos < 0) {
                        mPlayPos = mListPrograms.size() - 1;
                    }
                }
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "setPlayPosByMode()", e);
        }
    }

    /**
     * Set Random Position
     */
    private void setRandomPos() {
        if (!EmptyUtil.isEmpty(mListPrograms)) {
            mPlayPos = CommonUtil.getRandomNum(mPlayPos, mListPrograms.size());
        }
    }

    /**
     * Get mediaUrl of Selected Position
     */
    public String getCurrPosUrl() {
        try {
            return mListPrograms.get(mPlayPos).mediaUrl;
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getCurrPosUrl()", e);
            return "";
        }
    }

    @Override
    public void switchPlayMode(int supportFlag) {
        PlayMode storePlayMode = AudioPreferUtils.getAudioPlayMode(false, PlayMode.NONE);
        if (supportFlag == 0) {
            switch (storePlayMode) {
                case SINGLE:
                    AudioPreferUtils.getAudioPlayMode(true, PlayMode.RANDOM);
                    break;
                case RANDOM:
                    AudioPreferUtils.getAudioPlayMode(true, PlayMode.LOOP);
                    break;
                case LOOP:
                    AudioPreferUtils.getAudioPlayMode(true, PlayMode.ORDER);
                    break;
                case ORDER:
                    AudioPreferUtils.getAudioPlayMode(true, PlayMode.SINGLE);
                    break;
            }
        } else if (supportFlag == 1) {
            switch (storePlayMode) {
                case SINGLE:
                    AudioPreferUtils.getAudioPlayMode(true, PlayMode.RANDOM);
                    break;
                case RANDOM:
                    AudioPreferUtils.getAudioPlayMode(true, PlayMode.LOOP);
                    break;
                case LOOP:
                    AudioPreferUtils.getAudioPlayMode(true, PlayMode.SINGLE);
                    break;
            }
        }
        onPlayModeChange();
    }

    @Override
    public void setPlayMode(PlayMode mode) {
        PlayerPreferUtils.getAudioPlayMode(true, mode);
        onPlayModeChange();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setPlayList(List<? extends Program> listPros) {
        Logs.i(TAG, "^^ setPlayList() ^^");
        if (listPros != null) {
            this.mListPrograms = (List<ProAudio>) listPros;
        }
    }

    @Override
    public void setPlayPosition(int playPos) {
        if (playPos >= 0) {
            mPlayPos = playPos;
        }
    }

    @Override
    public Program getCurrMedia() {
        ProAudio program = null;
        try {
            if (!EmptyUtil.isEmpty(mListPrograms)) {
                program = mListPrograms.get(mPlayPos);
            }
        } catch (Exception e) {
            program = null;
        }
        return program;
    }

    public void removePlayRunnable() {
        if (mPlayNextSecRunnable != null) {
            mHandler.removeCallbacks(mPlayNextSecRunnable);
        }
        if (mPlayPrevSecRunnable != null) {
            mHandler.removeCallbacks(mPlayPrevSecRunnable);
        }
    }

    @Override
    public void play() {
        Logs.i(TAG, "^^ play() ^^");
        PlayEnableController.pauseByUser(false);
        Serializable serialPro = getCurrMedia();
        if (serialPro != null) {
            ProAudio program = (ProAudio) serialPro;
            playFixedMedia(program.mediaUrl);
            cacheProgram(program);
        }
    }

    @Override
    public void playPrev() {
        Logs.i(TAG, "^^ playPrev() ^^");
        try {
            mIsPlayPrev = true;
            setPlayPosByMode(2);
            play();
        } catch (Throwable e) {
            Logs.printStackTrace(TAG + "playPre()", e);
        }
    }

    @Override
    public void playPrevBySecurity() {
        Logs.i(TAG, "^^ playPrevBySecurity() ^^");
        removePlayRunnable();
        if (mPlayPrevSecRunnable == null) {
            mPlayPrevSecRunnable = new Runnable() {

                @Override
                public void run() {
                    playPrev();
                }
            };
        }
        mHandler.postDelayed(mPlayPrevSecRunnable, 500);
    }

    @Override
    public void playNext() {
        Logs.i(TAG, "^^ playNext() ^^");
        try {
            mIsPlayNext = true;
            setPlayPosByMode(1);
            play();
        } catch (Throwable e) {
            Logs.printStackTrace(TAG + "playNext()", e);
        }
    }

    @Override
    public void playNextBySecurity() {
        Logs.i(TAG, "^^ playNextBySecurity() ^^");
        removePlayRunnable();
        if (mPlayNextSecRunnable == null) {
            mPlayNextSecRunnable = new Runnable() {

                @Override
                public void run() {
                    playNext();
                }
            };
        }
        mHandler.postDelayed(mPlayNextSecRunnable, 500);
    }

    @Override
    public void pause() {
        Logs.i(TAG, "^^ pause() ^^");
        if (mAudioPlayer != null) {
            savePlayInfo();
            mAudioPlayer.pauseMedia();
        }
    }

    @Override
    public void resume() {
        Logs.i(TAG, "^^ resume() ^^");
        if (mAudioPlayer != null) {
            startPlay("");
        }
    }

    @Override
    public void release() {
        Logs.i(TAG, "^^ release() ^^");
        if (mAudioPlayer != null) {
            savePlayInfo();
            MusicPlayerFactory.instance().destroy();
            mAudioPlayer = null;
        }
    }

    @Override
    public String getLastMediaPath() {
        return super.getLastMediaPath();
    }

    @Override
    public long getLastProgress() {
        int lastProgress = 0;
        try {
            String lastPath = getLastMediaPath();
            Logs.i(TAG, "getLastProgress() -> [lastPath:" + lastPath);

            String[] mediaInfos = getPlayedMediaInfos();
            if (mediaInfos != null) {
                Logs.i(TAG, "getLastProgress() -> [lastPlayedMediaUrl:" + mediaInfos[0]);
                if (TextUtils.equals(mediaInfos[0], lastPath)) {
                    lastProgress = Integer.valueOf(mediaInfos[1]);
                } else {
                    clearPlayedMediaInfos();
                }
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getLastMediaProgress()", e);
        }
        return lastProgress;
    }

    @Override
    public String getCurrMediaPath() {
        if (mAudioPlayer != null) {
            return mAudioPlayer.getMediaPath();
        }
        return "";
    }

    @Override
    public int getCurrIdx() {
        return mPlayPos;
    }

    @Override
    public int getTotalCount() {
        if (mListPrograms != null) {
            return mListPrograms.size();
        }
        return 0;
    }

    @Override
    public int getProgress() {
        if (mAudioPlayer != null) {
            return mAudioPlayer.getMediaTime();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (mAudioPlayer != null) {
            return mAudioPlayer.getMediaDuration();
        }
        return 0;
    }

    @Override
    public boolean isPlayEnable() {
        return PlayEnableController.isPlayEnable();
    }

    @Override
    public boolean isPlaying() {
        return mAudioPlayer != null && mAudioPlayer.isMediaPlaying();
    }

    @Override
    public boolean isPauseByUser() {
        return PlayEnableController.isPauseByUser();
    }

    @Override
    public void seekTo(int msec) {
        Logs.i(TAG, "^^ seekTo(" + msec + ") ^^");
        if (mAudioPlayer != null) {
            mAudioPlayer.seekMediaTo(msec);
        }
    }

    @Override
    public String getLastTargetMediaPath() {
        return PlayerPreferUtils.getLastTargetMediaUrl(false, PlayerCxtFlag.MUSIC_PLAYER, "");
    }

    @Override
    public void saveTargetMediaPath(String mediaPath) {
        PlayerPreferUtils.getLastTargetMediaUrl(true, PlayerCxtFlag.MUSIC_PLAYER, mediaPath);
    }

    @Override
    public String[] getPlayedMediaInfos() {
        return PlayerPreferUtils.getLastPlayedMediaInfo(false, PlayerCxtFlag.MUSIC_PLAYER, "", 0);
    }

    @Override
    public void savePlayMediaInfos(String mediaUrl, int progress) {
        PlayerPreferUtils.getLastPlayedMediaInfo(true, PlayerCxtFlag.MUSIC_PLAYER, mediaUrl, progress);
        Logs.debugI(TAG, "savePlayMediaInfos(mediaUrl) -> [mediaUrl:" + mediaUrl + " ; progress:" + progress);
    }

    @Override
    public void clearPlayedMediaInfos() {
        PlayerPreferUtils.getLastPlayedMediaInfo(true, PlayerCxtFlag.MUSIC_PLAYER, "", 0);
    }

    /**
     * Save Playing Media Information
     */
    public void savePlayInfo() {
        if (isPlaying()) {
            savePlayMediaInfos(getCurrMediaPath(), getProgress());
        }
    }

    /**
     * Cache Program Information
     * <p>
     * This method used to set program path/name/image for Screen/Launcher
     */
    private void cacheProgram(ProAudio program) {
        PlayerLogicUtils.cacheMusicProgram(getContentResolver(), program);
    }

    /**
     * Cache Program Information
     * <p>
     * This method used to set playing status for Screen/Launcher
     */
    public void cachePlayState(PlayState playState) {
        int isPlaying = (playState == PlayState.PLAY || playState == PlayState.PREPARED) ? 1 : 0;
        PlayerLogicUtils.cachePlayerState(getContentResolver(), PlayerCxtFlag.MUSIC_PLAYER, isPlaying);
    }

    /**
     * Set Player Flag
     */
    protected void setCurrPlayer(boolean isInit, Context cxt) {
        if (isInit) {
            PlayerAppManager.putCxt(PlayerCxtFlag.MUSIC_PLAYER, cxt);
        } else {
            PlayerAppManager.removeCxt(PlayerCxtFlag.MUSIC_PLAYER);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.i(TAG, "^^ onDestroy() ^^");
        mIsDestoryed = true;
        release();
        cachePlayState(PlayState.NONE);
        registerAudioFocus(2);
        setCurrPlayer(false, this);
    }
}