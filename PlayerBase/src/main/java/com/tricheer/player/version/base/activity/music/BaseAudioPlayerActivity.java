package com.tricheer.player.version.base.activity.music;

import android.content.Context;
import android.os.Bundle;
import android.widget.SeekBar;

import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.engine.PlayerConsts.PlayMode;
import com.tricheer.player.engine.music.PlayerDelegate;
import com.tricheer.player.receiver.ReceiverOperates;
import com.tricheer.player.service.MusicPlayService;
import com.tricheer.player.utils.PlayerLogicUtils;
import com.tricheer.player.utils.PlayerPreferUtils;

import java.io.File;
import java.util.List;

import js.lib.android.media.IPlayerState;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.Logs;
import js.lib.http.IResponse;

/**
 * Music Player Base Activity
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioPlayerActivity extends BaseAudioFocusActivity implements PlayerDelegate {

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
    protected void onPlayServiceConnected(MusicPlayService service) {
        super.onPlayServiceConnected(service);
        setPlayerActionsListener(this);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void onPlayFromFolder(int playPos, List<String> listPlayPaths) {
        // Save last select media Path
        File selectMedia = new File(listPlayPaths.get(playPos));
        if (selectMedia.exists()) {
            File parentFolder = selectMedia.getParentFile();
            if (parentFolder != null) {
                PlayerPreferUtils.getLastMusicPath(true, parentFolder.getPath());
            }
        }
    }

    @Override
    public void onNotifyOperate(String opFlag) {
        Logs.i(TAG, "onNotifyOperate(" + opFlag + ")");
        if (ReceiverOperates.PREV_PAGE.equals(opFlag)) {
            execJumpPrevPage();
        } else if (ReceiverOperates.NEXT_PAGE.equals(opFlag)) {
            execJumpNextPage();

            // Process By Service
        } else {
            super.onNotifyOperate(opFlag);
        }
    }

    /**
     * Is Playing Media
     */
    protected boolean isPlayingSameMedia(String mediaUrl) {
        try {
            return mediaUrl.equals(getPath());
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "isPlayingSameMedia()", e);
            return false;
        }
    }

    /**
     * EXEC Play or Pause
     */
    public void execPlayOrPause() {
        Logs.i(TAG, "execPlayOrPause");
        if (isPlaying()) {
            pauseByUser();
        } else {
            resumeByUser();
        }
    }

    /**
     * Set Play Mode
     */
    public void execPlayModeSet() {
        setPlayMode(PlayMode.NONE);
    }

    /**
     * 打开上一页音乐
     */
    protected void execJumpPrevPage() {
    }

    /**
     * 打开下一页音乐
     */
    protected void execJumpNextPage() {
    }

    @Override
    public void onNotifyPlayState(int playState) {
        super.onNotifyPlayState(playState);
        //Check page status
        if (isDestroyed()) {
            return;
        }

        //Process play state
        switch (playState) {
            case IPlayerState.REFRESH_UI:
                refreshCurrMediaInfo();
                break;
            case IPlayerState.PLAY:
                refreshUIOfPlayBtn(1);
                break;
            case IPlayerState.PREPARED:
                refreshSeekBar(true);
                break;
            case IPlayerState.SEEK_COMPLETED:
                refreshSeekBar(false);
                break;
            case IPlayerState.PAUSE:
                refreshUIOfPlayBtn(2);
                break;
            case IPlayerState.COMPLETE:
            case IPlayerState.RESET:
            case IPlayerState.STOP:
            case IPlayerState.RELEASE:
                refreshUIOfPlayBtn(2);
                refreshFrameTime(true);
                break;
            case IPlayerState.ERROR:
                refreshUIOfPlayBtn(2);
                refreshFrameTime(true);
                onNotifyPlayState$Error();
                break;
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

    /**
     * After Play Error
     */
    protected void onNotifyPlayState$Error() {
        ProMusic programWithError = getCurrProgram();
        if (programWithError != null) {
            Logs.i(TAG, "onNotifyPlayState$Error :: " + programWithError.mediaUrl);
            PlayerLogicUtils.toastPlayError(mContext, programWithError.title);
        }
    }

    @Override
    public void onProgressChange(String mediaUrl, int progress, int duration) {
    }

    /**
     * Notify Search Music
     */
    @Override
    public void onNotifySearchMediaList(String title, String artist) {
    }

    @Override
    public void onNotifyPlaySearchedMusic(final ProMusic program) {
    }

    @Override
    public void callback(IResponse response) {
    }

    /**
     * Set Player Flag
     */
    protected void setCurrPlayer(boolean isInit, Context cxt) {
        if (isInit) {
            PlayerAppManager.putCxt(PlayerCxtFlag.MUSIC_PLAYER, cxt);
        } else {
            PlayerAppManager.removeCxt(PlayerCxtFlag.MUSIC_PLAYER);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (!isPlaying() && CommonUtil.isRunningBackground(mContext, getPackageName())) {
            PlayerAppManager.exitCurrPlayer();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        trimMemory(level);
        super.onTrimMemory(level);
    }

    private void trimMemory(int level) {
        Logs.i(TAG, "trimMemory(" + level + ")");
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            getImageLoader().clearMemoryCache();
        } else if (!isPlaying() && isHomeClicked()) {
            PlayerAppManager.exitCurrPlayer();
        }
    }

    @Override
    protected void onIDestroy() {
        super.onIDestroy();
    }
}
