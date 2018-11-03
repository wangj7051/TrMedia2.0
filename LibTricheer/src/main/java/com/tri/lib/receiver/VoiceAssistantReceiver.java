package com.tri.lib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class VoiceAssistantReceiver extends BroadcastReceiver {
    //TAG
    private static final String TAG = "VoiceAssistantReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action :: " + action);

        ActionEnum ae = ActionEnum.getByAction(action);
        if (ae != null) {
            switch (ae) {
                case MEDIA_EXIT_AUDIO:
                    break;
                case MEDIA_EXIT_VIDEO:
                    break;
                case MEDIA_PLAY_PREV:
                    break;
                case MEDIA_PLAY_NEXT:
                    break;
                case MEDIA_PLAY:
                    break;
                case MEDIA_PAUSE:
                    break;
                case MEDIA_RADIO_SET_FREQ:
                    break;
            }
        }
    }
}
