package com.yj.audio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tri.lib.receiver.ActionEnum;
import com.tri.lib.utils.SettingsSysUtil;
import com.tri.lib.utils.TrVideoPreferUtils;
import com.yj.audio.App;
import com.yj.audio.engine.PlayerAppManager;
import com.yj.audio.engine.PlayerConsts;
import com.yj.audio.engine.PlayerConsts.PlayerOpenMethod;

import java.util.ArrayList;
import java.util.List;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.android_media.scan.audio.AudioScanService;

/**
 * Player receiver
 *
 * @author Jun.Wang
 */
public class PlayerReceiver extends BroadcastReceiver {
    // LOG TAG
    private static final String TAG = "PlayerReceiver";

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
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action: " + action);

        ActionEnum ae = ActionEnum.getByAction(action);
        Log.i(TAG, "action-ae: " + ae);
        if (ae != null) {
            switch (ae) {
                case MEDIA_EXIT_AUDIO:
                    SettingsSysUtil.setRememberPlayFlag(context.getApplicationContext(), false);
                    PlayerAppManager.exitCurrPlayer(true);
                    break;

                // ### Click FileManager Media to Play ###
                case PLAY_MUSIC_BY_FILEMANAGER:
                    doPlayMusicFromFileManager(context, intent);
                    break;

                // ### System broadcast ###
                case BOOT_COMPLETED:
                    Logs.switchEnable(true);
                    TrVideoPreferUtils.getVideoWarningFlag(true, 1);
                    startMediaScanService(context);
                    break;

                // ### Open Logs ###
                case OPEN_LOGS:
                    Logs.switchEnable(false);
                    break;
            }
        }
    }

    /**
     * Notify Play Music From FileManager
     * <p>
     * Click media to play
     */
    private void doPlayMusicFromFileManager(Context context, Intent intent) {
        PlayerReceiverListener currPlayer = PlayerAppManager.getCurrPlayer();
        if (EmptyUtil.isNull(currPlayer)) {
            App.openMusicPlayer(context, PlayerOpenMethod.VAL_FILE_MANAGER, intent);
        } else {
            App.openMusicPlayer(context, "", null);
            // Play Selected Medias
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int playPos = bundle.getInt(PlayerConsts.INDEX);
                ArrayList<String> listPaths = bundle.getStringArrayList(PlayerConsts.FILE_LIST);
                currPlayer.onPlayFromFolder(playPos, listPaths);
            }
        }
    }

    /**
     * Start media scan service
     */
    private void startMediaScanService(Context context) {
        if (context != null) {
            Log.i(TAG, "startMediaScanService(Context)");
            Intent intentMediaScanService = new Intent(context, AudioScanService.class);
            intentMediaScanService.putExtra("START_BY", "PLAYER_RECEIVER");
            context.startService(intentMediaScanService);
        }
    }
}
