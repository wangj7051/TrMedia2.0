package js.lib.android.utils.gps;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

/**
 * GPS Common methods
 *
 * @author Jun.Wang
 */
public class GpsUtil {
    //TAG
//    private static final String TAG = "GpsUtil";

    /**
     * Get {@link LocationManager}
     */
    public static LocationManager getLocationManager(Context context) {
        return ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
    }

    /**
     * Check GPS enable status
     */
    public static boolean isEnable(Context context) {
        LocationManager locationManager = getLocationManager(context);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Open GPS Setting Page
     */
    public static void openGPSSet(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
