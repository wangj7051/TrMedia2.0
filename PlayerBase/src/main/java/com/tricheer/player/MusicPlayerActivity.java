package com.tricheer.player;

import android.os.Bundle;
import android.util.Log;

import com.tri.lib.engine.BtCallStateController;
import com.tri.lib.utils.SettingsGlobalUtil;
import com.tri.lib.utils.TrAudioPreferUtils;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.version.base.activity.BaseUsbLogicActivity;

import js.lib.android.media.player.PlayEnableController;
import js.lib.android.utils.Logs;

/**
 * Music Player
 *
 * @author Jun.Wang
 */
public class MusicPlayerActivity extends BaseUsbLogicActivity {
    // TAG
    private static final String TAG = "MusicPlayerActivity";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initBtCall();
        init();
    }

    private void init() {
        Logs.i(TAG, "^^ init() ^^");
//        if (isTest() || PlayerFileUtils.isHasSupportStorage()) {
        openPlayer();
//        } else {
//            toastMsg();
//        }
    }

    private boolean isTest() {
        int flag = TrAudioPreferUtils.getNoUDiskToastFlag(false);
        return (flag == 0);
    }

    private void openPlayer() {
        switch (PlayerAppManager.getCurrPlayerFlag()) {
            case PlayerAppManager.PlayerCxtFlag.VIDEO_LIST:
            case PlayerAppManager.PlayerCxtFlag.VIDEO_PLAYER:
                PlayerAppManager.exitCurrPlayer();
                break;
        }

        //Check play enable
        Log.i(TAG, "*** Print status ***");
        Log.i(TAG, PlayEnableController.getStateDesc());
        if (PlayEnableController.isPlayEnable()) {
            Log.i(TAG, "play enable -> true");
            App.openMusicPlayer(this, "", getIntent());
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
