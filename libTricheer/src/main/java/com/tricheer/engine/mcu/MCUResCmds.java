package com.tricheer.engine.mcu;

/**
 * MCU Response Commands
 * 
 * @author Jun.Wang
 */
public class MCUResCmds {
	/**
	 * 收音机列表响应广播
	 */
	public static final String ACTION_FREQLIST = "com.tricheer.fmfreqlist";

	/**
	 * 收音机响应通用广播
	 * <p>
	 * 参数1 : "resp", 类型为 byte[]
	 */
	public static final String ACTION_COMEVENT = "com.tricheer.comevent";
	/**
	 * CANBOX 手刹状态通知
	 * <p>
	 * 参数1(Integer) : brakeStatus, 取值[0,1]
	 */
	public static final String ACTION_HANDBRAKE_STATUS = "com.tricheer.canbox.HANDBRAKE_STATUS";

	/**
	 * 6. Get Radio Informations
	 * <p>
	 * Broadcast : {@link MCUResCmds.ACTION_COMEVENT}
	 */
	public static final byte RADIO_INFOS = 0x6B;

	/**
	 * Response 设置LOC ST状态
	 * <p>
	 * Broadcast : {@link MCUResCmds.ACTION_COMEVENT}
	 */
	public static final byte SETTING_STATUS = 0x67;
	/**
	 * Response 当前频率ST状态
	 * <p>
	 * Broadcast : {@link MCUResCmds.ACTION_COMEVENT}
	 */
	public static final byte CURR_FREQ_ST_STATUS = 0x66;

	/**
	 * Response 方向键
	 * <p>
	 * Broadcast : {@link MCUResCmds.ACTION_COMEVENT}
	 */
	public static final byte DIRECTION = 0x63;
	/**
	 * 表示方向键
	 */
	public static final byte DIRECTION_INNER_CMD = 0x01;

	/**
	 * Response Car Body Informations
	 * <p>
	 * Broadcast : {@link MCUResCmds.ACTION_COMEVENT}
	 */
	public static final byte CAR_BODY_INFOS = 0x65;
	/**
	 * 表示车身信息
	 */
	public static final byte CAR_BODY_INFOS_INNER_CMD = (byte) 0xFD;

	/**
	 * Response Car HandBrake Status
	 * <p>
	 * Broadcast : {@link MCUResCmds.ACTION_COMEVENT}
	 */
	public static final byte CAR_BRAKE_STATUS = 0x69;
}
