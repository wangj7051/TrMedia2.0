package com.tricheer.player.bean;

import js.lib.bean.BaseBean;

/**
 * Program
 * 
 * @author Jun.Wang
 */
public class Program extends BaseBean {

	/**
	 * Serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Program ID
	 */
	public int id;

	/**
	 * Program Title
	 */
	public String title = "";
	/**
	 * Spelling
	 */
	public String titlePinYin = "";

	/**
	 * Sort Letter [the first Char of Title]
	 */
	public String sortLetter = "";

	/**
	 * Media Play URL
	 */
	public String mediaUrl = "";

	/**
	 * Cover Image URL
	 */
	public String coverUrl = "";
	/**
	 * Cover Image Bitmap
	 */
	// public Bitmap coverBitmap;

	/**
	 * Program Duration
	 */
	public int duration = 0;

	/**
	 * Records create/update Time
	 */
	public long createTime, updateTime = 0;

	/**
	 * Is This Program Collect
	 * 
	 * if == 1 , yes
	 * <p>
	 * if == 0, not
	 */
	public int isCollected = 0;

	/**
	 * Is This Program Cause error when play
	 * 
	 * if == 1 , yes cause
	 * <p>
	 * if == 0, not cause
	 */
	public int isCauseError = 0;

	/**
	 * Is This Program Come from net
	 * 
	 * if == 1 , yes
	 * <p>
	 * if == 0, not
	 */
	public int isFromNet = 0;
}
