package js.lib.android.media.player.audio;

import android.content.Context;

import js.lib.android.media.player.PlayDelegate;

/**
 * Used to create player object
 *
 * @author Jun.Wang
 */
public class MusicPlayerFactory {

    /**
     * {@link IAudioPlayer} Object
     */
    private static IAudioPlayer mPlayer;

    /**
     * Player Type Flag
     */
    private int mPlayerType = PlayerType.VLC_PLAYER;

    public interface PlayerType {
        int VLC_PLAYER = 1;
        int MEDIA_PLAYER = 2;
    }

    /**
     * Private constructor
     */
    private MusicPlayerFactory() {
    }

    private static class SingletonHolder {
        private static final MusicPlayerFactory INSTANCE = new MusicPlayerFactory();
    }

    public static MusicPlayerFactory instance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Initialize player
     *
     * @param playerType : {@link PlayerType}
     */
    public void init(int playerType) {
        mPlayerType = playerType;
    }

    /**
     * Create
     */
    public IAudioPlayer create(Context cxt, String mediaPath, PlayDelegate delegate) {
        if (mPlayer == null) {
            switch (mPlayerType) {
                case PlayerType.VLC_PLAYER:
                    mPlayer = new AudioVlcPlayer(cxt, mediaPath, delegate);
                    break;
                case PlayerType.MEDIA_PLAYER:
                    mPlayer = new AudioMediaPlayer(cxt, mediaPath, delegate);
                    break;
            }
        }
        return mPlayer;
    }

    /**
     * Destroy
     */
    public void destroy() {
        if (mPlayer != null) {
            mPlayer.stopMedia();
            mPlayer.releaseMedia();
            mPlayer = null;
        }
    }
}
