package com.tricheer.audio.engine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Player Application Manager
 *
 * @author Jun.Wang
 */
public class AudioAppManager {
    // TAG
    private static final String TAG = "PlayerAppManager";

    public interface PlayerCxtFlag {
        int NONE = -1;
        int AUDIO_PLAYER = 101;
        int AUDIO_LIST = 102;
    }

    /**
     * Player Objects
     */
    @SuppressLint("UseSparseArrays")
    private static Map<Integer, Context> mMapCxts = new HashMap<>();

    /**
     * Cache Context[Activity or Service] Object
     */
    public static void putCxt(int cxtFlag, Context actObj) {
        mMapCxts.put(cxtFlag, actObj);
    }

    /**
     * Remove Context[Activity or Service] Object
     */
    public static void removeCxt(int cxtFlag) {
        mMapCxts.remove(cxtFlag);
    }

    /**
     * Get Context Object
     */
    public static Context getCxt(int cxtFlag) {
        return mMapCxts.get(cxtFlag);
    }

    /**
     * Exit Current Player
     */
    public static void exitCurrPlayer() {
        Iterator<Map.Entry<Integer, Context>> iterator = mMapCxts.entrySet().iterator();
        for (; iterator.hasNext(); ) {
            Map.Entry<Integer, Context> entry = iterator.next();
            if (entry != null) {
                // Close Context
                Context cxt = entry.getValue();
                if (cxt instanceof Activity) {
                    ((Activity) cxt).finish();
                } else if (cxt instanceof Service) {
                    ((Service) cxt).stopSelf();
                }
                // Remove Object
                iterator.remove();
            }
        }
    }
}