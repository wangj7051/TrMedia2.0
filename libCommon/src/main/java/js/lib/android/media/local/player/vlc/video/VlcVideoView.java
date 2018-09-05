package js.lib.android.media.local.player.vlc.video;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import js.lib.android.media.local.player.IPlayerListener;
import js.lib.android.media.local.player.IPlayerState;
import js.lib.android.media.local.player.video.IVideoPlayer;
import js.lib.android.utils.Logs;
import js.lib.utils.date.DateFormatUtil;

public class VlcVideoView extends SurfaceView {
    // TAG
    private final String TAG = "VlcVideoView";

    private Context mContext;

    // 正在播放的媒体文件路径
    private String mMediaPath = "";

    /**
     * 画布容器 / 目标输出图像画布容器
     */
    private SurfaceHolder surfaceHolder;

    private LibVLC mLibVLC;
    private MediaPlayer mMediaPlayer;

    private IVideoPlayer.OnProgressChangeListener mProgressListener;

    // 播放器监听器
    private android.media.MediaPlayer.OnPreparedListener mPreparedListener;
    private android.media.MediaPlayer.OnCompletionListener mCompletionListener;
    private android.media.MediaPlayer.OnErrorListener mErrorListener;
    private android.media.MediaPlayer.OnSeekCompleteListener mSeekCompleteListener;

    private IPlayerListener mPlayerListener;

    // 播放器状态
    private int mStatus = PlayerStatus.NONE;

    private interface PlayerStatus {
        int NONE = -1;
        int ASYNC_PREPARING = 1;
        int STARTED = 2;
        int PAUSED = 3;
    }

    public VlcVideoView(Context context) {
        super(context);
        init(context);
    }

