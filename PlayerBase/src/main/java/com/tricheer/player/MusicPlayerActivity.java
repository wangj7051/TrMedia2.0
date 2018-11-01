package com.tricheer.player;

import android.os.Bundle;

import com.tri.lib.utils.TrAudioPreferUtils;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.utils.PlayerFileUtils;
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
        if (isTest() || PlayerFileUtils.isHasSupportStorage()) {
            openPlayer();
        } else {
            toastMsg();
        }
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
        App.openMusicPlayer(this, "", getIntent());
        finish();
    }
}
