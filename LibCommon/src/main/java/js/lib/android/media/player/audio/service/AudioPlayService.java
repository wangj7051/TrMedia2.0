package js.lib.android.media.player.audio.service;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.MediaBase;
import js.lib.android.media.player.PlayEnableController;
import js.lib.android.media.player.PlayMode;
import js.lib.android.media.player.PlayState;
import js.lib.android.media.player.audio.AudioMediaPlayer;
import js.lib.android.media.player.audio.AudioVlcPlayer;
import js.lib.android.media.player.audio.IAudioPlayer;
import js.lib.android.media.player.audio.MusicPlayerFactory;
import js.lib.android.media.player.audio.utils.AudioPreferUtils;
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
     * {@link VolumeFadeController} Object.
     */
    private VolumeFadeController mVolumeFadeController;

    /**
     * Delay play handler;
     */
    private Handler mDelayPlayHandler = new Handler();

    /**
     * Service 是否销毁了
     */
    protected boolean mIsServiceDestroy = false;

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
    private List<? extends MediaBase> mListMedias;
    /**
     * Current Play Position
     */
    private int mPlayIdx = 0;

    /**
     * 媒体源数据
     * <p>所有播放列表的数据都应当来自此对象</p>
     */
    private List<? extends MediaBase> mListSrcMedias;

    @Override
    public void onCreate() {
        super.onCreate();
        mVolumeFadeController = new VolumeFadeController();
    }

    /**
     * 设置媒体源数据
     * <p>
     * 请注意，为了保证播放的正常运行，此方法必须在数据获取到以后就设置进来，因为所有的播放数据源都来自 {@link #mListSrcMedias}
     * </p>
     */
    @Override
    public void setListSrcMedias(List<? extends MediaBase> listSrcMedias) {
        if (EmptyUtil.isEmpty(listSrcMedias)) {
            mListSrcMedias = new ArrayList<>();
        } else {
            mListSrcMedias = listSrcMedias;
        }
    }

    /**
     * 获取媒体源数据
     */
    @Override
    public List<? extends MediaBase> getListSrcMedias() {
        return mListSrcMedias;
    }

    @Override
    public void setPlayList(List<? extends MediaBase> mediasToPlay) {
        Logs.i(TAG, "setPlayList(List<? extends Program>)");
        if (mediasToPlay == null) {
            mListMedias = new ArrayList<>();
        } else {
            mListMedias = mediasToPlay;
        }
    }

    @Override
    public List<? extends MediaBase> getListMedias() {
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
    public MediaBase getCurrMedia() {
        MediaBase currMedia = null;
        try {
            currMedia = mListMedias.get(getCurrIdx());
        } catch (Exception e) {
            Log.i(TAG, "ERROR :: getCurrMedia() > " + e.getMessage());
            e.printStackTrace();
        }
        return currMedia;
    }

    @Override
    public String getCurrMediaPath() {
        return (mAudioPlayer == null) ? "" : mAudioPlayer.getMediaPath();
    }

    @Override
    public long getProgress() {
        return (mAudioPlayer == null) ? 0 : mAudioPlayer.getMediaTime();
    }

    @Override
    public long getDuration() {
        return (mAudioPlayer == null) ? 0 : mAudioPlayer.getMediaDuration();
    }

    @Override
    public void play() {
        Logs.i(TAG, "play()");
        MediaBase media = getCurrMedia();
        if (media != null) {
            playFixedMedia(media.mediaUrl);
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

            //
//            boolean isPlayerSwitched = false;
//            //是否需要切换播放器源
//            if (AudioInfo.isQualcommSupport(mediaUrl)) {
//                //播放mp3使用原生{@link android.media.MediaPlayer}
//                if (mAudioPlayer == null || mAudioPlayer instanceof AudioVlcPlayer) {
//                    Log.i(TAG, "switchAudioPlayer -> MEDIA_PLAYER");
//                    release();
//                    MusicPlayerFactory.instance().init(MusicPlayerFactory.PlayerType.MEDIA_PLAYER);
//                    isPlayerSwitched = true;
//                }
//            } else {
//                //其他使用{@link AudioVlcPlayer}
//                if (mAudioPlayer == null || mIsPlayerInitError || mAudioPlayer instanceof AudioMediaPlayer) {
//                    Log.i(TAG, "switchAudioPlayer -> VLC_PLAYER");
//                    release();
//                    MusicPlayerFactory.instance().init(MusicPlayerFactory.PlayerType.VLC_PLAYER);
//                    isPlayerSwitched = true;
//                }
//            }

            //
            boolean isPlayerSwitched = false;
            if (mAudioPlayer == null) {
                Log.i(TAG, "switchAudioPlayer -> MEDIA_PLAYER");
                release();
                MusicPlayerFactory.instance().init(MusicPlayerFactory.PlayerType.MEDIA_PLAYER);
                isPlayerSwitched = true;
            }

            //播放器切换过了,需要重新初始化
            Log.i(TAG, "isPlayerSwitched : " + isPlayerSwitched);
            if (isPlayerSwitched) {
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
                MediaBase media = mListMedias.get(idx);
                if (TextUtils.equals(media.mediaUrl, mediaPath)) {
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
        Log.i(TAG, "-- PlayENABLE --\n" + PlayEnableController.getStateDesc());
        if (PlayEnableController.isPlayEnable()) {
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
            //
            mIsPlayPrev = true;
            setPlayPosByMode(2);
            mVolumeFadeController.resetAndFadeOut();
            Log.i(TAG, "mPlayIdx - " + mPlayIdx);

            //Exec play runnable
            mDelayPlayHandler.removeCallbacksAndMessages(null);
            //防止高频点击，即用户在短时间内频繁点击执行下一个操作
            mDelayPlayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearPlayedMediaInfo();
                    play();
                }
            }, 600);
        } catch (Throwable e) {
            Logs.printStackTrace(TAG + "playPrev()", e);
        }
    }

    @Override
    public void playNext() {
        Logs.i(TAG, "^^ playNext() ^^");
        try {
            //
            mIsPlayNext = true;
            setPlayPosByMode(1);
            mVolumeFadeController.resetAndFadeOut();
            Log.i(TAG, "mPlayIdx - " + mPlayIdx);

            //Exec play runnable
            mDelayPlayHandler.removeCallbacksAndMessages(null);
            //防止高频点击，即用户在短时间内频繁点击执行下一个操作
            mDelayPlayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearPlayedMediaInfo();
                    play();
                }
            }, 600);
        } catch (Throwable e) {
            Logs.printStackTrace(TAG + "playNext()", e);
        }
    }

    /**
     * Random select to play.
     */
    public void playRandom() {
        Logs.i(TAG, "^^ playRandom() ^^");
        setRandomPos();
        play();
    }

    /**
     * AUTO Select to play
     */
    public void playAuto() {
        Logs.i(TAG, "^^ playAuto() ^^");
        PlayMode storePlayMode = getPlayMode();
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
            PlayMode storePlayMode = getPlayMode();
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
            e.printStackTrace();
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
        clearAllRunnable();
        if (mAudioPlayer != null) {
            mAudioPlayer.pauseMedia();
        }
    }

    @Override
    public void resume() {
        Logs.i(TAG, "^^ resume() ^^");
        clearAllRunnable();
        if (!EmptyUtil.isEmpty(mListMedias)) {
            if (mAudioPlayer == null) {
                playFixedMedia(getLastMediaPath());
            } else if (mAudioPlayer instanceof AudioVlcPlayer) {
                startPlay(getLastMediaPath());
            } else if (mAudioPlayer instanceof AudioMediaPlayer) {
                startPlay("");
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
            String[] mediaInfos = getPlayedMediaInfo();
            if (mediaInfos != null) {
                if (TextUtils.equals(mediaInfos[0], lastPath)) {
                    lastProgress = Integer.valueOf(mediaInfos[1]);
                } else {
                    clearPlayedMediaInfo();
                }
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getLastProgress()", e);
        }
        return lastProgress;
    }

    @Override
    public void savePlayMediaInfo(String mediaPath, int progress) {
        if (isPlaying() && progress > 0) {
            AudioPreferUtils.getLastPlayedMediaInfo(true, mediaPath, progress);
        }
    }

    @Override
    public String[] getPlayedMediaInfo() {
        return AudioPreferUtils.getLastPlayedMediaInfo(false, "", 0);
    }

    @Override
    public void clearPlayedMediaInfo() {
        AudioPreferUtils.getLastPlayedMediaInfo(true, "", 0);
    }

    @Override
    public void switchPlayMode(int supportFlag) {
        PlayMode storePlayMode = getPlayMode();
        if (storePlayMode == null) {
            return;
        }
        switch (supportFlag) {
            case 11: {
                switch (storePlayMode) {
                    case SINGLE:
                        setPlayMode(PlayMode.RANDOM);
                        break;
                    case RANDOM:
                        setPlayMode(PlayMode.LOOP);
                        break;
                    case LOOP:
                        setPlayMode(PlayMode.ORDER);
                        break;
                    case ORDER:
                        setPlayMode(PlayMode.SINGLE);
                        break;
                }
            }
            break;
            case 12: {
                switch (storePlayMode) {
                    case SINGLE:
                        setPlayMode(PlayMode.RANDOM);
                        break;
                    case RANDOM:
                        setPlayMode(PlayMode.LOOP);
                        break;
                    case LOOP:
                        setPlayMode(PlayMode.SINGLE);
                        break;
                }
            }
            break;
        }
    }

    @Override
    public void setPlayMode(PlayMode mode) {
        AudioPreferUtils.getPlayMode(true, mode);
        onPlayModeChange();
    }

    @Override
    public PlayMode getPlayMode() {
        return AudioPreferUtils.getPlayMode(false, PlayMode.LOOP);
    }

    @Override
    public void onPlayModeChange() {
        super.onPlayModeChange();
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
//        super.setVolume(leftVolume, rightVolume);
        if (mAudioPlayer != null) {
            mAudioPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    @Override
    public void onPlayStateChanged(PlayState playState) {
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
                mVolumeFadeController.resetAndFadeIn();
                onMediaPrepared();
                break;
            case COMPLETE:
                clearPlayedMediaInfo();
                playAuto();
                break;
            case ERROR:
            case ERROR_FILE_NOT_EXIST:
                onMediaError(true);
                break;
            case ERROR_PLAYER_INIT:
                onMediaError(false);
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

    private void onMediaError(boolean isSeriousError) {
        Log.i(TAG, "^^ onMediaError(" + isSeriousError + ") ^^");
        try {
            //之前执行的动作是播放上一个
            if (mIsPlayPrev) {
                Log.i(TAG, "onMediaError - mIsPlayPrev");
                mIsPlayPrev = false;
                playPrev();
                //之前执行的动作是播放下一个
            } else if (mIsPlayNext) {
                Log.i(TAG, "onMediaError - mIsPlayNext");
                mIsPlayNext = false;
                playNext();
            } else {
                PlayMode playMode = getPlayMode();
                Log.i(TAG, "onMediaError - playMode : " + playMode);
                if (playMode == PlayMode.SINGLE) {
                    if (isSeriousError) {
                        Log.i(TAG, "onMediaError - SINGLE : playNext()");
                        playNext();
                    } else {
                        Log.i(TAG, "onMediaError - SINGLE : replay");
                        play(getCurrMediaPath());
                    }
                } else {
                    Log.i(TAG, "onMediaError - playNext()");
                    playNext();
                }
            }
            notifyPlayState(PlayState.REFRESH_ON_ERROR);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "onMediaError()", e);
        }
    }

    @Override
    public void onProgressChanged(String mediaPath, int progress, int duration) {
        Logs.debugI(TAG, "onProgressChanged(" + mediaPath + "," + progress + "," + duration + ")");
        //Error
        if (progress >= 0 && duration > 0 && progress > duration) {
            PlayMode playMode = getPlayMode();
            Log.i(TAG, "[" + playMode + "[" + progress + "/" + duration + "]");
            if (playMode == PlayMode.SINGLE) {
                play(getCurrMediaPath());
            } else {
                playNext();
            }
            return;
        }

        //Normal
        super.onProgressChanged(mediaPath, progress, duration);
        savePlayMediaInfo(mediaPath, progress);
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
        clearAllRunnable();
        pause();
    }

    @Override
    public void onAudioFocusGain() {
        super.onAudioFocusGain();
        Logs.i(TAG, "----$$ onAudioFocusGain() $$----");
//        resume();
    }

    @Override
    public void onAudioFocusLoss() {
        super.onAudioFocusLoss();
        Logs.i(TAG, "----$$ onAudioFocusLoss() $$----");
        pause();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroy = true;
        mDelayPlayHandler.removeCallbacksAndMessages(null);
        if (mVolumeFadeController != null) {
            mVolumeFadeController.destroy();
            mVolumeFadeController = null;
        }
        PlayEnableController.pauseByUser(false);
        super.onDestroy();
    }

    /**
     * Volume fade in/out logic controller.
     */
    private class VolumeFadeController {
        private Handler mmFadeHandler = new Handler();
        private float mmVolume = 0.0f;
        private final float VOLUME_MAX = 1.0f, VOLUME_MIN = 0.0f;
        private final float STEP_VOLUME = 0.2f;

        void resetAndFadeIn() {
            mmVolume = 0.0f;
            fadeIn();
        }

        void fadeIn() {
            setPlayerVolume();
            mmVolume += STEP_VOLUME;
            if (mmVolume > VOLUME_MAX) {
                mmVolume = VOLUME_MAX;
            }
            Log.i(TAG, "fadeIn() - mmVolume : " + mmVolume);
            if (mmVolume < VOLUME_MAX) {
                mmFadeHandler.removeCallbacksAndMessages(null);
                mmFadeHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fadeIn();
                    }
                }, 100);
            }
        }

        void resetAndFadeOut() {
            mmVolume = 1.f;
            fadeOut();
        }

        void fadeOut() {
            setPlayerVolume();
            //Calculator
            mmVolume -= STEP_VOLUME;
            if (mmVolume < VOLUME_MIN) {
                mmVolume = VOLUME_MIN;
            }
            Log.i(TAG, "fadeOut() - mmVolume : " + mmVolume);
            if (mmVolume > VOLUME_MIN) {
                mmFadeHandler.removeCallbacksAndMessages(null);
                mmFadeHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fadeOut();
                    }
                }, 100);
            }
        }

        void setPlayerVolume() {
            if (mmVolume >= VOLUME_MIN && mmVolume <= VOLUME_MAX) {
                setVolume(mmVolume, mmVolume);
            }
        }

        void destroy() {
            mmFadeHandler.removeCallbacksAndMessages(null);
        }
    }
}