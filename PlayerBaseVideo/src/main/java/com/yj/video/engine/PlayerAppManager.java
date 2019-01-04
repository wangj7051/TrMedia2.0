package com.yj.video.engine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.util.Log;

import com.yj.video.receiver.PlayerReceiver;
import com.yj.video.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoPlayerActivity;

import java.util.LinkedHashSet;
import java.util.Set;

import js.lib.android.utils.Logs;

/**
 * Player Application Manager
 *
 * @author Jun.Wang
 */
public class PlayerAppManager {
    // TAG
    private static final String TAG = "VideoAppManager";

    /**
     * Player Objects
     */
    @SuppressLint("UseSparseArrays")
    private static Set<Context> mSetContexts = new LinkedHashSet<>();

    /**
     * Cache Context[Activity or Service] Object
     */
    public static void addContext(Context actObj) {
        if (actObj != null) {
            mSetContexts.add(actObj);
        }
    }

    /**
     * Remove Context[Activity or Service] Object
     */
    public static void removeContext(Context actObj) {
        if (actObj != null) {
            mSetContexts.remove(actObj);
        }
    }

    /**
     * 获取当前播放器对象
     *
     * @return {@link PlayerReceiver.PlayerReceiverListener}
     */
    public static PlayerReceiver.PlayerReceiverListener getCurrPlayer() {
        for (Context context : mSetContexts) {
            if (context instanceof SclLc2010VdcVideoPlayerActivity) {
                return (PlayerReceiver.PlayerReceiverListener) context;
            }
        }
        return null;
    }

    /**
     * Exit Current Player
     */
    public static void exitCurrPlayer() {
        Logs.i(TAG, "exitCurrPlayer()");
        clearPlayer();
    }

    /**
     * 清空播放器
     */
    private static void clearPlayer() {
        try {
            Log.i(TAG, "clearPlayer() >>>>>>>>>>>>>>>>>>>>> mSetContexts.size():" + mSetContexts.size());
            Object[] objArr = mSetContexts.toArray();
            for (Object obj : objArr) {
                Context context = (Context) obj;
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    Log.i(TAG, "clear - " + activity);
                    activity.finish();
                } else if (context instanceof Service) {
                    Service service = (Service) context;
                    Log.i(TAG, "clear - " + context);
                    service.stopSelf();
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "clearPlayer() :: ERROR - " + e.getMessage());
            e.printStackTrace();
        }
    }
}