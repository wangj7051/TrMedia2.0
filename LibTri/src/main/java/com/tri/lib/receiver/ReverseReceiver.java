package com.tri.lib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Car reverse Logic Receiver
 *
 * @author Jun.Wang
 */
public class ReverseReceiver extends BroadcastReceiver {
    //TAG
    private static final String TAG = "ReverseReceiver";

    private static Set<ReverseDelegate> mSetDelegates = new HashSet<>();

    public interface ReverseDelegate {
        void onReverseOff();

        void onReverseOn();
    }

    public static void register(ReverseDelegate t) {
        if (t != null) {
            mSetDelegates.add(t);
        }
    }

    public static void unregister(ReverseDelegate t) {
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
                case REVERSE_ON:
                    notifyOn();
                    break;
                case REVERSE_OFF:
                    notifyOff();
                    break;
            }
        }
    }

    void notifyOn() {
        for (ReverseDelegate delegate : mSetDelegates) {
            if (delegate != null) {
                delegate.onReverseOn();
            }
        }
    }

    void notifyOff() {
        for (ReverseDelegate delegate : mSetDelegates) {
            if (delegate != null) {
                delegate.onReverseOff();
            }
        }
    }
}
