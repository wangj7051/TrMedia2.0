package com.yj.video;

import android.os.Bundle;
import android.util.Log;

import com.tri.lib.engine.BtCallStateController;
import com.tri.lib.utils.SettingsGlobalUtil;
import com.tri.lib.utils.TrVideoPreferUtils;
import com.yj.video.utils.PlayerFileUtils;
import com.yj.video.version.base.activity.BaseUsbLogicActivity;

import js.lib.android.media.player.PlayEnableController;
import js.lib.android.utils.Logs;

/**
 * Video Player
 *
 * @author Jun.Wang
 */
public class WelcomeActivity extends BaseUsbLogicActivity {
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
//        //TODO Test Play use android.widget.VideoView
//        startActivity(new Intent(this, TestVobPlayActivity.class));
//        finish();
//        //TODO Test Play use org.videolan.libvlc.MediaPlayer
//        startActivity(new Intent(this, TestVVPlayActivity.class));
//        finish();

        //
        if (isTest() || isHasStorage()) {
            //Open player
            openPlayer();
        } else {
            toastMsg();
        }
    }

    private boolean isTest() {
        int flag = TrVideoPreferUtils.getNoUDiskToastFlag(false);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
