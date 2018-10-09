package js.lib.android.utils;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Reboot Device Tool Class
 * 
 * @author Jun.Wang
 */
public class RebootUtil {

	/**
	 * {@link Intent#ACTION_REQUEST_SHUTDOWN}
	 * <p>
	 * Activity Action: Start this activity to request system shutdown. The optional boolean extra field
	 * {@link #EXTRA_KEY_CONFIRM} can be set to true to request confirmation from the user before shutting down.
	 * 
	 * <p class="note">
	 * This is a protected intent that can only be sent by the system.
	 */
	private static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";
	/**
	 * {@link Intent#EXTRA_KEY_CONFIRM}
	 * <p>
	 * Set to true in {@link #ACTION_REQUEST_SHUTDOWN} to request confirmation from the user before shutting down.
	 */
	private static final String EXTRA_KEY_CONFIRM = "android.intent.extra.KEY_CONFIRM";

	/**
	 * Reboot Device with Toast
	 * 
	 * @param cxt
	 *            :{@link Context}
	 */
	public static void rebootWithToast(Context cxt) {
		Intent intent = new Intent(ACTION_REQUEST_SHUTDOWN);
		intent.putExtra(EXTRA_KEY_CONFIRM, true);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		cxt.startActivity(intent);
	}

	/**
	 * Common Reboot methods
	 * 
	 * @param cxt
	 *            :{@link Context}
	 */
	public static void reboot(Context cxt) {
		Intent data = new Intent(Intent.ACTION_REBOOT);
		data.putExtra("nowait", 1);
		data.putExtra("interval", 1);
		data.putExtra("window", 0);
		cxt.sendBroadcast(data);
	}

	/**
	 * Reboot by {@link PowerManager}
	 * 
	 * @param cxt
	 *            : {@link Context}
	 */
	public static void rebootByPowerManager(Context cxt) {
		PowerManager pm = (PowerManager) cxt.getSystemService(Context.POWER_SERVICE);
		pm.reboot(null);
	}
}
