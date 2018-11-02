package js.lib.android.media.player.video;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import js.lib.android.media.engine.MediaUtils;
import js.lib.android.media.player.PlayDelegate;
import js.lib.android.media.player.PlayState;
import js.lib.android.utils.Logs;
import js.lib.utils.date.DateFormatUtil;

/**
 * 自定义视频播放控件
 * <p>
 * Video Player 1 解决跳屏. frameworks/av/media/libmediaplayerservice/nuplayer，
 * 注释掉startOffloadPauseTimeout();
 * </p>
 *
 * @author Jun.Wang
 */
public class IVideoPlayer extends SurfaceView {
    // TAG
    private final String TAG = "IVideoPlayer";

    //--------->| Widgets |<---------
    /**
     * 画布容器 / 目标输出图像画布容器
     */
    private SurfaceHolder surfaceHolder, targetSurfaceHolder;

    //--------->| Variables |<---------
    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 媒体播放器
     */
    private MediaPlayer mMediaPlayer;
    private boolean mIsPlayAtBgOnSurfaceDestoryed = false;

    // 正在播放的媒体文件路径
    private String mMediaPath = "";

    // 播放器监听器
    private MediaPlayer.OnPreparedListener mPreparedListener;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    private MediaPlayer.OnErrorListener mErrorListener;
    private MediaPlayer.OnSeekCompleteListener mSeekCompleteListener;

    // Play listener
    private PlayDelegate mPlayDelegate;

    // 进度计时器
    private ProgressTimer mProgressTimer;
    private OnProgressChangeListener mProgressListener;

    public interface OnProgressChangeListener {
        void onProgressChange(String mediaUrl, int progress, int duration);
    }

    // 播放器状态
    private StatusMachine mStatus = StatusMachine.NONE;

    private enum StatusMachine {
        NONE, ASYNC_PREPARING, STARTED, PAUSED
    }

    public IVideoPlayer(Context context) {
        super(context);
        init(context);
    }

    public IVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IVideoPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // Context
        mContext = context;

        //
        if (mProgressTimer == null) {
            mProgressTimer = new ProgressTimer();
        } else {
            mProgressTimer.cancel();
        }

