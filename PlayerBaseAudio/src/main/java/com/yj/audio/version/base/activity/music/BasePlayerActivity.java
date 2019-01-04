package com.yj.audio.version.base.activity.music;

import android.app.Service;
import android.os.Bundle;
import android.widget.SeekBar;

import com.yj.audio.utils.PlayerLogicUtils;

import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.player.PlayState;
import js.lib.android.utils.Logs;

/**
 * Music Player Base Activity
 *
 * @author Jun.Wang
 */
public abstract class BasePlayerActivity extends BaseUIActivity {

    // LOG TAG
    private final String TAG = "BaseAudioPlayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onAudioServiceConnChanged(Service service) {
        super.onAudioServiceConnChanged(service);
        setPlayListener(this);
    }

    @Override
    public void onPlayFromFolder(int playPos, List<String> listPlayPaths) {
        // //Save last select media Path
        // File selectMedia = new File(listPlayPaths.get(playPos));
    }

    @Override
    public void onPlayStateChanged(final PlayState playState) {
//        super.onPlayStateChanged(playState);
        if (isActActive()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Process play state
                    switch (playState) {
                        case REFRESH_UI:
                            refreshCurrMediaInfo();
                            break;
                        case PLAY:
                            refreshUIOfPlayBtn(1);
                            break;
                        case PREPARED:
                            refreshSeekBar(true);
                            break;
                        case SEEK_COMPLETED:
                            refreshSeekBar(false);
                            break;
                        case PAUSE:
                            refreshUIOfPlayBtn(2);
                            break;
                        case COMPLETE:
                        case RESET:
                        case STOP:
                        case RELEASE:
                            refreshUIOfPlayBtn(2);
                            refreshFrameTime(true);
                            break;
                        case ERROR:
                            refreshUIOfPlayBtn(2);
                            refreshFrameTime(true);

                            //Toast error message.
                            ProAudio programWithError = getCurrProgram();
                            if (programWithError != null) {
                                Logs.i(TAG, "onNotifyPlayState$Error :: " + programWithError.mediaUrl);
                                PlayerLogicUtils.toastPlayError(BasePlayerActivity.this, programWithError.title);
                            }
                            break;
                    }
                }
            });
        }
    }

    /**
     * Refresh current media information
     * <p>Should execute when media changed!!!</p>
     * <p>(1) Media name</p>
     * <p>(2) Media artist</p>
     * <p>(2) Media album</p>
     */
    protected abstract void refreshCurrMediaInfo();

    /**
     * Refresh play or pause status
     * <p>Refresh the UI of play button which is used to switch PLAY or PAUSE</p>
     *
     * @param flag 1-playing ; 2-Paused
     */
    protected abstract void refreshUIOfPlayBtn(int flag);

    /**
     * Reset {@link SeekBar} status
     * <p>Should execute when media is prepared</p>
     * <p>(1)Enable</p>
     * <p>(2)Set Max</p>
     * <p>(3)Set progress to 0</p>
     *
     * @param isInit true-Initialize the status of {@link SeekBar}
     */
    protected abstract void refreshSeekBar(boolean isInit);

    /**
     * Refresh frame time information
     * <p>Execute update receive media progress changed</p>
     * <p>Execute reset on media [COMPLETE/RESET/STOP/RELEASE/ERROR]</p>
     *
     * @param isInit true-Reset frame time
     */
    protected abstract void refreshFrameTime(boolean isInit);

    @Override
    public void onProgressChanged(String mediaUrl, int progress, int duration) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
