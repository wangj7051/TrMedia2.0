package com.tricheer.player.version.base.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.tricheer.player.R;
import com.tricheer.player.view.ToastView;

public abstract class BaseUsbLogicActivity extends BaseFragActivity {
    private Context mContext;
    private static Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        mContext = getApplicationContext();
    }

    protected void toastMsg() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ToastView.show(mContext, R.string.toast_usb_not_exist);
            }
        }, 300);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3300);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
