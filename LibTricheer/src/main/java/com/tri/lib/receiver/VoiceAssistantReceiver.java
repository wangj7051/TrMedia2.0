package com.tri.lib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Voice assistant helper
 *
 * @author Jun.Wang
 */
public class VoiceAssistantReceiver extends BroadcastReceiver {
    //TAG
    private static final String TAG = "VoiceAssistantReceiver";

    private static Set<VoiceAssistantDelegate> mSetDelegates = new LinkedHashSet<>();

    public interface VoiceAssistantDelegate {
        void onVoiceCommand(ActionEnum ae);
    }

    public static void register(VoiceAssistantDelegate t) {
        if (t != null) {
            mSetDelegates.add(t);
        }
    }

    public static void unregister(AccReceiver.AccDelegate t) {
        if (t != null) {
            mSetDelegates.remove(t);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action : " + action);
        ActionEnum ae = ActionEnum.getByAction(action);
        Log.i(TAG, "action-ae : " + ae);
        notifyVoiceCommand(ae);
    }

    void notifyVoiceCommand(ActionEnum ae) {
        for (VoiceAssistantDelegate delegate : mSetDelegates) {
            if (delegate != null) {
                delegate.onVoiceCommand(ae);
            }
        }
    }
}
