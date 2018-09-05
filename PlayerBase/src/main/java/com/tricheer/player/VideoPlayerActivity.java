package com.tricheer.player;

import js.lib.android.utils.Logs;
import android.os.Bundle;

import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.version.base.activity.BaseFragActivity;

/**
 * Video Player
 * 
 * @author Jun.Wang
 */
public class VideoPlayerActivity extends BaseFragActivity {
	// TAG
	private final String TAG = "VideoPlayerActivity";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		init();
	}

	private void init() {
		Logs.i(TAG, "^^ init() ^^");
		if (isBtCalling(true)) {
			finish();
		} else {
			if (PlayerAppManager.getCurrPlayerFlag() != PlayerAppManager.PlayerCxtFlag.VIDEO_PLAYER) {
				Logs.i(TAG, "wxp -> :" + "exitCurrPlayer : " + PlayerAppManager.getCurrPlayerFlag());
				PlayerAppManager.exitCurrPlayer();
			}
			selectVideoPlayer();
		}
	}

	private void selectVideoPlayer() {
		App.openVideoPlayer(this, "", getIntent());
		finish();
	}
}
