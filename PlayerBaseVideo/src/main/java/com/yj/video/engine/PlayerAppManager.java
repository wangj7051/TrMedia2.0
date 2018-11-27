package com.yj.video.engine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;

import com.yj.video.App;
import com.yj.video.receiver.PlayerReceiver.PlayerReceiverListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * Player Application Manager
 *
 * @author Jun.Wang
 */
public class PlayerAppManager {
    // TAG
    private static final String TAG = "PlayerAppManager";

    public interface PlayerCxtFlag {
        int NONE = -1;
        int MUSIC_PLAYER = 101;
        int MUSIC_LIST = 102;
        int VIDEO_PLAYER = 201;
        int VIDEO_LIST = 202;
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
        // 播放器类型判断
        switch (cxtFlag) {
            case PlayerCxtFlag.MUSIC_PLAYER:
            case PlayerCxtFlag.MUSIC_LIST:
                if (!PlayerType.isMusic()) {
                    clearPlayer();
                    PlayerType.setType(PlayerType.MUSIC);
                }
                break;
            case PlayerCxtFlag.VIDEO_PLAYER:
            case PlayerCxtFlag.VIDEO_LIST:
                if (!PlayerType.isVideo()) {
                    clearPlayer();
                    PlayerType.setType(PlayerType.VIDEO);
                }
                break;
        }

        // 存储当前对象
        mMapCxts.put(cxtFlag, actObj);
    }

    /**
     * 清空播放器
     */
    private static void clearPlayer() {
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

    /**
     * Remove Context[Activity or Service] Object
     */
    public static void removeCxt(int cxtFlag) {
        mMapCxts.remove(cxtFlag);
        if (EmptyUtil.isEmpty(mMapCxts)) {
            PlayerType.setType(-1);
        }
    }

    /**
     * Get Context Object
     */
    public static Context getCxt(int cxtFlag) {
        return mMapCxts.get(cxtFlag);
    }

    /**
     * 获取当前播放器标记
     *
     * @return {@link PlayerCxtFlag}
     */
    public static int getCurrPlayerFlag() {
        for (Integer flag : mMapCxts.keySet()) {
            switch (flag) {
                case PlayerCxtFlag.MUSIC_PLAYER:
                case PlayerCxtFlag.VIDEO_PLAYER:
                    return flag;
            }
        }
        return PlayerCxtFlag.NONE;
    }

    /**
     * 获取当前播放器对象
     *
     * @return {@link PlayerReceiverListener}
     */
    public static PlayerReceiverListener getCurrPlayer() {
        Context currPlayer = mMapCxts.get(getCurrPlayerFlag());
        if (currPlayer instanceof PlayerReceiverListener) {
            return (PlayerReceiverListener) currPlayer;
        }
        return null;
    }

    /**
     * Start Player By Player Flag
     */
    public static void startPlayer(Context cxt, int cxtFlag) {
        switch (cxtFlag) {
            case PlayerCxtFlag.MUSIC_PLAYER:
                App.openMusicPlayer(cxt, "", null);
                break;
            case PlayerCxtFlag.VIDEO_PLAYER:
                App.openVideoPlayer(cxt, "", null);
                break;
        }
    }

    /**
     * Close Music Player
     */
    public static void closeMusicPlayer() {
        Logs.i(TAG, "^^ closeMusicPlayer() ^^");
        if (PlayerType.isMusic()) {
            clearPlayer();
        }
    }

    /**
     * Close Music Player
     */
    public static void closeVideoPlayer() {
        Logs.i(TAG, "^^ closeVideoPlayer() ^^");
        if (PlayerType.isVideo()) {
            clearPlayer();
        }
    }

    /**
     * Exit Current Player
     */
    public static void exitCurrPlayer() {
        Logs.i(TAG, "exitCurrPlayer() -> [Player.type():" + PlayerType.type());
        clearPlayer();
    }
}