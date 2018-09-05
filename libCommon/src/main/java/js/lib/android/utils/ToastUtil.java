package js.lib.android.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast Methods
 * 
 * @author Jun.Wang
 */
public class ToastUtil {
	public static void toast(Context cxt, int msgResID) {
		toast(cxt, msgResID, Toast.LENGTH_SHORT);
	}

	public static void toast(Context cxt, String msg) {
		toast(cxt, msg, Toast.LENGTH_SHORT);
	}

	public static void toast(Context cxt, int msgResID, int duration) {
		Toast.makeText(cxt, msgResID, duration).show();
	}

	public static void toast(Context cxt, String msg, int duration) {
		Toast.makeText(cxt, msg, duration).show();
	}
}
