package js.lib.android.media.audio.player_vlc;

import android.content.Context;
import android.util.Log;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import js.lib.android.media.IPlayerListener;
import js.lib.android.media.IPlayerState;
import js.lib.android.media.audio.IAudioPlayer;
import js.lib.android.utils.Logs;

/**
 * VLC audio player
 */
public class AudioVlcPlayer implements IAudioPlayer {
    // LOG TAG
    private final String TAG = "VlcPlayer";

    /**
     * Context Object
     */
    private Context mContext;

    /**
     * {@link org.videolan.libvlc.LibVLC}
     */
    private LibVLC mLibVLC;
    /**
     * {@link org.videolan.libvlc.MediaPlayer}
     */
    private static MediaPlayer mMediaPlayer;
    /**
     * 当前正在播放的媒体路径
     */
    private String mMediaPath;
    /**
     * Player Listener
     */
    private IPlayerListener mPlayerListener;

    /**
     * Create Music Player - MediaPlayer
     */
    public AudioVlcPlayer(Context cxt, String mediaPath, IPlayerListener l) {
        this.mContext = cxt;
        this.mMediaPath = mediaPath;
        this.mPlayerListener = l;
        createMediaPlayer(cxt, mediaPath);
    }

    /**
     * Create Media Player Object
     */
    private void createMediaPlayer(Context cxt, String mediaPath) {
        Log.i(TAG, "createMediaPlayer(cxt," + mediaPath + ")");
        try {
            mLibVLC = new LibVLC(cxt);
            mMediaPlayer = new MediaPlayer(new Media(mLibVLC, mediaPath));
            mMediaPlayer.setEventListener(new PlayerEventImpl());
        } catch (Exception e) {
            Log.i(TAG, "createMediaPlayer - Exception");
        }
    }

    private class PlayerEventImpl implements MediaPlayer.EventListener {

        @Override
        public void onEvent(MediaPlayer.Event event) {
            switch (event.type) {
                case MediaPlayer.Event.MediaChanged:
                    Logs.debugI(TAG, "MediaPlayerEvent :: MediaChanged");
                    notifyPlayState(IPlayerState.REFRESH_UI);
                    break;
                case MediaPlayer.Event.Opening:
                    notifyPlayState(IPlayerState.PLAY);
                    notifyPlayState(IPlayerState.PREPARED);
                    Logs.debugI(TAG, "MediaPlayerEvent :: Opening");
                    break;
                case MediaPlayer.Event.Buffering:
                    Logs.debugI(TAG, "MediaPlayerEvent :: Buffering");
                    break;

                //Media is playing
                case MediaPlayer.Event.Playing:
                    Logs.debugI(TAG, "MediaPlayerEvent :: Playing");
                    notifyPlayState(IPlayerState.PLAY);
                    break;

                //Media is paused
                case MediaPlayer.Event.Paused:
                    Logs.debugI(TAG, "MediaPlayerEvent :: Paused");
                    notifyPlayState(IPlayerState.PAUSE);
                    break;

                case MediaPlayer.Event.Stopped:
                    Logs.debugI(TAG, "MediaPlayerEvent :: Stopped");
                    notifyPlayState(IPlayerState.STOP);
                    break;

                //Media completed
                case MediaPlayer.Event.EndReached:
                    Logs.debugI(TAG, "MediaPlayerEvent :: EndReached");
                    notifyPlayState(IPlayerState.COMPLETE);
                    break;

                //Media error on playing
                case MediaPlayer.Event.EncounteredError:
                    Logs.debugI(TAG, "MediaPlayerEvent :: EncounteredError");
                    notifyPlayState(IPlayerState.ERROR);
                    break;

                //Media current time changed
                case MediaPlayer.Event.TimeChanged:
                    Logs.debugI(TAG, "MediaPlayerEvent :: TimeChanged");
                    notifyProgress(getMediaPath(), getMediaTime(), getMediaDuration());
                    break;

                //Media current position changed
                case MediaPlayer.Event.PositionChanged:
                    //Logs.debugI(TAG, "MediaPlayerEvent :: PositionChanged");
                    break;

                case MediaPlayer.Event.SeekableChanged:
                    Logs.debugI(TAG, "MediaPlayerEvent :: SeekableChanged");
                    break;
                case MediaPlayer.Event.PausableChanged:
                    Logs.debugI(TAG, "MediaPlayerEvent :: PausableChanged");
                    break;
            }
        }
    }

    @Override
    public void playMedia(String mediaUrl) {
        Log.i(TAG, "play(" + mediaUrl + ")");
        try {
            setMediaPath(mediaUrl);
            if (mMediaPlayer == null) {
                createMediaPlayer(mContext, mediaUrl);
            } else {
                mMediaPlayer.setMedia(new Media(mLibVLC, mediaUrl));
            }
            playMedia();
        } catch (Exception e) {
            Log.i(TAG, "play(mediaUrl) - Exception");
        }
    }

    @Override
    public void playMedia() {
        Log.i(TAG, "play()");
        if (mMediaPlayer != null) {
            mMediaPlayer.play();
        }
    }

    @Override
    public void pauseMedia() {
        Log.i(TAG, "pause()");
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void resumeMedia() {
    }

    @Override
    public void resetMedia() {
    }

    @Override
    public void stopMedia() {
        Log.i(TAG, "stopMedia()");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void releaseMedia() {
        Log.i(TAG, "release()");
        if (mMediaPlayer != null) {
            //Release Media
            final Media media = mMediaPlayer.getMedia();
            if (media != null) {
                media.setEventListener(null);
            }

            //Release MediaPlayer
            mMediaPlayer.setEventListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.setMedia(null);
            mMediaPlayer.release();
            mMediaPlayer = null;

            //Release LibVLC
            mLibVLC.release();
            mLibVLC = null;
        }
    }

    @Override
    public boolean isMediaPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public int getMediaTime() {
        if (mMediaPlayer != null) {
            return (int) mMediaPlayer.getTime();
        }
        return 0;
    }

    @Override
    public int getMediaDuration() {
        if (mMediaPlayer != null) {
            return (int) mMediaPlayer.getLength();
        }
        return 0;
    }

    @Override
    public void seekMediaTo(int msec) {
        if (mMediaPlayer != null && mMediaPlayer.isSeekable()) {
            mMediaPlayer.setTime(msec);
            notifyPlayState(IPlayerState.SEEK_COMPLETED);
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
    public void setPlayerListener(IPlayerListener l) {
        this.mPlayerListener = l;
    }

    /**
     * Notify Play state
     */
    private void notifyPlayState(int playState) {
        if (mPlayerListener != null) {
            mPlayerListener.onNotifyPlayState(playState);
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
        if (mPlayerListener != null) {
            mPlayerListener.onProgressChange(path, time, duration);
        }
    }
}
