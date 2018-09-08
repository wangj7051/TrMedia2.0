package com.tricheer.player.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.tricheer.player.R;

public class ToastUsb {
    public static void show(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams")
        View contentV = inflater.inflate(R.layout.toast_usb, null, false);

        //Show
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(contentV);
        toast.show();
    }
}
