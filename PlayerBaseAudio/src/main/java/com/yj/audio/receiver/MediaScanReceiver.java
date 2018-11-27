package com.yj.audio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.yj.audio.engine.PlayerAppManager;
import com.yj.audio.utils.PlayerFileUtils;

import js.lib.android_media_scan.MediaScanService;

/**
 * Media Scan Receiver
 *
 * @author Jun.Wang
 */
public class MediaScanReceiver extends BroadcastReceiver {
    //TAG
    private static String TAG = "MediaScanReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Action
        String action = intent.getAction();
        Log.i(TAG, "onReceive() -> [action: " + action + "]");
//        Toast.makeText(context, action, Toast.LENGTH_LONG).show();

        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            if (!PlayerFileUtils.isHasSupportStorage()) {
                Log.i(TAG, "### Exit Audio Player ###");
                PlayerAppManager.exitCurrPlayer();
            }

            // SDCard Mounted
        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            notifyScanService(context, MediaScanService.PARAM_SCAN_VAL_START);

            // SDCard UnMounted
        } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
            notifyScanService(context, MediaScanService.PARAM_SCAN_VAL_CANCEL);

            //Test
        } else if ("com.tricheer.player.START.LIST.ALL_MEDIAS".equals(action)) {
            notifyScanService(context, MediaScanService.PARAM_SCAN_VAL_START);
        }
    }

    /**
     * Notify scan service operate.
     *
     * @param context   {@link Context}
     * @param scanParam {@link MediaScanService#PARAM_SCAN_VAL_START} or {@link MediaScanService#PARAM_SCAN_VAL_CANCEL}
     */
    private void notifyScanService(Context context, String scanParam) {
        Intent intentScan = new Intent(context, MediaScanService.class);
        intentScan.putExtra(MediaScanService.PARAM_SCAN, scanParam);
        context.startService(intentScan);
    }
}
