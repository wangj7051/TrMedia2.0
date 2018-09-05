package com.tricheer.engine.mcu;

/**
 * MCU Request Commands
 * 
 * @author Jun.Wang
 */
public class MCUReqCmds {
	/**
	 * 打开指定频道类型，频率
	 */
	public static final int OPEN_FM = 0x8A;
	/**
	 * 打开的频率所在列表中的位置设置
	 * <p>
	 * param1-{Position byte[0] , Section [1~6]}
	 */
	public static final int OPEN_FM_BY_POS = 0x9B;

	/**
	 * 保存当前频率到选中位置
	 * <p>
	 * param1-{Position byte[0] , Section [1~6]}
	 */
	public static final int SAVE_CURR_FREQ = 0x91;

	/**
	 * 设置ST标记
	 * <p>
	 * param1-{Position byte[0] , Section [0:ST off; 1:ST on; -1:表示不做设置，保持原值]}
	 * <p>
	 * param2-{Position byte[1] , Section [-1表示不做设置，保持原值]}
	 */
	public static final int SET_ST_FLAG = 0x90;
	/**
	 * 设置LOC标记
	 * <p>
	 * param1-{Position byte[0] , Section [-1表示不做设置，保持原值]}
	 * <p>
	 * param2-{Position byte[1] , Section [0:LOC off; 1:LOC on; -1:表示不做设置，保持原值]}
	 */
	public static final int SET_LOC_FLAG = 0x90;

	/**
	 * 扫频
	 * <p>
	 * param1-{Position byte[0] , 0:FM扫描，1：AM扫描}
	 * <p>
	 * param1-{Position byte[0] , 0:手动扫描向上，1：手动扫描向下，2:自动扫描 3:手动向上步进 4:手动向下步进,
	 * 5:SCAN}
	 */
	public static final int SCAN_FREQS = 0x8C;
	
	/**
	 * 设置声音静音与否
	 * <p>
	 * param1-{Position byte[0] , 0：打开声音 ，1：关闭声音}
	 */
	public static final int SET_VOL = 0xB3;

	/**
	 * 设备状态查询
	 */
	public static final int GET_DEVICE_STATUS = 0x83;

	/**
	 * 获取设备掉电状态
	 */
	public static final int GET_DEVICE_POWER_DOWN_STATUS = 0xBA;
	public static final int DEVICE_POWER$DOWN_STATUS_GET = 0x01, DEVICE_POWER$DOWN_STATUS_CLEAR = 0x02;
}
