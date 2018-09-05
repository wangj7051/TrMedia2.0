package com.tricheer.engine.utils;

import js.lib.android.utils.Logs;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.tricheer.app.receiver.PlayerReceiverActions;
import com.tricheer.app.receiver.RadioReceiverActions;

/**
 * System Media Control Methods
 * 
 * @author Jun.Wang
 */
public class SysMediaControlUtil {
	// TAG
	private String TAG = "SysMediaControlUtil";
	private Context mContext;

	/**
	 * Parse BlueTooth Response Listener
	 */
	private SysMediaRespListener mRespListener;

	public interface SysMediaRespListener {
		public void respMediaSessionChange();

		public void respStopMedia(String processname);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            : 上下文
	 * @param l
	 *            : {@link SysMediaRespListener}
	 */
	public SysMediaControlUtil(Context context, SysMediaRespListener l) {
		mContext = context;
		mRespListener = l;
	}

	public void setLogTAG(String logTAG) {
		TAG += logTAG;
	}

	/**
	 * 媒体控制广播接收
	 */
	private BroadcastReceiver mMediaControlReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Logs.i(TAG, "mMediaControlReceiver -> [action : " + action);
			// 接到该广播表示，第三方音频打开了，此时要退出播放器，类似于播放器失去了声音焦点
			if (RadioReceiverActions.MEDIA_SESSION_CHANGE.equals(action)) {
				respSessionChange();

				// 接到该广播表示，第三方音频打开了，此时要退出播放器，类似于播放器失去了声音焦点
			} else if (PlayerReceiverActions.STOP_ALL_MEDIA.equals(action)) {
				String processname = intent.getStringExtra("processname");
				if (TextUtils.equals(processname, mContext.getPackageName())) {
					respStopMedia(processname);
				}
			}
		}

		private void respSessionChange() {
			if (mRespListener != null) {
				mRespListener.respMediaSessionChange();
			}
		}

		private void respStopMedia(String processname) {
			if (mRespListener != null) {
				mRespListener.respStopMedia(processname);
			}
		}
	};

	/**
	 * 注册控制媒体广播
	 */
	public void registerMediaControlReceiver(boolean isReg) {
		try {
			if (isReg) {
				IntentFilter ifControlMedia = new IntentFilter();
				ifControlMedia.addAction(RadioReceiverActions.MEDIA_SESSION_CHANGE);
				ifControlMedia.addAction(PlayerReceiverActions.STOP_ALL_MEDIA);
				mContext.registerReceiver(mMediaControlReceiver, ifControlMedia);
			} else {
				mContext.unregisterReceiver(mMediaControlReceiver);
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "->registerMediaControlReceiver()", e);
		}
	}
}
