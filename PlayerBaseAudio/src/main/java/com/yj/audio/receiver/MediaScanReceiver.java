package com.yj.audio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yj.audio.engine.PlayerAppManager;

import js.lib.android_media.scan.audio.AudioScanService;

/**
 * Media Scan Receiver
 *
 * @author Jun.Wang
 */
public class MediaScanReceiver extends BroadcastReceiver {
    //TAG
    private static String TAG = "AudioScanReceiver";

    /**
     * U盘是否已经挂载
     */
    private static boolean mIsSdMounted = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Action
        String action = intent.getAction();
        Log.i(TAG, "onReceive() -> [action: " + action + "]");
//        Toast.makeText(context, action, Toast.LENGTH_LONG).show();

        if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)
                || Intent.ACTION_MEDIA_EJECT.equals(action)) {
            if (mIsSdMounted) {
                Log.i(TAG, "### Exit Audio Player ###");
                notifyScanService(context, AudioScanService.PARAM_SCAN_VAL_CANCEL);
                PlayerAppManager.exitCurrPlayer(true);
            }
            mIsSdMounted = false;

            // SDCard Mounted
        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)
                || "com.yj.test.scan_audios".equals(action)) {
            mIsSdMounted = true;
            notifyScanService(context, AudioScanService.PARAM_SCAN_VAL_START);
        }
    }

    /**
     * Notify scan service operate.
     *
     * @param context   {@link Context}
     * @param scanParam {@link AudioScanService#PARAM_SCAN_VAL_START} or {@link AudioScanService#PARAM_SCAN_VAL_CANCEL}
     */
    private void notifyScanService(Context context, String scanParam) {
        Intent intentScan = new Intent(context, AudioScanService.class);
        intentScan.putExtra(AudioScanService.PARAM_SCAN, scanParam);
        context.startService(intentScan);
    }
}
