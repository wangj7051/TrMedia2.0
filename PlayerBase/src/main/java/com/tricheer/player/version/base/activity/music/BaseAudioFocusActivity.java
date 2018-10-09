package com.tricheer.player.version.base.activity.music;

import android.app.Service;
import android.os.Bundle;

import com.tricheer.player.service.TrPlayService;

public abstract class BaseAudioFocusActivity extends BaseAudioKeyEventActivity {

    private Service mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mService instanceof TrPlayService) {
            ((TrPlayService) mService).resumeByUser();
        }
    }

    @Override
    protected void onPlayServiceConnected(Service service) {
        super.onPlayServiceConnected(service);
        if (service != null) {
            mService = service;
        }
    }
}
