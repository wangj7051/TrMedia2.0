package com.tricheer.player.engine;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioListActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioPlayerActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoListActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoPlayerActivity;

import js.lib.android.utils.Logs;

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
    private static int mMusicPlayerOpenConfirm = 0, mVideoPlayerOpenConfirm = 0;

    /**
     * 是否 后视镜/车机版本
     */
    private static boolean mFlag$IsCj2Version = false;
    /**
     * 是否使用本地音乐播放器
     */
    private static boolean mFlag$IsUseLocalMusicPlayer = false;
    /**
     * 是否支持视频混音标记
     */
    private static boolean mFlag$IsSupportVideoMix = false;
    /**
     * 是否支持视频关灯模式
     */
    private static boolean mFlag$IsSupportVideoTurnOffLight = false;
    /**
     * 是否支持列出所支持的全部格式
     */
    private static boolean mFlag$IsSupportListAllMedias = false;
    /**
     * 是否支持在Boot后自动恢复播放
     */
    private static boolean mFlag$IsAutoResumeAfterBoot = false;
    /**
     * 是否支持仪表盘信息
     */
    private static boolean mFlag$IsSupportDashboard = false;
    /**
     * 是否支持"顺序模式(即播放完成一次列表媒体后停止)"
     */
    private static boolean mFlag$IsSupportOrderPlayMode = false;
    /**
     * 是否处理视频分辨率
     */
    private static boolean mFlag$IsProcessVideoResolution = false;

    /**
     * 音频 - 列表/播放器
     */
    private static Class<?> mClsMusicList, mClsMusicPlayer;
    /**
     * 视频 - 列表/播放器
     */
    private static Class<?> mClsVideoList, mClsVideoPlayer;

    /**
     * Player Customized Version
     */
    private static int version = PlayerVersions.SLC_LC2010_VDC;

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
            case PlayerVersions.SLC_LC2010_VDC:
                //Player open confirm
                mMusicPlayerOpenConfirm = 1;
                mVideoPlayerOpenConfirm = 1;
                // mFlag$IsHsjVersion = false;
                mFlag$IsCj2Version = true;
                mFlag$IsUseLocalMusicPlayer = true;
                mFlag$IsSupportVideoMix = false;
                mFlag$IsSupportVideoTurnOffLight = true;
                mFlag$IsSupportListAllMedias = true;
                mFlag$IsAutoResumeAfterBoot = false;
                mFlag$IsSupportDashboard = false;
                mFlag$IsSupportOrderPlayMode = false;
                mFlag$IsProcessVideoResolution = false;
                // Class
                mClsMusicList = SclLc2010VdcAudioListActivity.class;
                mClsMusicPlayer = SclLc2010VdcAudioPlayerActivity.class;
                mClsVideoList = SclLc2010VdcVideoListActivity.class;
                mClsVideoPlayer = SclLc2010VdcVideoPlayerActivity.class;
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
     * New Video Player Intent
     */
    public static Intent newVideoPlayerIntent(Context cxt) {
        Intent targetIntent = null;
        if (mVideoPlayerOpenConfirm == 1 && PlayerAppManager.getCurrPlayer() == null) {
            if (mClsVideoList != null) {
                Logs.i(TAG, "newVideoPlayerIntent - > :" + "mClsVideoList" + mClsVideoList);
                targetIntent = new Intent(cxt, mClsVideoList);
            }
        } else {
            targetIntent = intentOfVideoPlayer(cxt);
        }
        return targetIntent;
    }

    public static Intent intentOfVideoPlayer(Context context) {
        if (mClsVideoPlayer != null) {
            Logs.i(TAG, "intentOfVideoPlayer - > :" + "mClsVideoPlayer" + mClsVideoPlayer);
            return new Intent(context, mClsVideoPlayer);
        }
        return null;
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
    public static boolean isCj2Version() {
        return mFlag$IsCj2Version;
    }

    /**
     * 是否使用本地音乐播放器
     */
    public static boolean isUseLocalMusicPlayer() {
        return mFlag$IsUseLocalMusicPlayer;
    }

    /**
     * 是否支持视频混音
     */
    public static boolean isSupportVideoMix() {
        return mFlag$IsSupportVideoMix;
    }

    /**
     * 是否支持“视频关灯模式”
     */
    public static boolean isCanTurnOffLight() {
        return mFlag$IsSupportVideoTurnOffLight;
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

    /**
     * 是否支持自动恢复播放
     */
    public static boolean isCanAutoResume() {
        return mFlag$IsAutoResumeAfterBoot;
    }

    /**
     * 是否支持仪表盘信息
     */
    public static boolean isSupportDashboard() {
        return mFlag$IsSupportDashboard;
    }

    /**
     * 是否是使用语音AIOS V2.0
     */
    public static boolean isAiosV2() {
        return true;
    }

    /**
     * 是否支持"顺序播放(即播放完成一次列表媒体后停止)"
     */
    public static boolean isSupportOrderPlay() {
        return mFlag$IsSupportOrderPlayMode;
    }

    /**
     * 是否处理视频分辨率
     */
    public static boolean isProcessVideoResolution() {
        return mFlag$IsProcessVideoResolution;
    }
}
