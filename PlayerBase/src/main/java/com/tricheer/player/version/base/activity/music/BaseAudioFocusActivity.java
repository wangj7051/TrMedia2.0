package com.tricheer.player.version.base.activity.music;

import android.os.Bundle;

import com.tricheer.player.service.MusicPlayService;

public abstract class BaseAudioFocusActivity extends BaseAudioKeyEventActivity {

    private MusicPlayService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mService != null) {
            mService.resumeByUser();
        }
    }

    @Override
    protected void onPlayServiceConnected(MusicPlayService service) {
        super.onPlayServiceConnected(service);
        if (service != null) {
            mService = service;
        }
    }
}
