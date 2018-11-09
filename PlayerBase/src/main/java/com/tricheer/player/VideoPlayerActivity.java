package com.tricheer.player;

import android.os.Bundle;
import android.util.Log;

import com.tri.lib.engine.BtCallStateController;
import com.tri.lib.utils.SettingsGlobalUtil;
import com.tri.lib.utils.TrVideoPreferUtils;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.utils.PlayerFileUtils;
import com.tricheer.player.version.base.activity.BaseUsbLogicActivity;

import js.lib.android.media.player.PlayEnableController;
import js.lib.android.utils.Logs;

/**
 * Video Player
 *
 * @author Jun.Wang
 */
public class VideoPlayerActivity extends BaseUsbLogicActivity {
    // TAG
    private static final String TAG = "VideoPlayerActivity";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initBtCall();
        init();
    }

    private void init() {
        Logs.i(TAG, "^^ init() ^^");
        Log.i(TAG, "isHave: " + PlayerFileUtils.isHasSupportStorage());
//        if (isTest() || PlayerFileUtils.isHasSupportStorage()) {
        openPlayer();
//        } else {
//            toastMsg();
//        }
    }

    private boolean isTest() {
        int flag = TrVideoPreferUtils.getNoUDiskToastFlag(false);
        return (flag == 0);
    }

    private void openPlayer() {
        switch (PlayerAppManager.getCurrPlayerFlag()) {
            case PlayerAppManager.PlayerCxtFlag.MUSIC_LIST:
            case PlayerAppManager.PlayerCxtFlag.MUSIC_PLAYER:
                PlayerAppManager.exitCurrPlayer();
                break;
        }

        //Check play enable
        Log.i(TAG, "*** Print status ***");
        Log.i(TAG, PlayEnableController.getStateDesc());
        if (PlayEnableController.isPlayEnable()) {
            Log.i(TAG, "play enable -> true");
            App.openVideoPlayer(this, "", getIntent());
        }
        finish();
    }

    private void initBtCall() {
        //Initialize BT call state
        int btCallState = SettingsGlobalUtil.getBtCallState(this);
        Log.i(TAG, "btCallState: " + btCallState);
        BtCallStateController.initBtState(btCallState);
    }
}
