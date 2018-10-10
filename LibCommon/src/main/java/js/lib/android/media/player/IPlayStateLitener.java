package js.lib.android.media.player;

/**
 * Play state Listener
 *
 * @author Jun.Wang
 */
public interface IPlayStateLitener {
    /**
     * 通知播放器状态
     *
     * @param playState : {@link PlayState}
     */
    void onPlayStateChanged(PlayState playState);
}
