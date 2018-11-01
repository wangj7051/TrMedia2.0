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
import com.tricheer.player.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcAudioCollectsAdapter;

import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.media.player.audio.utils.AudioSortUtils;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * SCL_LC2010_VDC - Music collects fragment
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioCollectsFrag extends BaseAudioListFrag {
    // TAG
    private static final String TAG = "MusicCollectsFrag";


    //==========Widgets in this Fragment==========
    private View contentV;
    private View layoutNoneToast;
    private ListView lvDatas;
    private LetterSideBar lsb;

    //==========Variables in this Fragment==========
    private SclLc2010VdcAudioListActivity mAttachedActivity;
    private static Handler mHandler = new Handler();

    /**
     * Is ListView Item is Clicking
     */
    private boolean mIsLvItemClicking = false;

    /**
     * Media list
     */
    private List<ProAudio> mListMedias;

    /**
     * Data adapter
     */
    private SclLc2010VdcAudioCollectsAdapter mDataAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAttachedActivity = (SclLc2010VdcAudioListActivity) activity;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentV = inflater.inflate(R.layout.scl_lc2010_vdc_activity_audio_list_frag_collects, null);
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
        lsb.setVisibility(View.INVISIBLE);

        layoutNoneToast = contentV.findViewById(R.id.layout_none_toast);
        layoutNoneToast.setVisibility(View.INVISIBLE);

        //----Widgets----
        mDataAdapter = new SclLc2010VdcAudioCollectsAdapter(mAttachedActivity, 0);
        mDataAdapter.setCollectListener(new CollectBtnCallback());

        lvDatas = (ListView) contentV.findViewById(R.id.lv_datas);
        lvDatas.setAdapter(mDataAdapter);
        lvDatas.setOnItemClickListener(new LvItemClick());
        loadDataList();
    }

    @Override
    public void loadDataList() {
        if (!isAdded()) {
            return;
        }

        //Check NULL
        String targetMediaUrl = mAttachedActivity.getLastMediaPath();
        List<ProAudio> listSrcMedias = mAttachedActivity.getListSrcMedias();
        if (listSrcMedias == null) {
            return;
        }

        //Filter collected
        mListMedias = new ArrayList<>();
        for (ProAudio media : listSrcMedias) {
            if (media.isCollected == 1) {
                mListMedias.add(media);
            }
        }

        //Refresh UI
        AudioSortUtils.sortByUpdateTime(mListMedias);
        for (ProAudio media : mListMedias) {
            Log.i(TAG, media.title + " - " + media.isCollected + " - " + media.updateTime);
        }

        layoutNoneToast.setVisibility(EmptyUtil.isEmpty(mListMedias) ? View.VISIBLE : View.INVISIBLE);
        mDataAdapter.refreshData(mListMedias, targetMediaUrl);
    }

    @Override
    public void refreshDataList() {
        if (isAdded()) {
            mDataAdapter.refreshData();
        }
    }

    @Override
    public void refreshPlaying(String targetMediaUrl) {
        if (isAdded()) {
            mDataAdapter.refreshData(targetMediaUrl);
        }
    }

    @Override
    public void selectNext() {
        if (isAdded()) {
            int nextPos = mDataAdapter.getNextPos();
            mDataAdapter.refreshData(nextPos);
            lvDatas.setSelection(nextPos);
        }
    }

    @Override
    public void selectPrev() {
        if (isAdded()) {
            int prevPos = mDataAdapter.getPrevPos();
            mDataAdapter.refreshData(prevPos);
            lvDatas.setSelection(prevPos);
        }
    }

    @Override
    public void playSelected() {
    }


    @Override
    public void playSelected(String mediaUrl) {
        if (isAdded() && mAttachedActivity != null) {
            mAttachedActivity.openPlayerActivity(mediaUrl, mListMedias);
        }
    }

    @Override
    public void onMediaScanningStart() {
        Log.i(TAG, "onMediaScanningStart()");
    }

    @Override
    public void onMediaScanningEnd() {
        Log.i(TAG, "onMediaScanningEnd()");
    }

    @Override
    public void onMediaScanningCancel() {
        Log.i(TAG, "onMediaScanningCancel()");
    }

    /**
     * ListView Item Click Event
     */
    private class LvItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!mIsLvItemClicking) {
                mIsLvItemClicking = true;
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(mmDelayResetClickingFlagRunnable, 1000);

                // Play Select MediaUrl
                Logs.i(TAG, "LvItemClick -> onItemClick(AdapterView," + position + ",id)");
                final Object objItem = parent.getItemAtPosition(position);
                if (objItem != null) {
                    ProAudio program = (ProAudio) objItem;
                    mAttachedActivity.openPlayerActivity(program.mediaUrl, mListMedias);
                }
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
            ProAudio item = mDataAdapter.getItem(pos);
            if (item == null) {
                return;
            }
            switch (item.isCollected) {
                case 0:
                    item.isCollected = 1;
                    item.updateTime = System.currentTimeMillis();
                    AudioDBManager.instance().updateMediaCollect(item);
                    ivCollect.setImageResource(R.drawable.favor_c);
                    break;
                case 1:
                    item.isCollected = 0;
                    item.updateTime = System.currentTimeMillis();
                    AudioDBManager.instance().updateMediaCollect(item);
                    ivCollect.setImageResource(R.drawable.favor_c_n);
                    removeUnCollected(item.mediaUrl);
                    break;
            }
        }

        private void removeUnCollected(String mediaUrl) {
            if (!EmptyUtil.isEmpty(mListMedias)) {
                for (ProAudio media : mListMedias) {
                    if (TextUtils.equals(mediaUrl, media.mediaUrl)) {
                        mListMedias.remove(media);
                        break;
                    }
                }
                mDataAdapter.refreshData();
                layoutNoneToast.setVisibility(EmptyUtil.isEmpty(mListMedias) ? View.VISIBLE : View.INVISIBLE);
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
        return 0;
    }
}