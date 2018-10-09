package js.lib.android.media.audio.service;

import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import js.lib.android.media.PlayEnableController;
import js.lib.android.media.PlayMode;
import js.lib.android.media.PlayState;
import js.lib.android.media.audio.AudioPreferUtils;
import js.lib.android.media.audio.IAudioPlayer;
import js.lib.android.media.audio.MusicPlayerFactory;
import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.Program;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.JsFileUtils;
import js.lib.android.utils.Logs;

/**
 * Audio play service
 *
 * @author Jun.Wang
 */
public abstract class AudioPlayService extends BaseAudioService {
    // TAG
    private final String TAG = "AudioPlayService";

    /**
     * Service 是否销毁了
     */
    protected boolean mIsServiceDestroy = false;

    /**
     * 是否在播放器初始化的时，产生了异常错误
     * <p>
     * {@link PlayState#ERROR_PLAYER_INIT}
     * </p>
     */
    private boolean mIsPlayerInitError = false;

    /**
     * 是否在第一个媒体加载完成后执行停止播放？
     * <p>
     * "顺序播放" 模式下，播放完成最后一个音频后，跳转到第一个媒体，停止播放
     * </p>
     */
    private boolean mIsPauseOnFirstLoaded = false;

    /**
     * Play Next/Previous Media Flag
     */
    private boolean mIsPlayNext = false, mIsPlayPrev = false;

    /**
     * Music Player Object
     */
    private IAudioPlayer mAudioPlayer;
    /**
     * Media list
     */
    private List<? extends Program> mListMedias;
    /**
     * Current Play Position
     */
    private int mPlayIdx = 0;

    /**
     * Play Previous/Next Security Runnable
     */
    private Runnable mPlayPrevSecRunnable, mPlayNextSecRunnable;

    @Override
    public void setPlayList(List<? extends Program> listMedias) {
        Logs.i(TAG, "^^ List<? extends Program> ^^");
        if (listMedias != null) {
            this.mListMedias = listMedias;
        }
    }

    @Override
    public List<? extends Program> getListMedias() {
        return mListMedias;
    }

    @Override
    public void setPlayPosition(int position) {
        mPlayIdx = position;
        if (mPlayIdx < 0 || mPlayIdx >= getTotalCount()) {
            mPlayIdx = 0;
        }
    }

    @Override
    public int getTotalCount() {
        return (mListMedias == null) ? 0 : mListMedias.size();
    }

    @Override
    public int getCurrIdx() {
        return mPlayIdx;
    }

