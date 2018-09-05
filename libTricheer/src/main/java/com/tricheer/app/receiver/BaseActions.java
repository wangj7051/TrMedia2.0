package com.tricheer.app.receiver;

/**
 * Base Receiver Actions
 * 
 * @author Jun.Wang
 */
public interface BaseActions {

	/**
	 * ### Open Logs ###
	 * <p>
	 * Parameter1(boolean) : "IS_OPEN"[true,false]
	 */
	public static final String OPEN_LOGS = "com.tricheer.app.OPEN_LOGS";

	/**
	 * 系统开启
	 */
	public final String SYSTEM_UP = "com.tricheer.SYSTEM_UP";
	/**
	 * 系统关闭
	 */
	public final String SYSTEM_DOWN = "com.tricheer.SYSTEM_DOWN";

	/**
	 * 80s后系统彻底休眠通知
	 */
	public static final String SYSTEM_POWER_DISCONN = "android.hardware.input.action.POWER_DISCONNECTED";

	/**
	 * AIOS语音状态Action
	 */
	// ### AIS Window Status ###
	// Parameter|String : "SmallWindow" - ["Open","Close"]
	public static final String AIS_STATUS = "com.tricheer.AIOS_WINDOW";
}
