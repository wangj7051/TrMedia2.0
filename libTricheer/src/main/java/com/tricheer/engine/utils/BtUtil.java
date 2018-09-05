package com.tricheer.engine.utils;

import js.lib.android.utils.Logs;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tricheer.app.receiver.RadioReceiverActions;

/**
 * BlueTooth Methods
 * 
 * @author Jun.Wang
 */
public class BtUtil {
	// TAG
	private String TAG = "BtUtil";
	private Context mContext;

	/**
	 * BlueTooth Response Listener
	 */
	private BtRespListener mRespListener;

	public interface BtRespListener {
		public void respCallEnd();
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            : 上下文
	 * @param l
	 *            : {@link BtRespListener}
	 */
	public BtUtil(Context context, BtRespListener l) {
		mContext = context;
		mRespListener = l;
	}

	public void setLogTAG(String logTAG) {
		TAG += logTAG;
	}

	/**
	 * BtCall Status Receiver
	 */
	private BroadcastReceiver mBtCallStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Logs.i(TAG, action);
			if (RadioReceiverActions.BT_CALL_END.equals(action)) {
				respBtCallEnd();
			}
		}

		private void respBtCallEnd() {
			if (mRespListener != null) {
				mRespListener.respCallEnd();
			}
		}
	};

	/**
	 * Register BluetoohCall Status Receiver
	 */
	public void registerBtStatusReceiver(boolean isReg) {
		try {
			if (isReg) {
				// Screen Status
				IntentFilter ifBtStatus = new IntentFilter(RadioReceiverActions.BT_CALL_END);
				mContext.registerReceiver(mBtCallStatusReceiver, ifBtStatus);
			} else {
				mContext.unregisterReceiver(mBtCallStatusReceiver);
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "->registerBtStatusReceiver(" + isReg + ")", e);
		}
	}
}
