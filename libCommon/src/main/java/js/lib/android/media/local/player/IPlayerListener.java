package js.lib.android.media.local.player;

/**
 * Player Listener
 * 
 * @author Jun.Wang
 */
public interface IPlayerListener {
	/**
	 * 通知播放器状态
	 * 
	 * @param playState
	 *            : {@link IPlayerState}
	 */
	public void onNotifyPlayState(int playState);

	/**
	 * 刷新进度
	 * 
	 * @param isPerSecond
	 *            : 是否是整秒
	 */
	/**
	 * 更新进度
	 * 
	 * @param isPerSecond
	 *            : 是否是整秒
	 */
	public void onProgressChange(String mediaUrl, int progress, int duration, boolean isPerSecond);
}
