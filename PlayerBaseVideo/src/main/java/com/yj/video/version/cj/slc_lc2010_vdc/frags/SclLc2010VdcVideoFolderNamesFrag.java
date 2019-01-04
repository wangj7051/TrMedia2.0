package com.yj.video.version.cj.slc_lc2010_vdc.frags;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.yj.video.R;
import com.yj.video.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoListActivity;
import com.yj.video.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoPlayerActivity;
import com.yj.video.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcVideoNamesAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.utils.Logs;

public class SclLc2010VdcVideoFolderNamesFrag extends BaseVideoFolderGroupsFrag {
    // TAG
    private static final String TAG = "VideoFolderNamesFrag";

    //==========Widgets in this Fragment==========
    private View contentV;

    /**
     * List view for video folders
     */
    private GridView gvData;

    //==========Variables in this Fragment==========
    private SclLc2010VdcVideoListActivity mAttachedActivity;
    // Data Adapter
    private SclLc2010VdcVideoNamesAdapter mDataAdapter;

    /**
     * Is ListView Item is Clicking
     */
    private boolean mIsClicking = false;
    /**
     * ListView item click event
     */
    private GvItemClick mGvItemClick;

    /**
     * Media list
     */
    private List<ProVideo> mListData;

    @SuppressWarnings("unchecked")
    @Override
    protected void setListData(List<?> listData) {
        Log.i(TAG, "setListData(" + listData + ")");
        if (listData == null) {
            mListData = new ArrayList<>();
        } else {
            mListData = (List<ProVideo>) listData;
        }
    }

    /**
     * Parent fragment.
     */
    private SclLc2010VdcVideoFoldersFrag mFragParent;

    @Override
    protected void setListener(SclLc2010VdcVideoFoldersFrag fragParent) {
        Log.i(TAG, "setListener(" + fragParent + ")");
        mFragParent = fragParent;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAttachedActivity = (SclLc2010VdcVideoListActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentV = inflater.inflate(R.layout.scl_lc2010_vdc_activity_video_list_frag_folder_names, container, false);
        return contentV;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        refreshPlaying(mAttachedActivity.getLastMediaPath());
    }

    private void init() {
        // Data
        mDataAdapter = new SclLc2010VdcVideoNamesAdapter(mAttachedActivity);
        gvData = (GridView) contentV.findViewById(R.id.gv_data);
        gvData.setAdapter(mDataAdapter);
        gvData.setOnItemClickListener((mGvItemClick = new GvItemClick()));
        loadDataList();
    }

    @SuppressWarnings("unchecked")
    private void loadDataList() {
        if (mListData == null) {
            mListData = new ArrayList<>();
        }
        mDataAdapter.refreshData(mListData, mAttachedActivity.getLastMediaPath());
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
            mDataAdapter.refreshData(listMedias);
        }
    }

    @Override
    public void refreshPlaying(String mediaUrl) {
        if (isAdded()) {
            Log.i(TAG, "refreshPlaying(" + mediaUrl + ")");
            int playingPos = getPlayingPosInMediaList(mAttachedActivity.getLastMediaPath(), mListData);
            mDataAdapter.select(playingPos);
            gvData.setSelection(getGvPageFirstPos(playingPos));
        }
    }

    @Override
    public void selectPrev() {
        int prevPos;
        int currPos = mDataAdapter.getSelectPos();
        if (currPos == -1) {
            prevPos = getPlayingPosInMediaList(mAttachedActivity.getLastMediaPath(), mListData);
            if (prevPos == -1) {
                prevPos = 0;
            }

        } else {
            prevPos = mDataAdapter.getPrevPos();
        }
        Log.i(TAG, "selectPrev() - prevPos:" + prevPos);
        mDataAdapter.select(prevPos);
        gvData.setSelection(getGvPageFirstPos(prevPos));
    }

    @Override
    public void selectNext() {
        int nextPos;
        int currPos = mDataAdapter.getSelectPos();
        if (currPos == -1) {
            nextPos = getPlayingPosInMediaList(mAttachedActivity.getLastMediaPath(), mListData);
            if (nextPos == -1) {
                nextPos = 0;
            }
        } else {
            nextPos = mDataAdapter.getNextPos();
        }
        Log.i(TAG, "selectNext() - nextPos:" + nextPos);
        mDataAdapter.select(nextPos);
        gvData.setSelection(getGvPageFirstPos(nextPos));
    }

    @Override
    public void playSelected() {
        Log.i(TAG, "playSelected()");
        if (isAdded()) {
            int selectPos = mDataAdapter.getSelectPos();
            Log.i(TAG, "playSelected() > selectPos:" + selectPos);
            mGvItemClick.execItemClick(mDataAdapter.getItem(selectPos));
        }
    }

    @Override
    public void onSidebarCallback(int pos, String letter) {
        Log.i(TAG, "onSidebarCallback(" + pos + "," + letter + ")");
        int sectionPos = mDataAdapter.getPositionForSection(letter.charAt(0));
        if (sectionPos != -1) {
            Logs.i(TAG, "sectionPos:" + sectionPos);
            gvData.setSelection(getGvPageFirstPos(sectionPos));
        }
    }

    /**
     * GridView Item Click Event
     */
    @SuppressWarnings("unchecked")
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
            ProVideo media = (ProVideo) objItem;
            mAttachedActivity.openVideoPlayerActivity(media.mediaUrl, mListData);
        }
    }

    @Override
    public void onDestroy() {
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
}
