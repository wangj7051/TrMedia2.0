package com.tricheer.player.version.base.activity.video;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.tricheer.player.engine.ThemeController;

public abstract class BaseVideoUIActivity extends BaseVideoKeyEventActivity implements ThemeController.ThemeChangeDelegate {
    //TAG
    private static final String TAG = "BaseVideoUIActivity";

    private Handler mHandler = new Handler();

    private ThemeController mThemeController;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //
        mThemeController = new ThemeController(this, mHandler);
        mThemeController.addCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndUpdateTheme();
    }

    public void checkAndUpdateTheme() {
        if (mThemeController != null) {
            mThemeController.checkAndUpdateTheme();
        }
    }

    @Override
    public void updateThemeToIos() {
    }

    @Override
    public void updateThemeToDefault() {
    }

    public int getImgResId(String imgResName) {
        if (mThemeController != null) {
            return mThemeController.getImgResId(imgResName);
        }
        return 0;
    }

    protected void updateImgRes(ImageView iv, String resName) {
        iv.setTag(resName);
        iv.setImageResource(getImgResId(resName));
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
