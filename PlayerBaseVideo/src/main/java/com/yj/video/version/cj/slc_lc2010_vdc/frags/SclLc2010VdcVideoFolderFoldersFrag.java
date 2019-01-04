package com.yj.video.version.cj.slc_lc2010_vdc.frags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yj.video.R;
import com.yj.video.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoListActivity;
import com.yj.video.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcVideoFoldersAdapter;
import com.yj.video.version.cj.slc_lc2010_vdc.bean.VideoFilter;

import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.utils.Logs;

public class SclLc2010VdcVideoFolderFoldersFrag extends BaseVideoFolderGroupsFrag {
    // TAG
    private static final String TAG = "VideoFolderFoldersFrag";

    //==========Widgets in this Fragment==========
    private View contentV;

    /**
     * List view for video folders
     */
    private ListView lvData;

    //==========Variables in this Fragment==========
    /**
     * Attached activity of this fragment.
     */
    private SclLc2010VdcVideoListActivity mAttachedActivity;

    /**
     * Data list adapter
     */
    private SclLc2010VdcVideoFoldersAdapter mDataAdapter;

    /**
     * Is ListView Item is Clicking
     */
    private boolean mIsClicking = false;
    /**
     * ListView item click event
     */
    private LvItemClick mLvItemClick;

    /**
     * Media list
     */
    private List<VideoFilter> mListData;

    @SuppressWarnings("unchecked")
    @Override
    protected void setListData(List<?> listData) {
        Log.i(TAG, "setListData(" + listData + ")");
        if (listData == null) {
            mListData = new ArrayList<>();
        } else {
            mListData = (List<VideoFilter>) listData;
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
        contentV = inflater.inflate(R.layout.scl_lc2010_vdc_activity_video_list_frag_folder_folders, container, false);
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

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        // Data
        mDataAdapter = new SclLc2010VdcVideoFoldersAdapter(mAttachedActivity);
        lvData = (ListView) contentV.findViewById(R.id.lv_data);
        lvData.setAdapter(mDataAdapter);
        lvData.setOnItemClickListener((mLvItemClick = new LvItemClick()));

        //
        updateThemeCommon();
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
    }

    @Override
    public void refreshPlaying(String mediaUrl) {
        if (isAdded()) {
            Log.i(TAG, "refreshPlaying(" + mediaUrl + ")");
            int playingPos = 0;
            int currPos = mDataAdapter.getSelectPos();
            if (currPos == -1) {
                playingPos = getPlayingPosInGroupList(mAttachedActivity.getLastMediaPath(), mListData);
            }
            mDataAdapter.select(playingPos);
            lvData.setSelection(getLvPageFirstPos(playingPos));
        }
    }

    @Override
    public void selectPrev() {
        if (isAdded()) {
            int prevPos;
            int currPos = mDataAdapter.getSelectPos();
            if (currPos == -1) {
                prevPos = getPlayingPosInGroupList(mAttachedActivity.getLastMediaPath(), mListData);
                if (prevPos == -1) {
                    prevPos = 0;
                }
            } else {
                prevPos = mDataAdapter.getPrevPos();
            }
            Log.i(TAG, "selectPrev() - prevPos:" + prevPos);
            mDataAdapter.select(prevPos);
            lvData.setSelection(getLvPageFirstPos(prevPos));
        }
    }

    @Override
    public void selectNext() {
        if (isAdded()) {
            int nextPos;
            int currPos = mDataAdapter.getSelectPos();
            if (currPos == -1) {
                nextPos = getPlayingPosInGroupList(mAttachedActivity.getLastMediaPath(), mListData);
                if (nextPos == -1) {
                    nextPos = 0;
                }
            } else {
                nextPos = mDataAdapter.getNextPos();
            }
            Log.i(TAG, "selectPrev() - prevPos:" + nextPos);
            mDataAdapter.select(nextPos);
            lvData.setSelection(getLvPageFirstPos(nextPos));
        }
    }

    @Override
    public void playSelected() {
        Log.i(TAG, "playSelected()");
        if (isAdded()) {
            int selectPos = mDataAdapter.getSelectPos();
            Log.i(TAG, "playSelected() > selectPos:" + selectPos);
            mLvItemClick.execItemClick(mDataAdapter.getItem(selectPos));
        }
    }

    @Override
    public void onSidebarCallback(int pos, String letter) {
        Log.i(TAG, "onSidebarCallback(" + pos + "," + letter + ")");
        int sectionPos = mDataAdapter.getPositionForSection(letter.charAt(0));
        if (sectionPos != -1) {
            Logs.i(TAG, "sectionPos:" + sectionPos);
            lvData.setSelection(getLvPageFirstPos(sectionPos));
        }
    }

    /**
     * ListView item click event
     */
    @SuppressWarnings("unchecked")
    private class LvItemClick implements AdapterView.OnItemClickListener {
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
            VideoFilter item = (VideoFilter) objItem;
            if (mFragParent != null) {
                mFragParent.loadFragNames(item.listMedias);
            }
        }

        private void destroy() {
            mmHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        if (mLvItemClick != null) {
            mLvItemClick.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void updateThemeToDefault() {
        Log.i(TAG, "updateThemeToDefault()");
        if (isAdded()) {
            updateThemeCommon();
        }
    }

    @Override
    public void updateThemeToIos() {
        Log.i(TAG, "updateThemeToIos()");
        if (isAdded()) {
            updateThemeCommon();
        }
    }

    private void updateThemeCommon() {
        lvData.setDivider(mAttachedActivity.getDrawable(mAttachedActivity.getImgResId("separate_line_h")));
        lvData.setSelector(mAttachedActivity.getImgResId("bg_audio_item_selector"));
    }
}
