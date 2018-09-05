package com.tricheer.player.version.cj.slc_lc2010_vdc.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tricheer.player.R;
import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.engine.Keys;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.receiver.MediaScanReceiver;
import com.tricheer.player.utils.SettingsSysUtil;
import com.tricheer.player.version.base.activity.music.BaseKeyEventActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.adapter.SlcLc2010VdcMusicListAdapter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * SLC_LC2010_VDC Music List Activity
 *
 * @author Jun.Wang
 */
public class SlcLc2010VdcMusicListActivity extends BaseKeyEventActivity {
    // TAG
    private static final String TAG = "MusicListActivity";

    /**
     * ==========Variable in this Activity==========
     */
    // -- Variables --
    private static Handler mHandler = new Handler();

    // Request Current Playing Media Url
    protected final int M_REQ_PLAYING_MEDIA_URL = 1;

    /**
     * Is ListView Item is Clicking
     */
    private boolean mIsClicking = false;

    /**
     * Data adapter
     */
    private SlcLc2010VdcMusicListAdapter mDataAdapter;

    // -- Widgets --
    private ListView lvDatas;
    private View vMyFavor, vFolder, vMediaName, vArtist, vAlbum;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SettingsSysUtil.setMusicState(this, 1);
        setContentView(R.layout.slc_lc2010_vdc_activity_music_list);
        PlayerAppManager.putCxt(PlayerCxtFlag.MUSIC_LIST, this);
        init();
    }

    @Override
    protected void init() {
        super.init();
        // -- Widgets --
        // Switch items
        vMyFavor = findViewById(R.id.v_my_favorite);
        vFolder = findViewById(R.id.v_folder);
        vMediaName = findViewById(R.id.v_music_name);
        vArtist = findViewById(R.id.v_artist);
        vAlbum = findViewById(R.id.v_album);
        switchFilter(vMediaName);

        vMyFavor.setOnClickListener(mFilterViewOnClick);
        vFolder.setOnClickListener(mFilterViewOnClick);
        vMediaName.setOnClickListener(mFilterViewOnClick);
        vArtist.setOnClickListener(mFilterViewOnClick);
        vAlbum.setOnClickListener(mFilterViewOnClick);

        //ListView
        mDataAdapter = new SlcLc2010VdcMusicListAdapter(mContext, 0);
        lvDatas = findView(R.id.lv_datas);
        lvDatas.setAdapter(mDataAdapter);
        lvDatas.setOnItemClickListener(new LvItemClick());

        //
        bindAndCreatePlayService(1, 2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPlaying()) {
            Log.i(TAG, "----Delay reopen player----");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "----Delay reopen player--EXEC--");
                    playSelectMedia(getLastPath(), false);
                }
            }, 5000);
        }
    }

    @Override
    protected void onPlayServiceConnected() {
        super.onPlayServiceConnected();
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
        if (loadingFlag == MediaScanReceiver.ScanActives.START) {
//			showLctLm8917Loading(true);
        } else if (loadingFlag == MediaScanReceiver.ScanActives.TASK_CANCEL) {
//			showLctLm8917Loading(false);
        }
    }

    @Override
    protected void refreshPageOnScan(List<ProMusic> listScannedAudios, boolean isScaned) {
        super.refreshPageOnScan(listScannedAudios, isScaned);
        if (EmptyUtil.isEmpty(mListPrograms)) {
            //showLctLm8917Loading(false);
        } else {
            Logs.i(TAG, "refreshPageOnScan() -> [VideoSize:" + mListPrograms.size() + " ; isScanEnd:" + isScaned);
            if (isScaned) {
                loadLocalMedias();
            } else {
                //showLctLm8917Loading(false);
                refreshDatas();
            }
        }
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

    /**
     * 刷新列表-媒体
     */
    private void refreshDatas() {
        // 刷新媒体列表
        mDataAdapter.refreshDatas(mListPrograms, getLastPath());
        //sbLetters.refreshLetters(mMediaListSortLetters.toArray());
    }

    private View.OnClickListener mFilterViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switchFilter(v);
            notifyScanMedias(true);
        }
    };

    private void switchFilter(View v) {
        if (v == vMyFavor) {
            vMyFavor.setBackgroundResource(R.drawable.bg_title_item_c);
        } else {
            vMyFavor.setBackgroundResource(R.drawable.btn_collect_selector);
        }

        if (v == vFolder) {
            vFolder.setBackgroundResource(R.drawable.bg_title_item_c);
        } else {
            vFolder.setBackgroundResource(R.drawable.btn_collect_selector);
        }

        if (v == vMediaName) {
            vMediaName.setBackgroundResource(R.drawable.bg_title_item_c);
        } else {
            vMediaName.setBackgroundResource(R.drawable.btn_collect_selector);
        }

        if (v == vArtist) {
            vArtist.setBackgroundResource(R.drawable.bg_title_item_c);
        } else {
            vArtist.setBackgroundResource(R.drawable.btn_collect_selector);
        }

        if (v == vAlbum) {
            vAlbum.setBackgroundResource(R.drawable.bg_title_item_c);
        } else {
            vAlbum.setBackgroundResource(R.drawable.btn_collect_selector);
        }
    }

    /**
     * ListView Item Click Event
     */
    private class LvItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!mIsClicking) {
                mIsClicking = true;
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(mmDelayResetClickingFlagRunnable, 1000);

                // Play Select MediaUrl
                Logs.i(TAG, "LvItemClick -> onItemClick(AdapterView," + position + ",id)");
                final Object objItem = parent.getItemAtPosition(position);
                if (objItem != null) {
                    ProMusic program = (ProMusic) objItem;
                    playSelectMedia(program.mediaUrl, false);
                }
            }
        }

        private Runnable mmDelayResetClickingFlagRunnable = new Runnable() {

            @Override
            public void run() {
                mIsClicking = false;
            }
        };
    }

    protected void playSelectMedia(final String mediaUrl, boolean isNeedDelay) {
        if (!EmptyUtil.isEmpty(mediaUrl)) {
            if (isNeedDelay) {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        openPlayerActivity(mediaUrl, mListPrograms);
                    }
                }, 300);
            } else {
                openPlayerActivity(mediaUrl, mListPrograms);
            }
        }
    }

    protected void openPlayerActivity(String mediaUrl, List<ProMusic> listPrograms) {
        try {
            Intent playerIntent = new Intent(mContext, SlcLc2010VdcMusicPlayerActivity.class);
            playerIntent.putExtra("SELECT_MEDIA_URL", mediaUrl);
            playerIntent.putExtra("MEDIA_LIST", (Serializable) listPrograms);
            startActivityForResult(playerIntent, M_REQ_PLAYING_MEDIA_URL);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "openPlayerActivity()", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == M_REQ_PLAYING_MEDIA_URL) {
            mDataAdapter.refreshDatas(getLastPath());
        }
    }

    @Override
    public void onGetKeyCode(int keyCode) {
        switch (keyCode) {
            case Keys.KeyVals.KEYCODE_PREV:
                mDataAdapter.refreshDatas(mDataAdapter.getPrevPos());
                playPrevBySecurity();
                break;
            case Keys.KeyVals.KEYCODE_NEXT:
                mDataAdapter.refreshDatas(mDataAdapter.getNextPos());
                playNextBySecurity();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onIDestroy();
    }

    @Override
    protected void onIDestroy() {
        super.onIDestroy();
        mHandler.removeCallbacksAndMessages(null);
        bindAndCreatePlayService(3, 4);
        SettingsSysUtil.setMusicState(this, 0);
        PlayerAppManager.removeCxt(PlayerCxtFlag.MUSIC_LIST);
    }
}