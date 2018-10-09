package com.tricheer.player;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.tri.lib.utils.TrAudioPreferUtils;
import com.tri.lib.utils.TrVideoPreferUtils;
import com.tricheer.player.engine.PlayerConsts.PlayerOpenMethod;
import com.tricheer.player.engine.VersionController;
import com.tricheer.player.receiver.MediaScanReceiver;
import com.tricheer.player.receiver.PlayerReceiver;
import com.tricheer.player.utils.PlayerFileUtils;

import java.util.List;

import js.lib.android.media.audio.db.AudioDBManager;
import js.lib.android.media.audio.utils.AudioInfo;
import js.lib.android.media.audio.utils.AudioUtils;
import js.lib.android.media.video.db.VideoDBManager;
import js.lib.android.media.video.utils.VideoInfo;
import js.lib.android.media.video.utils.VideoUtils;
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

        //Initialize PlayerReceiver
        PlayerReceiver.init(this);

        // 初始化Preference操作类
        TrAudioPreferUtils.init(this);
        TrVideoPreferUtils.init(this);
        // 初始化日志类
        Logs.init("-Player");
        // 初始化文件管理类
        PlayerFileUtils.init(this);
        // 初始化图片管理类
        ImageLoaderUtils.initLoader(this);
        // 初始化异常处理类
        AppCrashHandler.instance().init(this);
        // 初始化数据库操作类
        AudioDBManager.instance().init(this, PlayerFileUtils.getDBPath() + "/PlayerAudio.sqlite");
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
        // 初始化媒体信息 - 音乐
        AudioInfo.setSupportMedias(VersionController.isSupportAllMedias());
        AudioUtils.init(this, listSupportPaths, listBlacklistPaths);
        // 初始化媒体信息 - 视频
        VideoInfo.setSupportMedias(VersionController.isSupportAllMedias());
        VideoUtils.init(this, listSupportPaths, listBlacklistPaths);

        // 初始化媒体扫描
        MediaScanReceiver.init(this);
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
