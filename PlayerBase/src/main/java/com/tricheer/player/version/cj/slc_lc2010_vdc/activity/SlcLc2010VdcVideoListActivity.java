package com.tricheer.player.version.cj.slc_lc2010_vdc.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.tricheer.player.R;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.engine.Keys;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.receiver.MediaScanReceiver;
import com.tricheer.player.version.base.activity.video.BaseKeyEventActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.adapter.SlcLc2010VdcVideoListAdapter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * SLC_LC2010_VDC Video List Activity
 *
 * @author Jun.Wang
 */
public class SlcLc2010VdcVideoListActivity extends BaseKeyEventActivity {
    // TAG
    private static final String TAG = "VideoListActivityImpl";

    /**
     * ==========Widgets in this Activity==========
     */
    private View vMediaName, vFolder;
    /**
     * Grid view for list videos
     */
    private GridView gvDatas;

    /**
     * ==========Variables in this Activity==========
     */
    // Data Adapter
    private SlcLc2010VdcVideoListAdapter mDataAdapter;

    /**
     * Is ListView Item is Clicking
     */
    private boolean mIsClicking = false;

    /**
     * Request Current Playing Media Url
     */
    protected final int M_REQ_PLAYING_MEDIA_URL = 1;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.slc_lc2010_vdc_activity_video_list);
        PlayerAppManager.putCxt(PlayerCxtFlag.VIDEO_LIST, this);
        init();
    }

    @Override
    protected void init() {
        super.init();
        //
        vMediaName = findViewById(R.id.v_media_name);
        vMediaName.setOnClickListener(mFilterViewOnClick);

        vFolder = findViewById(R.id.v_folder);
        vFolder.setOnClickListener(mFilterViewOnClick);

        // Data
        mDataAdapter = new SlcLc2010VdcVideoListAdapter(mContext, 0);
        gvDatas = (GridView) findViewById(R.id.v_datas);
        gvDatas.setAdapter(mDataAdapter);
        gvDatas.setOnItemClickListener(new GvItemClick());

        //
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
        Log.i(TAG, "loadLocalMedias()");
        CommonUtil.cancelTask(mLoadLocalMediasTask);
        mLoadLocalMediasTask = new LoadLocalMediasTask();
        mLoadLocalMediasTask.execute(new LoadMediaListener() {

            @Override
            public void afterLoad(String selectMediaUrl) {
                Log.i(TAG, "loadLocalMedias() ->afterLoad(" + selectMediaUrl + ")");
                if (EmptyUtil.isEmpty(mListPrograms)) {
                    loadSDCardMedias();
                } else {
                    mDataAdapter.refreshDatas(mListPrograms);
                    loadMediaImage();
                    // doPlayAfterLoad();
                }
            }

            @Override
            public void refreshUI() {
            }
        });
    }

    @Override
    protected void loadSDCardMedias() {
        Log.i(TAG, "loadSDCardMedias()");
        CommonUtil.cancelTask(mLoadSDCardMediasTask);
        mLoadSDCardMediasTask = new LoadSDCardMediasTask(null, new LoadMediaListener() {

            @Override
            public void afterLoad(String selectMediaUrl) {
                Log.i(TAG, "loadSDCardMedias() ->afterLoad(" + selectMediaUrl + ")");
                if (EmptyUtil.isEmpty(mListPrograms)) {
                    notifyScanMedias(true);
                } else {
                    mDataAdapter.refreshDatas(mListPrograms);
                    loadMediaImage();
                }
            }

            @Override
            public void refreshUI() {
            }
        });
        mLoadSDCardMediasTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void loadMediaImage() {
        CommonUtil.cancelTask(mLoadMediaImageTask);
        mLoadMediaImageTask = new LoadMediaImageTask();
        mLoadMediaImageTask.execute(mListPrograms, new LoadImgListner() {

            @Override
            public void afterLoad() {
                mDataAdapter.refreshDatas();
            }
        });
    }

    @Override
    protected void refreshOnNotifyLoading(int loadingFlag) {
        super.refreshOnNotifyLoading(loadingFlag);
        if (loadingFlag == MediaScanReceiver.ScanActives.START) {
            //showLctLm8917Loading(true);
        } else if (loadingFlag == MediaScanReceiver.ScanActives.END || loadingFlag == MediaScanReceiver.ScanActives.TASK_CANCEL) {
            //showLctLm8917Loading(false);
        }
    }

    @Override
    protected void refreshPageOnScan(List<ProVideo> listScannedVideos, boolean isScaned) {
        super.refreshPageOnScan(listScannedVideos, isScaned);
        if (!EmptyUtil.isEmpty(listScannedVideos)) {
            Logs.i(TAG, "refreshPageOnScan() -> [VideoSize:" + listScannedVideos.size() + " ; isScaned:" + isScaned);
            if (isScaned) {
                loadLocalMedias();
            } else {
                refreshDatas();
            }
        }
    }

    @Override
    protected void refreshPageOnClear(Set<String> allSdMountedPaths) {
        super.refreshPageOnClear(allSdMountedPaths);
        if (EmptyUtil.isEmpty(mListPrograms)) {
            clearPlayInfo();
        } else {
            refreshDatas();
        }
    }

    private void refreshDatas() {
        String lastMediaUrl = getLastPath();
        //sbLetters.refreshLetters(mListSortLetters.toArray());
        mDataAdapter.refreshDatas(mListPrograms, lastMediaUrl);
        // Scroll
        if (!EmptyUtil.isEmpty(lastMediaUrl)) {
            //delayJumpToSelectedItem();
        }
    }

    private View.OnClickListener mFilterViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switchFilter(v);
            notifyScanMedias(true);
        }

        private void switchFilter(View v) {
            if (v == vMediaName) {
                vMediaName.setBackgroundResource(R.drawable.bg_title_item_c);
            } else {
                vMediaName.setBackgroundResource(R.drawable.btn_collect_selector);
            }

            if (v == vFolder) {
                vFolder.setBackgroundResource(R.drawable.bg_title_item_c);
            } else {
                vFolder.setBackgroundResource(R.drawable.btn_collect_selector);
            }
        }
    };

    /**
     * GridView Item Click Event
     */
    private class GvItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!mIsClicking) {
                mIsClicking = true;
                mHandler.postDelayed(mmDelayResetClickingFlagRunnable, 1000);

                // Play Select MediaUrl
                Logs.i(TAG, "LvItemClick -> onItemClick(" + position + ")");
                final Object objItem = parent.getItemAtPosition(position);
                if (objItem != null) {
                    ProVideo item = (ProVideo) objItem;
                    Logs.i(TAG, "LvItemClick -> onItemClick ----Just Play----");
                    playSelectMedia(item.mediaUrl, false);
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
                        openVideoPlayerActivity(mediaUrl, mListPrograms);
                    }
                }, 300);
            } else {
                openVideoPlayerActivity(mediaUrl, mListPrograms);
            }
        }
    }

    protected void openVideoPlayerActivity(String mediaUrl, List<ProVideo> listPrograms) {
        try {
            Intent playerIntent = new Intent(mContext, SlcLc2010VdcVideoPlayerActivity.class);
            playerIntent.putExtra("SELECT_MEDIA_URL", mediaUrl);
            playerIntent.putExtra("MEDIA_LIST", (Serializable) listPrograms);
            startActivityForResult(playerIntent, M_REQ_PLAYING_MEDIA_URL);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "openVideoPlayerActivity()", e);
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
                break;
            case Keys.KeyVals.KEYCODE_NEXT:
                mDataAdapter.refreshDatas(mDataAdapter.getNextPos());
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
        PlayerAppManager.removeCxt(PlayerCxtFlag.VIDEO_LIST);
    }
}
