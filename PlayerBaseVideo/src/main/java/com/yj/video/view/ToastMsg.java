package com.yj.video.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tri.lib.utils.SettingsSysUtil;
import com.yj.video.R;

public final class ToastMsg {
    public static void show(Context cxt, String msg) {
        try {
            //
            Context appCxt = cxt.getApplicationContext();

            //
            @SuppressLint("InflateParams")
            View rootV = LayoutInflater.from(appCxt).inflate(R.layout.v_dialog_toast, null);
            TextView tvMsg = (TextView) rootV.findViewById(R.id.tv_msg);
            tvMsg.setText(msg);
            switchStyle(appCxt, tvMsg);

            //
            Toast toast = new Toast(appCxt);
            toast.setView(rootV);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void switchStyle(Context cxt, TextView tvMsg) {
        if (tvMsg != null) {
            int themeVal = SettingsSysUtil.getThemeVal(cxt);
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
