package com.tricheer.player.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import js.lib.android.media.audio.MusicPlayerFactory;
import js.lib.android.media.audio.service.AudioPlayService;

public class TrPlayService extends AudioPlayService {
    // TAG
    private final String TAG = "TrPlayService";

    /**
     * {@link LocalBinder} Object
     */
    private LocalBinder mLocalBinder;

    public class LocalBinder extends Binder {
        public TrPlayService getService() {
            return TrPlayService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MusicPlayerFactory.instance().init(MusicPlayerFactory.PlayerType.VLC_PLAYER);
        Log.i(TAG, "onCreate()");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        if (mLocalBinder == null) {
            mLocalBinder = new LocalBinder();
        }
        return getLocalBinder();
    }

    private LocalBinder getLocalBinder() {
        if (mLocalBinder == null) {
            mLocalBinder = new LocalBinder();
        }
        return mLocalBinder;
    }
}
