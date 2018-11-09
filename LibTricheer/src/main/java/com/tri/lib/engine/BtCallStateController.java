package com.tri.lib.engine;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.tricheer.bt.service.phone.BTCallState;
import com.tricheer.bt.service.phone.BTPhone;
import com.tricheer.bt.service.phone.BTPhoneCallback;

import js.lib.android.media.player.PlayEnableController;

/**
 * BT call state controller
 */
public class BtCallStateController {
    //TAG
    private static final String TAG = "BtCallStateController";

    private static BtCallSateDelegate mBtCallStateDelegate;

    public interface BtCallSateDelegate {
        void onBtCallStateChanged(boolean isBtRunning);
    }

    public void register(BtCallSateDelegate t) {
        mBtCallStateDelegate = t;
    }

    public void unregister() {
        mBtCallStateDelegate = null;
    }

    public BtCallStateController(Context context) {
        BTPhone btPhone = new BTPhone(context.getApplicationContext(), new BTPhoneCallback() {
            @Override
            public void onConnectionStateChanged(BluetoothDevice bluetoothDevice, int i) {
            }

            @Override
            public void onCallStateChanged(BTCallState btCallState) {
                Log.i(TAG, "onCallStateChanged(BTCallState)");
                //通话状态发生改变主动回调触发
                int state = btCallState.getState();
                onBtCallStateChanged(state);
            }

            @Override
            public void onCallAudioStateChanged(int i) {
            }

            @Override
            public void onBatteryLevelChanged(int i) {
            }

            @Override
            public void onNetWorkLevelChanged(int i) {
            }
        });
    }

    public static void initBtState(int state) {
        onBtCallStateChanged(state);
    }

    private static void onBtCallStateChanged(int state) {
        // 通话中
        boolean isBtRunning = false;
        if (state != -1 && state != BTCallState.CALL_STATE_TERMINATED) {
            isBtRunning = true;
            PlayEnableController.onBtCallStateChanged(true);
        } else {
            PlayEnableController.onBtCallStateChanged(false);
        }

        //Callback
        Log.i(TAG, "onBtCallStateChanged(" + state + ") :: isBtRunning[" + isBtRunning + "]");
        if (mBtCallStateDelegate != null) {
            mBtCallStateDelegate.onBtCallStateChanged(isBtRunning);
        }
    }
}
