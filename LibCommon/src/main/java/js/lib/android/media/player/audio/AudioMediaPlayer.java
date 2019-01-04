package js.lib.android.media.player.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import js.lib.android.media.engine.MediaUtils;
import js.lib.android.media.player.PlayDelegate;
import js.lib.android.media.player.PlayState;
import js.lib.android.utils.Logs;

/**
 * Android Native Player
 *
 * @author Jun.Wang
 */
public class AudioMediaPlayer implements IAudioPlayer {
    // LOG TAG
    private final String TAG = "AudioMediaPlayer";

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
    private String mMediaPath;
    /**
     * 是否是异步加载
     */
    private boolean mIsPrepareAsync = false;
    /**
     * prepareAsync，在加载过程中有可能会提前调用onCompletion/onError,这个标记就是用来区分是否已经prepare结束了
     */
    private boolean mIsPreparing = false;

    /**
     * Player Listener
     */
    private PlayDelegate mPlayDelegate;

    /**
     * Create Music Player - MediaPlayer
     */
    public AudioMediaPlayer(Context cxt, String mediaPath, PlayDelegate l) {
        this.mContext = cxt;
        this.mMediaPath = mediaPath;
        this.mPlayDelegate = l;
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
                    notifyPlayState(PlayState.PREPARED);
                    mIsPreparing = false;
                    if (mIsPrepareAsync) {
                        mIsPrepareAsync = false;
                        playMedia();
                    }
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (!mIsPreparing) {
                        notifyPlayState(PlayState.COMPLETE);
                    }
                }
            });
            mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                            MediaPlayer.MEDIA_INFO_UNKNOWN(未知的信息),
//                            MEDIA_INFO_VEDIO_TRACK_LAGGING(视频过于复杂解码太慢),
//                            MEDIA_INFO_VEDIO_RENDERING_START(开始渲染第一帧),
//                            MEDIA_INFO_BUFFRING_START(暂停播放开始缓冲更多数据),
//                            MEDIA_INFO_BUFFERING_END(缓冲了足够的数据重新开始播放),
//                            MEDIA_INFO_BAD_INTERLEAVING(错误交叉),
//                            MEDIA_INFO_NOT_SEEKABLE(媒体不能够搜索),
//                            MEDIA_INFO_METADATA_UPDATE(一组新的元数据用),
//                            MEDIA_INFO_UNSUPPORTED_SUBTITLE(不支持字幕),
//                            MEDIA_INFO_SUBTITLE_TIMED_OUT(读取字幕使用时间过长);
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_UNKNOWN:
                            Log.i(TAG, "onInfo - 未知的信息");
                            break;
                        case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                            Log.i(TAG, "onInfo - 视频过于复杂解码太慢");
                            break;
                        case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            Log.i(TAG, "onInfo - 开始渲染第一帧");
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            Log.i(TAG, "onInfo - 暂停播放开始缓冲更多数据");
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            Log.i(TAG, "onInfo - 缓冲了足够的数据重新开始播放");
                            break;
                        case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                            Log.i(TAG, "onInfo - 错误交叉");
                            break;
                        case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                            Log.i(TAG, "onInfo - 媒体不能够搜索");
                            break;
                        case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                            Log.i(TAG, "onInfo - 一组新的元数据用");
                            break;
                        case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                            Log.i(TAG, "onInfo - 读取字幕使用时间过长");
                            break;
                    }
                    return false;
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
                        notifyPlayState(PlayState.ERROR);
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
                        notifyPlayState(PlayState.SEEK_COMPLETED);
                    }
                }
            });
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "createMediaPlayer()", e);
            notifyPlayState(PlayState.ERROR_PLAYER_INIT);
        }
    }

    @Override
    public void playMedia(String mediaUrl) {
        Logs.i(TAG, "^^ play(" + mediaUrl + ") ^^");
        try {
            startProgressTimer(false);
            this.mIsPrepareAsync = true;
            this.mIsPreparing = true;
            this.mMediaPath = mediaUrl;
            notifyPlayState(PlayState.REFRESH_UI);
            if (mMediaPlayer == null) {
                createMediaPlayer(mContext, mediaUrl);
            } else {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mediaUrl);
                mMediaPlayer.prepareAsync();
            }
        } catch (Exception e) {
            notifyPlayState(PlayState.ERROR);
            Logs.printStackTrace(TAG + "play(mediaUrl)", e);
        }
    }

    @Override
    public void playMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            startProgressTimer(true);
            notifyPlayState(PlayState.PLAY);
        }
    }

    @Override
    public void pauseMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            startProgressTimer(false);
            notifyPlayState(PlayState.PAUSE);
        }
    }

    @Override
    public void resumeMedia() {
    }

    /**
     * Reset Player
     * <p>
     * Resets the MediaPlayer to its uninitialized state. After calling this method, you will have
     * to initialize it again by setting the data source and calling prepare().
     */
    @Override
    public void resetMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            startProgressTimer(false);
            notifyPlayState(PlayState.RESET);
        }
    }

    @Override
    public void stopMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            startProgressTimer(false);
            notifyPlayState(PlayState.STOP);
        }
    }

    @Override
    public void releaseMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            startProgressTimer(false);
            notifyPlayState(PlayState.RELEASE);
        }
    }

    @Override
    public boolean isMediaPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public int getMediaTime() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * Get total duration
     *
     * @return the duration in milliseconds, if no duration is available (for example, if streaming
     * live content), -1 is returned.
     */
    @Override
    public int getMediaDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * Seek to selected position
     *
     * @param msec : milliseconds
     */
    public void seekMediaTo(int msec) {
        if (mMediaPlayer != null) {
            if (msec >= (getMediaDuration() - 1000)) {
                if (!mIsPreparing) {
                    notifyPlayState(PlayState.COMPLETE);
                }
            } else {
                mMediaPlayer.seekTo(msec);
            }
        }
    }

    @Override
    public void setMediaPath(String mediaUrl) {
        this.mMediaPath = mediaUrl;
    }

    @Override
    public String getMediaPath() {
        return this.mMediaPath;
    }

    @Override
    public void setPlayerDelegate(PlayDelegate l) {
        this.mPlayDelegate = l;
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    /**
     * EXEC Start or Cancel Progress Timer
     */
    private void startProgressTimer(boolean isStart) {
        if (isStart) {
            notifyProgress(getMediaPath(), getMediaTime(), getMediaDuration());
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startProgressTimer(true);
                }
            }, 1000);
        } else {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * Notify Play state
     */
    private void notifyPlayState(PlayState playState) {
        if (mPlayDelegate != null) {
            mPlayDelegate.onPlayStateChanged(playState);
        }
    }


    /**
     * Notify progress
     *
     * @param path     - Media path
     * @param time     - Media time in duration
     * @param duration - Media duration
     */
    private void notifyProgress(String path, int time, int duration) {
        if (mPlayDelegate != null) {
            mPlayDelegate.onProgressChanged(path, time, duration);
        }
    }
}
