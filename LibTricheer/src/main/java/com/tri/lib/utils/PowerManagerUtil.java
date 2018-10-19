package com.tri.lib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

import js.lib.android.utils.Logs;

public class PowerManagerUtil {
    private final String TAG = "PlayerPowerManager";

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    @SuppressLint("InvalidWakeLockTag")
    public PowerManagerUtil(Context cxt) {
        this.mPowerManager = (PowerManager) cxt.getSystemService(Context.POWER_SERVICE);
        if (mPowerManager != null) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,
                    "PlayerPowerManager");
        }
    }

    @SuppressLint("WakelockTimeout")
    public void acquire() {
        if (!mPowerManager.isScreenOn()) {
            if (mWakeLock != null) {
                mWakeLock.acquire();
            }
        }
    }

    /**
     * if you used #WakeLock.acquire()
     * <p>
     * you must call this method on you OnDestroy().
     */
    public void releaseAcquire() {
        if (mWakeLock != null) {
            mWakeLock.release();
        }
    }

    /**
     * 保持亮屏
     */
    public void keepScreenOn(Activity act, boolean keep) {
        Logs.v(TAG, "keepScreenOn " + keep);
        try {
            if (act == null) {
                Logs.v(TAG, "keepScreenOn act null");
                return;
            }
            Window win = act.getWindow();
            if (win == null) {
                Logs.v(TAG, "keepScreenOn win null");
                return;
            }
            if (keep) {
                win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
