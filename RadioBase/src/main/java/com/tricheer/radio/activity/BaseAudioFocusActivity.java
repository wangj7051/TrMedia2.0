package com.tricheer.radio.activity;

import android.app.Service;
import android.os.Bundle;

import com.tricheer.radio.service.BaseAudioFocusService;
import com.tricheer.radio.service.RadioPlayerService;

public abstract class BaseAudioFocusActivity extends BaseKeyEventActivity implements BaseAudioFocusService.AudioFocusListener {

    private RadioPlayerService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mService != null) {
            int result = mService.registerAudioFocus(1);
            switch (result) {
                case 1:
                    mService.onAudioFocusGain();
                    break;
            }
        }
    }

    @Override
    protected void onServiceStatusChanged(Service service, boolean isConnected) {
        if (service != null) {
            mService = (RadioPlayerService) service;
            mService.setAudioFocusListener(this);
        }
    }

    @Override
    public void onAudioFocusGain() {
        if (!isRadioOpened()) {
            execOpenRadio();
        }
    }

    @Override
    public void onAudioFocusTransient() {
    }

    @Override
    public void onAudioFocusDuck() {
    }

    @Override
    public void onAudioFocusLoss() {
    }

    @Override
    protected void onDestroy() {
        if (mService != null) {
            mService.removeAudioFocusListener(this);
            mService = null;
        }
        super.onDestroy();
    }
}
