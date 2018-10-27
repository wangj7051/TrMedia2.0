package com.tricheer.player;

import android.os.Bundle;

import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.version.base.activity.BaseUsbLogicActivity;

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
        boolean isTest = getIntent().getBooleanExtra("IS_TEST", false);
        getIntent().removeExtra("IS_TEST");
        return isTest;
    }

    private void openPlayer() {
        switch (PlayerAppManager.getCurrPlayerFlag()) {
            case PlayerAppManager.PlayerCxtFlag.VIDEO_LIST:
            case PlayerAppManager.PlayerCxtFlag.VIDEO_PLAYER:
                PlayerAppManager.exitCurrPlayer();
                break;
        }
        App.openMusicPlayer(this, "", getIntent());
        finish();
    }
}
