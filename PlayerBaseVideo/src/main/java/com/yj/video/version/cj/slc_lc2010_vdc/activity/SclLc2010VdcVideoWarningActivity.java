package com.yj.video.version.cj.slc_lc2010_vdc.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.tri.lib.utils.TrVideoPreferUtils;
import com.yj.video.R;
import com.yj.video.engine.PlayerAppManager;
import com.yj.video.engine.ThemeController;
import com.yj.video.version.base.activity.BaseFragActivity;

import js.lib.android.utils.CommonUtil;

public class SclLc2010VdcVideoWarningActivity extends BaseFragActivity implements ThemeController.ThemeChangeDelegate {
    //TAG
    private static final String TAG = "video_warning";

    private ImageView ivVSeparate1, ivVSeparate2, ivHSeparate1;
    private View vAgree, vNoToast, vExit;


    private Handler mHandler = new Handler();
    private ThemeController mThemeController;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.scl_lc2010_vdc_activity_video_warning);
        PlayerAppManager.addContext(this);
        init();
    }

    private void init() {
        //----Widgets----
        vAgree = findViewById(R.id.v_agree);
        vAgree.setOnClickListener(mViewOnClick);

        vNoToast = findViewById(R.id.v_no_toast);
        vNoToast.setOnClickListener(mViewOnClick);

        vExit = findViewById(R.id.v_exit);
        vExit.setOnClickListener(mViewOnClick);

        //
        ivVSeparate1 = (ImageView) findViewById(R.id.iv_v_separate1);
        ivVSeparate2 = (ImageView) findViewById(R.id.iv_v_separate2);
        ivHSeparate1 = (ImageView) findViewById(R.id.iv_h_separate1);

        //
        mThemeController = new ThemeController(this, mHandler);
        mThemeController.addCallback(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //Hide navigation bar
        CommonUtil.setNavigationBar(this, hasFocus ? 0 : 1);
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

    private final View.OnClickListener mViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == vAgree) {
                TrVideoPreferUtils.getVideoWarningFlag(true, 1);
                finishByOperate("EXIT_WARNING");
            } else if (v == vNoToast) {
                TrVideoPreferUtils.getVideoWarningFlag(true, 2);
                finishByOperate("EXIT_WARNING");
            } else if (v == vExit) {
                PlayerAppManager.exitCurrPlayer();
            }
        }
    };

    private void finishByOperate(String flag) {
        //Finish
        Intent data = new Intent();
        data.putExtra("flag", flag);
        setResult(0, data);
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        PlayerAppManager.exitCurrPlayer();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause()");
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    public void finish() {
        Log.i(TAG, "finish()");
        clearActivity();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy()");
        clearActivity();
        super.onDestroy();
    }

    private void clearActivity() {
        if (mThemeController != null) {
            mThemeController.destroy();
            mThemeController = null;
        }
        PlayerAppManager.removeContext(this);
    }

    @Override
    public void updateThemeToDefault() {
        Log.i(TAG, "updateThemeToDefault()");
        updateThemeCommon();
    }

    @Override
    public void updateThemeToIos() {
        Log.i(TAG, "updateThemeToIos()");
        updateThemeCommon();
    }

    private void updateThemeCommon() {
        ivHSeparate1.setImageResource(getColorResId("video_warning_separate_line"));
        ivVSeparate1.setImageResource(getColorResId("video_warning_separate_line"));
        ivVSeparate2.setImageResource(getColorResId("video_warning_separate_line"));
    }

    public int getColorResId(String imgResName) {
        if (mThemeController != null) {
            return mThemeController.getColorResId(imgResName);
        }
        return 0;
    }
}
