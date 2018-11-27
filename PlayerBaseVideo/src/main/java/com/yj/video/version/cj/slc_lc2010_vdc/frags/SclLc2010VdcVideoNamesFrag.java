package com.yj.video.version.cj.slc_lc2010_vdc.frags;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.js.sidebar.LetterSideBar;
import com.yj.video.R;
import com.yj.video.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoListActivity;
import com.yj.video.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoPlayerActivity;
import com.yj.video.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcVideoNamesAdapter;

import java.io.Serializable;
import java.util.List;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.FrameAnimationController;
import js.lib.android.utils.Logs;

/**
 * SCL_LC2010_VDC - Video names fragment
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcVideoNamesFrag extends BaseVideoListFrag {
    // TAG
    private static final String TAG = "VideoNamesFrag";

    //==========Widgets in this Fragment==========
    private View contentV;
    /**
     * Grid view for list videos
     */
    private GridView gvData;
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
    private GvItemClick mGvItemClick;

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
        contentV = inflater.inflate(R.layout.scl_lc2010_vdc_activity_video_list_frag_names, null);
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
        showLoading(mAttachedActivity.isMediaScanning());

        // Data
        mDataAdapter = new SclLc2010VdcVideoNamesAdapter(mAttachedActivity);
        gvData = (GridView) contentV.findViewById(R.id.v_data);
        gvData.setAdapter(mDataAdapter);
        gvData.setOnItemClickListener((mGvItemClick = new GvItemClick()));
        gvData.setOnScrollListener(new GvOnScroll());
        refreshData(mAttachedActivity.getListMedias(), mAttachedActivity.getLastMediaPath());
    }

    @Override
    public void refreshData() {
        if (isAdded()) {
            mDataAdapter.refreshData();
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
            mHandler.removeCallbacksAndMessages(null);
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
        showLoading(true);
    }

    @Override
    public void onMediaScanningNew() {
        Log.i(TAG, "onMediaScanningNew()");
        if (EmptyUtil.isEmpty(mListMedias)) {
            showLoading(true);
        }
    }

    @Override
    public void onMediaScanningEnd() {
        Log.i(TAG, "onMediaScanningEnd()");
    }

    @Override
    public void onMediaParseEnd() {
        Log.i(TAG, "onMediaScanningEnd()");
        showLoading(false);
    }

    @Override
    public void onMediaScanningCancel() {
        Log.i(TAG, "onMediaScanningCancel()");
//        if (isAdded()) {
//            mFrameAnimController.stop();
//            ivLoading.setVisibility(View.INVISIBLE);
//        }
    }

    @Override
    public void showLoading(boolean isShow) {
        if (isAdded()) {
            if (isShow) {
                lsb.setVisibility(View.INVISIBLE);
                ivLoading.setVisibility(View.VISIBLE);
                mFrameAnimController.start();
            } else {
                lsb.setVisibility(View.VISIBLE);
                ivLoading.setVisibility(View.INVISIBLE);
                mFrameAnimController.stop();
            }
        }
    }

    private class LetterSideBarCallback implements LetterSideBar.LetterSideBarListener {
        @Override
        public void callback(int pos, String letter) {
            Logs.i(TAG, "LetterSideBarCallback -> callback(" + pos + "," + letter + ")");
            int sectionPos = mDataAdapter.getPositionForSection(letter.charAt(0));
            Logs.i(TAG, "sectionPos:" + sectionPos);
            if (sectionPos != -1) {
                gvData.setSelection(getPageFirstPos(sectionPos));
            }
        }

        @Override
        public void onScroll(boolean isScrolling) {
            Log.i(TAG, "LetterSideBarCallback - onScroll(" + isScrolling + ")");
            mDataAdapter.setScrollState(isScrolling);
        }
    }

    /**
     * GridView Item Click Event
     */
    private class GvItemClick implements AdapterView.OnItemClickListener {

        private Handler mmHandler = new Handler();

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Logs.i(TAG, "GvItemClick -> onItemClick(AdapterView," + position + ",id)");
            execItemClick(parent.getItemAtPosition(position));
        }

        private Runnable mmDelayResetClickingFlagRunnable = new Runnable() {

            @Override
            public void run() {
                mIsClicking = false;
            }
        };

        private void execItemClick(Object objItem) {
            if (objItem == null) {
                return;
            }

            if (mIsClicking) {
                mIsClicking = false;
                Logs.i(TAG, "##### ---Forbidden click because of frequency !!!--- #####");
                return;
            } else {
                mIsClicking = true;
                mmHandler.removeCallbacksAndMessages(null);
                mmHandler.postDelayed(mmDelayResetClickingFlagRunnable, 1000);
            }

            //
            ProVideo item = (ProVideo) objItem;
            Logs.i(TAG, "LvItemClick -> onItemClick ----Just Play----");
            openVideoPlayerActivity(item.mediaUrl, mListMedias);
        }
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
    public void selectNext() {
        if (isAdded()) {
            int nextPos;
            int currPos = mDataAdapter.getSelectPos();
            if (currPos == -1) {
                nextPos = mAttachedActivity.getCurrIdx();
            } else {
                nextPos = mDataAdapter.getNextPos();
            }
            Log.i(TAG, "nextPos~" + nextPos);
            mDataAdapter.select(nextPos);
            gvData.setSelection(getPageFirstPos(nextPos));
        }
    }

    @Override
    public void selectPrev() {
        if (isAdded()) {
            int prevPos;
            int currPos = mDataAdapter.getSelectPos();
            if (currPos == -1) {
                prevPos = mAttachedActivity.getCurrIdx();
            } else {
                prevPos = mDataAdapter.getPrevPos();
            }
            Log.i(TAG, "prevPos~" + prevPos);
            mDataAdapter.select(prevPos);
            gvData.setSelection(getPageFirstPos(prevPos));
        }
    }

    @Override
    public void playSelected() {
        if (isAdded()) {
            int selectPos = mDataAdapter.getSelectPos();
            Log.i(TAG, "playSelected() > selectPos:" + selectPos);
            mGvItemClick.execItemClick(mDataAdapter.getItem(selectPos));
        }
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

    @Override
    public void updateThemeToDefault() {
        Log.i(TAG, "updateThemeToDefault()");
    }

    @Override
    public void updateThemeToIos() {
        Log.i(TAG, "updateThemeToIos()");
    }

    private class GvOnScroll implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.i(TAG, "GvOnScroll - onScrollStateChanged(AbsListView," + scrollState + ")");
            switch (scrollState) {
                //停止滚动
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    mDataAdapter.setScrollState(false);
                    break;
                //滚动做出了抛的动作
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    mDataAdapter.setScrollState(true);
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    mDataAdapter.setScrollState(true);
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    }
}