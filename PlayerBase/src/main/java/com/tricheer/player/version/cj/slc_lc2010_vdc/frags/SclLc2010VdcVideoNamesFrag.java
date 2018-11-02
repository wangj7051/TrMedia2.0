package com.tricheer.player.version.cj.slc_lc2010_vdc.frags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.js.sidebar.LetterSideBar;
import com.tricheer.player.R;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoListActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoPlayerActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcVideoNamesAdapter;

import java.io.Serializable;
import java.util.List;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.FrameAnimationController;
import js.lib.android.utils.Logs;

public class SclLc2010VdcVideoNamesFrag extends BaseVideoListFrag {
    // TAG
    private static final String TAG = "VideoNamesFrag";

    //==========Widgets in this Fragment==========
    private View contentV;
    /**
     * Grid view for list videos
     */
    private GridView gvDatas;
    private ImageView ivLoading;
    private LetterSideBar lsb;

    //==========Variables in this Fragment==========
    private SclLc2010VdcVideoListActivity mAttachedActivity;
    private static Handler mHandler = new Handler();

    // Data Adapter
    private SclLc2010VdcVideoNamesAdapter mDataAdapter;

    /**
     * Is ListView Item is Clicking
     */
    private boolean mIsClicking = false;

    /**
     * Request Current Playing Media Url
     */
    protected final int M_REQ_PLAYING_MEDIA_URL = 1;

    /**
     * Media list
     */
    private List<ProVideo> mListMedias;

    // ImageView frame animation control
    private FrameAnimationController mFrameAnimController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAttachedActivity = (SclLc2010VdcVideoListActivity) activity;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentV = inflater.inflate(R.layout.scl_lc2010_vdc_activity_video_frag_names, null);
        return contentV;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        //----Widgets----
        //Side bar
        lsb = (LetterSideBar) contentV.findViewById(R.id.lsb);
        lsb.refreshLetters(null);
        lsb.addCallback(new LetterSideBarCallback());

        //
        ivLoading = (ImageView) contentV.findViewById(R.id.iv_loading);
        mFrameAnimController = new FrameAnimationController();
        mFrameAnimController.setIv(ivLoading);
        mFrameAnimController.setFrameImgResArr(LOADING_RES_ID_ARR);
        //TODO if (mAttachedActivity.isMediaScanning()) {
        //TODO onMediaScanningStart();
        //TODO }

        // Data
        mDataAdapter = new SclLc2010VdcVideoNamesAdapter(mAttachedActivity, 0);
        gvDatas = (GridView) contentV.findViewById(R.id.v_datas);
        gvDatas.setAdapter(mDataAdapter);
        gvDatas.setOnItemClickListener(new GvItemClick());
        refreshData(mAttachedActivity.getListMedias(), mAttachedActivity.getLastMediaPath());
    }

    @Override
    public void refreshData() {
        if (isAdded()) {
            mDataAdapter.refreshData();
        }
    }

    @Override
    public void refreshData(List<ProVideo> listMedias) {
        if (isAdded()) {
            mListMedias = listMedias;
            mDataAdapter.refreshData(mListMedias);
        }
    }

    @Override
    public void refreshData(List<ProVideo> listMedias, String targetMediaUrl) {
        if (isAdded()) {
            mListMedias = listMedias;
            mDataAdapter.refreshData(mListMedias, targetMediaUrl);
            autoPlayHistory();
        }
    }

    public void autoPlayHistory() {
        mHandler.removeCallbacksAndMessages(null);
        if (isAdded()) {
            if (EmptyUtil.isEmpty(mListMedias) || !mAttachedActivity.isAutoPlay()) {
                return;
            }

            //Refresh ListView
            String targetMediaPath = mAttachedActivity.getLastTargetMediaPath();
            if (TextUtils.isEmpty(targetMediaPath)) {
                ProVideo first = mListMedias.get(0);
                mDataAdapter.refreshData(mListMedias, first.mediaUrl);
            } else {
                mDataAdapter.refreshData(mListMedias, targetMediaPath);
            }

            //Delay Play
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    play();
                }
            }, 500);
        }
    }

    @Override
    public void play() {
        if (!mAttachedActivity.isWarningShowing()) {
            openVideoPlayerActivity(mAttachedActivity.getLastMediaPath(), mListMedias);
        }
    }

    @Override
    public void onMediaScanningStart() {
        Log.i(TAG, "onMediaScanningStart()");
        if (isAdded()) {
            lsb.setVisibility(View.INVISIBLE);
            ivLoading.setVisibility(View.VISIBLE);
            mFrameAnimController.start();
        }
    }

    @Override
    public void onMediaScanningEnd() {
        Log.i(TAG, "onMediaScanningEnd()");
        if (isAdded()) {
            lsb.setVisibility(View.VISIBLE);
            ivLoading.setVisibility(View.INVISIBLE);
            mFrameAnimController.stop();
        }
    }

    @Override
    public void onMediaScanningCancel() {
        Log.i(TAG, "onMediaScanningCancel()");
        if (isAdded()) {
            mFrameAnimController.stop();
            ivLoading.setVisibility(View.INVISIBLE);
        }
    }

    private class LetterSideBarCallback implements LetterSideBar.LetterSideBarListener {
        @Override
        public void callback(int pos, String letter) {
            Logs.i(TAG, "LetterSideBarCallback -> callback(" + pos + "," + letter + ")");
            gvDatas.setSelection(pos);
        }
    }

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
                    openVideoPlayerActivity(item.mediaUrl, mListMedias);
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

    protected void openVideoPlayerActivity(String mediaUrl, List<ProVideo> listPrograms) {
        try {
            if (EmptyUtil.isEmpty(listPrograms)) {
                return;
            }
            Intent playerIntent = new Intent(mAttachedActivity, SclLc2010VdcVideoPlayerActivity.class);
            playerIntent.putExtra("SELECT_MEDIA_URL", mediaUrl);
            playerIntent.putExtra("MEDIA_LIST", (Serializable) listPrograms);
            startActivityForResult(playerIntent, M_REQ_PLAYING_MEDIA_URL);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "openVideoPlayerActivity()", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String flag = data.getStringExtra("flag");
            if ("EXIT_VIDEO_PLAYER".equals(flag)) {
                mDataAdapter.refreshData(mAttachedActivity.getLastMediaPath());
            }
        }
    }

    @Override
    public void next() {
        if (isAdded()) {
            int nextPos = mDataAdapter.getNextPos();
            mDataAdapter.refreshData(nextPos);
            gvDatas.setSelection(nextPos);
        }
    }

    @Override
    public void prev() {
        if (isAdded()) {
            int prevPos = mDataAdapter.getPrevPos();
            mDataAdapter.refreshData(prevPos);
            gvDatas.setSelection(prevPos);
        }
    }

    @Override
    public void playSelected() {
    }

    @Override
    public int onBackPressed() {
        return 0;
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}