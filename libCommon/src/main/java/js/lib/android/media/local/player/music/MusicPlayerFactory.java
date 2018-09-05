package js.lib.android.media.local.player.music;

import android.content.Context;

import js.lib.android.media.local.player.IPlayerListener;
import js.lib.android.media.local.player.vlc.music.MusicVlcPlayer;

/**
 * Used to create player object
 *
 * @author Jun.Wang
 */
public class MusicPlayerFactory {

    /**
     * IMusicPlayer Object
     */
    private static IMusicPlayer mIMusicPlayer;

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
    public IMusicPlayer create(Context cxt, String mediaPath, IPlayerListener l) {
        if (mIMusicPlayer == null) {
            switch (mPlayerType) {
                case PlayerType.VLC_PLAYER:
                    mIMusicPlayer = new MusicVlcPlayer(cxt, mediaPath, l);
                    break;
                case PlayerType.MEDIA_PLAYER:
                    mIMusicPlayer = new MusicMediaPlayer(cxt, mediaPath, l);
                    break;
            }
        }
        return mIMusicPlayer;
    }

    /**
     * Destroy
     */
    public void destroy() {
        if (mIMusicPlayer != null) {
            mIMusicPlayer.stop();
            mIMusicPlayer.release();
            mIMusicPlayer = null;
        }
    }
}
