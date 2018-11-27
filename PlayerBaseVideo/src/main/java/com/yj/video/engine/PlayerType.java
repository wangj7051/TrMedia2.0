package com.yj.video.engine;

/**
 * 播放器类型
 * <p>
 * 该类用来区分到底是哪个播放器
 * </p>
 * 
 * @author Jun.Wang
 */
public class PlayerType {
	/**
	 * 播放器类型
	 */
	private static int mType = -1;

	/**
	 * 播放器标记 - 音乐
	 */
	public static final int MUSIC = 1;
	/**
	 * 播放器标记 - 视频
	 */
	public static final int VIDEO = 2;

	/**
	 * 设置播放器类型
	 * 
	 * @param type
	 *            : {@link PlayerType#MUSIC} or {@link PlayerType#VIDEO}
	 */
	public static void setType(int type) {
		mType = type;
	}

	/**
	 * 获取播放器类型
	 * 
	 * @return : {@link PlayerType#MUSIC} or {@link PlayerType#VIDEO}
	 */
	public static int type() {
		return mType;
	}

	/**
	 * 播放器类型判断 - 音乐
	 * 
	 * @return : true means Music Player
	 */
	public static boolean isMusic() {
		return (mType == MUSIC);
	}

	/**
	 * 播放器类型判断 - 视频
	 * 
	 * @return : true means Video Player
	 */
	public static boolean isVideo() {
		return (mType == VIDEO);
	}
}
