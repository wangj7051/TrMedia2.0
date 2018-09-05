package com.tricheer.app.controller;

import js.lib.android.utils.AudioFocusUtil;
import js.lib.android.utils.AudioFocusUtil.AudioFocusListener;
import android.content.Context;

import com.tricheer.engine.mcu.MCUConsts.HandBrakeStatus;
import com.tricheer.engine.mcu.MCUReqUtil;
import com.tricheer.engine.mcu.MCUResUtil;
import com.tricheer.engine.mcu.MCUResUtil.ParseMcuRespListener;
import com.tricheer.engine.utils.BtUtil;
import com.tricheer.engine.utils.BtUtil.BtRespListener;
import com.tricheer.engine.utils.SysMediaControlUtil;
import com.tricheer.engine.utils.SysMediaControlUtil.SysMediaRespListener;
import com.tricheer.engine.utils.SystemUtil;
import com.tricheer.engine.utils.SystemUtil.SystemRespListener;

/**
 * Controller
 * 
 * @author Jun.Wang
 */
public abstract class Controller {
	// TAG
	// private String mLogTag = "Controller -> ";
	private String mSrcLogTag = "";

	/**
	 * Context
	 */
	protected Context mContext;

	/**
	 * MCU Request
	 */
	protected MCUReqUtil mMCUReqUtil;
	/**
	 * MCU Response
	 */
	protected MCUResUtil mMCUResUtil;

	/**
	 * System Response
	 */
	protected SystemUtil mSysUtil;

	/**
	 * BlueTooth Methods
	 */
	protected BtUtil mBtUtil;

	/**
	 * System Media Control Methods
	 */
	protected SysMediaControlUtil mSysMediaControlUtil;

	/**
	 * Audio Focus Control Methods
	 */
	protected AudioFocusUtil mAudioFocusUtil;

	public Controller(Context context) {
		mContext = context;
	}

	/**
	 * Control使能设置
	 */
	public abstract void setControlEnable(boolean isEnable);

	/**
	 * Set Log TAG
	 * 
	 * @param logTAG
	 *            : Java File Log TAG
	 */
	public void setLogTAG(String srcLogTAG) {
		mSrcLogTag = srcLogTAG;
		// mLogTag += mSrcLogTag;
	}

	/**
	 * Add MCU Request Control
	 */
	public void addMCUReqControl() {
		mMCUReqUtil = new MCUReqUtil(mContext);
		mMCUReqUtil.setLogTAG(mSrcLogTag);
	}

	/**
	 * Add MCU Response Control
	 */
	public void addMCURespControl(ParseMcuRespListener l) {
		mMCUResUtil = new MCUResUtil(mContext, l);
		mMCUResUtil.setLogTAG(mSrcLogTag);
	}

	/**
	 * Add BlueTooth Control
	 */
	public void addBtControl(BtRespListener l) {
		mBtUtil = new BtUtil(mContext, l);
		mBtUtil.setLogTAG(mSrcLogTag);
	}

	/**
	 * Add System Status Control
	 */
	public void addSysStatusControl(SystemRespListener l) {
		mSysUtil = new SystemUtil(mContext, l);
		mSysUtil.setLogTAG(mSrcLogTag);
	}

	/**
	 * Add System Media Control
	 */
	public void addSysMediaControl(SysMediaRespListener l) {
		mSysMediaControlUtil = new SysMediaControlUtil(mContext, l);
		mSysMediaControlUtil.setLogTAG(mSrcLogTag);
	}

	/**
	 * Add Audio Focus Control
	 */
	public void addAudioFocusControl(AudioFocusListener l) {
		mAudioFocusUtil = new AudioFocusUtil(mContext, l);
		mAudioFocusUtil.setLogTAG(mSrcLogTag);
	}

	/**
	 * Register Audio Focus
	 * <p>
	 * if==1 : Register audio focus
	 * <p>
	 * if==2 : Abandon audio focus
	 */
	public int registerAudioFocus(int flag) {
		if (mAudioFocusUtil != null) {
			return mAudioFocusUtil.registerAudioFocus(flag);
		}
		return -1;
	}

	/**
	 * Get HandBrake Status
	 * 
	 * @return Integer : {@link HandBrakeStatus}
	 */
	public int getHandBrakeOperateStatus() {
		if (mMCUReqUtil != null) {
			return mMCUReqUtil.getHandBrakeOperateStatus();
		}
		return HandBrakeStatus.OFF;
	}

	/**
	 * 获取设备掉电状态
	 * 
	 * @param operate
	 *            : {@link RadioReqCmds.DEVICE_POWER$DOWN_STATUS_GET} or
	 *            {@link RadioReqCmds.DEVICE_POWER$DOWN_STATUS_CLEAR}
	 * @return boolean : 是否掉电了
	 */
	public boolean isLastDevicePowerDown() {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.isLastDevicePowerDown();
		}
		return false;
	}
}
