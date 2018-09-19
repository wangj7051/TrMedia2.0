package com.tricheer.player.version.cj.slc_lc2010_vdc.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.tricheer.player.R;
import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.engine.Keys;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.service.MusicPlayService;
import com.tricheer.player.utils.SettingsSysUtil;
import com.tricheer.player.version.base.activity.music.BaseAudioFocusActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.BaseAudioListFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioAlbumsFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioArtistsFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioCollectsFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioFoldersFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioNamesFrag;

import java.util.List;
import java.util.Set;

import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.FragUtil;
import js.lib.android.utils.Logs;

/**
 * SCL_LC2010_VDC Music List Activity
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioListActivity extends BaseAudioFocusActivity {
    // TAG
    private static final String TAG = "MusicListActivity";

    //==========Widgets in this Activity==========
    private ImageView ivRhythmAnim;

    //==========Variables in this Activity==========
    // -- Variables --
    private Handler mHandler = new Handler();

    // Request Current Playing Media Url
    private BaseAudioListFrag mFragMedias;

    // -- Widgets --
    private View[] vItems = new View[5];

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SettingsSysUtil.setMusicState(this, 1);
        setContentView(R.layout.scl_lc2010_vdc_activity_audio_list);
        PlayerAppManager.putCxt(PlayerCxtFlag.MUSIC_LIST, this);
        init();
    }

    @Override
    protected void init() {
        super.init();
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

        //
        loadFragment(2);
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
        //
        registerAudioFocus(1);
        autoOpenPlayer(true);
        //
        if (isPlaying()) {
            activeAnimRhythm();
        }
    }

    private void activeAnimRhythm() {
        Drawable drawable = ivRhythmAnim.getDrawable();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animDrawable = (AnimationDrawable) drawable;
            if (!animDrawable.isRunning()) {
                animDrawable.start();
            }
        }
    }

    @Override
    protected void onPlayServiceConnected(MusicPlayService service) {
        super.onPlayServiceConnected(service);
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
            public void postRefresh(ProMusic program, boolean isLoadEnd) {
            }
        });
        mLoadMediaImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void refreshOnNotifyLoading(int loadingFlag) {
        super.refreshOnNotifyLoading(loadingFlag);
//        if (loadingFlag == MediaScanReceiver.ScanActives.START) {
//			showLctLm8917Loading(true);
//        } else if (loadingFlag == MediaScanReceiver.ScanActives.TASK_CANCEL) {
//			showLctLm8917Loading(false);
//        }
    }

    @Override
    protected void refreshPageOnScan(List<ProMusic> listScannedAudios, boolean isScaned) {
        super.refreshPageOnScan(listScannedAudios, isScaned);
//        if (EmptyUtil.isEmpty(mListPrograms)) {
        //showLctLm8917Loading(false);
//        } else {
        Logs.i(TAG, "refreshPageOnScan() -> [Size:" + mListPrograms.size() + " ; isScanEnd:" + isScaned);
        if (isScaned) {
            loadLocalMedias();
        }
//        }
    }

    @Override
    protected void refreshPageOnClear(Set<String> allSdMountedPaths) {
        super.refreshPageOnClear(allSdMountedPaths);
        if (EmptyUtil.isEmpty(mListPrograms)) {
            release();
            //showLctLm8917Loading(false);
        } else {
            String currPlayerUrl = getPath();
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

    public List<ProMusic> getListMedias() {
        return mListPrograms;
    }

    /**
     * 刷新列表-媒体
     */
    private void refreshDatas() {
        // 刷新媒体列表
        if (mFragMedias != null) {
            mFragMedias.refreshDatas(mListPrograms, getLastPath());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        autoOpenPlayer(false);
        return super.dispatchTouchEvent(ev);
    }

    private View.OnClickListener mFilterViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Forbidden click the same item
            //TODO

            //
            notifyScanMedias(true);
            final int loop = vItems.length;
            for (int idx = 0; idx < loop; idx++) {
                View item = vItems[idx];
                if (item == v) {
                    setBg(item, true);
                    loadFragment(idx);
                } else {
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
    };

    @Override
    public void onGetKeyCode(int keyCode) {
        switch (keyCode) {
            case Keys.KeyVals.KEYCODE_PREV:
                playPrevBySecurity();
                if (mFragMedias != null) {
                    mFragMedias.prev();
                }
                break;
            case Keys.KeyVals.KEYCODE_NEXT:
                playNextBySecurity();
                if (mFragMedias != null) {
                    mFragMedias.next();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        autoOpenPlayer(false);
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        cancelAllTasks();
        mHandler.removeCallbacksAndMessages(null);
        bindAndCreatePlayService(3, 4);
        SettingsSysUtil.setMusicState(this, 0);
        PlayerAppManager.removeCxt(PlayerCxtFlag.MUSIC_LIST);
        super.onDestroy();
    }

    /**
     * Demand : Automatically open player after 5s.
     */
    private void autoOpenPlayer(boolean isExec) {
        if (isExec && isPlaying()) {
            mHandler.postDelayed(mmDelayOpenAudioRunnable, 5000);
        } else {
            mHandler.removeCallbacks(mmDelayOpenAudioRunnable);
        }
    }

    private Runnable mmDelayOpenAudioRunnable = new Runnable() {
        @Override
        public void run() {
            if (mFragMedias != null) {
                mFragMedias.playSelectMedia(getLastPath());
            }
        }
    };
}