    @Override
    public Program getCurrMedia() {
        try {
            return mListMedias.get(getCurrIdx());
        } catch (Exception e) {
            Log.i(TAG, "ERROR :: getCurrMedia() > " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getCurrMediaPath() {
        return (mAudioPlayer == null) ? "" : mAudioPlayer.getMediaPath();
    }

    @Override
    public int getProgress() {
        return (mAudioPlayer == null) ? 0 : mAudioPlayer.getMediaTime();
    }

    @Override
    public int getDuration() {
        return (mAudioPlayer == null) ? 0 : mAudioPlayer.getMediaDuration();
    }

    @Override
    public boolean isPlayEnable() {
        return PlayEnableController.isPlayEnable();
    }

    @Override
    public void play() {
        Logs.i(TAG, "play()");
        PlayEnableController.pauseByUser(false);
        Program media = getCurrMedia();
        if (media != null) {
            ProAudio proAudio = (ProAudio) media;
            playFixedMedia(proAudio.mediaUrl);
        }
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

    @Override
    public void play(String mediaPath) {
        int playPos = getPosAtList(mediaPath);
        play((playPos >= 0 ? playPos : 0));
    }

    /**
     * Get media position at list
     */
    private int getPosAtList(String mediaPath) {
        int pos = -1;
        if (!EmptyUtil.isEmpty(mListMedias)) {
            int loop = mListMedias.size();
            for (int idx = 0; idx < loop; idx++) {
                Program program = mListMedias.get(idx);
                if (TextUtils.equals(program.mediaUrl, mediaPath)) {
                    pos = idx;
                    break;
                }
            }
        }
        return pos;
    }

    @Override
    public void play(int pos) {
        setPlayPosition(pos);
        play();
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

    @Override
    public void playPrev() {
        Logs.i(TAG, "^^ playPrev() ^^");
        try {
            mIsPlayPrev = true;
            setPlayPosByMode(2);
            play();
        } catch (Throwable e) {
            Logs.printStackTrace(TAG + "playPrev()", e);
        }
    }

    @Override
    public void playPrevBySecurity() {
        clearAllRunables();
        if (mPlayPrevSecRunnable == null) {
            mPlayPrevSecRunnable = new Runnable() {

                @Override
                public void run() {
                    playPrev();
                }
            };
        }
        postDelayRunnable(mPlayPrevSecRunnable, 500);
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
        clearAllRunables();
        if (mPlayNextSecRunnable == null) {
            mPlayNextSecRunnable = new Runnable() {

                @Override
                public void run() {
                    playNext();
                }
            };
        }
        postDelayRunnable(mPlayNextSecRunnable, 500);
    }

    /**
     * AUTO Select to play
     */
    public void playAuto() {
        Logs.i(TAG, "^^ playAuto() ^^");
        PlayMode storePlayMode = AudioPreferUtils.getPlayMode(false, PlayMode.LOOP);
        // 如果是播放模式是“顺序模式”，并且已经播放完毕了最后一个，那么下面的动作是在跳转到第一个媒体后，停止播放
        if (storePlayMode == PlayMode.ORDER) {
            if (mPlayIdx >= (getTotalCount() - 1)) {
                mIsPauseOnFirstLoaded = true;
            }
        }

        // 如果不是单曲循环，获取下一个播放位置
        if (storePlayMode != PlayMode.SINGLE) {
            setPlayPosByMode(1);
        }
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
            PlayMode storePlayMode = AudioPreferUtils.getPlayMode(false, PlayMode.LOOP);
            // MODE : RANDOM
            switch (storePlayMode) {
                case RANDOM:
                    setRandomPos();
                    break;

                // SINGLE/LOOP/ORDER
                default:
                    switch (flag) {
                        case 1:
                            setNextPos();
                            break;
                        case 2:
                            setPrevPos();
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "setPlayPosByMode()", e);
        }
    }

    /**
     * Set Random Position
     */
    private void setRandomPos() {
        if (!EmptyUtil.isEmpty(mListMedias)) {
            mPlayIdx = CommonUtil.getRandomNum(mPlayIdx, getTotalCount());
        }
    }

    private void setNextPos() {
        mPlayIdx++;
        if (mPlayIdx >= getTotalCount()) {
            mPlayIdx = 0;
        }
    }

    private void setPrevPos() {
        mPlayIdx--;
        if (mPlayIdx < 0) {
            mPlayIdx = getTotalCount() - 1;
        }
        if (mPlayIdx < 0) {
            mPlayIdx = 0;
        }
    }

    @Override
    public boolean isPlaying() {
        return mAudioPlayer != null && mAudioPlayer.isMediaPlaying();
    }

    @Override
    public void pause() {
        Logs.i(TAG, "^^ pause() ^^");
        if (mAudioPlayer != null) {
            mAudioPlayer.pauseMedia();
        }
    }

    @Override
    public void pauseByUser() {
        Logs.i(TAG, "^^ pauseByUser() ^^");
        PlayEnableController.pauseByUser(true);
        clearAllRunables();
        pause();
    }

    @Override
    public boolean isPauseByUser() {
        return PlayEnableController.isPauseByUser();
    }

    @Override
    public void resume() {
        Logs.i(TAG, "^^ resume() ^^");
        if (mAudioPlayer != null) {
            startPlay("");
        }
    }

    @Override
    public void resumeByUser() {
        Logs.i(TAG, "^^ resumeByUser() ^^");
        clearAllRunables();
        if (!EmptyUtil.isEmpty(mListMedias)) {
            if (mAudioPlayer == null) {
                playFixedMedia(getLastMediaPath());
            } else {
                resume();
            }
        }
    }

    @Override
    public void release() {
        Logs.i(TAG, "^^ release() ^^");
        if (mAudioPlayer != null) {
            MusicPlayerFactory.instance().destroy();
            mAudioPlayer = null;
        }
    }

    @Override
    public void seekTo(int time) {
        Logs.i(TAG, "^^ seekTo(" + time + ") ^^");
        if (mAudioPlayer != null) {
            mAudioPlayer.seekMediaTo(time);
        }
    }

    @Override
    public void saveTargetMediaPath(String mediaPath) {
        AudioPreferUtils.getLastTargetMediaUrl(true, mediaPath);
    }

    @Override
    public String getLastTargetMediaPath() {
        return AudioPreferUtils.getLastTargetMediaUrl(false, "");
    }

    @Override
    public String getLastMediaPath() {
        String lastTargetMediaPath = getLastTargetMediaPath();
        if (TextUtils.isEmpty(lastTargetMediaPath)) {
            return "";
        } else {
            return lastTargetMediaPath;
        }
    }

    @Override
    public long getLastProgress() {
        int lastProgress = 0;
        try {
            String lastPath = getLastMediaPath();
            String[] mediaInfos = getPlayedMediaInfos();
            if (mediaInfos != null) {
                if (TextUtils.equals(mediaInfos[0], lastPath)) {
                    lastProgress = Integer.valueOf(mediaInfos[1]);
                } else {
                    clearPlayedMediaInfos();
                }
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getLastProgress()", e);
        }
        return lastProgress;
    }

    @Override
    public void savePlayMediaInfos(String mediaPath, int progress) {
        if (isPlaying()) {
            AudioPreferUtils.getLastPlayedMediaInfo(true, mediaPath, progress);
        }
    }

    @Override
    public String[] getPlayedMediaInfos() {
        return AudioPreferUtils.getLastPlayedMediaInfo(false, "", 0);
    }

    @Override
    public void clearPlayedMediaInfos() {
        AudioPreferUtils.getLastPlayedMediaInfo(true, "", 0);
    }

    @Override
    public void switchPlayMode(int supportFlag) {
        PlayMode storePlayMode = AudioPreferUtils.getPlayMode(false, PlayMode.LOOP);
        if (storePlayMode == null) {
            return;
        }
        if (supportFlag == 0) {
            switch (storePlayMode) {
                case SINGLE:
                    AudioPreferUtils.getPlayMode(true, PlayMode.RANDOM);
                    break;
                case RANDOM:
                    AudioPreferUtils.getPlayMode(true, PlayMode.LOOP);
                    break;
                case LOOP:
                    AudioPreferUtils.getPlayMode(true, PlayMode.ORDER);
                    break;
                case ORDER:
                    AudioPreferUtils.getPlayMode(true, PlayMode.SINGLE);
                    break;
            }
        } else if (supportFlag == 1) {
            switch (storePlayMode) {
                case SINGLE:
                    AudioPreferUtils.getPlayMode(true, PlayMode.RANDOM);
                    break;
                case RANDOM:
                    AudioPreferUtils.getPlayMode(true, PlayMode.LOOP);
                    break;
                case LOOP:
                    AudioPreferUtils.getPlayMode(true, PlayMode.SINGLE);
                    break;
            }
        }
        onPlayModeChange();
    }

    @Override
    public void setPlayMode(PlayMode mode) {
        AudioPreferUtils.getPlayMode(true, mode);
        onPlayModeChange();
    }

    @Override
    public void onPlayModeChange() {
        super.onPlayModeChange();
    }

    @Override
    public void onPlayStateChanged(PlayState playState) {
        super.onPlayStateChanged(playState);
        // 阻止继续执行
        // (1) 已经执行了Service销毁
        if (mIsServiceDestroy) {
            return;
        }

        // Service正常运行中
        Logs.i(TAG, "---->>>> onPlayStateChanged(" + playState + ") <<<<----");
        super.onPlayStateChanged(playState);
        // Process By Play State
        switch (playState) {
            case PREPARED:
                onMediaPrepared();
                break;
            case COMPLETE:
                clearPlayedMediaInfos();
                playAuto();
                break;
            case ERROR:
            case ERROR_FILE_NOT_EXIST:
                onMediaError();
                break;
            case ERROR_PLAYER_INIT:
                mIsPlayerInitError = true;
                onMediaError();
                break;
        }
    }

    private void onMediaPrepared() {
        Logs.i(TAG, "^^ onMediaPrepared() ^^");
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

    private void onMediaError() {
        Logs.i(TAG, "^^ onMediaError() ^^");
        try {
            if (mIsPlayPrev) {
                mIsPlayPrev = false;
                playPrevBySecurity();
            } else if (mIsPlayNext) {
                mIsPlayNext = false;
                playNextBySecurity();
            }
            notifyPlayState(PlayState.REFRESH_ON_ERROR);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "onMediaError()", e);
        }
    }

    @Override
    public void onProgressChanged(String mediaPath, int progress, int duration) {
        super.onProgressChanged(mediaPath, progress, duration);
        savePlayMediaInfos(mediaPath, progress);
    }

    @Override
    public void onAudioFocusDuck() {
        super.onAudioFocusDuck();
        Logs.i(TAG, "----$$ onAudioFocusDuck() $$----");
    }

    @Override
    public void onAudioFocusTransient() {
        super.onAudioFocusTransient();
        Logs.i(TAG, "----$$ onAudioFocusTransient() $$----");
        clearAllRunables();
        pause();
    }

    @Override
    public void onAudioFocusGain() {
        super.onAudioFocusGain();
        Logs.i(TAG, "----$$ onAudioFocusGain() $$----");
        resumeByUser();
    }

    @Override
    public void onAudioFocusLoss() {
        super.onAudioFocusLoss();
        Logs.i(TAG, "----$$ onAudioFocusLoss() $$----");
        pauseByUser();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroy = true;
        super.onDestroy();
    }
}