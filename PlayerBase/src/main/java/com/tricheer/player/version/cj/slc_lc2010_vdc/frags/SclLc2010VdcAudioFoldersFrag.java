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
import android.widget.ImageView;
import android.widget.ListView;

import com.js.sidebar.LetterSideBar;
import com.tricheer.player.R;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioListActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.adapter.BaseAudioAdapter;
import com.tricheer.player.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcAudioFoldersAdapter;
import com.tricheer.player.version.cj.slc_lc2010_vdc.bean.AudioFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.FrameAnimationController;
import js.lib.android.utils.Logs;

/**
 * SCL_LC2010_VDC - Music collects fragment
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioFoldersFrag extends BaseAudioListFrag {
    // TAG
    private static final String TAG = "MusicFoldersFrag";


    //==========Widgets in this Fragment==========
    private View contentV;
    private ListView lvData;
    private ImageView ivLoading;
    private LetterSideBar lsb;

    //==========Variables in this Fragment==========
    private SclLc2010VdcAudioListActivity mAttachedActivity;

    /**
     * Is ListView Item is Clicking
     */
    private boolean mIsLvItemClicking = false;

    /**
     * Media list
     */
    private List<?> mListDatas;
    private List<AudioFilter> mListFilters;

    /**
     * Data adapter
     */
    private SclLc2010VdcAudioFoldersAdapter mDataAdapter;

    // ImageView frame animation control
    private FrameAnimationController mFrameAnimController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAttachedActivity = (SclLc2010VdcAudioListActivity) activity;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentV = inflater.inflate(R.layout.scl_lc2010_vdc_activity_audio_list_frag_artists, null);
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
        if (mAttachedActivity.isMediaScanning()) {
            onMediaScanningStart();
        }

        //ListView
        mDataAdapter = new SclLc2010VdcAudioFoldersAdapter(mAttachedActivity, 0);
        mDataAdapter.setCollectListener(new CollectBtnCallback());

        lvData = (ListView) contentV.findViewById(R.id.lv_datas);
        lvData.setSelector(mAttachedActivity.getImgResId("bg_audio_item_selector"));
        lvData.setAdapter(mDataAdapter);
        lvData.setOnItemClickListener(new LvItemClick());
        loadDataList();
    }

    @Override
    public void loadDataList() {
        if (!isAdded()) {
            return;
        }

        //Check if second priority page
        if (!EmptyUtil.isEmpty(mListDatas)) {
            Object item = mListDatas.get(0);
            // 防止刷新导致二级界面跳转到一级界面
            if (item instanceof ProAudio) {
                Log.i(TAG, "### Current page is Folders-2222 list page ####");
                return;
            }
            Log.i(TAG, "### Current page is Folders-1111 list page ####");
        }

        //Check NULL
        String targetMediaUrl = mAttachedActivity.getLastMediaPath();
        List<ProAudio> listSrcMedias = mAttachedActivity.getListSrcMedias();
        if (listSrcMedias == null) {
            return;
        }

        //Filter collected
        Map<String, AudioFilter> mapDatas = new HashMap<>();
        for (ProAudio media : listSrcMedias) {
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
            AudioFilter audioFilter = mapDatas.get(folderPath);
            if (audioFilter == null) {
                audioFilter = new AudioFilter();
                audioFilter.folderPath = folderPath;
                audioFilter.folderPathPinYin = media.mediaDirectoryPinYin;
                audioFilter.listMedias = new ArrayList<>();
                audioFilter.listMedias.add(media);
                mapDatas.put(audioFilter.folderPath, audioFilter);
            } else {
                audioFilter.listMedias.add(media);
            }

            //
            if (!audioFilter.isSelected) {
                audioFilter.isSelected = TextUtils.equals(targetMediaUrl, media.mediaUrl);
            }
        }

        //Refresh UI
        refreshFilters((mListFilters = new ArrayList<>(mapDatas.values())));
    }

    private void refreshFilters(List<AudioFilter> listFilters) {
        if (!EmptyUtil.isEmpty(listFilters)) {
            mListDatas = listFilters;
            AudioFilter.sortByFolder((List<AudioFilter>) mListDatas);
        }
        mDataAdapter.refreshData(mListDatas, mAttachedActivity.getLastMediaPath());
    }

    @Override
    public void refreshDataList() {
        if (isAdded()) {
            mDataAdapter.refreshData();
        }
    }

    @Override
    public void refreshPlaying(String targetMediaUrl) {
        if (isAdded() && mListDatas != null) {
            for (Object obj : mListDatas) {
                if (obj instanceof AudioFilter) {
                    AudioFilter filter = (AudioFilter) obj;
                    if (filter.listMedias != null) {
                        for (ProAudio media : filter.listMedias) {
                            filter.isSelected = TextUtils.equals(targetMediaUrl, media.mediaUrl);
                            if (filter.isSelected) {
                                break;
                            }
                        }
                    }
                    if (filter.isSelected) {
                        break;
                    }
                } else {
                    break;
                }
            }
            mDataAdapter.refreshData(targetMediaUrl);
        }
    }

    @Override
    public void selectNext() {
        if (isAdded()) {
            int nextPos = mDataAdapter.getNextPos();
            mDataAdapter.refreshData(nextPos);
            lvData.setSelection(nextPos);
        }
    }

    @Override
    public void selectPrev() {
        if (isAdded()) {
            int prevPos = mDataAdapter.getPrevPos();
            mDataAdapter.refreshData(prevPos);
            lvData.setSelection(prevPos);
        }
    }

    @Override
    public void playSelected() {
    }

    @Override
    public void playSelected(String mediaUrl) {
        try {
            Object obj = mListDatas.get(0);
            if (obj instanceof ProAudio) {
                mAttachedActivity.openPlayerActivity(mediaUrl, (List<ProAudio>) mListDatas);
            }
        } catch (Exception e) {
            Log.i(TAG, "playSelectMedia> " + e.getMessage());
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
            int sectionPos = mDataAdapter.getPositionForSection(letter.charAt(0));
            if (sectionPos != -1) {
                Logs.i(TAG, "LetterSideBarCallback -> callback(" + pos + "," + letter + "-" + sectionPos + ")");
                lvData.setSelection(sectionPos);
            }
        }
    }

    /**
     * ListView Item Click Event
     */
    private class LvItemClick implements AdapterView.OnItemClickListener {

        private Handler mmHandler = new Handler();

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Logs.i(TAG, "LvItemClick -> onItemClick(AdapterView," + position + ",id)");
            final Object objItem = parent.getItemAtPosition(position);
            if (objItem == null) {
                return;
            }

            // Play Select MediaUrl
            if (objItem instanceof AudioFilter) {
                AudioFilter item = (AudioFilter) objItem;
                mListDatas = item.listMedias;
                mDataAdapter.refreshData(mListDatas);

                //Click Media
            } else if (objItem instanceof ProAudio) {
                if (mIsLvItemClicking) {
                    mIsLvItemClicking = false;
                    Logs.i(TAG, "##### ---Forbidden click because of frequency !!!--- #####");
                    return;
                } else {
                    mIsLvItemClicking = true;
                    mmHandler.removeCallbacksAndMessages(null);
                    mmHandler.postDelayed(mmDelayResetClickingFlagRunnable, 1000);
                }

                ProAudio program = (ProAudio) objItem;
                mAttachedActivity.openPlayerActivity(program.mediaUrl, (List<ProAudio>) mListDatas);
            }
        }

        private Runnable mmDelayResetClickingFlagRunnable = new Runnable() {

            @Override
            public void run() {
                mIsLvItemClicking = false;
            }
        };
    }

    private class CollectBtnCallback implements BaseAudioAdapter.CollectListener {
        @Override
        public void onClickCollectBtn(ImageView ivCollect, int pos) {
            Object item = mDataAdapter.getItem(pos);
            if (item instanceof ProAudio) {
                ProAudio media = (ProAudio) item;
                switch (media.isCollected) {
                    case 0:
                        media.isCollected = 1;
                        media.updateTime = System.currentTimeMillis();
                        AudioDBManager.instance().updateMediaCollect(media);
                        ivCollect.setImageResource(R.drawable.favor_c);
                        break;
                    case 1:
                        media.isCollected = 0;
                        media.updateTime = System.currentTimeMillis();
                        AudioDBManager.instance().updateMediaCollect(media);
                        ivCollect.setImageResource(R.drawable.favor_c_n);
                        break;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAttachedActivity.onActivityResult(requestCode, resultCode, data);
        mDataAdapter.refreshData(mAttachedActivity.getLastMediaPath());
    }

    @Override
    public int onBackPressed() {
        if (EmptyUtil.isEmpty(mListDatas)) {
            return 0;
        }

        Object objItem = mListDatas.get(0);
        if (objItem instanceof ProAudio) {
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
        if (isAdded()) {
            updateThemeCommon();
        }
    }

    @Override
    public void updateThemeToIos() {
        if (isAdded()) {
            updateThemeCommon();
        }
    }

    private void updateThemeCommon() {
        lvData.setSelector(mAttachedActivity.getImgResId("bg_audio_item_selector"));
    }
}