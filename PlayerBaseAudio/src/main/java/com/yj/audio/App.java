package com.yj.audio;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.tri.lib.utils.TrAudioPreferUtils;
import com.yj.audio.engine.PlayerConsts.PlayerOpenMethod;
import com.yj.audio.engine.VersionController;
import com.yj.audio.utils.PlayerFileUtils;

import java.util.List;

import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.media.engine.audio.utils.AudioUtils;
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
        TrAudioPreferUtils.init(this);
        // 初始化文件管理类
        PlayerFileUtils.init(this);
        // 初始化图片管理类
        ImageLoaderUtils.initLoader(this);
        // 初始化异常处理类
        AppCrashHandler.instance().init(this);
        // 初始化数据库操作类
        AudioDBManager.instance().init(this, PlayerFileUtils.getDBPath() + "/PlayerAudio.sqlite");

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
        Logs.i(TAG, " ^^^ Application Information ^^^");
        Logs.i(TAG, "[appName:" + AppUtil.pkgName + "]");
        Logs.i(TAG, "[appVName:" + AppUtil.versionName + "]");
        Logs.i(TAG, "[appVCode:" + AppUtil.versionCode + "]");

        // 初始化媒体信息 - 路径
        List<String> listBlacklistPaths = PlayerFileUtils.getBlacklistPaths();
        List<String> listSupportPaths = PlayerFileUtils.getListSuppportPaths();
        // 初始化媒体信息 - 音乐
        AudioUtils.init(this, listSupportPaths, listBlacklistPaths);
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
}
