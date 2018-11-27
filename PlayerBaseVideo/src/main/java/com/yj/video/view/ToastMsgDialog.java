package com.yj.video.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.yj.video.R;

public class ToastMsgDialog extends AlertDialog {

    // ---- Widgets ----
    private TextView tvMsg;

    // ---- Variables ----
    private String mStrMsg = "";

    public ToastMsgDialog(Context context) {
        super(context);
    }

    protected ToastMsgDialog(Context context, int theme) {
        super(context, theme);
    }

    protected ToastMsgDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_dialog_toast);

        tvMsg = (TextView) findViewById(R.id.tv_msg);
        tvMsg.setText(mStrMsg);
    }

    public void setMsg(String msg) {
        mStrMsg = msg;
        if (tvMsg != null) {
            tvMsg.setText(msg);
        }
    }
}
