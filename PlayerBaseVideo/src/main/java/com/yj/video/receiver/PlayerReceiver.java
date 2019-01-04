package com.yj.video.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tri.lib.receiver.ActionEnum;
import com.tri.lib.utils.TrVideoPreferUtils;
import com.yj.video.App;
import com.yj.video.engine.PlayerAppManager;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.android_media.scan.video.VideoScanService;

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
         * @param data <p>(1)"fileList" - "List&lt;String&gt;" - Media url list ;</p>
         *             <p>(2)"index" - int - The position of target media url in list</p>
         */
        void onPlayFromFolder(Intent data);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action: " + action);

        ActionEnum ae = ActionEnum.getByAction(action);
        Log.i(TAG, "action-ae: " + ae);
        if (ae != null) {
            switch (ae) {
                case MEDIA_EXIT_VIDEO:
                    PlayerAppManager.exitCurrPlayer();
                    break;

                // ### Click FileManager Media to Play ###
                case PLAY_VIDEO_BY_FILEMANAGER:
                    doPlayVideoFromFileManager(context, intent);
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
     * <p>Execute when you are clicking the media in a folder!</p>
     * <p>(1) Should contain list used to storage media urls.</p>
     * <p>(2) Should contain index used to notify the position of target media url.</p>
     */
    private void doPlayVideoFromFileManager(Context context, Intent intent) {
        //It is no meanings to open a player with no contents.
        if (intent.getExtras() == null) {
            return;
        }

        //Try to get player
        PlayerReceiverListener currPlayer = PlayerAppManager.getCurrPlayer();
        //Open a new player to play media
        if (EmptyUtil.isNull(currPlayer)) {
            App.openVideoPlayerByFileManager(context, intent);

            //Notify exist player to play media
        } else {
            App.openVideoPlayer(context, "", null);
            currPlayer.onPlayFromFolder(intent);
        }
    }

    /**
     * Start media scan service
     */
    private void startMediaScanService(Context context) {
        if (context != null) {
            Log.i(TAG, "startMediaScanService(Context)");
            Intent intentMediaScanService = new Intent(context, VideoScanService.class);
            intentMediaScanService.putExtra("START_BY", "PLAYER_RECEIVER");
            context.startService(intentMediaScanService);
        }
    }
}
