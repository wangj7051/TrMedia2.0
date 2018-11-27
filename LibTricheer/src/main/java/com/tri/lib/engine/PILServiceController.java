package com.tri.lib.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * ACC
 */
public class PILServiceController {
    private Object mPILManager;

    @SuppressLint("WrongConstant")
    public PILServiceController(Context context) {
        try {
            mPILManager = context.getSystemService("pil");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get acc state.
     *
     * @return int 0-OFF;1-ON
     */
    public int getAccState() {
        int accState = 0;
        if (mPILManager != null) {
            try {
                Class<?> cls = mPILManager.getClass();
                Method method = cls.getMethod("getAccState");
                method.setAccessible(true);
                accState = (int) method.invoke(mPILManager);
                Log.i("PILServiceController", "accState:" + accState);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return accState;
    }
}
