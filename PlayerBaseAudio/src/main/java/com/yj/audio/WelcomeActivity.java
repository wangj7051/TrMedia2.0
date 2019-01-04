package com.yj.audio;

import android.os.Bundle;
import android.util.Log;

import com.tri.lib.engine.BtCallStateController;
import com.tri.lib.utils.SettingsGlobalUtil;
import com.tri.lib.utils.TrAudioPreferUtils;
import com.yj.audio.utils.PlayerFileUtils;
import com.yj.audio.version.base.activity.BaseUsbLogicActivity;

import js.lib.android.media.player.PlayEnableController;
import js.lib.android.utils.Logs;

/**
 * Music Player
 *
 * @author Jun.Wang
 */
public class WelcomeActivity extends BaseUsbLogicActivity {
    // TAG
    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initBtCall();
        init();
    }

    private void init() {
        Logs.i(TAG, "^^ init() ^^");
        if (isTest() || isHasStorage()) {
            //Open player
            openPlayer();
        } else {
            toastMsg();
        }
    }

    private boolean isTest() {
        int flag = TrAudioPreferUtils.getNoUDiskToastFlag(false);
        Log.i(TAG, "isTest() - flag:" + flag);
        return (flag == 0);
    }

    private boolean isHasStorage() {
        boolean isHasSD = PlayerFileUtils.isHasSupportStorage();
        Log.i(TAG, "isHasStorage() - isHasSD:" + isHasSD);
        return isHasSD;
    }

    private void openPlayer() {
        //Check play enable
        Log.i(TAG, "*** Print status ***");
        Log.i(TAG, PlayEnableController.getStateDesc());
        if (PlayEnableController.isSysAllowPlay()) {
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

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
    }
}
