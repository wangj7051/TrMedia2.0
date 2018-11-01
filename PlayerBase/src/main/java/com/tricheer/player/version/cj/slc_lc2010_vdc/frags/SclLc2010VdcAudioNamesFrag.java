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
import com.tricheer.player.receiver.MediaScanReceiver;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioListActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.adapter.BaseAudioAdapter;
import com.tricheer.player.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcAudioNamesAdapter;

import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.FrameAnimationController;
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
    private ListView lvData;
    private ImageView ivLoading;
    private LetterSideBar lsb;

    //==========Variables in this Fragment==========
    private SclLc2010VdcAudioListActivity mAttachedActivity;

    private static Handler mHandler = new Handler();

    /**
     * Is ListView Item is Clicking
     */
    private boolean mIsLvItemClicking = false;
    private LvItemClick mLvItemClick;

    /**
     * Media list
     */
    private List<ProAudio> mListDatas;

    /**
     * Data adapter
     */
    private SclLc2010VdcAudioNamesAdapter mDataAdapter;

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

        //
        ivLoading = (ImageView) contentV.findViewById(R.id.iv_loading);
        mFrameAnimController = new FrameAnimationController();
        mFrameAnimController.setIv(ivLoading);
        mFrameAnimController.setFrameImgResArr(LOADING_RES_ID_ARR);
        if (MediaScanReceiver.isMediaScanning()) {
            onMediaScanningStart();
        }

        //ListView
        mDataAdapter = new SclLc2010VdcAudioNamesAdapter(mAttachedActivity, 0);
        mDataAdapter.setCollectListener(new CollectBtnCallback());

        lvData = (ListView) contentV.findViewById(R.id.lv_datas);
        lvData.setAdapter(mDataAdapter);
        lvData.setOnItemClickListener((mLvItemClick = new LvItemClick()));
        loadDataList();
    }

    @Override
    public void loadDataList() {
        if (!isAdded()) {
            return;
        }

        String targetMediaUrl = mAttachedActivity.getLastMediaPath();
        List<ProAudio> listSrcMedias = mAttachedActivity.getListSrcMedias();
        if (EmptyUtil.isEmpty(listSrcMedias)) {
            mDataAdapter.refreshData();
        } else if (TextUtils.isEmpty(targetMediaUrl)) {
            ProAudio firstAudio = listSrcMedias.get(0);
            mDataAdapter.refreshData((mListDatas = listSrcMedias), firstAudio.mediaUrl);
            delayPlay(firstAudio.mediaUrl);
        } else {
            mDataAdapter.refreshData((mListDatas = listSrcMedias), targetMediaUrl);
            delayPlay(targetMediaUrl);
        }
    }


    private void delayPlay(final String targetMediaUrl) {
        if (isAdded()) {
            if (mAttachedActivity.isPlaying()) {
                lvData.setSelection(mAttachedActivity.getCurrIdx());
            } else if (mAttachedActivity.isPauseByUser()) {
                lvData.setSelection(mAttachedActivity.getCurrIdx());
            } else {
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAttachedActivity.setPlayList(mListDatas);
                        mAttachedActivity.play(targetMediaUrl);
                        lvData.setSelection(mAttachedActivity.getCurrIdx());
                    }
                }, 500);
            }
        }
    }

    @Override
    public void refreshDataList() {
        if (isAdded()) {
            mDataAdapter.refreshData();
        }
    }

    @Override
    public void refreshPlaying(String mediaUrl) {
        if (isAdded()) {
            mDataAdapter.refreshPlaying(mediaUrl);
        }
    }

    @Override
    public void selectNext() {
        if (isAdded()) {
            int nextPos = mDataAdapter.getNextPos();
            mDataAdapter.select(nextPos);
            lvData.setSelection(nextPos);
        }
    }

    @Override
    public void selectPrev() {
        if (isAdded()) {
            int prevPos = mDataAdapter.getPrevPos();
            mDataAdapter.select(prevPos);
            lvData.setSelection(prevPos);
        }
    }

    @Override
    public void playSelected() {
        if (isAdded()) {
            int selectPos = mDataAdapter.getSelectPos();
            Log.i(TAG, "playSelected() > selectPos:" + selectPos);
            mLvItemClick.execItemClick(mDataAdapter.getItem(selectPos));
        }
    }

    @Override
    public void playSelected(String mediaUrl) {
        if (isAdded() && mAttachedActivity != null) {
            mAttachedActivity.openPlayerActivity(mediaUrl, mListDatas);
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
            execItemClick(parent.getItemAtPosition(position));
        }

        private Runnable mmDelayResetClickingFlagRunnable = new Runnable() {

            @Override
            public void run() {
                mIsLvItemClicking = false;
            }
        };

        private void execItemClick(Object objItem) {
            if (objItem == null) {
                return;
            }

            if (mIsLvItemClicking) {
                mIsLvItemClicking = false;
                Logs.i(TAG, "##### ---Forbidden click because of frequency !!!--- #####");
                return;
            } else {
                mIsLvItemClicking = true;
                mmHandler.removeCallbacksAndMessages(null);
                mmHandler.postDelayed(mmDelayResetClickingFlagRunnable, 1000);
            }

            //
            ProAudio program = (ProAudio) objItem;
            mAttachedActivity.openPlayerActivity(program.mediaUrl, mListDatas);
        }
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
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (data != null) {
//            String flag = data.getStringExtra("flag");
//            if ("PLAYER_FINISH_ON_CLICK_LIST".equals(flag) || "PLAYER_FINISH_ON_GET_KEY".equals(flag)) {
//            }
//        }
    }

    @Override
    public int onBackPressed() {
        return 0;
    }

    @Override
    public void onDestroy() {
        if (mFrameAnimController != null) {
            mFrameAnimController.destroy();
            mFrameAnimController = null;
        }
        super.onDestroy();
    }
}