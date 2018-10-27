package com.tri.lib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import js.lib.android.media.player.PlayEnableController;

/**
 * Screen state receiver
 *
 * @author Jun.Wang
 */
public class ScreenStateReceiver extends BroadcastReceiver {
    //TAG
    private static final String TAG = "ScreenStateReceiver";

    private static Set<ScreenSateDelegate> mSetDelegates = new HashSet<>();

    public interface ScreenSateDelegate {
        void onScreenOff();

        void onScreenOn();
    }

    public static void register(ScreenSateDelegate t) {
        if (t != null) {
            mSetDelegates.add(t);
        }
    }

    public static void unregister(ScreenSateDelegate t) {
        if (t != null) {
            mSetDelegates.remove(t);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action : " + action);

        ActionEnum ae = ActionEnum.getByAction(action);
        if (ae != null) {
            switch (ae) {
                case SCREEN_ON:
                    notifyOn();
                    break;
                case SCREEN_OFF:
                    notifyOff();
                    break;
            }
        }
    }

    void notifyOn() {
        PlayEnableController.onScreenStateChanged(false);
        for (ScreenSateDelegate delegate : mSetDelegates) {
            if (delegate != null) {
                delegate.onScreenOn();
            }
        }
    }

    void notifyOff() {
        PlayEnableController.onScreenStateChanged(true);
        for (ScreenSateDelegate delegate : mSetDelegates) {
            if (delegate != null) {
                delegate.onScreenOff();
            }
        }
    }
}
