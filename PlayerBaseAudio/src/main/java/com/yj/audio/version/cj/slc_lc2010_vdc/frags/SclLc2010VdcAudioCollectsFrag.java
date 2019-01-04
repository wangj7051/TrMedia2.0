package com.yj.audio.version.cj.slc_lc2010_vdc.frags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.js.sidebar.LetterBg;
import com.js.sidebar.LetterSideBar;
import com.yj.audio.R;
import com.yj.audio.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioListActivity;
import com.yj.audio.version.cj.slc_lc2010_vdc.adapter.BaseAudioAdapter;
import com.yj.audio.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcAudioNamesAdapter;

import java.util.ArrayList;
import java.util.List;

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
public class SclLc2010VdcAudioCollectsFrag extends BaseAudioListFrag {
    // TAG
    private static final String TAG = "AudioCollectsFrag";


    //==========Widgets in this Fragment==========
    private View layoutNoneToast;
    private View contentV;
    private ListView lvData;
    private ImageView ivLoading;

    //Letter sidebar
    private LetterSideBar letterSidebar;
    private LetterBg letterCircle;

    //==========Variables in this Fragment==========
    private SclLc2010VdcAudioListActivity mAttachedActivity;

    // ImageView frame animation control
    private FrameAnimationController mFrameAnimController;

    /**
     * Is ListView Item is Clicking
     */
    private boolean mIsLvItemClicking = false;
    private LvItemClick mLvItemClick;

    /**
     * Media list
     */
    private List<ProAudio> mListData;

    /**
     * Data adapter
     */
    private SclLc2010VdcAudioNamesAdapter mDataAdapter;

    /**
     * Parameters for this fragment.
     */
    protected String[] mParams;

    @Override
    public void setParam(String... params) {
        mParams = params;
    }

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
        layoutNoneToast = contentV.findViewById(R.id.layout_none_toast);
        layoutNoneToast.setVisibility(View.INVISIBLE);

        //Side bar
        letterCircle = (LetterBg) contentV.findViewById(R.id.letter_circle);
        letterCircle.setVisibility(View.INVISIBLE);
        letterSidebar = (LetterSideBar) contentV.findViewById(R.id.lsb);
        letterSidebar.refreshLetters(null);
        letterSidebar.addCallback(new LetterSideBarCallback());

        ivLoading = (ImageView) contentV.findViewById(R.id.iv_loading);
        mFrameAnimController = new FrameAnimationController();
        mFrameAnimController.setIv(ivLoading);
        mFrameAnimController.setFrameImgResArr(LOADING_RES_ID_ARR);
        showLoading(mAttachedActivity.isMediaScanning());

        //----Widgets----
        mDataAdapter = new SclLc2010VdcAudioNamesAdapter(mAttachedActivity);
        mDataAdapter.setCollectListener(new CollectBtnCallback());

        lvData = (ListView) contentV.findViewById(R.id.lv_datas);
        lvData.setSelector(mAttachedActivity.getImgResId("bg_audio_item_selector"));
        lvData.setAdapter(mDataAdapter);
        lvData.setOnItemClickListener((mLvItemClick = new LvItemClick()));
        lvData.setOnScrollListener(new LvOnScroll());

        //Loading page
        Log.i(TAG, "init() -loadLocalMedias-");
        updateThemeCommon();
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
        //Get source information
        String targetMediaUrl = mAttachedActivity.getLastMediaPath();
        List<ProAudio> listSrcMedias = mAttachedActivity.getListSrcMedias();
        if (listSrcMedias == null) {
            listSrcMedias = new ArrayList<>();
        }

