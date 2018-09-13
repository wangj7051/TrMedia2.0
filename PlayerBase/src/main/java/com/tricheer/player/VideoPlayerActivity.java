package com.tricheer.player;

import android.os.Bundle;

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
        if (PlayerFileUtils.isHasSupportStorage()) {
            checkAndOpenPlayer();
        } else {
            toastMsg();
        }
    }

    private void checkAndOpenPlayer() {
        if (isBtCalling(true)) {
            finish();
        } else {
            if (PlayerAppManager.getCurrPlayerFlag() != PlayerAppManager.PlayerCxtFlag.VIDEO_PLAYER) {
                Logs.i(TAG, "wxp -> :" + "exitCurrPlayer : " + PlayerAppManager.getCurrPlayerFlag());
                PlayerAppManager.exitCurrPlayer();
            }
            App.openVideoPlayer(this, "", getIntent());
            finish();
        }
    }
}
