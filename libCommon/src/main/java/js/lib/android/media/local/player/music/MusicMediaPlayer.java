package js.lib.android.media.local.player.music;

import java.util.Timer;
import java.util.TimerTask;

import js.lib.android.media.local.player.IPlayerListener;
import js.lib.android.media.local.player.IPlayerState;
import js.lib.android.media.local.utils.MediaUtils;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.Logs;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;

/**
 * Android Native Player
 *
 * @author Jun.Wang
 */
public class MusicMediaPlayer implements IMusicPlayer {
    // LOG TAG
    private final String TAG = "MusicMediaPlayer";

    /**
     * Context Object
     */
    private Context mContext;
    /**
     * Handler Object
     */
    private Handler mHandler = new Handler();

    /**
     * Android MediaPlayer
     */
    private static MediaPlayer mMediaPlayer;
    /**
     * 当前正在播放的媒体路径
     */
    private String mMediaPath = "";
    /**
     * 是否是异步加载
     */
    private boolean mIsPrepareAsync = false;
    /**
     * prepareAsync，在加载过程中有可能会提前调用onCompletion/onError,这个标记就是用来区分是否已经prepare结束了
     */
    private boolean mIsPreparing = false;

    /**
     * Progress
     */
    private Timer mProgressTimer;
    private final int M_REFRESH_PERIOD = 200;
    private int mRefreshSeekCount = 0;

    /**
     * Player Listener
     */
    private IPlayerListener mPlayerListener;

    /**
     * Create Music Player - MediaPlayer
     */
    public MusicMediaPlayer(Context cxt, String mediaPath, IPlayerListener l) {
        this.mContext = cxt;
        this.mMediaPath = mediaPath;
        this.mPlayerListener = l;
        createMediaPlayer(cxt, mediaPath);
    }

