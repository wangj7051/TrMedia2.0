package com.yj.audio.version.cj.slc_lc2010_vdc.frags;

import android.text.TextUtils;
import android.util.Log;

import com.yj.audio.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcAudioArtistAdapter;
import com.yj.audio.version.cj.slc_lc2010_vdc.bean.AudioFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.utils.EmptyUtil;

/**
 * SCL_LC2010_VDC - Music collects fragment
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioArtistsFrag extends BaseAudioGroupsFrag {
    // TAG
    private static final String TAG = "AudioArtistsFrag";

    @Override
    protected void initAdapter() {
        mDataAdapter = new SclLc2010VdcAudioArtistAdapter(mAttachedActivity);
        mDataAdapter.setCollectListener(new CollectBtnCallback());
    }

    @Override
    public void loadLocalMedias() {
        loadDataList();
    }

    @Override
    public void loadDataList() {
        if (!isAdded()) {
            return;
        }

        //Check if second priority page
        if (isAudioList(mListData)) {
            Log.i(TAG, "### Current page is Artists-2222 list page ####");
            return;
        }
        Log.i(TAG, "### Current page is Artists-1111 list page ####");

        //Check NULL
        String targetMediaUrl = mAttachedActivity.getLastMediaPath();
        List<ProAudio> listSrcMedias = mAttachedActivity.getListSrcMedias();
        if (listSrcMedias == null) {
            return;
        }

        //根据传入的参数，加载指定分组
        String targetArtist = null;
        AudioFilter targetAudioFilter = null;
        if (mParams != null && mParams.length > 0) {
            targetArtist = mParams[0];
            mParams = null;//参数只用一次
        }

        //Filter collected
        Map<String, AudioFilter> mapData = new HashMap<>();
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
            AudioFilter audioFilter = mapData.get(media.artist);
            if (audioFilter == null) {
                audioFilter = new AudioFilter();
                audioFilter.folderPath = folderPath;
                audioFilter.artist = media.artist;
                audioFilter.artistPinYin = media.artistPinYin;
                audioFilter.listMedias = new ArrayList<>();
                audioFilter.listMedias.add(media);
                mapData.put(media.artist, audioFilter);
            } else {
                audioFilter.listMedias.add(media);
            }

            //
            if (!audioFilter.isSelected) {
                audioFilter.isSelected = TextUtils.equals(targetMediaUrl, media.mediaUrl);
            }

            //Set target
            if (targetArtist != null && TextUtils.equals(targetArtist, audioFilter.artist)) {
                targetAudioFilter = audioFilter;
            }
        }

        //Refresh UI
        if (targetAudioFilter != null) {
            refreshMedias(targetAudioFilter);
        } else {
            refreshFilters((mListFilters = new ArrayList<>(mapData.values())));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void refreshMedias(AudioFilter audioFilter) {
        super.refreshMedias(audioFilter);
        if (audioFilter != null && mDataAdapter != null) {
            mDataAdapter.resetSelect();
            mDataAdapter.refreshData(audioFilter.listMedias);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void refreshFilters(List<AudioFilter> listFilters) {
        super.refreshFilters(listFilters);
        if (!EmptyUtil.isEmpty(listFilters)) {
            mListData = listFilters;
            AudioFilter.sortByArtist((List<AudioFilter>) mListData);
        }
        mDataAdapter.refreshData(mListData, mAttachedActivity.getLastMediaPath());
    }

    @Override
    public void refreshDataList() {
        if (isAdded()) {
            mDataAdapter.refreshData();
        }
    }
}