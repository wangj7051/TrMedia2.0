package js.lib.android.media.local.player.vlc.music;

import android.content.Context;
import android.util.Log;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import js.lib.android.media.local.player.IPlayerListener;
import js.lib.android.media.local.player.IPlayerState;
import js.lib.android.media.local.player.music.IMusicPlayer;
import js.lib.android.utils.Logs;

public class MusicVlcPlayer implements IMusicPlayer {
    // LOG TAG
    private final String TAG = "MusicVlcPlayer";

    /**
     * Context Object
     */
    private Context mContext;

    /**
     * ???
     */
    private LibVLC mLibVLC;
    /**
     * Android MediaPlayer
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
    public MusicVlcPlayer(Context cxt, String mediaPath, IPlayerListener l) {
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
            Media media = new Media(mLibVLC, mediaPath);
            mMediaPlayer = new MediaPlayer(media);
            mMediaPlayer.setEventListener(new MediaPlayerEvent());
        } catch (Exception e) {
            Log.i(TAG, "createMediaPlayer - Exception");
        }
    }

    private class MediaPlayerEvent implements MediaPlayer.EventListener {

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
                    if (mPlayerListener != null) {
                        mPlayerListener.onProgressChange(getMediaUrl(), getCurrentPos(), getDuration(), true);
                    }
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
    public void play(String mediaUrl) {
        Log.i(TAG, "play(" + mediaUrl + ")");
        try {
            setMediaUrl(mediaUrl);
            if (mMediaPlayer == null) {
                createMediaPlayer(mContext, mediaUrl);
            } else {
                mMediaPlayer.setMedia(new Media(mLibVLC, mediaUrl));
            }
            play();
        } catch (Exception e) {
            Log.i(TAG, "play(mediaUrl) - Exception");
        }
    }

    @Override
    public void playSync(String mediaUrl) {
    }

    @Override
    public void play() {
        Log.i(TAG, "play()");
        if (mMediaPlayer != null) {
            mMediaPlayer.play();
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public void pause() {
        Log.i(TAG, "pause()");
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void stop() {
        Log.i(TAG, "stop()");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void release() {
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
    public void reset() {
    }

    @Override
    public int getDuration() {
        if (mMediaPlayer != null) {
            return (int) mMediaPlayer.getLength();
        }
        return 0;
    }

    @Override
    public int getCurrentPos() {
        if (mMediaPlayer != null) {
            return (int) mMediaPlayer.getTime();
        }
        return 0;
    }

    @Override
    public void seekTo(int msec) {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isSeekable()) {
                mMediaPlayer.setTime(msec);
                notifyPlayState(IPlayerState.SEEK_COMPLETED);
            }
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
    }

    @Override
    public void setMediaUrl(String mediaUrl) {
        this.mMediaPath = mediaUrl;
    }

    @Override
    public String getMediaUrl() {
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
}
