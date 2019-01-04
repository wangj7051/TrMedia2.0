package com.yj.audio.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.tri.lib.utils.SettingsSysUtil;
import com.yj.audio.R;

public class ToastMsgDialog extends Dialog {

    // ---- Widgets ----
    private TextView tvMsg;

    // ---- Variables ----
    private String mStrMsg = "";

    public ToastMsgDialog(Context context) {
        super(context, R.style.DIALOG_NEW);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_dialog_toast);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        tvMsg.setText(mStrMsg);
        switchStyle();
    }

    public void setMsg(String msg) {
        mStrMsg = msg;
        if (tvMsg != null) {
            tvMsg.setText(msg);
        }
    }

    private void switchStyle() {
        if (tvMsg != null) {
            Context context = getContext();
            int themeVal = SettingsSysUtil.getThemeVal(context);
            switch (themeVal) {
                case 1:
                    tvMsg.setBackgroundResource(R.drawable.ios_bg_corners_toast_util);
                    break;
                case 0:
                default:
                    tvMsg.setBackgroundResource(R.drawable.bg_corners_toast_util);
                    break;
            }
        }
    }
}
