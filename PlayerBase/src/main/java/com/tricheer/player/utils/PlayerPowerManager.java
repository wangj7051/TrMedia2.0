package com.tricheer.player.utils;

import js.lib.android.utils.Logs;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

public class PlayerPowerManager {
	private final String TAG = "PlayerPowerManager";

	private Context mContext;
	private PowerManager mPowerManager;
	private PowerManager.WakeLock mWakeLock;

	@SuppressWarnings("deprecation")
	public PlayerPowerManager(Context cxt) {
		this.mContext = cxt;
		this.mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
		this.mWakeLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,
				"PlayerPowerManager");
	}

	@SuppressWarnings("deprecation")
	public void makeScreenOn(boolean isAcquire) {
		// Screen Status
		// Logs.i(TAG ,"makeScreenOn(isAcquire:" + isAcquire +
		// ") -> [isInteractive:" + mPowerManager.isInteractive());
		// if (mPowerManager.isInteractive()) {
		Logs.i(TAG, "makeScreenOn(isAcquire:" + isAcquire + ") -> [isInteractive:" + mPowerManager.isScreenOn());
		if (mPowerManager.isScreenOn()) {
			cancelSimpleNavi();
		} else if (isAcquire) {
			mWakeLock.acquire();
		}
	}

	private void cancelSimpleNavi() {
		Intent intent = new Intent();
		intent.setAction("com.tricheer.simple_navi");
		intent.setPackage("com.tricheer.maskapp");
		mContext.stopService(intent);
		// mPowerManager.userActivity(System.currentTimeMillis(), false);
	}

	/**
	 * if you used #WakeLock.acquire()
	 * <p>
	 * you must call this method on you OnDestroy().
	 */
	public void releaseAcquire() {
		mWakeLock.release();
	}

	/**
	 * 保持亮屏
	 */
	public void keepScreenOn(Activity act, boolean keep) {
		Logs.v(TAG, "keepScreenOn " + keep);
		try {
			if (act == null) {
				Logs.v(TAG, "keepScreenOn act null");
				return;
			}
			Window win = act.getWindow();
			if (win == null) {
				Logs.v(TAG, "keepScreenOn win null");
				return;
			}
			if (keep) {
				win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			} else {
				win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
		} catch (Exception e) {
		}
	}
}
