package com.tricheer.app.controller;

import android.content.Context;


/**
 * Controller Implement - Player
 * 
 * @author Jun.Wang
 */
public class PlayerController extends Controller {

	public PlayerController(Context context) {
		super(context);
	}

	@Override
	public void setControlEnable(boolean isEnable) {
		if (mMCUResUtil != null) {
			mMCUResUtil.registerMCUInfoReceiver(isEnable);
		}
		if (mSysUtil != null) {
			mSysUtil.registerSystemStatusReceiver(isEnable);
		}
		if (mSysMediaControlUtil != null) {
			mSysMediaControlUtil.registerMediaControlReceiver(isEnable);
		}
	}

	public void setMCURespEnable(boolean isEnable) {
		if (mMCUResUtil != null) {
			mMCUResUtil.registerMCUInfoReceiver(isEnable);
		}
	}
}