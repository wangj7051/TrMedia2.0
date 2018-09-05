package com.tricheer.engine.utils;

import js.lib.android.utils.Logs;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tricheer.app.receiver.RadioReceiverActions;

/**
 * System Methods
 * 
 * @author Jun.Wang
 */
public class SystemUtil {
	// TAG
	private String TAG = "SystemUtil";
	private Context mContext;

	/**
	 * System Response Listener
	 */
	private SystemRespListener mRespListener;

	public interface SystemRespListener {
		public void respSystemUp();

		public void respSystemDown();
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            : 上下文
	 * @param l
	 *            : {@link SystemRespListener}
	 */
	public SystemUtil(Context context, SystemRespListener l) {
		mContext = context;
		mRespListener = l;
	}

	public void setLogTAG(String logTAG) {
		TAG += logTAG;
	}

	/**
	 * System Status Receiver
	 */
	private BroadcastReceiver mSystemStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Logs.i(TAG, action);
			if (RadioReceiverActions.SYSTEM_UP.equals(action)) {
				respSysUp();
			} else if (RadioReceiverActions.SYSTEM_DOWN.equals(action)) {
				respSysDown();
			}
		}

		private void respSysUp() {
			if (mRespListener != null) {
				mRespListener.respSystemUp();
			}
		}

		private void respSysDown() {
			if (mRespListener != null) {
				mRespListener.respSystemDown();
			}
		}
	};

	/**
	 * Register System Status Receiver
	 */
	public void registerSystemStatusReceiver(boolean isReg) {
		try {
			if (isReg) {
				// Screen Status
				IntentFilter ifSystemStatus = new IntentFilter();
				ifSystemStatus.addAction(RadioReceiverActions.SYSTEM_UP);
				ifSystemStatus.addAction(RadioReceiverActions.SYSTEM_DOWN);
				mContext.registerReceiver(mSystemStatusReceiver, ifSystemStatus);
			} else {
				mContext.unregisterReceiver(mSystemStatusReceiver);
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "->registerSystemStatusReceiver(" + isReg + ")", e);
		}
	}
}
