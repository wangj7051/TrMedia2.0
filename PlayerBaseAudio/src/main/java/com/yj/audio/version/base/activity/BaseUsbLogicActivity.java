package com.yj.audio.version.base.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yj.audio.engine.ToastMsgController;

/**
 * Usb logic process
 */
public abstract class BaseUsbLogicActivity extends BaseFragActivity {
    //TAG
    private static final String TAG = "BaseUsbLogicActivity";

    private ToastMsgController mToastMsgController;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        mToastMsgController = new ToastMsgController(this);
    }

    @Override
    protected void onHomeKeyClick() {
        super.onHomeKeyClick();
        if (mToastMsgController != null) {
            mToastMsgController.onHomeKeyClick();
        }
    }

    @Override
    public void finish() {
        Log.i(TAG, "finish()");
        if (mToastMsgController != null) {
            mToastMsgController.finish();
        }
        super.finish();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy()");
        if (mToastMsgController != null) {
            mToastMsgController.onDestroy();
        }
        super.onDestroy();
    }

    protected void toastMsg() {
        Log.i(TAG, "toastMsg()");
        if (mToastMsgController != null) {
            mToastMsgController.toastMsg();
        }
    }
}
