package com.tricheer.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.tri.lib.receiver.ActionEnum;
import com.tri.lib.utils.TrAudioPreferUtils;
import com.tri.lib.utils.TrVideoPreferUtils;
import com.tricheer.player.App;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerConsts;
import com.tricheer.player.engine.PlayerConsts.PlayerOpenMethod;
import com.tricheer.player.engine.PlayerType;
import com.tricheer.player.service.MediaScanService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * Player receiver
 *
 * @author Jun.Wang
 */
public class PlayerReceiver extends BroadcastReceiver {
    // LOG TAG
    private static final String TAG = "PlayerReceiver";

    /**
     * Context
     */
    private static WeakReference<Context> mContext;

    /**
     * Handler
     */
    protected static Handler mHandler = new Handler();

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
        void onPlayFromFolder(int playPos, List<String> listPlayPaths);

        /**
         * Play from folder
         *
         * @param data <p>(1)"fileList" - "List&lt;String&gt;" - Media url list ;</p>
         *             <p>(2)"index" - int - The position of target media url in list</p>
         */
        void onPlayFromFolder(Intent data);
    }

    public static void init(Context context) {
        mContext = new WeakReference<>(context);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action: " + action);

        ActionEnum ae = ActionEnum.getByAction(action);
        Log.i(TAG, "action-ae: " + ae);
        if (ae != null) {
            switch (ae) {
                // ### Click FileManager Media to Play ###
                case PLAY_MUSIC_BY_FILEMANAGER:
                    doPlayMusicFromFileManager(intent);
                    break;
                case PLAY_VIDEO_BY_FILEMANAGER:
                    doPlayVideoFromFileManager(intent);
                    break;

                // ### System broadcast ###
                case BOOT_COMPLETED:
                    TrVideoPreferUtils.getVideoWarningFlag(true, 1);
                    startMediaScanService(context);
                    break;

                // ### Open Logs ###
                case OPEN_LOGS:
                    Logs.setPrintLog(intent.getBooleanExtra("IS_OPEN", false));
                    break;

                //Test
                case TEST_OPEN_VIDEO_LIST:
                    testSendVideoList();
                    break;
                case TEST_OPEN_VIDEO:
                    TrVideoPreferUtils.getNoUDiskToastFlag(true);
                    break;
                case TEST_OPEN_AUDIO:
                    TrAudioPreferUtils.getNoUDiskToastFlag(true);
                    break;
                case TEST_EXIT_PLAYER:
                    PlayerAppManager.exitCurrPlayer();
                    break;
            }
        }
    }

    /**
     * Notify Play Music From FileManager
     * <p>
     * Click media to play
     */
    private void doPlayMusicFromFileManager(Intent intent) {
        PlayerReceiverListener currPlayer = PlayerAppManager.getCurrPlayer();
        if (EmptyUtil.isNull(currPlayer) || !PlayerType.isMusic()) {
            App.openMusicPlayer(mContext.get(), PlayerOpenMethod.VAL_FILE_MANAGER, intent);
        } else {
            App.openMusicPlayer(mContext.get(), "", null);
            // Play Selected Medias
            Bundle bundle = intent.getExtras();
            int playPos = bundle.getInt(PlayerConsts.INDEX);
            ArrayList<String> listPaths = bundle.getStringArrayList(PlayerConsts.FILE_LIST);
            notifyPlayFromFolder(currPlayer, playPos, listPaths);
        }
    }

    /**
     * Notify Play Music From FileManager
     * <p>Execute when you are clicking the media in a folder!</p>
     * <p>(1) Should contain list used to storage media urls.</p>
     * <p>(2) Should contain index used to notify the position of target media url.</p>
     */
    private void doPlayVideoFromFileManager(Intent intent) {
        //It is no meanings to open a player with no contents.
        if (intent.getExtras() == null) {
            return;
        }

        //Try to get player
        PlayerReceiverListener currPlayer = PlayerAppManager.getCurrPlayer();
        //Open a new player to play media
        if (EmptyUtil.isNull(currPlayer) || !PlayerType.isVideo()) {
            App.openVideoPlayerByFileManager(mContext.get(), intent);

            //Notify exist player to play media
        } else {
            App.openVideoPlayer(mContext.get(), "", null);
            notifyPlayFromFolder(currPlayer, intent);
        }
    }

    /**
     * Start media scan service
     */
    private void startMediaScanService(Context context) {
        if (context != null) {
            Log.i(TAG, "startMediaScanService(Context)");
            Intent intentMediaScanService = new Intent(context, MediaScanService.class);
            intentMediaScanService.putExtra("START_BY", "PLAYER_RECEIVER");
            context.startService(intentMediaScanService);
        }
    }

    private void testSendVideoList() {
        try {
            ArrayList<String> listUrls = new ArrayList<>();
            listUrls.add("/storage/emulated/0/Music/maozhuxideguanghui.mkv");
            listUrls.add("/storage/emulated/0/Music/shenghua.mkv");
            listUrls.add("/storage/emulated/0/Music/taomagan.mkv");
            listUrls.add("/storage/emulated/0/Music/huohuodeguniang.flv");
            listUrls.add("/storage/emulated/0/Music/fenshengbang-huakaihualuo.flv");
            listUrls.add("/storage/emulated/0/Music/daoguangjianying.mkv");

            Intent data = new Intent(ActionEnum.PLAY_VIDEO_BY_FILEMANAGER.getAction());
            data.putExtra(PlayerConsts.FILE_LIST, listUrls);
            data.putExtra(PlayerConsts.INDEX, 0);

            mContext.get().sendBroadcast(data);
        } catch (Exception e) {
            Logs.debugI(TAG, "testSendVideoList");
        }
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
}
