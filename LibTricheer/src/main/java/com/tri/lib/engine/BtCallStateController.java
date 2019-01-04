package com.tri.lib.engine;

import android.bluetooth.BluetoothAdapter;
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
                Log.i(TAG, "onConnectionStateChanged(" + i + ")");
                switch (i) {
                    case BluetoothAdapter.STATE_CONNECTING:
                        break;
                    default:
                        if (i != BluetoothAdapter.STATE_CONNECTED) {
                            onBtCallStateChanged(false);
                        }
                        break;
                }
            }

            @Override
            public void onCallStateChanged(BTCallState btCallState) {
                Log.i(TAG, "onCallStateChanged(" + btCallState + ")");
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
        Log.i(TAG, "onBtCallStateChanged(" + state + ")");
        boolean isBtRunning = (state != -1) && (state != BTCallState.CALL_STATE_TERMINATED);
        onBtCallStateChanged(isBtRunning);
    }

    private static void onBtCallStateChanged(boolean isCalling) {
        PlayEnableController.onBtCallStateChanged(isCalling);
        //Callback
        Log.i(TAG, "onBtCallStateChanged(" + isCalling + ")");
        if (mBtCallStateDelegate != null) {
            mBtCallStateDelegate.onBtCallStateChanged(isCalling);
        }
    }
}
