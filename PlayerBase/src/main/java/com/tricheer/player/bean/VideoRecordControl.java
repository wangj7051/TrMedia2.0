package com.tricheer.player.bean;

/**
 * Record Control Entity
 * 
 * @author Jun.Wang
 * 
 */
public class VideoRecordControl {
	/**
	 * Means never Cancel or Close Record when 1080P is Playing
	 */
	public static final int RESET = -1;
	/**
	 * Means Continue Record when 1080p is Playing
	 */
	public static final int CONTINUE_ON_1080P_PLAYING = 1;
	/**
	 * Means Close Record when 1080p is Playing
	 */
	public static final int CLOSE_ON_1080P_PLAYING = 2;
}
