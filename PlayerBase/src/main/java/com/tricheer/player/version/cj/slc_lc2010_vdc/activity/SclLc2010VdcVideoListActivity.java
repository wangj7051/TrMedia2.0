package com.tricheer.player.version.cj.slc_lc2010_vdc.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.tri.lib.engine.KeyEnum;
import com.tricheer.player.R;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.utils.PlayerPreferUtils;
import com.tricheer.player.version.base.activity.video.BaseVideoKeyEventActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.BaseVideoListFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcVideoFoldersFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcVideoNamesFrag;

import java.util.List;
import java.util.Set;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.FragUtil;
import js.lib.android.utils.Logs;

/**
 * SLC_LC2010_VDC Video List Activity
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcVideoListActivity extends BaseVideoKeyEventActivity {
    // TAG
    private static final String TAG = "VideoListActivityImpl";

    /**
     * ==========Widgets in this Activity==========
     */
    private View[] vItems = new View[2];


    //==========Variables in this Activity==========
    // Request Current Playing Media Url
    private BaseVideoListFrag mFragMedias;

    /**
     * Request Current Playing Media Url
     */
    protected final int M_REQ_WARNING = 2;

    private boolean mIsWarningShowing = false;
    private static Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.scl_lc2010_vdc_activity_video_list);
        PlayerAppManager.putCxt(PlayerCxtFlag.VIDEO_LIST, this);
        init();
    }

    @Override
    protected void init() {
        super.init();
        //
        vItems[0] = findViewById(R.id.v_media_name);
        vItems[0].setOnClickListener(mFilterViewOnClick);

        vItems[1] = findViewById(R.id.v_folder);
        vItems[1].setOnClickListener(mFilterViewOnClick);

        //
        showWarning();
        loadFragment(0);
        loadLocalMedias();
    }

    private void showWarning() {
        int flag = PlayerPreferUtils.getVideoWarningFlag(false, 0);
        switch (flag) {
            case 1:
                mIsWarningShowing = true;
                Intent warningIntent = new Intent(this, SclLc2010VdcVideoWarningActivity.class);
                startActivityForResult(warningIntent, M_REQ_WARNING);
                break;
            case 2:
                break;
        }
    }

    private void loadFragment(int idx) {
        //Remove Old
        if (mFragMedias != null) {
            FragUtil.removeV4Fragment(mFragMedias, getSupportFragmentManager());
        }

        //Load New
        switch (idx) {
            case 0:
                mFragMedias = new SclLc2010VdcVideoNamesFrag();
                break;
            case 1:
                mFragMedias = new SclLc2010VdcVideoFoldersFrag();
                break;
        }
        if (mFragMedias != null) {
            FragUtil.loadV4Fragment(R.id.layout_frag, mFragMedias, getSupportFragmentManager());
        }
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
                    refreshDatas();
                    loadMediaImage();
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
                    refreshDatas();
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
        mLoadMediaImageTask.execute(mListPrograms, new LoadImgListener() {

            @Override
            public void afterLoad() {
                if (mFragMedias != null) {
                    mFragMedias.refreshDatas();
                }
            }
        });
    }

    @Override
    protected void refreshOnNotifyLoading(int loadingFlag) {
        super.refreshOnNotifyLoading(loadingFlag);
//        if (loadingFlag == MediaScanReceiver.ScanActives.START) {
        //showLctLm8917Loading(true);
//        } else if (loadingFlag == MediaScanReceiver.ScanActives.END || loadingFlag == MediaScanReceiver.ScanActives.TASK_CANCEL) {
        //showLctLm8917Loading(false);
//        }
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

    public List<ProVideo> getListMedias() {
        return mListPrograms;
    }

    private void refreshDatas() {
        //sbLetters.refreshLetters(mListSortLetters.toArray());
        if (mFragMedias != null) {
            mFragMedias.refreshDatas(mListPrograms, getLastMediaPath());
        }
    }

    private View.OnClickListener mFilterViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switchTab(v, true);
        }
    };

    private void switchTab(View v, boolean isFromUser) {
        //Load new data
        if (isFromUser) {
            notifyScanMedias(true);
        }

        //Switch TAB
        final int loop = vItems.length;
        for (int idx = 0; idx < loop; idx++) {
            View item = vItems[idx];
            if (item == v) {
                item.requestFocus();
                setBg(item, true);
                loadFragment(idx);
            } else {
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
    public void onGetKeyCode(KeyEnum key) {
        switch (key) {
            case KEYCODE_PREV:
                if (mFragMedias != null) {
                    mFragMedias.prev();
                }
                break;
            case KEYCODE_NEXT:
                if (mFragMedias != null) {
                    mFragMedias.next();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String flag = data.getStringExtra("flag");
            if ("EXIT_WARNING".equals(flag)) {
                mIsWarningShowing = false;
                if (mFragMedias instanceof SclLc2010VdcVideoNamesFrag) {
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFragMedias.play();
                        }
                    }, 800);
                }
            }
        }
    }

    public boolean isWarningShowing() {
        return mIsWarningShowing;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
        onIDestroy();
    }

    @Override
    protected void onIDestroy() {
        super.onIDestroy();
        PlayerAppManager.removeCxt(PlayerCxtFlag.VIDEO_LIST);
    }

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

    }
}
