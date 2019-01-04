package com.yj.audio.engine;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

import com.yj.audio.R;
import com.yj.audio.view.ToastMsgDialog;

public class ToastMsgController {
    //TAG
    private static final String TAG = "ToastMsgController";

    private Activity mActivity;

    private ToastMsgDialog mToastMsgDialog;
    private static Handler mAutoDismissHandler = new Handler();

    public ToastMsgController(Activity activity) {
        mActivity = activity;
    }

    public void onHomeKeyClick() {
        Log.i(TAG, "onHomeKeyClick()");
        mAutoDismissHandler.removeCallbacksAndMessages(null);
        dismissToast();
    }

    public void finish() {
        mAutoDismissHandler.removeCallbacksAndMessages(null);
        dismissToast();
    }

    public void onDestroy() {
        mAutoDismissHandler.removeCallbacksAndMessages(null);
        dismissToast();
    }

    public void toastMsg() {
        showToast();
        mAutoDismissHandler.removeCallbacksAndMessages(null);
        mAutoDismissHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "toastMsg() -Timeout-");
                dismissToast();
            }
        }, 3000);
    }

    private void showToast() {
        Log.i(TAG, "showToast()");
        if (mToastMsgDialog == null) {
            mToastMsgDialog = new ToastMsgDialog(mActivity);
            mToastMsgDialog.setCancelable(false);
            mToastMsgDialog.setCanceledOnTouchOutside(true);
            mToastMsgDialog.setMsg(mActivity.getString(R.string.toast_usb_not_exist));
            mToastMsgDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Log.i(TAG, "showToast() -onDismiss-");
                    mAutoDismissHandler.removeCallbacksAndMessages(null);
                    if (mActivity != null && !mActivity.isFinishing() && !mActivity.isDestroyed()) {
                        Log.i(TAG, "showToast() -Finish Activity-");
                        mActivity.finish();
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
            Log.i(TAG, "dismissToast()");
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
}