    /**
     * Create Media Player Object
     */
    private void createMediaPlayer(Context cxt, String mediaPath) {
        try {
            mMediaPlayer = MediaPlayer.create(cxt, Uri.parse(mediaPath));
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    notifyPlayState(IPlayerState.PREPARED);
                    mIsPreparing = false;
                    if (mIsPrepareAsync) {
                        mIsPrepareAsync = false;
                        play();
                    }
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (!mIsPreparing) {
                        notifyPlayState(IPlayerState.COMPLETE);
                    }
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // Process Error
                    boolean isProcessError = true;
                    switch (what) {
                        // 未发现该问题有何用处，暂不处理该错误
                        case -38:
                            isProcessError = false;
                            break;
                        // Player Died Error
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                            mMediaPlayer.reset();
                            break;
                    }
                    // 通知监听器处理ERROR
                    if (isProcessError) {
                        notifyPlayState(IPlayerState.ERROR);
                    }

                    // LOG
                    Logs.i(TAG, "createMediaPlayer() -> onError(mp," + what + "," + extra + ")");
                    MediaUtils.printError(mp, what, extra);
                    return true;
                }
            });
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {

                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    if (!mIsPreparing) {
                        notifyPlayState(IPlayerState.SEEK_COMPLETED);
                    }
                }
            });
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "createMediaPlayer()", e);
            notifyPlayState(IPlayerState.ERROR_PLAYER_INIT);
        }
    }

    @Override
    public void play(String mediaUrl) {
        Logs.i(TAG, "^^ play(" + mediaUrl + ") ^^");
        try {
            CommonUtil.cancelTimer(mProgressTimer);
            this.mIsPrepareAsync = true;
            this.mIsPreparing = true;
            this.mMediaPath = mediaUrl;
            notifyPlayState(IPlayerState.REFRESH_UI);
            if (mMediaPlayer == null) {
                createMediaPlayer(mContext, mediaUrl);
            } else {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mediaUrl);
                mMediaPlayer.prepareAsync();
            }
        } catch (Exception e) {
            notifyPlayState(IPlayerState.ERROR);
            Logs.printStackTrace(TAG + "play(mediaUrl)", e);
        }
    }

    @Override
    public void playSync(String mediaUrl) {
        Logs.i(TAG, "^^ playSync(" + mediaUrl + ") ^^");
        try {
            CommonUtil.cancelTimer(mProgressTimer);
            this.mIsPrepareAsync = false;
            this.mMediaPath = mediaUrl;
            notifyPlayState(IPlayerState.REFRESH_UI);
            if (mMediaPlayer == null) {
                createMediaPlayer(mContext, mediaUrl);
            } else {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mediaUrl);
                mMediaPlayer.prepare();
            }
            play();
        } catch (Exception e) {
            notifyPlayState(IPlayerState.ERROR);
            Logs.printStackTrace(TAG + "play(mediaUrl)", e);
        }
    }

    @Override
    public String getMediaUrl() {
        return this.mMediaPath;
    }

    @Override
    public void setMediaUrl(String mediaUrl) {
        this.mMediaPath = mediaUrl;
    }

    @Override
    public void play() {
        setVolume(1.0f, 1.0f);
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            startProgressTimer();
            notifyPlayState(IPlayerState.PLAY);
        }
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            CommonUtil.cancelTimer(mProgressTimer);
            notifyPlayState(IPlayerState.PAUSE);
        }
    }

    /**
     * Reset Player
     * <p>
     * Resets the MediaPlayer to its uninitialized state. After calling this method, you will have
     * to initialize it again by setting the data source and calling prepare().
     */
    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            CommonUtil.cancelTimer(mProgressTimer);
            notifyPlayState(IPlayerState.RESET);
        }
    }

    /**
     * Get total duration
     *
     * @return the duration in milliseconds, if no duration is available (for example, if streaming
     * live content), -1 is returned.
     */
    @Override
    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * Get current play or pause position
     *
     * @return the current position in milliseconds
     */
    @Override
    public int getCurrentPos() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * Seek to selected position
     *
     * @param msec : milliseconds
     */
    public void seekTo(int msec) {
        if (mMediaPlayer != null) {
            if (msec >= (getDuration() - 1000)) {
                if (!mIsPreparing) {
                    notifyPlayState(IPlayerState.COMPLETE);
                }
            } else {
                mMediaPlayer.seekTo(msec);
            }
        }
    }

    @Override
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            CommonUtil.cancelTimer(mProgressTimer);
            notifyPlayState(IPlayerState.STOP);
        }
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            CommonUtil.cancelTimer(mProgressTimer);
            notifyPlayState(IPlayerState.RELEASE);
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null) {
            Logs.i(TAG, "setVolume(leftVolume,rightVolume) -> [left:" + leftVolume + "; right:" + rightVolume + "]");
            mMediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    @Override
    public void setPlayerListener(IPlayerListener l) {
        this.mPlayerListener = l;
    }

    /**
     * Notify Play state
     */
    public void notifyPlayState(int playState) {
        if (mPlayerListener != null) {
            mPlayerListener.onNotifyPlayState(playState);
        }
    }

    /**
     * EXEC Start or Cancel Progress Timer
     */
    private void startProgressTimer() {
        // Reset
        CommonUtil.cancelTimer(mProgressTimer);
        mRefreshSeekCount = 0;
        // Start
        mProgressTimer = new Timer();
        mProgressTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (isPlaying()) {
                    mHandler.post(mmRefreshProgressRunnable);
                    mRefreshSeekCount += M_REFRESH_PERIOD;
                }
            }

            private Runnable mmRefreshProgressRunnable = new Runnable() {

                @Override
                public void run() {
                    if (mPlayerListener != null) {
                        boolean isPerSecond = mRefreshSeekCount % 1000 == 0;
                        mPlayerListener.onProgressChange(getMediaUrl(), getCurrentPos(), getDuration(), isPerSecond);
                    }
                }
            };
        }, 0, M_REFRESH_PERIOD);
    }
}
