package js.lib.android.media;

/**
 * Player Listener
 *
 * @author Jun.Wang
 */
public interface IPlayerListener {
    /**
     * 通知播放器状态
     *
     * @param playState : {@link IPlayerState}
     */
    void onNotifyPlayState(int playState);

    /**
     * 更新进度
     */
    void onProgressChange(String mediaUrl, int progress, int duration);
}
