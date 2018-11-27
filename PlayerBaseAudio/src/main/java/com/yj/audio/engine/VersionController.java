package com.yj.audio.engine;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yj.audio.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioListActivity;
import com.yj.audio.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioPlayerActivity;

/**
 * Player Versions Controller
 *
 * @author Jun.Wang
 */
public class VersionController {
    // TAG
    private static final String TAG = "VersionController";

    /**
     * 播放器打开验证标记
     * <p>
     * 1 表示如果已经打开了播放器，则打开已有的播放器 ; 否则打开媒体列表页面
     * <p>
     * 0 表示直接打开播放器
     */
    private static int mMusicPlayerOpenConfirm = 0;

    /**
     * 是否 后视镜/车机版本
     */
    private static int mFlagCjVersion = 1;
    /**
     * 是否使用本地音乐播放器
     */
    private static boolean mFlag$IsUseLocalMusicPlayer = false;
    /**
     * 是否支持列出所支持的全部格式
     */
    private static boolean mFlag$IsSupportListAllMedias = false;

    /**
     * 音频 - 列表/播放器
     */
    private static Class<?> mClsMusicList, mClsMusicPlayer;

    /**
     * Player Customized Version
     */
    private static final int version = PlayerVersions.SLC_LC2010_VDC;

    private interface PlayerVersions {
        // >>>----CheJi----<<<
        // >>>----CheJi2.0----<<<
        // WJ HAR_LC3110_BAS-[8-inch]
        int HAR_LC3110_BAS = 201;
        int SLC_LC2010_VDC = 301;
    }

    /**
     * 初始化版本控制信息
     */
    public static void init() {
        switch (version) {
            // ----车机2----
            case PlayerVersions.HAR_LC3110_BAS:
                //Player open confirm
                mMusicPlayerOpenConfirm = 1;
                mFlagCjVersion = 2;
                mFlag$IsUseLocalMusicPlayer = true;
                mFlag$IsSupportListAllMedias = true;
                // Class
                mClsMusicList = SclLc2010VdcAudioListActivity.class;
                mClsMusicPlayer = SclLc2010VdcAudioPlayerActivity.class;
                break;
            // ----车机2----
            case PlayerVersions.SLC_LC2010_VDC:
                //Player open confirm
                mMusicPlayerOpenConfirm = 1;
                mFlagCjVersion = 3;
                mFlag$IsUseLocalMusicPlayer = true;
                mFlag$IsSupportListAllMedias = true;
                // Class
                mClsMusicList = SclLc2010VdcAudioListActivity.class;
                mClsMusicPlayer = SclLc2010VdcAudioPlayerActivity.class;
                break;
        }
    }

    /**
     * New Music Player Intent
     */
    public static Intent newMusicPlayerIntent(Context cxt) {
        Intent targetIntent = null;
        if (mMusicPlayerOpenConfirm == 1 && PlayerAppManager.getCurrPlayer() == null) {
            if (mClsMusicList != null) {
                Log.i(TAG, "newMusicPlayerIntent(cxt)-> mClsMusicList:" + mClsMusicList);
                targetIntent = new Intent(cxt, mClsMusicList);
            }
        } else if (mClsMusicPlayer != null) {
            Log.i(TAG, "newMusicPlayerIntent(cxt)-> mClsMusicPlayer:" + mClsMusicPlayer);
            targetIntent = new Intent(cxt, mClsMusicPlayer);
        }
        return targetIntent;
    }

    /**
     * 是否是车机版本
     */
    public static boolean isCjVersion() {
        return false;
    }

    /**
     * 是否是车机2版本
     */
    public static int getCjVersion() {
        return mFlagCjVersion;
    }

    /**
     * 是否使用本地音乐播放器
     */
    public static boolean isUseLocalMusicPlayer() {
        return mFlag$IsUseLocalMusicPlayer;
    }

    /**
     * 是否支持列出所支持的全部格式
     * <p>
     * 音乐全部格式 : {".aac" , ".m4a" , ".mid" , ".mp3" , ".wav" , ".flac"}
     * <p>
     * 视频全部格式 : {".3gp" , ".avi" , ".flv" , ".mp4" , ".mkv" , ".ts"}
     */
    public static boolean isSupportAllMedias() {
        return mFlag$IsSupportListAllMedias;
    }
}
