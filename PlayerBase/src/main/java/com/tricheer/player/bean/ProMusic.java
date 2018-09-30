package com.tricheer.player.bean;

import js.lib.android.media.audio.bean.AudioInfo;

/**
 * Music Program
 * 
 * @author Jun.Wang
 */
public class ProMusic extends Program {
	/**
	 * Serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Empty Construct
	 */
	public ProMusic() {
	}

	/**
	 * Construct ProMusic From AudioInfo
	 */
	public ProMusic(AudioInfo info) {
		this.sysMediaID = info.sysMediaID;
		this.title = info.title;
		this.titlePinYin = info.titlePinYin;
		this.artist = info.artist;
		this.albumID = info.albumID;
		this.albumName = info.album;
		this.mediaUrl = info.path;
		this.duration = info.duration;
	}

	/**
	 * Construct ProMusic From Media File
	 */
	public ProMusic(String mediaUrl, String title) {
		this.mediaUrl = mediaUrl;
		this.title = title;
	}

	/**
	 * System Database Media ID
	 */
	public long sysMediaID = 0;

	/**
	 * Album ID
	 */
	public long albumID = 0;

	/**
	 * Album Name
	 */
	public String albumName = "";

	/**
	 * Artist
	 */
	public String artist = "";

	/**
	 * words of a song
	 */
	public String lyric = "";

	/**
	 * Is Parsed By MP3File
	 * <p>
	 * 0 尚未获取信息
	 * <p>
	 * 1 已经获取过信息
	 */
	public int parseInfoFlag = 0;

	/**
	 * Copy srcPro`s DATA to targetPro
	 */
	public static void copy(ProMusic targetPro, ProMusic srcPro) {
		try {
			targetPro.sysMediaID = srcPro.sysMediaID;
			targetPro.title = srcPro.title;
			targetPro.titlePinYin = srcPro.titlePinYin;
			targetPro.artist = srcPro.artist;
			targetPro.albumID = srcPro.albumID;
			targetPro.albumName = srcPro.albumName;
			targetPro.mediaUrl = srcPro.mediaUrl;
			targetPro.duration = srcPro.duration;
		} catch (Exception e) {
		}
	}
}
