package com.yj.video.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yj.video.engine.PlayerAppManager;

import js.lib.android_media.scan.video.VideoScanService;


/**
 * Media Scan Receiver
 *
 * @author Jun.Wang
 */
public class MediaScanReceiver extends BroadcastReceiver {
    //TAG
    private static final String TAG = "MediaScanReceiver";

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
                try {
                    // notifyScanService(context, MediaScanService.PARAM_SCAN_VAL_CANCEL);
                    Log.i(TAG, "### Exit Video Player ###");
                    mIsSdMounted = false;
                    PlayerAppManager.exitCurrPlayer();
                } catch (Exception e) {
                    Log.i(TAG, "### Exit Video Player ### :: Exception");
                    e.printStackTrace();
                }
            }

            // SDCard Mounted
        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)
                || "com.yj.test.scan_videos".equals(action)) {
            mIsSdMounted = true;
            notifyScanService(context, VideoScanService.PARAM_SCAN_VAL_START);
        }
    }

    /**
     * Notify scan service operate.
     *
     * @param context   {@link Context}
     * @param scanParam {@link VideoScanService#PARAM_SCAN_VAL_START} or {@link VideoScanService#PARAM_SCAN_VAL_CANCEL}
     */
    private void notifyScanService(Context context, String scanParam) {
        Intent intentScan = new Intent(context, VideoScanService.class);
        intentScan.putExtra(VideoScanService.PARAM_SCAN, scanParam);
        context.startService(intentScan);
    }
}
