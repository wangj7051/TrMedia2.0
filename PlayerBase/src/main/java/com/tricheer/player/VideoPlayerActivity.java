package com.tricheer.player;

import android.os.Bundle;
import android.util.Log;

import com.tri.lib.utils.TrVideoPreferUtils;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.utils.PlayerFileUtils;
import com.tricheer.player.version.base.activity.BaseUsbLogicActivity;

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
        init();
    }

    private void init() {
        Logs.i(TAG, "^^ init() ^^");
        Log.i(TAG, "isHave: " + PlayerFileUtils.isHasSupportStorage());
        if (isTest() || PlayerFileUtils.isHasSupportStorage()) {
            openPlayer();
        } else {
            toastMsg();
        }
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
        App.openVideoPlayer(this, "", getIntent());
        finish();
    }
}
