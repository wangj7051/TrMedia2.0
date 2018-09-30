package com.tricheer.radio.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tricheer.radio.R;

/**
 * Toast View
 */
public class ToastView {

    public static void show(Context context, @StringRes int msgId) {
        show(context, context.getString(msgId));
    }

    public static void show(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams")
        View contentV = inflater.inflate(R.layout.v_toast_util, null, false);
        TextView tvMsg = (TextView) contentV.findViewById(R.id.tv_msg);
        Log.i("ToastView", "tvMsg-> " + tvMsg);

        if (tvMsg != null && !TextUtils.isEmpty(msg)) {
            tvMsg.setText(msg);

            //Show
            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(contentV);
            toast.show();
        }
    }
}
