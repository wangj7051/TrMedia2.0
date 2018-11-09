package com.tricheer.radio.service;

import android.content.Intent;
import android.os.IBinder;

import com.yj.lib.radio.service.BaseRadioService;

/**
 * Radio operate service
 * <p>
 * 1. Control Radio Play
 * </p>
 *
 * @author Jun.Wang
 */
public class RadioPlayerService extends BaseRadioService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return new LocalBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
