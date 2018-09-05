package js.lib.android.utils;

import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 移动数据开关
 * 
 * @author Jun.Wang
 */
public class MobileDataUtil {

	public static boolean getMobileDataStatus(Context context) {
		boolean isOpen = false;
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			isOpen = (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOpen;
	}

	public static void toggleMobileData(Context context, boolean enabled) {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			Method setMobileDataEnabl = telephonyManager.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
			setMobileDataEnabl.invoke(telephonyManager, enabled);
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("wj", "MobileDataUtil > toggleMobileData > e:" + e.getMessage());
		}
	}
}
