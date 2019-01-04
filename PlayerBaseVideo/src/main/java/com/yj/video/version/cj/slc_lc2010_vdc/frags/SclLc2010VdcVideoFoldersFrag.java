package com.yj.video.version.cj.slc_lc2010_vdc.frags;

import android.annotation.SuppressLint;
import android.app.Activity;
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

    /**
     * Child fragment- [Folders or Names]
     */
    private BaseVideoFolderGroupsFrag fragChild;

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

        //Loading page
        Log.i(TAG, "init() -loadLocalMedias-");
        loadLocalMedias();
    }

    @Override
    public void loadLocalMedias() {
        Log.i(TAG, "loadLocalMedias()");
        refreshData();
    }

    @Override
    public void refreshData() {
        if (!isAdded()) {
            return;
        }

        Log.i(TAG, "refreshData()");
        //设置媒体源数据
        List<ProVideo> listMedias = mAttachedActivity.getListMedias();
        if (EmptyUtil.isEmpty(listMedias)) {
            mListData = new ArrayList<>();
        } else {
            mListData = new ArrayList<>(listMedias);
            String targetMediaUrl = mAttachedActivity.getLastTargetMediaPath();

            //Filter
            showLoading(false);
            Map<String, VideoFilter> mapFilters = new HashMap<>();
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
                VideoFilter videoFilter = mapFilters.get(folderPath);
                if (videoFilter == null) {
                    videoFilter = new VideoFilter();
                    videoFilter.folderPath = folderPath;
                    videoFilter.folderPathPinYin = media.mediaDirectoryPinYin;
                    videoFilter.listMedias = new ArrayList<>();
                    videoFilter.listMedias.add(media);
                    mapFilters.put(videoFilter.folderPath, videoFilter);
                } else {
                    videoFilter.listMedias.add(media);
                }

                //
                if (!videoFilter.isSelected) {
                    videoFilter.isSelected = TextUtils.equals(targetMediaUrl, media.mediaUrl);
                }
            }
            refreshFilters((mListFilters = new ArrayList<>(mapFilters.values())));
        }
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
        if (fragChild != null) {
            FragUtil.removeV4Fragment(fragChild, getChildFragmentManager());
        }

        //Load
        fragChild = new SclLc2010VdcVideoFolderFoldersFrag();
        fragChild.setListData(listFilters);
        fragChild.setListener(this);
        FragUtil.loadV4ChildFragment(R.id.rl_frag, fragChild, getChildFragmentManager());
    }

    public void loadFragNames(List<ProVideo> listMedias) {
        //Remove
        if (fragChild != null) {
            FragUtil.removeV4Fragment(fragChild, getChildFragmentManager());
        }

        //Load
        fragChild = new SclLc2010VdcVideoFolderNamesFrag();
        fragChild.setListData(listMedias);
        fragChild.setListener(this);
        FragUtil.loadV4ChildFragment(R.id.rl_frag, fragChild, getChildFragmentManager());
    }

    @Override
    public void onMediaScanningStart() {
        Log.i(TAG, "onMediaScanningStart()");
        showLoading(true);
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
            if (fragChild != null) {
                fragChild.onSidebarCallback(pos, letter);
            }
        }

        @Override
        public void onTouchDown() {
        }

        @Override
        public void onTouchMove() {
        }

        @Override
        public void onTouchUp() {
        }
    }

    @Override
    public void selectNext() {
        if (isAdded()) {
            if (fragChild != null) {
                fragChild.selectNext();
            }
        }
    }

    @Override
    public void selectPrev() {
        if (isAdded()) {
            if (fragChild != null) {
                fragChild.selectPrev();
            }
        }
    }

    @Override
    public void playSelected() {
        if (isAdded()) {
            if (fragChild != null) {
                fragChild.playSelected();
            }
        }
    }

    @Override
    public int onBackPressed() {
        if (EmptyUtil.isEmpty(mListData)) {
            return 0;
        }

        if (fragChild instanceof SclLc2010VdcVideoFolderNamesFrag) {
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
        if (fragChild != null) {
            fragChild.updateThemeToDefault();
        }
    }

    @Override
    public void updateThemeToIos() {
        Log.i(TAG, "updateThemeToIos()");
        if (fragChild != null) {
            fragChild.updateThemeToIos();
        }
    }
}
