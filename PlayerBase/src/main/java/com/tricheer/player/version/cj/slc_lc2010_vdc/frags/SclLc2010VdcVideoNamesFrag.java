package com.tricheer.player.version.cj.slc_lc2010_vdc.frags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.tricheer.player.R;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoListActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoPlayerActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcVideoNamesAdapter;

import java.io.Serializable;
import java.util.List;

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

    //==========Variables in this Fragment==========
    private SclLc2010VdcVideoListActivity mAttachedActivity;

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
        // Data
        mDataAdapter = new SclLc2010VdcVideoNamesAdapter(mAttachedActivity, 0);
        gvDatas = (GridView) contentV.findViewById(R.id.v_datas);
        gvDatas.setAdapter(mDataAdapter);
        gvDatas.setOnItemClickListener(new GvItemClick());
        refreshDatas(mAttachedActivity.getListMedias(), mAttachedActivity.getLastPath());
    }

    @Override
    public void refreshDatas() {
        if (isAdded()) {
            mDataAdapter.refreshDatas();
        }
    }

    @Override
    public void refreshDatas(List<ProVideo> listMedias) {
        if (isAdded()) {
            mListMedias = listMedias;
            mDataAdapter.refreshDatas(mListMedias);
        }
    }

    @Override
    public void refreshDatas(List<ProVideo> listMedias, String targetMediaUrl) {
        if (isAdded()) {
            mListMedias = listMedias;
            mDataAdapter.refreshDatas(mListMedias, targetMediaUrl);
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
        switch (requestCode) {
//            case M_REQ_PLAYING_MEDIA_URL:
//                mDataAdapter.refreshDatas(mAttachedActivity.getLastPath());
//                break;
        }
    }

    @Override
    public void prev() {
        mDataAdapter.refreshDatas(mDataAdapter.getPrevPos());
    }

    @Override
    public void next() {
        mDataAdapter.refreshDatas(mDataAdapter.getNextPos());
    }
}