package js.lib.android.utils.net;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Net Common methods
 * 
 * @author Jun.Wang
 */
public class NetUtils {
	/**
	 * Open WIFI Setting Page
	 */
	public static void openWifiSettings(Context cxt) {
		Intent intent = new Intent("android.settings.WIFI_SETTINGS");
		cxt.startActivity(intent);
	}

	/**
	 * Check WIFI Status and Toast
	 */
	public static boolean isNetWorkActive(Context cxt, boolean isShowToast) {
		boolean isActvie = isNetWorkActive(cxt);
		if (isShowToast && !isActvie) {
			toastNetError(cxt);
		}
		return isActvie;
	}

	/**
	 * 网络是否已连接
	 */
	public static boolean isNetWorkActive(Context cxt) {
		ConnectivityManager connManager = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager != null) {
			// Mobile
			NetworkInfo mobileInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mobileInfo != null) {
				if (mobileInfo.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}

			// WIFI
			NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiInfo != null) {
				if (wifiInfo.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check WIFI Status
	 */
	public static boolean isWifiActive(Context cxt) {
		ConnectivityManager connManager = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (netInfo != null) {
			if (netInfo.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Toast Net Error
	 */
	public static void toastNetError(Context cxt) {
		Toast.makeText(cxt, "Net Error!", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Get {@link NetworkInfo}
	 */
	private static NetworkInfo getNetworkInfo(Context cxt, int networkType) {
		ConnectivityManager connManager = getConnManager(cxt);
		if (connManager != null) {
			return connManager.getNetworkInfo(networkType);
		}
		return null;
	}

	/**
	 * Get {@link ConnectivityManager}
	 */
	private static ConnectivityManager getConnManager(Context cxt) {
		return (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * WIFI 是否已连接
	 */
	public static boolean isWifiConnected(Context cxt) {
		NetworkInfo wifiInfo = getNetworkInfo(cxt, ConnectivityManager.TYPE_WIFI);
		if (wifiInfo != null) {
			return (wifiInfo.getState() == NetworkInfo.State.CONNECTED);
		}
		return false;
	}

	/**
	 * WIFI 是否已连接
	 */
	public static boolean isWifiEnable(Context cxt) {
		NetworkInfo wifiInfo = getNetworkInfo(cxt, ConnectivityManager.TYPE_WIFI);
		if (wifiInfo != null) {
			return wifiInfo.isAvailable();
		}
		return false;
	}

	/**
	 * 移动数据 是否已连接
	 */
	public static boolean isMobileConnected(Context cxt) {
		NetworkInfo mobileInfo = getNetworkInfo(cxt, ConnectivityManager.TYPE_MOBILE);
		if (mobileInfo != null) {
			return (mobileInfo.getState() == NetworkInfo.State.CONNECTED);
		}
		return false;
	}

	/**
	 * 移动数据 是否可用
	 */
	public static boolean isMobileEnable(Context cxt) {
		NetworkInfo mobileInfo = getNetworkInfo(cxt, ConnectivityManager.TYPE_MOBILE);
		if (mobileInfo != null) {
			return mobileInfo.isAvailable();
		}
		return false;
	}
}
