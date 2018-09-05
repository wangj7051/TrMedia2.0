package com.tricheer.player.engine;

import java.io.Serializable;

import js.lib.android.utils.Logs;
import android.text.TextUtils;

/**
 * 播放使能标记
 * 
 * @author Jun.Wang
 */
public class PlayEnableFlag implements Serializable {
	// TAG
	private final String TAG = "PlayEnableFlag";

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 标记字符
	 */
	private final String FLAG = "FLAG";

	/**
	 * 可以播放
	 */
	private final String PLAY = "PLAY";
	/**
	 * 不允许播放 - 用户点击暂停了
	 */
	private final String PAUSE_BY_USER = "PAUSE_BY_USER";
	/**
	 * 不允许播放 - 蓝牙电话进行中
	 */
	private final String PAUSE_BY_BT_CALLING = "PAUSE_BY_BT_CALLING";
	/**
	 * 不允许播放 - 屏熄灭了
	 */
	private final String PAUSE_BY_SCREEN_OFF = "PAUSE_BY_SCREEN_OFF";
	/**
	 * 不允许播放 - 收到了广播"com.tricheer.SYSTEM_DOWN"
	 */
	private final String PAUSE_BY_SYSTEM_DOWN = "PAUSE_BY_SYSTEM_DOWN";
	/**
	 * 不允许播放 - AIOS打开
	 */
	private final String PAUSE_BY_AIOS_OPEN = "PAUSE_BY_AIOS_OPEN";
	/**
	 * 不允许播放 - E-Dog播报
	 */
	private final String PAUSE_BY_EDOG_PLAY = "PAUSE_BY_EDOG_PLAY";

	/**
	 * 播放标记
	 * <p>
	 * "FLAG | PLAY" 或者 "FLAG | PAUSE_BY_USER | PAUSE_BY_BT_CALLING | ..."
	 * <p>
	 * "FLAG | PLAY" 表示可以播放
	 */
	private String flag = "";

	/**
	 * 暂停 BY 用户点击了暂停
	 * 
	 * @param isPauseByUser
	 *            :用户是否点击了暂停
	 */
	public void pauseByUser(boolean isPauseByUser) {
		if (isPauseByUser) {
			flag += " | " + PAUSE_BY_USER;
		}
	}

	/**
	 * 暂停 BY 蓝牙电话进行中
	 * 
	 * @param isPauseByBtCalling
	 *            :蓝牙电话是否进行中
	 */
	public void pauseByBtCalling(boolean isPauseByBtCalling) {
		if (isPauseByBtCalling) {
			flag += " | " + PAUSE_BY_BT_CALLING;
		}
	}

	/**
	 * 暂停 BY 屏熄灭
	 * 
	 * @param isPauseByScreenOff
	 *            :屏是否熄灭
	 */
	public void pauseByScreenOff(boolean isPauseByScreenOff) {
		if (isPauseByScreenOff) {
			flag += " | " + PAUSE_BY_SCREEN_OFF;
		}
	}

	/**
	 * 暂停 BY 收到了广播"com.tricheer.SYSTEM_DOWN"
	 * 
	 * @param isSystemDown
	 *            :是否System Down
	 */
	public void pauseBySystemDown(boolean isSystemDown) {
		if (isSystemDown) {
			flag += " | " + PAUSE_BY_SYSTEM_DOWN;
		}
	}

	/**
	 * 暂停 BY AIOS打开
	 * 
	 * @param isAiosOpen
	 *            :是否AIOS打开
	 */
	public void pauseByAiosOpen(boolean isAiosOpen) {
		if (isAiosOpen) {
			flag += " | " + PAUSE_BY_AIOS_OPEN;
		}
	}

	/**
	 * 暂停 BY E-Dog播报
	 * 
	 * @param isAiosOpen
	 *            :E-Dog是否播报
	 */
	public void pauseByEDogPlay(boolean isEDogPlay) {
		if (isEDogPlay) {
			flag += " | " + PAUSE_BY_EDOG_PLAY;
		}
	}

	/**
	 * 使能标记设置完成
	 */
	public void complete() {
		if (TextUtils.isEmpty(flag)) {
			flag = (FLAG + " | " + PLAY);
		} else {
			flag = (FLAG + flag);
		}
	}

	/**
	 * 是否可以播放
	 */
	public boolean isPlayEnable() {
		return (FLAG + " | " + PLAY).equals(flag);
	}

	public boolean isBtCalling() {
		if (!TextUtils.isEmpty(flag) && flag.contains(PAUSE_BY_BT_CALLING)) {
			return true;
		}
		return false;
	}

	/**
	 * 打印当前标记
	 */
	public void print() {
		Logs.i(TAG, "print() -> [" + flag + "]");
		Logs.i(TAG, " ");
	}
}
