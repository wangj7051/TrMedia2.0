package com.tricheer.player.version.base.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.tricheer.player.R;
import com.tricheer.player.view.ToastMsgDialog;

public abstract class BaseUsbLogicActivity extends BaseFragActivity {
    private Context mContext;
    private static Handler mHandler = new Handler();


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
        }
        if (!mToastMsgDialog.isShowing()) {
            mToastMsgDialog.show();
        }
    }

    private void dismissToast() {
        try {
            if (mToastMsgDialog != null) {
                if (mToastMsgDialog.isShowing()) {
                    mToastMsgDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
