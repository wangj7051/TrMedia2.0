package com.tricheer.player.version.base.activity.music;

import android.os.Bundle;

import com.tricheer.player.service.MusicPlayService;

public abstract class BaseAudioFocusActivity extends BaseKeyEventActivity {

    private MusicPlayService mService;

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
    protected void onPlayServiceConnected(MusicPlayService service) {
        super.onPlayServiceConnected(service);
        if (service != null) {
            mService = service;
        }
    }

    @Override
    public void onAudioFocusGain() {
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
        super.onDestroy();
    }
}