        // SurfaceView
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Logs.i(TAG, "init(context) -> surfaceCreated(holder)");
                targetSurfaceHolder = surfaceHolder;
                targetSurfaceHolder.setKeepScreenOn(true);
                play(false);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Logs.i(TAG, "init(context) -> surfaceChanged(holder,format,width,height)");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Logs.i(TAG, "init(context) -> surfaceDestroyed(holder)");
                targetSurfaceHolder = null;
                if (mIsPlayAtBgOnSurfaceDestoryed) {
                    play(false);
                } else if (mMediaPlayer != null) {
                    mMediaPlayer.setDisplay(null);
                }
            }
        });
    }

    private boolean createMediaPlayer() {
        Logs.i(TAG, "^^ createMediaPlayer() ^^");
        boolean isNewCreated = false;
        // 新建媒体播放器
        if (mMediaPlayer == null) {
            if (TextUtils.isEmpty(mMediaPath) || TextUtils.isEmpty(mMediaPath.trim())) {
                return false;
            }

            // 创建
            mMediaPlayer = MediaPlayer.create(mContext, Uri.parse(mMediaPath));
            if (mMediaPlayer == null) {
                if (mErrorListener != null) {
                    mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, -1);
                }
                return false;
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                isNewCreated = true;
            }

            // 设置监听加载
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    Logs.i(TAG, "createMediaPlayer() -> onPrepared() -> [mStatus:" + mStatus + "]");
                    // 异步加载完成后，启动播放
                    if (mStatus == StatusMachine.ASYNC_PREPARING) {
                        if (mPreparedListener != null) {
                            mPreparedListener.onPrepared(mp);
                        }
                        start();
                    }
                }
            });
            // 设置监听播放完成
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mProgressTimer.cancel();
                    Logs.i(TAG, "createMediaPlayer() -> onCompletion() -> [mStatus:" + mStatus + "]");
                    if (mStatus != StatusMachine.ASYNC_PREPARING) {
                        if (mCompletionListener != null) {
                            mCompletionListener.onCompletion(mp);
                        }
                    }
                }
            });
            // 设置监听异常
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // LOG
                    Logs.i(TAG, "createMediaPlayer() -> onError(mp," + what + "," + extra + ")");
                    MediaUtils.printError(mp, what, extra);

                    //Cancel progress timer
                    mProgressTimer.cancel();

                    // Process Error
                    boolean isProcessError = true;
                    switch (what) {
                        // 未发现该问题有何用处，暂不处理该错误
                        case -38:
                            isProcessError = false;
                            break;

                        //Media server died. In this case, the application must release the
                        //MediaPlayer object and instantiate a new one.
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                            release();
                            break;
                    }
                    // Error callback
                    if (isProcessError && mErrorListener != null) {
                        mErrorListener.onError(mp, what, extra);
                    }
                    return false;
                }
            });
            // 监听Seek
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {

                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    Logs.i(TAG, "createMediaPlayer() -> onSeekComplete() -> [mStatus:" + mStatus + "]");
                    if (mStatus != StatusMachine.ASYNC_PREPARING) {
                        if (mSeekCompleteListener != null) {
                            mSeekCompleteListener.onSeekComplete(mp);
                        }
                    }
                }
            });
            mMediaPlayer.setOnInfoListener(new OnInfoListener() {

                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    // Logs.i(TAG ,"createMediaPlayer() -> onInfo(mp," + what +
                    // "," + extra + ")");
                    return false;
                }
            });
        }
        return isNewCreated;
    }

    /**
     * 执行播放流程，播放指定媒体文件
     */
    public void play(String mediaPath) {
        Logs.i(TAG, "^^ play(" + mediaPath + ") ^^");
        setMediaPath(mediaPath);
        play();
    }

    /**
     * 执行播放流程
     */
    public void play() {
        Logs.i(TAG, "^^ play() ^^");
        if (TextUtils.isEmpty(mMediaPath) || TextUtils.isEmpty(mMediaPath.trim())) {
        } else if (mStatus == StatusMachine.PAUSED) {
            resume();
        } else {
            boolean isNewCreated = createMediaPlayer();
            play(isNewCreated);
        }
    }

    private void play(boolean isJustPlay) {
        Logs.i(TAG, "^^ play(" + isJustPlay + ") ^^");
        try {
            if (mMediaPlayer != null && !TextUtils.isEmpty(mMediaPath)) {
                if (isJustPlay) {
                    mMediaPlayer.setDisplay(targetSurfaceHolder);
                    start();
                } else {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(mMediaPath);
                    mMediaPlayer.setDisplay(targetSurfaceHolder);
                    mMediaPlayer.prepareAsync();
                    mStatus = StatusMachine.ASYNC_PREPARING;
                }
            }
        } catch (IllegalArgumentException e) {
            Logs.i(TAG, "----IllegalArgumentException----");
            e.printStackTrace();
        } catch (SecurityException e) {
            Logs.i(TAG, "----SecurityException----");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Logs.i(TAG, "----IllegalStateException----");
            e.printStackTrace();
        } catch (IOException e) {
            Logs.i(TAG, "----IOException----");
            e.printStackTrace();
        } catch (Exception e) {
            Logs.i(TAG, "----Exception----");
            e.printStackTrace();
        }
    }

    /**
     * 播放媒体
     */
    public void start() {
        Logs.i(TAG, "^^ start() ^^");
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            mStatus = StatusMachine.STARTED;
            mProgressTimer.scheduleRun();
        }
    }

    /**
     * 从播放状态暂停
     */
    public void pause() {
        Logs.i(TAG, "^^ pause() ^^");
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mProgressTimer.cancel();
                mMediaPlayer.pause();
                mStatus = StatusMachine.PAUSED;
            }
        }
    }

    /**
     * 从暂停状态恢复播放
     */
    private void resume() {
        Logs.i(TAG, "^^ resume() ^^");
        if (mStatus == StatusMachine.PAUSED) {
            start();
        }
    }

    private void release() {
        Logs.i(TAG, "^^ release() ^^");
        if (mMediaPlayer != null) {
            mProgressTimer.cancel();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mStatus = StatusMachine.NONE;
        }
    }

    public void seekTo(int pos) {
        Logs.i(TAG, "^^ seekTo(" + pos + ") ^^");
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(pos);
        }
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    /**
     * 设置媒体播放路径
     */
    public void setMediaPath(String path) {
        mMediaPath = path;
    }

    public String getMediaPath() {
        return mMediaPath;
    }

    /**
     * 当Surface销毁后，是否执行后台播放
     */
    public void setPlayAtBgOnSufaceDestoryed(boolean isPlayAtBgOnSurfaceDestoryed) {
        this.mIsPlayAtBgOnSurfaceDestoryed = isPlayAtBgOnSurfaceDestoryed;
    }

    /**
     * 监听媒体流是否加载完成
     *
     * @param l : {@link MediaPlayer.OnPreparedListener}
     */
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        this.mPreparedListener = l;
    }

    /**
     * 监听媒体流是否播放完毕
     *
     * @param l : {@link MediaPlayer.OnCompletionListener}
     */
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
        this.mCompletionListener = l;
    }

    /**
     * 监听媒体流是否播放异常
     *
     * @param l : {@link MediaPlayer.OnErrorListener}
     */
    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
        this.mErrorListener = l;
    }

    /**
     * 监听媒体流Seek是否完成
     *
     * @param l : {@link MediaPlayer.OnSeekCompleteListener}
     */
    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener l) {
        this.mSeekCompleteListener = l;
    }

    /**
     * 监听进度
     *
     * @param l : {@link OnProgressChangeListener}
     */
    public void setOnProgressChangeListener(OnProgressChangeListener l) {
        this.mProgressListener = l;
    }

    private class ProgressTimer {
        //TAG
        final String MM_TAG = "ProgressTimer";
        ScheduledThreadPoolExecutor mmExecutor;

        ProgressTimer() {
        }

        void scheduleRun() {
            // 隔DELAY_PERIOD后开始执行任务，并且在上一次任务开始后隔REFRESH_PERIOD再执行一次
            cancel();
            Log.i(MM_TAG, "scheduleRun()");
            mmExecutor = new ScheduledThreadPoolExecutor(5);
            mmExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        int duration = getDuration();
                        int progress = getCurrentPosition();
                        mProgressListener.onProgressChange(mMediaPath, progress, duration);
                    } catch (Exception e) {
                        Log.i(TAG, "EXCEPTION :: " + e.getMessage());
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        void cancel() {
            Log.i(MM_TAG, "cancel()");
            if (mmExecutor != null) {
                try {
                    mmExecutor.shutdown();
                    mmExecutor = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void playMedia() {
        play();
        notifyPlayState(PlayState.PLAY);
    }

    public void pauseMedia() {
        pause();
        notifyPlayState(PlayState.PAUSE);
    }

    public void resumeMedia() {
        resume();
    }

    public void releaseMedia() {
        release();
        notifyPlayState(PlayState.RELEASE);
    }

    public int getMediaDuration() {
        return DateFormatUtil.getIntSecondMsec(getDuration());
    }

    public int getMediaProgress() {
        return DateFormatUtil.getIntSecondMsec(getCurrentPosition());
    }

    public boolean isMediaPlaying() {
        return isPlaying();
    }

    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null) {
            try {
                Logs.i(TAG, "setVolume(leftVolume,rightVolume) -> [leftVolume:" + leftVolume + "; rightVolume:" + rightVolume
                        + "]");
                mMediaPlayer.setVolume(leftVolume, rightVolume);
            } catch (Throwable e) {
                Logs.printStackTrace(TAG + "setVolume()", e);
            }
        }
    }

    public void setPlayStateListener(PlayDelegate l) {
        this.mPlayDelegate = l;
        setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer = mp;
                notifyPlayState(PlayState.PREPARED);
            }
        });
        setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mMediaPlayer != null) {
                    mMediaPlayer = null;
                    notifyPlayState(PlayState.COMPLETE);
                }
            }
        });
        setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                notifyPlayState(PlayState.ERROR);
                return true;
            }
        });
        setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {

            @Override
            public void onSeekComplete(MediaPlayer mp) {
                notifyPlayState(PlayState.SEEK_COMPLETED);
            }
        });
        setOnProgressChangeListener(new OnProgressChangeListener() {

            @Override
            public void onProgressChange(String mediaUrl, int progress, int duration) {
                if (mPlayDelegate != null) {
                    mPlayDelegate.onProgressChanged(mediaUrl, progress, duration);
                }
            }
        });
    }

    /**
     * 通知播放器状态
     */
    private void notifyPlayState(PlayState playState) {
        if (mPlayDelegate != null) {
            mPlayDelegate.onPlayStateChanged(playState);
        }
    }
}