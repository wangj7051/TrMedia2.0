package com.tricheer.player.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Media scan service.
 * <p>This service should start when the system boot completed.</p>
 *
 * @author Jun.Wang
 */
public class MediaScanService extends Service {
    //TAG
    private static final String TAG = "MediaScanService";

    /**
     * Start list all medias action
     */
    public static final String ACTION_START_LIST = "com.tricheer.player.START.LIST.ALL_MEDIAS";

    /**
     * 挂载的路径 / 未挂载的路径
     */
    private Set<String> mSetPathsMounted = new HashSet<>(), mSetPathsUnMounted = new HashSet<>();

    //
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        Log.i(TAG, "init()");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
