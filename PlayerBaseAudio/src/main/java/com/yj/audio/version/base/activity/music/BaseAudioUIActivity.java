package com.yj.audio.version.base.activity.music;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.yj.audio.engine.ThemeController;

public abstract class BaseAudioUIActivity extends BaseAudioKeyEventActivity implements ThemeController.ThemeChangeDelegate {
    //TAG
    private static final String TAG = "BaseAudioUIActivity";

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
}
