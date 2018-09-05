package com.tricheer.player.bean;

import js.lib.bean.BaseBean;

/**
 * Online Music Search Information
 * 
 * @author Jun.Wang
 */
public class OnlineMusicInfo extends BaseBean {

	/**
	 * Serialization
	 */
	private static final long serialVersionUID = 1L;

	public int id;
	public String key = "";
	public String keyPinyin = "";
	public String value = "";

	//
	public long createTime, updateTime;
}
