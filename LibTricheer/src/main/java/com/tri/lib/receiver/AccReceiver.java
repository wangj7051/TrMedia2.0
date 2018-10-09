package com.tri.lib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tri.lib.utils.TrVideoPreferUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * ACC Logic Receiver
 *
 * @author Jun.Wang
 */
public class AccReceiver extends BroadcastReceiver {
    //TAG
    private static final String TAG = "AccOffReceiver";

    private static Set<AccDelegate> mSetDelegates = new HashSet<>();

    public interface AccDelegate {
        void onAccOff();

        void onAccOffTrue();

        void onAccOn();
    }

    public static void register(AccDelegate t) {
        if (t != null) {
            mSetDelegates.add(t);
        }
    }

    public static void unregister(AccDelegate t) {
        if (t != null) {
            mSetDelegates.remove(t);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action : " + action);

        ActionEnum ae = ActionEnum.getByAction(action);
        switch (ae) {
            case ACC_ON:
                notifyOn();
                break;
            case ACC_OFF:
                notifyOff();
                break;
            case ACC_OFF_TRUE:
                notifyTrue();
                break;
        }
    }

    void notifyOn() {
        for (AccDelegate delegate : mSetDelegates) {
            delegate.onAccOn();
        }
    }

    void notifyOff() {
        for (AccDelegate delegate : mSetDelegates) {
            delegate.onAccOff();
        }
    }

    void notifyTrue() {
        TrVideoPreferUtils.getVideoWarningFlag(true, 1);
        for (AccDelegate delegate : mSetDelegates) {
            delegate.onAccOffTrue();
        }
    }
}
