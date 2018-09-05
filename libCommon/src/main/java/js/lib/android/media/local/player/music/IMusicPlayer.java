package js.lib.android.media.local.player.music;

import js.lib.android.media.local.player.IPlayerListener;

/**
 * Define Player Active
 * 
 * @author Jun.Wang
 */
public interface IMusicPlayer {

	// Play State
	public void play(String mediaUrl);

	public void playSync(String mediaUrl);

	public void play();

	public boolean isPlaying();

	public void pause();

	public void stop();

	public void release();

	public void reset();

	public int getDuration();

	public int getCurrentPos();

	public void seekTo(int msec);

	/**
	 * 设置左右声道
	 * 
	 * @param leftVolume
	 *            [0.0f~1.0f]
	 * @param rightVolume
	 *            [0.0f~1.0f]
	 */
	public void setVolume(float leftVolume, float rightVolume);

	/**
	 * Set Target MediaUrl
	 */
	public void setMediaUrl(String mediaUrl);

	/**
	 * Get current Media URL
	 */
	public String getMediaUrl();

	/**
	 * Set Player Listener
	 */
	public void setPlayerListener(IPlayerListener l);
}