        //Filter
        if (!EmptyUtil.isEmpty(listSrcMedias)) {
            showLoading(false);
            mListData = new ArrayList<>();

            //Loop and filter
            try {
                for (ProAudio media : listSrcMedias) {
                    if (media.isCollected == 1) {
                        mListData.add(media);
                    }
                }
            } catch (Exception e) {
                Log.i(TAG, "refreshData() - ERROR:" + e.getMessage());
                e.printStackTrace();
            }

            //Refresh UI
            layoutNoneToast.setVisibility(EmptyUtil.isEmpty(mListData) ? View.VISIBLE : View.INVISIBLE);
            mDataAdapter.refreshData(mListData, targetMediaUrl);
        }
    }

    public void refreshPlaying(String mediaUrl) {
        if (isAdded()) {
            mDataAdapter.refreshPlaying(mediaUrl);

            //Switch page.
            int currIdx = mAttachedActivity.getCurrIdx();
            Log.i(TAG, "currIdx~" + currIdx);
            lvData.setSelection(getPageFirstPos(currIdx));
        }
    }

    @Override
    public void selectNext() {
        Log.i(TAG, "selectNext()");
        if (isAdded() && !EmptyUtil.isEmpty(mListData)) {
            int nextPos;
            int currPos = mDataAdapter.getSelectPos();
            if (currPos < 0) {
                int loop = mListData.size();
                for (int idx = 0; idx < loop; idx++) {
                    ProAudio media = mListData.get(idx);
                    if (TextUtils.equals(mAttachedActivity.getCurrMediaPath(), media.mediaUrl)) {
                        currPos = idx;
                        break;
                    }
                }
                if (currPos < 0) {
                    nextPos = 0;
                } else {
                    nextPos = currPos;
                }
            } else {
                nextPos = mDataAdapter.getNextPos();
            }
            Log.i(TAG, "nextPos~" + nextPos);
            mDataAdapter.select(nextPos);
            lvData.setSelection(getPageFirstPos(nextPos));
        }
    }

    @Override
    public void selectPrev() {
        Log.i(TAG, "selectPrev()");
        if (isAdded() && !EmptyUtil.isEmpty(mListData)) {
            int prevPos;
            int currPos = mDataAdapter.getSelectPos();
            if (currPos < 0) {
                int loop = mListData.size();
                for (int idx = 0; idx < loop; idx++) {
                    ProAudio media = mListData.get(idx);
                    if (TextUtils.equals(mAttachedActivity.getCurrMediaPath(), media.mediaUrl)) {
                        currPos = idx;
                        break;
                    }
                }
                if (currPos < 0) {
                    prevPos = 0;
                } else {
                    prevPos = currPos;
                }
            } else {
                prevPos = mDataAdapter.getPrevPos();
            }
            Log.i(TAG, "prevPos~" + prevPos);
            mDataAdapter.select(prevPos);
            lvData.setSelection(getPageFirstPos(prevPos));
        }
    }

    @Override
    public void playSelected() {
        if (isAdded()) {
            int selectPos = mDataAdapter.getSelectPos();
            Log.i(TAG, "playSelected() > selectPos:" + selectPos);
            mLvItemClick.execItemClick(selectPos);
        }
    }

    @Override
    public void onMediaScanningStart() {
        Log.i(TAG, "onMediaScanningStart()");
    }

    @Override
    public void onMediaScanningCancel() {
        Log.i(TAG, "onMediaScanningCancel()");
    }

    @Override
    public void showLoading(boolean isShow) {
        if (isAdded()) {
            if (isShow) {
                letterSidebar.setVisibility(View.INVISIBLE);
                ivLoading.setVisibility(View.VISIBLE);
                mFrameAnimController.start();
            } else {
                letterSidebar.setVisibility(View.VISIBLE);
                ivLoading.setVisibility(View.INVISIBLE);
                mFrameAnimController.stop();
            }
        }
    }

    @Override
    public int onBackPressed() {
        return 0;
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
        lvData.setDivider(mAttachedActivity.getDrawable(mAttachedActivity.getImgResId("separate_line_h")));
        lvData.setSelector(mAttachedActivity.getImgResId("bg_audio_item_selector"));
    }

    /**
     * Letter side bar touch callback.
     */
    private class LetterSideBarCallback implements LetterSideBar.LetterSideBarListener {

        private Character mmTouchedLetter;

        @Override
        public void callback(int pos, String letter) {
            try {
                Logs.i(TAG, "LetterSideBarCallback -> callback(" + pos + "," + letter + ")");
                mmTouchedLetter = letter.charAt(0);
                int sectionPos = mDataAdapter.getPositionForSection(mmTouchedLetter);
                if (sectionPos != -1) {
                    Logs.i(TAG, "LetterSideBarCallback -> callback(" + pos + "," + letter + "-" + sectionPos + ")");
                    lvData.setSelection(sectionPos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTouchDown() {
            try {
                Log.i(TAG, "LetterSideBarCallback - onTouchDown()");
                letterCircle.refreshLetter(mmTouchedLetter);
                letterCircle.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTouchMove() {
            try {
                Log.i(TAG, "LetterSideBarCallback - onTouchMove()");
                letterCircle.refreshLetter(mmTouchedLetter);
                letterCircle.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTouchUp() {
            try {
                Log.i(TAG, "LetterSideBarCallback - onTouchUp()");
                letterCircle.setVisibility(View.INVISIBLE);
                letterSidebar.refreshHlLetter(mmTouchedLetter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ListView scroll event.
     */
    private class LvOnScroll implements AbsListView.OnScrollListener {

        private boolean mmIsTouchScrolling;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case SCROLL_STATE_TOUCH_SCROLL:
                    mmIsTouchScrolling = true;
                    Log.i(TAG, "LvOnScroll -SCROLL_STATE_TOUCH_SCROLL-");
                    break;
                case SCROLL_STATE_IDLE:
                    mmIsTouchScrolling = false;
                    Log.i(TAG, "LvOnScroll -SCROLL_STATE_IDLE-");
                    break;
                case SCROLL_STATE_FLING:
                    Log.i(TAG, "LvOnScroll -SCROLL_STATE_FLING-");
                    break;

            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            try {
                if (mmIsTouchScrolling) {
                    int section = mDataAdapter.getSectionForPosition(firstVisibleItem);
                    letterSidebar.refreshHlLetter((char) section);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            execItemClick(position);
        }

        private Runnable mmDelayResetClickingFlagRunnable = new Runnable() {

            @Override
            public void run() {
                mIsLvItemClicking = false;
            }
        };

        private void execItemClick(int position) {
            Object objItem = mDataAdapter.getItem(position);
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
            mAttachedActivity.playAndOpenPlayerActivity(position, program.mediaUrl, mListData);
        }
    }

    /**
     * Collect operate callback.
     */
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
                    removeUnCollected(pos);
                    break;
            }
        }

        private void removeUnCollected(int pos) {
            if (!EmptyUtil.isEmpty(mListData)) {
                mListData.remove(pos);
                mDataAdapter.refreshData(mListData);
                layoutNoneToast.setVisibility(EmptyUtil.isEmpty(mListData) ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }
}