package com.tricheer.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.engine.PlayerType;

import java.util.List;
import java.util.Set;

import js.lib.android.utils.EmptyUtil;

/**
 * Player Receiver Base
 *
 * @author Jun.Wang
 */
public class PlayerBaseReceiver extends BroadcastReceiver {
    /**
     * Handler
     */
    protected Handler mHandler = new Handler();
    protected final int M_DEFAULT_DELAY_TIME = 300;

    /**
     * Music Key Word
     * <p>
     * If AiSpeech Voice contains these strings, show music Player
     */
    private final String AIS_MUSIC_KEYWORD1 = "歌", AIS_MUSIC_KEYWORD2 = "音乐";

    /**
     * Player Receiver Listener
     */
    public interface PlayerReceiverListener {

        /**
         * Play from folder
         *
         * @param playPos       : target play position
         * @param listPlayPaths : target media URL list
         */
        public void onPlayFromFolder(int playPos, List<String> listPlayPaths);

        /**
         * Play from folder
         *
         * @param data <p>(1)"fileList" - "List&lt;String&gt;" - Media url list ;</p>
         *             <p>(2)"index" - int - The position of target media url in list</p>
         */
        void onPlayFromFolder(Intent data);

        /**
         * Notify Operate Player
         *
         * @param operateFlag : {@link PlayerActives}
         */
        public void onNotifyOperate(String operateFlag);

        /**
         * Is cache media information on AccOff
         * <p>
         * 1. 视频播放器不活动： （1）视频在后台 （2）视频再前台，且被主动暂停
         * <p>
         * 2. 视频再行车记录时候
         * <p>
         * 4. LPT_LV8918_LB8 版本
         * <p>
         * 以上四种种情况将会在即日起的版本，统一处理为AccOff 不保存，AccOn 不恢复
         */
        public boolean isCacheOnAccOff();

        /**
         * Notify Search
         */
        public void onNotifySearchMediaList(String title, String artist);

        /**
         * Notify Play Search Result
         */
        public void onNotifyPlaySearchedMusic(ProMusic program);

        /**
         * 通知播放指定路径媒体
         *
         * @param path : 媒体路径
         */
        public void onNotifyPlayMedia(String path);

        /**
         * Notify Scan Medias
         *
         * @param flag        : Scan Flag
         * @param listPrgrams : Medias list
         */
        public void onNotifyScanAudios(int flag, List<ProMusic> listPrgrams, Set<String> allSdMountedPaths);

        /**
         * Notify Scan Medias
         *
         * @param flag        : Scan Flag
         * @param listPrgrams : Medias list
         */
        public void onNotifyScanVideos(int flag, List<ProVideo> listPrgrams, Set<String> allSdMountedPaths);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    }

    /**
     * 判断语音中是否包含音乐播放器关键字，如果包含，则需要在此时打开音乐播放器
     *
     * @param voiceStr : 语音字符串
     */
    protected boolean isNeedOpenMusicPlayer(String voiceStr) {
        return !EmptyUtil.isNull(voiceStr) && (voiceStr.contains(AIS_MUSIC_KEYWORD1) || voiceStr.contains(AIS_MUSIC_KEYWORD2));
    }

    protected void notifyPlayFromFolder(PlayerReceiverListener l, int playPos, List<String> listPaths) {
        if (l == null) {
            l = PlayerAppManager.getCurrPlayer();
        }
        if (l != null) {
            l.onPlayFromFolder(playPos, listPaths);
        }
    }

    protected void notifyPlayFromFolder(PlayerReceiverListener l, Intent data) {
        if (l == null) {
            l = PlayerAppManager.getCurrPlayer();
        }
        if (l != null) {
            l.onPlayFromFolder(data);
        }
    }

    /**
     * @param l           : {@link PlayerReceiverListener}
     * @param operateFlag : {@link PlayerActives}
     */
    protected void notifyOperate(PlayerReceiverListener l, String operateFlag) {
        // 判断是否是播放器
        if (l == null) {
            l = PlayerAppManager.getCurrPlayer();
        }
        // 判斷是否是视频播放器列表
        if (l == null) {
            if (PlayerType.isVideo()) {
                Context cxtVideoList = PlayerAppManager.getCxt(PlayerCxtFlag.VIDEO_LIST);
                if (cxtVideoList instanceof PlayerReceiverListener) {
                    l = (PlayerReceiverListener) cxtVideoList;
                }
            }
        }
        // 通知执行
        if (l != null) {
            l.onNotifyOperate(operateFlag);
        }
    }
}
