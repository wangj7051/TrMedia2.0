package com.tricheer.player.version.cj.slc_lc2010_vdc.activity;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.tri.lib.engine.KeyEnum;
import com.tri.lib.receiver.AccReceiver;
import com.tri.lib.receiver.ReverseReceiver;
import com.tri.lib.utils.SettingsSysUtil;
import com.tricheer.player.R;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.receiver.MediaScanReceiver;
import com.tricheer.player.version.base.activity.music.BaseAudioKeyEventActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.BaseAudioListFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioAlbumsFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioArtistsFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioCollectsFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioFoldersFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioNamesFrag;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.mediabtn.MediaBtnController;
import js.lib.android.media.engine.mediabtn.MediaBtnReceiver;
import js.lib.android.media.player.PlayState;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.FragUtil;
import js.lib.android.utils.Logs;

/**
 * SCL_LC2010_VDC Music List Activity
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioListActivity extends BaseAudioKeyEventActivity
        implements AccReceiver.AccDelegate, ReverseReceiver.ReverseDelegate,
        MediaBtnReceiver.MediaBtnListener {

    // TAG
    private static final String TAG = "MusicListActivity";

    //==========Widgets in this Activity==========
    private ImageView ivRhythmAnim;
    private View[] vItems = new View[5];

    //==========Variables in this Activity==========
    // -- Variables --
    private Handler mHandler = new Handler();

    // Request Current Playing Media Url
    private BaseAudioListFrag mFragMedias;

    // Media Button Controller
    private MediaBtnController mMediaBtnController;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ReverseReceiver.register(this);
        AccReceiver.register(this);
        MediaBtnReceiver.setListener(this);
        SettingsSysUtil.setAudioState(this, 1);
        setContentView(R.layout.scl_lc2010_vdc_activity_audio_list);
        PlayerAppManager.putCxt(PlayerCxtFlag.MUSIC_LIST, this);
        init();
    }

    @Override
    protected void init() {
        super.init();
        //Variables
        mMediaBtnController = new MediaBtnController(this);

        // -- Widgets --
        // Switch items
        vItems[0] = findViewById(R.id.v_my_favorite);
        vItems[0].setOnClickListener(mFilterViewOnClick);

        vItems[1] = findViewById(R.id.v_folder);
        vItems[1].setOnClickListener(mFilterViewOnClick);

        vItems[2] = findViewById(R.id.v_music_name);
        vItems[2].setOnClickListener(mFilterViewOnClick);

        vItems[3] = findViewById(R.id.v_artist);
        vItems[3].setOnClickListener(mFilterViewOnClick);

        vItems[4] = findViewById(R.id.v_album);
        vItems[4].setOnClickListener(mFilterViewOnClick);

        ivRhythmAnim = (ImageView) findViewById(R.id.v_rate);
        ivRhythmAnim.setOnClickListener(mFilterViewOnClick);

        //
        switchTab(vItems[2], true);
        bindAndCreatePlayService(1, 2);
    }

    private void loadFragment(int idx) {
        //Remove Old
        if (mFragMedias != null) {
            FragUtil.removeV4Fragment(mFragMedias, getSupportFragmentManager());
        }

        //Load New
        switch (idx) {
            case 0:
                mFragMedias = new SclLc2010VdcAudioCollectsFrag();
                break;
            case 1:
                mFragMedias = new SclLc2010VdcAudioFoldersFrag();
                break;
            case 2:
                mFragMedias = new SclLc2010VdcAudioNamesFrag();
                break;
            case 3:
                mFragMedias = new SclLc2010VdcAudioArtistsFrag();
                break;
            case 4:
                mFragMedias = new SclLc2010VdcAudioAlbumsFrag();
                break;
        }
        FragUtil.loadV4Fragment(R.id.layout_frag, mFragMedias, getSupportFragmentManager());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaBtnController.register();
        if (isPlaying()) {
            activeAnimRhythm(true);
        } else if (!isPauseByUser()) {
            resume();
        }
    }

    @Override
    protected void onPlayServiceConnected(Service service) {
        super.onPlayServiceConnected(service);
        setPlayListener(this);
        loadLocalMedias();
    }

    @Override
    public void onPlayFromFolder(int playPos, List<String> listPlayPaths) {
    }

    @Override
    public void onPlayFromFolder(Intent data) {
    }

    @Override
    protected void loadLocalMedias() {
        super.loadLocalMedias();
        CommonUtil.cancelTask(mLoadLocalMediasTask);
        mLoadLocalMediasTask = new LoadLocalMediasTask(new LoadMediaListener() {

            @Override
            public void afterLoaded() {
                if (EmptyUtil.isEmpty(mListPrograms)) {
                    loadSDCardMedias();
                } else {
                    refreshDatas();
                    loadMediaImage();
                }
            }
        });
        mLoadLocalMediasTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void loadSDCardMedias() {
        super.loadSDCardMedias();
        CommonUtil.cancelTask(mLoadSDCardMediasTask);
        mLoadSDCardMediasTask = new LoadSDCardMediasTask(new LoadMediaListener() {

            @Override
            public void afterLoaded() {
                if (EmptyUtil.isEmpty(mListPrograms)) {
                    notifyScanMedias(true);
                } else {
                    refreshDatas();
                    loadMediaImage();
                }
            }
        });
        mLoadSDCardMediasTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void loadMediaImage() {
        super.loadMediaImage();
        CommonUtil.cancelTask(mLoadMediaImageTask);
        mLoadMediaImageTask = new LoadMediaImageTask(new LoadImgListener() {

            @Override
            public void postRefresh(ProAudio program, boolean isLoadEnd) {
            }
        });
        mLoadMediaImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void refreshOnNotifyLoading(MediaScanReceiver.MediaScanActives loadingFlag) {
        super.refreshOnNotifyLoading(loadingFlag);
//        if (loadingFlag == MediaScanReceiver.ScanActives.START) {
//			showLctLm8917Loading(true);
//        } else if (loadingFlag == MediaScanReceiver.ScanActives.TASK_CANCEL) {
//			showLctLm8917Loading(false);
//        }
    }

    @Override
    protected void refreshPageOnScan(List<ProAudio> listScannedAudios, boolean isScanned) {
        super.refreshPageOnScan(listScannedAudios, isScanned);
//        if (EmptyUtil.isEmpty(mListPrograms)) {
        //showLctLm8917Loading(false);
//        } else {
        Logs.i(TAG, "refreshPageOnScan() -> [Size:" + mListPrograms.size() + " ; isScanEnd:" + isScanned);
        loadLocalMedias();
//        }
    }

    @Override
    protected void refreshPageOnClear(Set<String> allSdMountedPaths) {
        super.refreshPageOnClear(allSdMountedPaths);
        if (EmptyUtil.isEmpty(mListPrograms)) {
            release();
            //showLctLm8917Loading(false);
        } else {
            String currPlayerUrl = getCurrMediaPath();
            if (!EmptyUtil.isEmpty(currPlayerUrl)) {
                boolean isCurrPlayingExist = false;
                for (String path : allSdMountedPaths) {
                    if (currPlayerUrl.startsWith(path)) {
                        isCurrPlayingExist = true;
                        break;
                    }
                }
                if (!isCurrPlayingExist) {
                    pause();
                }
            }

            // Refresh And Continue Play
            refreshDatas();
        }
    }

    /**
     * 刷新列表-媒体
     */
    private void refreshDatas() {
        // 刷新媒体列表
        if (mFragMedias != null) {
            mFragMedias.refreshDatas(mListPrograms, getLastMediaPath());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        cancelOpenPlayer();
        return super.dispatchTouchEvent(ev);
    }

    private View.OnClickListener mFilterViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == ivRhythmAnim) {
                if (isPlaying()) {
                    openPlayerActivity(getLastMediaPath(), getListMedias());
                }
            } else {
                switchTab(v, true);
            }
        }
    };

    public void openPlayerActivity(String mediaUrl, List<?> listPrograms) {
        try {
            Intent playerIntent = new Intent(this, SclLc2010VdcAudioPlayerActivity.class);
            playerIntent.putExtra("SELECT_MEDIA_URL", mediaUrl);
            playerIntent.putExtra("MEDIA_LIST", (Serializable) listPrograms);
            startActivityForResult(playerIntent, 1);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "openPlayerActivity()", e);
        }
    }

    private void switchTab(View v, boolean isFromUser) {
        //Switch
        final int loop = vItems.length;
        for (int idx = 0; idx < loop; idx++) {
            View item = vItems[idx];
            if (item == v) {
                if (idx == 2) {
                    notifyScanMedias(true);
                }
                item.setFocusable(true);
                item.requestFocus();
                setBg(item, true);
                loadFragment(idx);
            } else {
                item.setFocusable(false);
                item.clearFocus();
                setBg(item, false);
            }
        }
    }

    private void setBg(View v, boolean selected) {
        if (selected) {
            v.setBackgroundResource(R.drawable.bg_title_item_c);
        } else {
            v.setBackgroundResource(R.drawable.btn_collect_selector);
        }
    }

    @Override
    public void onGotMediaKeyCode(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            KeyEnum key = KeyEnum.getKey(event.getKeyCode());
            switch (key) {
                case KEYCODE_MEDIA_PREVIOUS:
                    if (!isForeground()) {
                        playPrevBySecurity();
                        if (mFragMedias != null) {
                            mFragMedias.prev();
                        }
                    }
                    break;
                case KEYCODE_MEDIA_NEXT:
                    if (!isForeground()) {
                        playNextBySecurity();
                        if (mFragMedias != null) {
                            mFragMedias.next();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onGetKeyCode(KeyEnum key) {
        switch (key) {
            case KEYCODE_MEDIA_PREVIOUS:
                if (isForeground()) {
                    playPrevBySecurity();
                    if (mFragMedias != null) {
                        mFragMedias.prev();
                    }
                }
                break;
            case KEYCODE_MEDIA_NEXT:
                if (isForeground()) {
                    playNextBySecurity();
                    if (mFragMedias != null) {
                        mFragMedias.next();
                    }
                }
                break;

            case KEYCODE_DPAD_LEFT:
                if (mFragMedias != null) {
                    mFragMedias.prev();
                }
                break;
            case KEYCODE_DPAD_RIGHT:
                if (mFragMedias != null) {
                    mFragMedias.next();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String flag = data.getStringExtra("flag");
            if ("PLAYER_FINISH_ON_CLICK_LIST".equals(flag)) {
                refreshOnPlayerFinish();
            } else if ("PLAYER_FINISH_ON_GET_KEY".equals(flag)) {
                refreshOnPlayerFinish();
                autoOpenPlayer();
            }
        }
    }

    private void refreshOnPlayerFinish() {
        Log.i(TAG, "refreshOnPlayerFinish()");
        if (mFragMedias instanceof SclLc2010VdcAudioNamesFrag) {
            mListPrograms = getListMedias();
            mFragMedias.refreshDatas(mListPrograms, getLastMediaPath());
        }
    }

    @Override
    public List<ProAudio> getListMedias() {
//        return super.getListMedias();
        return mListPrograms;
    }

    @Override
    public void onAccOn() {
        resume();
    }

    @Override
    public void onAccOff() {
        pause();
    }

    @Override
    public void onAccOffTrue() {
    }

    @Override
    public void onReverseOn() {
        pause();
    }

    @Override
    public void onReverseOff() {
        resume();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (mFragMedias != null) {
            int backRes = mFragMedias.onBackPressed();
            switch (backRes) {
                case 1:
                    break;
                default:
                    cancelOpenPlayer();
                    moveTaskToBack(true);
                    break;
            }
        }
    }

    @Override
    public void onPlayStateChanged(PlayState playState) {
        super.onPlayStateChanged(playState);
        Log.i(TAG, "playState -> " + playState);
        switch (playState) {
            case PLAY:
            case PREPARED:
                activeAnimRhythm(true);
                if (mFragMedias != null) {
                    mFragMedias.refreshDatas(getCurrMediaPath());
                }
                break;
            default:
                activeAnimRhythm(false);
                break;
        }
    }

    private void activeAnimRhythm(boolean isActive) {
        Drawable drawable = ivRhythmAnim.getDrawable();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animDrawable = (AnimationDrawable) drawable;
            if (isActive) {
                if (!animDrawable.isRunning()) {
                    animDrawable.start();
                }
            } else {
                if (animDrawable.isRunning()) {
                    animDrawable.stop();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        //
        mMediaBtnController.unregister();
        MediaBtnReceiver.setListener(null);

        //
        removePlayListener(this);
        AccReceiver.unregister(this);
        ReverseReceiver.unregister(this);
        cancelAllTasks();
        mHandler.removeCallbacksAndMessages(null);
        bindAndCreatePlayService(3, 4);
        SettingsSysUtil.setAudioState(this, 0);
        PlayerAppManager.removeCxt(PlayerCxtFlag.MUSIC_LIST);
        super.onDestroy();
    }

    /**
     * Demand : Automatically open player after 5s.
     */
    private void autoOpenPlayer() {
        if (isPlaying()) {
            cancelOpenPlayer();
            Log.i(TAG, "autoOpenPlayer() -EXEC-");
            mHandler.postDelayed(mmDelayOpenAudioRunnable, 5000);
        }
    }

    private void cancelOpenPlayer() {
        Log.i(TAG, "autoOpenPlayer() -CANCEL-");
        mHandler.removeCallbacks(mmDelayOpenAudioRunnable);
    }

    private Runnable mmDelayOpenAudioRunnable = new Runnable() {
        @Override
        public void run() {
            if (mFragMedias != null) {
                mFragMedias.playSelectMedia(getLastMediaPath());
            }
        }
    };

    @Override
    public void switchPlayMode(int supportFlag) {
    }

    @Override
    public boolean isPlayEnable() {
        return false;
    }

    @Override
    public void onAudioFocusDuck() {

    }

    @Override
    public void onAudioFocusTransient() {

    }

    @Override
    public void onAudioFocusGain() {

    }

    @Override
    public void onAudioFocusLoss() {
        mMediaBtnController.unregister();
    }
}