package com.yj.audio.engine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.util.Log;

import com.yj.audio.receiver.PlayerReceiver;
import com.yj.audio.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioListActivity;
import com.yj.audio.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioPlayerActivity;

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
    private static final String TAG = "AudioAppManager";

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
            if (context instanceof SclLc2010VdcAudioPlayerActivity) {
                return (PlayerReceiver.PlayerReceiverListener) context;
            }
        }
        return null;
    }

    /**
     * 获取当前播放器对象
     *
     * @return {@link PlayerReceiver.PlayerReceiverListener}
     */
    public static SclLc2010VdcAudioListActivity getAudioListActivity() {
        for (Context context : mSetContexts) {
            if (context instanceof SclLc2010VdcAudioListActivity) {
                return (SclLc2010VdcAudioListActivity) context;
            }
        }
        return null;
    }

    /**
     * Exit Current Player
     */
    public static void exitCurrPlayer(boolean isForceDestroy) {
        Logs.i(TAG, "exitCurrPlayer(" + isForceDestroy + ")");
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