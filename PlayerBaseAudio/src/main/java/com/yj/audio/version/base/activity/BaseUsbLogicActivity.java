package com.yj.audio.version.base.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yj.audio.R;
import com.yj.audio.view.ToastMsgDialog;

/**
 * Usb logic process
 */
public abstract class BaseUsbLogicActivity extends BaseFragActivity {
    //TAG
    private static final String TAG = "BaseUsbLogicActivity";

    private static Handler mHandler = new Handler();
    private boolean mIsActivityFinished = false;
    private ToastMsgDialog mToastMsgDialog;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        mContext = getApplicationContext();
    }

    protected void toastMsg() {
        showToast();
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "toastMsg() -Timeout-");
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        dismissToast();
        super.onDestroy();
    }

    private void showToast() {
        if (mToastMsgDialog == null) {
            mToastMsgDialog = new ToastMsgDialog(this);
            mToastMsgDialog.setCancelable(false);
            mToastMsgDialog.setCanceledOnTouchOutside(true);
            mToastMsgDialog.setMsg(getString(R.string.toast_usb_not_exist));
            mToastMsgDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Log.i(TAG, "showToast() -onDismiss-");
                    mHandler.removeCallbacksAndMessages(null);
                    if (!mIsActivityFinished) {
                        Log.i(TAG, "showToast() -FINISH on dismiss-");
                        finish();
                    }
                }
            });
        }
        if (!mToastMsgDialog.isShowing()) {
            mToastMsgDialog.show();
        }
    }

    private void dismissToast() {
        try {
            Log.i(TAG, "dismissToast() -START-");
            if (mToastMsgDialog != null) {
                if (mToastMsgDialog.isShowing()) {
                    Log.i(TAG, "dismissToast() -EXEC-");
                    mToastMsgDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHomeKeyClick() {
        super.onHomeKeyClick();
        Log.i(TAG, "onHomeKeyClick()");
        finish();
    }

    @Override
    public void finish() {
        Log.i(TAG, "finish()");
        mIsActivityFinished = true;
        super.finish();
    }
}
