package js.lib.android.utils;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

/**
 * GPS Common methods
 * 
 * @author Jun.Wang
 */
public class GPSUtils {
	/**
	 * Open GPS Setting Page
	 */
	public static void openGPSSet() {
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	/**
	 * Check GPS Status
	 */
	public static boolean isGPSActive(Context cxt) {
		LocationManager locationManager = ((LocationManager) cxt.getSystemService(Context.LOCATION_SERVICE));
		if (locationManager != null) {
			return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		return false;
	}
}