    public VlcVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VlcVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        surfaceHolder = getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Logs.i(TAG, "init(context) -> surfaceCreated(holder)");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Logs.i(TAG, "init(context) -> surfaceChanged(holder,format,width,height)");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Logs.i(TAG, "init(context) -> surfaceDestroyed(holder)");
            }
        });
        createMediaPlayer();
    }

    private void createMediaPlayer() {
        Logs.i(TAG, "^^ createMediaPlayer() ^^");
        if (mMediaPlayer == null) {
            mLibVLC = new LibVLC(mContext);
            mMediaPlayer = new MediaPlayer(mLibVLC);
            IVLCVout vlcVout = mMediaPlayer.getVLCVout();
            vlcVout.addCallback(new IVLCVout.Callback() {

                @Override
                public void onSurfacesCreated(IVLCVout ivlcVout) {
                }

                @Override
                public void onSurfacesDestroyed(IVLCVout ivlcVout) {
                }
            });
            vlcVout.attachViews();
            vlcVout.setVideoView(this);
        }
    }

    /**
     * 执行播放流程，播放指定媒体文件
     */
    public void play(String mediaPath) {
        Logs.i(TAG, "^^ play(" + mediaPath + ") ^^");
        try {
            setMediaPath(mediaPath);
            if (mMediaPlayer == null) {
                createMediaPlayer();
            } else {
                mMediaPlayer.setMedia(new Media(mLibVLC, mediaPath));
            }
            play();
        } catch (Exception e) {
            Log.i(TAG, "play(mediaUrl) - Exception");
        }
    }

    /**
     * 执行播放流程
     */
    public void play() {
        Logs.i(TAG, "^^ play() ^^");
        if (mStatus == PlayerStatus.PAUSED) {
            resume();
        } else if (mMediaPlayer != null) {
            mMediaPlayer.play();
        }
    }

    public void start() {
        play();
        mStatus = PlayerStatus.STARTED;
    }

    public void pause() {
        Logs.i(TAG, "^^ pause() ^^");
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mStatus = PlayerStatus.PAUSED;
        }
    }

    /**
     * 从暂停状态恢复播放
     */
    private void resume() {
        Logs.i(TAG, "^^ resume() ^^");
        if (mStatus == PlayerStatus.PAUSED) {
            start();
        }
    }

    private void release() {
        Logs.i(TAG, "^^ release() ^^");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mStatus = PlayerStatus.NONE;
        }
    }

    public void seekTo(int pos) {
        Logs.i(TAG, "^^ seekTo(" + pos + ") ^^");
        if (mMediaPlayer != null) {
            mMediaPlayer.setTime(pos);
        }
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            return (int) mMediaPlayer.getLength();
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return (int) mMediaPlayer.getTime();
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
    }

    /**
     * 监听媒体流是否加载完成
     *
     * @param l : {@link android.media.MediaPlayer.OnPreparedListener}
     */
    public void setOnPreparedListener(android.media.MediaPlayer.OnPreparedListener l) {
        this.mPreparedListener = l;
        // if (mMediaPlayer != null) {
        // mMediaPlayer.setOnPreparedListener(mPreparedListener);
        // }
    }

    /**
     * 监听媒体流是否播放完毕
     *
     * @param l : {@link android.media.MediaPlayer.OnCompletionListener}
     */
    public void setOnCompletionListener(android.media.MediaPlayer.OnCompletionListener l) {
        this.mCompletionListener = l;
        // if (mMediaPlayer != null) {
        // mMediaPlayer.setOnCompletionListener(mCompletionListener);
        // }
    }

    /**
     * 监听媒体流是否播放异常
     *
     * @param l : {@link android.media.MediaPlayer.OnErrorListener}
     */
    public void setOnErrorListener(android.media.MediaPlayer.OnErrorListener l) {
        this.mErrorListener = l;
        // if (mMediaPlayer != null) {
        // mMediaPlayer.setOnErrorListener(mErrorListener);
        // }
    }

    /**
     * 监听媒体流Seek是否完成
     *
     * @param l : {@link android.media.MediaPlayer.OnSeekCompleteListener}
     */
    public void setOnSeekCompleteListener(android.media.MediaPlayer.OnSeekCompleteListener l) {
        this.mSeekCompleteListener = l;
        // if (mMediaPlayer != null) {
        // mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
        // }
    }

    /**
     * 监听进度
     *
     * @param l : {@link IVideoPlayer.OnProgressChangeListener}
     */
    public void setOnProgressChangeListener(IVideoPlayer.OnProgressChangeListener l) {
        this.mProgressListener = l;
    }

    public void playMedia() {
        play();
        notifyPlayState(IPlayerState.PLAY);
    }

    public void pauseMedia() {
        pause();
        notifyPlayState(IPlayerState.PAUSE);
    }

    public void resumeMedia() {
        resume();
    }

    public void releaseMedia() {
        release();
        notifyPlayState(IPlayerState.RELEASE);
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
                //mMediaPlayer.setVolume(leftVolume, rightVolume);
            } catch (Throwable e) {
                Logs.printStackTrace(TAG + "setVolume()", e);
            }
        }
    }

    public void setPlayStateListener(IPlayerListener l) {
        this.mPlayerListener = l;
        setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(android.media.MediaPlayer mp) {
//                mMediaPlayer = mp;
                notifyPlayState(IPlayerState.PREPARED);
            }
        });
        setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(android.media.MediaPlayer mp) {
                if (mMediaPlayer != null) {
                    mMediaPlayer = null;
                    notifyPlayState(IPlayerState.COMPLETE);
                }
            }
        });
        setOnErrorListener(new android.media.MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
                notifyPlayState(IPlayerState.ERROR);
                return true;
            }
        });
        setOnSeekCompleteListener(new android.media.MediaPlayer.OnSeekCompleteListener() {

            @Override
            public void onSeekComplete(android.media.MediaPlayer mp) {
                notifyPlayState(IPlayerState.SEEK_COMPLETED);
            }
        });
        setOnProgressChangeListener(new IVideoPlayer.OnProgressChangeListener() {

            @Override
            public void onProgressChange(String mediaUrl, int progress, int duration) {
                if (mPlayerListener != null) {
                    mPlayerListener.onProgressChange(mediaUrl, progress, duration, true);
                }
            }
        });
    }

    /**
     * 通知播放器状态
     */
    private void notifyPlayState(int playState) {
        if (mPlayerListener != null) {
            mPlayerListener.onNotifyPlayState(playState);
        }
    }
}
