package com.lib.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.tricheer.player.R;

import js.lib.android.utils.Logs;

/**
 * Request Dialog
 *
 * @author Jun.Wang
 */
public class RequestProcessDialog extends AlertDialog {
    // TAG
    private static final String TAG = "RequestProcessDialog -> ";

    //==========Widget in this View==========
    /**
     * Progress Message
     */
    private TextView tvMsgInfo;

    //==========Variable in this View==========
    /**
     * Context
     */
    private Context mContext;

    /**
     * Thread Handler
     */
    private Handler mHandler = new Handler();

    /**
     * Message Text Color
     */
    private int mMsgColor = -1;
    /**
     * Message Text
     */
    private String mMsg = "";

    /**
     * Dialog Opened Index
     */
    private final int M_TIMEOUT_PERIOD = 60 * 1000;

    /**
     * @param msgColor : if == -1,means will use default text color
     */
    public RequestProcessDialog(Context context, int msgColor) {
        super(context);
        this.mContext = context;
        this.mMsgColor = msgColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_v_request_progress);

        //
        tvMsgInfo = (TextView) findViewById(R.id.tv_v34_info);
        tvMsgInfo.setTextColor(mMsgColor);
        tvMsgInfo.setText(mMsg);
    }

    public void setMsgInfo(int resID) {
        setMsgInfo(mContext.getString(resID));
    }

    public void setMsgInfo(String msg) {
        mMsg = msg;
        if (tvMsgInfo != null) {
            tvMsgInfo.setText(mMsg);
        }
    }

    @Override
    public void show() {
        super.show();
        updateTimeout();
    }

    /**
     * Update Timeout Runnable
     */
    public void updateTimeout() {
        mHandler.removeCallbacks(mTimeoutRunnable);
        mHandler.postDelayed(mTimeoutRunnable, M_TIMEOUT_PERIOD);
    }

    private Runnable mTimeoutRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                if (isShowing()) {
                    dismiss();
                }
            } catch (Throwable e) {
                Logs.printStackTrace(TAG + "mTimeoutRunnable()", e);
            }
        }
    };

    @Override
    public void dismiss() {
        try {
            mHandler.removeCallbacksAndMessages(null);
            super.dismiss();
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "dismiss()", e);
        }
    }
}
