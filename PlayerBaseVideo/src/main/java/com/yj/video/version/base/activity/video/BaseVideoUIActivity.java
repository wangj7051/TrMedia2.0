package com.yj.video.version.base.activity.video;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.yj.video.engine.ThemeController;

public abstract class BaseVideoUIActivity extends BaseVideoKeyEventActivity implements ThemeController.ThemeChangeDelegate {
    //TAG
    private static final String TAG = "BaseVideoUIActivity";

    private Handler mHandler = new Handler();

    private ThemeController mThemeController;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.i(TAG, "onCreate(Bundle)");

        //
        mThemeController = new ThemeController(this, mHandler);
        mThemeController.addCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        checkAndUpdateTheme();
    }

    public void checkAndUpdateTheme() {
        Log.i(TAG, "checkAndUpdateTheme()");
        if (mThemeController != null) {
            mThemeController.checkAndUpdateTheme();
        }
    }

    @Override
    public void updateThemeToIos() {
        Log.i(TAG, "updateThemeToIos()");
    }

    @Override
    public void updateThemeToDefault() {
        Log.i(TAG, "updateThemeToDefault()");
    }

    public int getImgResId(String imgResName) {
        if (mThemeController != null) {
            return mThemeController.getImgResId(imgResName);
        }
        return 0;
    }

    public int getColorResId(String imgResName) {
        if (mThemeController != null) {
            return mThemeController.getColorResId(imgResName);
        }
        return 0;
    }

    protected void updateImgRes(final ImageView iv, final String resName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv.setTag(resName);
                iv.setImageResource(getImgResId(resName));
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mThemeController != null) {
            mThemeController.destroy();
            mThemeController = null;
        }
        super.onDestroy();
    }
}
