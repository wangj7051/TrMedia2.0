package com.tricheer.player.version.cj.slc_lc2010_vdc.frags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.js.sidebar.LetterSideBar;
import com.tricheer.player.R;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioListActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioPlayerActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcAudioNamesAdapter;

import java.io.Serializable;
import java.util.List;

import js.lib.android.media.audio.db.AudioDBManager;
import js.lib.android.media.bean.ProAudio;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * SCL_LC2010_VDC - Music names fragment
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioNamesFrag extends BaseAudioListFrag {
    // TAG
    private static final String TAG = "MusicNamesFrag";


    //==========Widgets in this Fragment==========
    private View contentV;
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
    private SclLc2010VdcAudioNamesAdapter mDataAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAttachedActivity = (SclLc2010VdcAudioListActivity) activity;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentV = inflater.inflate(R.layout.scl_lc2010_vdc_activity_audio_list_frag_names, null);
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

        //ListView
        mDataAdapter = new SclLc2010VdcAudioNamesAdapter(mAttachedActivity, 0);
        mDataAdapter.setCollectListener(new CollectBtnCallback());

        lvDatas = (ListView) contentV.findViewById(R.id.lv_datas);
        lvDatas.setAdapter(mDataAdapter);
        lvDatas.setOnItemClickListener(new LvItemClick());
        refreshDatas(mAttachedActivity.getListMedias(), mAttachedActivity.getLastMediaPath());
    }

    @Override
    public void refreshDatas(List<ProAudio> listMedias, String targetMediaUrl) {
        if (isAdded()) {
            mDataAdapter.refreshDatas((mListMedias = listMedias), targetMediaUrl);
            lvDatas.setSelection(mAttachedActivity.getCurrIdx());
        }
    }

    @Override
    public void refreshDatas(String targetMediaUrl) {
        if (isAdded()) {
            mDataAdapter.refreshDatas(targetMediaUrl);
        }
    }

    @Override
    public void next() {
        if (isAdded()) {
            mDataAdapter.refreshDatas(mDataAdapter.getNextPos());
        }
    }

    @Override
    public void prev() {
        if (isAdded()) {
            mDataAdapter.refreshDatas(mDataAdapter.getPrevPos());
        }
    }

    @Override
    public void playSelectMedia(String mediaUrl) {
        openPlayerActivity(mediaUrl, mListMedias);
    }

    private class LetterSideBarCallback implements LetterSideBar.LetterSideBarListener {
        @Override
        public void callback(int pos, String letter) {
            Logs.i(TAG, "LetterSideBarCallback -> callback(" + pos + "," + letter + ")");
            lvDatas.setSelection(pos);
        }
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
                    playSelectMedia(program.mediaUrl, false);
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

    protected void playSelectMedia(final String mediaUrl, boolean isNeedDelay) {
        if (!EmptyUtil.isEmpty(mediaUrl)) {
            if (isNeedDelay) {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        openPlayerActivity(mediaUrl, mListMedias);
                    }
                }, 300);
            } else {
                openPlayerActivity(mediaUrl, mListMedias);
            }
        }
    }

    protected void openPlayerActivity(String mediaUrl, List<ProAudio> listPrograms) {
        try {
            Intent playerIntent = new Intent(mAttachedActivity, SclLc2010VdcAudioPlayerActivity.class);
            playerIntent.putExtra("SELECT_MEDIA_URL", mediaUrl);
            playerIntent.putExtra("MEDIA_LIST", (Serializable) listPrograms);
            startActivityForResult(playerIntent, M_REQ_PLAYING_MEDIA_URL);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "openPlayerActivity()", e);
        }
    }

    private class CollectBtnCallback implements SclLc2010VdcAudioNamesAdapter.CollectListener {
        @Override
        public void onClickCollectBtn(ImageView ivCollect, int pos) {
            ProAudio item = mDataAdapter.getItem(pos);
            if (item == null) {
                return;
            }

            switch (item.isCollected) {
                case 0:
                    item.isCollected = 1;
                    AudioDBManager.instance().updateMediaCollect(item);
                    ivCollect.setImageResource(R.drawable.favor_c);
                    break;
                case 1:
                    item.isCollected = 0;
                    AudioDBManager.instance().updateMediaCollect(item);
                    ivCollect.setImageResource(R.drawable.favor_c_n);
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAttachedActivity.onActivityResult(requestCode, resultCode, data);
        mDataAdapter.refreshDatas(mAttachedActivity.getLastMediaPath());
    }
}