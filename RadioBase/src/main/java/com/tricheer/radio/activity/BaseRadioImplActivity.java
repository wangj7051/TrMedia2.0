package com.tricheer.radio.activity;

import android.app.Service;
import android.content.Intent;

import com.tri.lib.radio.activity.BaseRadioLogicActivity;
import com.tricheer.radio.service.RadioPlayerService;

/**
 * Radio logic implement activity
 *
 * @author Jun.Wang
 */
public abstract class BaseRadioImplActivity extends BaseRadioLogicActivity {
    @Override
    protected void onServiceStatusChanged(Service service, boolean isConnected) {
    }

    /**
     * Bind and create control service
     *
     * @param flags 1 start; 2 bind; 3 unbind; 4 stop
     */
    protected void bindAndCreateControlService(int... flags) {
        try {
            for (int flag : flags) {
                Intent serviceIntent = new Intent(this, RadioPlayerService.class);
                switch (flag) {
                    case 1:
                        startService(serviceIntent);
                        break;
                    case 2:
                        bindService(serviceIntent, mControlServiceConn, BIND_AUTO_CREATE);
                        break;
                    case 3:
                        if (mControlService != null) {
                            unbindService(mControlServiceConn);
                        }
                        break;
                    case 4:
                        if (mControlService != null) {
                            stopService(serviceIntent);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
