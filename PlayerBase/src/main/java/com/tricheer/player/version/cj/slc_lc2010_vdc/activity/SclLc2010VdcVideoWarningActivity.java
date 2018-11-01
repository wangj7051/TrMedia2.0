package com.tricheer.player.version.cj.slc_lc2010_vdc.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.tri.lib.utils.TrVideoPreferUtils;
import com.tricheer.player.R;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.version.base.activity.BaseFragActivity;

import js.lib.android.utils.CommonUtil;

public class SclLc2010VdcVideoWarningActivity extends BaseFragActivity {
    //TAG
    private static final String TAG = "VideoWarningActivity";

    private View vAgree, vNoToast, vExit;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.scl_lc2010_vdc_activity_video_warning);
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
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //Hide navigation bar
        CommonUtil.setNavigationBar(this, hasFocus ? 0 : 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    private View.OnClickListener mViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == vAgree) {
                TrVideoPreferUtils.getVideoWarningFlag(true, 1);
                finishByOperate("EXIT_WARNING");
            } else if (v == vNoToast) {
                TrVideoPreferUtils.getVideoWarningFlag(true, 2);
                finishByOperate("EXIT_WARNING");
            } else if (v == vExit) {
                exitPlayer();
                finishByOperate("EXIT_PLAYER");
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
        exitPlayer();
    }

    private void exitPlayer() {
        finish();
        PlayerAppManager.exitCurrPlayer();
    }
}
