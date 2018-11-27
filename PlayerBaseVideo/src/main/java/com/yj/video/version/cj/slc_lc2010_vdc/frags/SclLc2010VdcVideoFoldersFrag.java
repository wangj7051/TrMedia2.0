package com.yj.video.version.cj.slc_lc2010_vdc.frags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.js.sidebar.LetterSideBar;
import com.yj.video.R;
import com.yj.video.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcVideoListActivity;
import com.yj.video.version.cj.slc_lc2010_vdc.bean.VideoFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.FragUtil;
import js.lib.android.utils.FrameAnimationController;
import js.lib.android.utils.Logs;

/**
 * SCL_LC2010_VDC - Video folders fragment
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcVideoFoldersFrag extends BaseVideoListFrag {
    // TAG
    private static final String TAG = "VideoFoldersFrag";

    //==========Widgets in this Fragment==========
    private View contentV;
    private ImageView ivLoading;
    private LetterSideBar lsb;

    //==========Variables in this Fragment==========
    private SclLc2010VdcVideoListActivity mAttachedActivity;

    /**
     * Request Current Playing Media Url
     */
    protected final int M_REQ_PLAYING_MEDIA_URL = 1;

    /**
     * Media list
     */
    private List<ProVideo> mListData;
    private List<VideoFilter> mListFilters;

    // ImageView frame animation control
    private FrameAnimationController mFrameAnimController;

    /**
     * Child fragment- [Folders or Names]
     */
    private BaseVideoFolderGroupsFrag mFragChild;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAttachedActivity = (SclLc2010VdcVideoListActivity) activity;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentV = inflater.inflate(R.layout.scl_lc2010_vdc_activity_video_list_frag_folders, null);
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
        lsb.setVisibility(View.VISIBLE);

        //
        ivLoading = (ImageView) contentV.findViewById(R.id.iv_loading);
        mFrameAnimController = new FrameAnimationController();
        mFrameAnimController.setIv(ivLoading);
        mFrameAnimController.setFrameImgResArr(LOADING_RES_ID_ARR);
        showLoading(mAttachedActivity.isMediaScanning());

        // Data
        refreshData(mAttachedActivity.getListMedias(), mAttachedActivity.getLastMediaPath());
    }

    @Override
    public void refreshData() {
        if (isAdded()) {
            if (mFragChild != null) {
                mFragChild.refreshData();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void refreshData(List<ProVideo> listMedias, String targetMediaUrl) {
        if (!isAdded() || EmptyUtil.isEmpty(listMedias)) {
            return;
        }

        //
        mListData = listMedias;

        //Check if second priority page
        if (mFragChild instanceof SclLc2010VdcVideoFolderNamesFrag) {
            Log.i(TAG, "### Current page is folders-2222 list page ####");
            return;
        }
        Log.i(TAG, "### Current page is folders-1111 list page ####");

        //Filter collected
        Map<String, VideoFilter> mapData = new HashMap<>();
        for (ProVideo media : listMedias) {
            //Folder
            String folderPath = "";
            File file = new File(media.mediaUrl);
            if (file.exists()) {
                File parentFile = file.getParentFile();
                if (parentFile != null) {
                    folderPath = parentFile.getPath();
                }
            }

            //
            VideoFilter videoFilter = mapData.get(folderPath);
            if (videoFilter == null) {
                videoFilter = new VideoFilter();
                videoFilter.folderPath = folderPath;
                videoFilter.folderPathPinYin = media.mediaDirectoryPinYin;
                videoFilter.listMedias = new ArrayList<>();
                videoFilter.listMedias.add(media);
                mapData.put(videoFilter.folderPath, videoFilter);
            } else {
                videoFilter.listMedias.add(media);
            }

            //
            if (!videoFilter.isSelected) {
                videoFilter.isSelected = TextUtils.equals(targetMediaUrl, media.mediaUrl);
            }
        }

        //Refresh UI
        refreshFilters((mListFilters = new ArrayList<>(mapData.values())));
    }

    @SuppressWarnings("unchecked")
    private void refreshFilters(List<VideoFilter> listFilters) {
        if (!EmptyUtil.isEmpty(listFilters)) {
            VideoFilter.sortByFolder(listFilters);
        }
        loadFragFolders(listFilters);
    }

    private void loadFragFolders(List<VideoFilter> listFilters) {
        //Remove
        if (mFragChild != null) {
            FragUtil.removeV4Fragment(mFragChild, getChildFragmentManager());
        }

        //Load
        mFragChild = new SclLc2010VdcVideoFolderFoldersFrag();
        mFragChild.setListData(listFilters);
        mFragChild.setListener(this);
        FragUtil.loadV4ChildFragment(R.id.rl_frag, mFragChild, getChildFragmentManager());
    }

    public void loadFragNames(List<ProVideo> listMedias) {
        //Remove
        if (mFragChild != null) {
            FragUtil.removeV4Fragment(mFragChild, getChildFragmentManager());
        }

        //Load
        mFragChild = new SclLc2010VdcVideoFolderNamesFrag();
        mFragChild.setListData(listMedias);
        mFragChild.setListener(this);
        FragUtil.loadV4ChildFragment(R.id.rl_frag, mFragChild, getChildFragmentManager());
    }

    @Override
    public void play() {
    }

    @Override
    public void onMediaScanningStart() {
        Log.i(TAG, "onMediaScanningStart()");
        showLoading(true);
    }

    @Override
    public void onMediaScanningNew() {
        Log.i(TAG, "onMediaScanningNew()");
        if (EmptyUtil.isEmpty(mListData)) {
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
            if (mFragChild != null) {
                mFragChild.onSidebarCallback(pos, letter);
            }
        }

        @Override
        public void onScroll(boolean isScrolling) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case M_REQ_PLAYING_MEDIA_URL:
                if (mFragChild != null) {
                    mFragChild.refreshPlaying(mAttachedActivity.getLastMediaPath());
                }
                break;
        }
    }

    @Override
    public void selectNext() {
        if (isAdded()) {
            if (mFragChild != null) {
                mFragChild.selectNext();
            }
        }
    }

    @Override
    public void selectPrev() {
        if (isAdded()) {
            if (mFragChild != null) {
                mFragChild.selectPrev();
            }
        }
    }

    @Override
    public void playSelected() {
        if (isAdded()) {
            if (mFragChild != null) {
                mFragChild.playSelected();
            }
        }
    }

    @Override
    public int onBackPressed() {
        if (EmptyUtil.isEmpty(mListData)) {
            return 0;
        }

        if (mFragChild instanceof SclLc2010VdcVideoFolderNamesFrag) {
            refreshFilters(mListFilters);
            return 1;
        } else {
            return 0;
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
