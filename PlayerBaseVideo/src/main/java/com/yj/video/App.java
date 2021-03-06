package com.yj.video;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.tri.lib.utils.TrVideoPreferUtils;
import com.yj.video.engine.PlayerConsts.PlayerOpenMethod;
import com.yj.video.engine.VersionController;
import com.yj.video.utils.PlayerFileUtils;

import java.util.List;

import js.lib.android.media.engine.video.db.VideoDBManager;
import js.lib.android.media.engine.video.utils.VideoUtils;
import js.lib.android.utils.AppCrashHandler;
import js.lib.android.utils.AppUtil;
import js.lib.android.utils.ImageLoaderUtils;
import js.lib.android.utils.Logs;
import js.lib.android.utils.ScreenInfoUtil;

/**
 * Player Activity Application
 *
 * @author Jun.Wang
 */
public class App extends Application {
    // TAG
    private static final String TAG = "PLAYER_APP";

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化版本控制
        VersionController.init();
        // 初始化Preference操作类
        TrVideoPreferUtils.init(this);
        // 初始化文件管理类
        PlayerFileUtils.init(this);
        // 初始化图片管理类
        ImageLoaderUtils.initLoader(this);
        // 初始化异常处理类
        AppCrashHandler.instance().init(this);
        // 初始化数据库操作类
        VideoDBManager.instance().init(this, PlayerFileUtils.getDBPath() + "/PlayerVideo.sqlite");

        // 初始化屏信息
        ScreenInfoUtil.init(this);
        Logs.i(TAG, " ^^^ Resolutions(" + ScreenInfoUtil.width + "x" + ScreenInfoUtil.height + ") ^^^");
        Logs.i(TAG, "[Resolutions: " + ScreenInfoUtil.width + "x" + ScreenInfoUtil.height + "]");
        Logs.i(TAG, "[density:" + ScreenInfoUtil.density + "]");
        Logs.i(TAG, "[densityDpi:" + ScreenInfoUtil.densityDpi + "]");
        Logs.i(TAG, "[densityStr:" + ScreenInfoUtil.densityStr + "]");
        Logs.i(TAG, "[scaledDensity:" + ScreenInfoUtil.scaledDensity + "]");
        Logs.i(TAG, " ");

        // 初始化应用信息
        AppUtil.init(this);
        Logs.i(TAG, " ^^^ Application Informations ^^^");
        Logs.i(TAG, "[appName:" + AppUtil.pkgName + "]");
        Logs.i(TAG, "[appVName:" + AppUtil.versionName + "]");
        Logs.i(TAG, "[appVCode:" + AppUtil.versionCode + "]");

        // 初始化媒体信息 - 路径
        List<String> listBlacklistPaths = PlayerFileUtils.getBlacklistPaths();
        List<String> listSupportPaths = PlayerFileUtils.getListSuppportPaths();
        // 初始化媒体信息 - 视频
        VideoUtils.init(this, listSupportPaths, listBlacklistPaths);
    }

    /**
     * Open Music Player
     *
     * @param openMethod : {@link PlayerOpenMethod}
     */
    public static void openMusicPlayer(Context cxt, String openMethod, Intent data) {
        if (VersionController.isUseLocalMusicPlayer()) {
            Intent playerIntent = VersionController.newMusicPlayerIntent(cxt);
            if (playerIntent != null) {
                if (data != null) {
                    playerIntent.putExtras(data);
                }
                playerIntent.putExtra(PlayerOpenMethod.PARAM, openMethod);
                playerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cxt.startActivity(playerIntent);
            }
        }
    }

    /**
     * Open Video Player
     *
     * @param openMethod : {@link PlayerOpenMethod}
     */
    public static void openVideoPlayer(Context cxt, String openMethod, Intent data) {
        Intent playerIntent = VersionController.newVideoPlayerIntent(cxt);
        if (playerIntent != null) {
            if (data != null) {
                playerIntent.putExtras(data);
            }
            playerIntent.putExtra(PlayerOpenMethod.PARAM, openMethod);
            playerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cxt.startActivity(playerIntent);
        }
    }

    /**
     * Open video player from file manager
     *
     * @param context {@link Context}
     * @param data    {@link Intent}
     */
    public static void openVideoPlayerByFileManager(Context context, Intent data) {
        Intent intentOfPlayer = VersionController.intentOfVideoPlayer(context);
        if (intentOfPlayer != null) {
            if (data != null) {
                intentOfPlayer.putExtras(data);
            }
            intentOfPlayer.putExtra(PlayerOpenMethod.PARAM, PlayerOpenMethod.VAL_FILE_MANAGER);
            intentOfPlayer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentOfPlayer);
        }
    }
}
