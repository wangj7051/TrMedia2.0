package js.lib.android.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * {@link Application} Informations
 * 
 * @author Jun.Wang
 */
public class AppUtil {
	/**
	 * 应用程序包名, 格式如:"com.test.sms"
	 */
	public static String pkgName = "";

	/**
	 * 版本名称，格式如 "1.0.0"
	 */
	public static String versionName = "";
	/**
	 * 版本号,如"1"、"2"...
	 */
	public static int versionCode = 0;

	private AppUtil() {
	}

	/**
	 * Initialize
	 * 
	 * @throws NameNotFoundException
	 */
	public static void init(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			pkgName = pInfo.packageName;
			versionName = pInfo.versionName;
			versionCode = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			pkgName = "";
			versionName = "";
			versionCode = 0;
		}
	}
}
