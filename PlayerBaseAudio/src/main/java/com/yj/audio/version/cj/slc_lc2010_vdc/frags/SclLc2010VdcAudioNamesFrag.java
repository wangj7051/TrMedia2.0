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
 * SCL_LC2010_VDC - Music names fragment
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioNamesFrag extends BaseAudioListFrag {
    // TAG
    private static final String TAG = "AudioNamesFrag";


    //==========Widgets in this Fragment==========
    private View contentV;
    private ListView lvData;
    private ImageView ivLoading;

    //Letter sidebar
    private LetterSideBar letterSidebar;
    private LetterBg letterCircle;

    //==========Variables in this Fragment==========
    //Attached activity object
    private SclLc2010VdcAudioListActivity mAttachedActivity;

    /**
     * Is ListView Item is Clicking
     */
    private boolean mIsLvItemClicking = false;
    private LvItemClick mLvItemClick;

    /**
     * Data list
     */
    private List<ProAudio> mListData;

    /**
     * Data adapter
     */
    private SclLc2010VdcAudioNamesAdapter mDataAdapter;

    // ImageView frame animation control
    private FrameAnimationController mFrameAnimController;

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
        letterCircle = (LetterBg) contentV.findViewById(R.id.letter_circle);
        letterCircle.setVisibility(View.INVISIBLE);
        letterSidebar = (LetterSideBar) contentV.findViewById(R.id.lsb);
        letterSidebar.refreshLetters(null);
        letterSidebar.addCallback(new LetterSideBarCallback());

        //
        ivLoading = (ImageView) contentV.findViewById(R.id.iv_loading);
        mFrameAnimController = new FrameAnimationController();
        mFrameAnimController.setIv(ivLoading);
        mFrameAnimController.setFrameImgResArr(LOADING_RES_ID_ARR);
        showLoading(mAttachedActivity.isMediaScanning());

        //ListView
        mDataAdapter = new SclLc2010VdcAudioNamesAdapter(mAttachedActivity);
        mDataAdapter.setCollectListener(new CollectBtnCallback());

        lvData = (ListView) contentV.findViewById(R.id.lv_datas);
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
        Log.i(TAG, "loadLocalMedias()^^^^ Start ^^^^");
        refreshData();
    }

    @Override
    public void refreshData() {
        if (!isAdded()) {
            return;
        }

        //
        Log.i(TAG, "loadLocalMedias()^^^^ Start ^^^^");
        //Get source information
        String targetMediaUrl = mAttachedActivity.getLastMediaPath();
        List<ProAudio> listSrcMedias = mAttachedActivity.getListSrcMedias();
        if (listSrcMedias == null) {
            listSrcMedias = new ArrayList<>();
        }

        //Check empty
        if (EmptyUtil.isEmpty(listSrcMedias)) {
            Log.i(TAG, "==== Refresh Empty ====");
            mDataAdapter.refreshData();

            //刷新
        } else {
            showLoading(false);
            if (TextUtils.isEmpty(targetMediaUrl)) {
                ProAudio firstAudio = listSrcMedias.get(0);
                mDataAdapter.refreshData((mListData = listSrcMedias), firstAudio.mediaUrl);
            } else {
                mDataAdapter.refreshData((mListData = listSrcMedias), targetMediaUrl);
            }
        }
    }

    @Override
    public void refreshPlaying(String mediaUrl) {
        if (isAdded()) {
            mDataAdapter.refreshPlaying(mediaUrl);

            //Switch page.
            int currIdx = mAttachedActivity.getCurrIdx();
            Log.i(TAG, "currIdx~" + currIdx);
            setLvSelection(getPageFirstPos(currIdx), false);
        }
    }

    @Override
    public void selectNext() {
        if (isAdded()) {
            int nextPos;
            int currPos = mDataAdapter.getSelectPos();
            if (currPos == -1) {
                nextPos = mAttachedActivity.getCurrIdx();
            } else {
                nextPos = mDataAdapter.getNextPos();
            }
            Log.i(TAG, "nextPos~" + nextPos);
            mDataAdapter.select(nextPos);
            setLvSelection(getPageFirstPos(nextPos), false);
        }
    }

    @Override
    public void selectPrev() {
        if (isAdded()) {
            int prevPos;
            int currPos = mDataAdapter.getSelectPos();
            if (currPos == -1) {
                prevPos = mAttachedActivity.getCurrIdx();
            } else {
                prevPos = mDataAdapter.getPrevPos();
            }
            Log.i(TAG, "prevPos~" + prevPos);
            mDataAdapter.select(prevPos);
            setLvSelection(getPageFirstPos(prevPos), false);
        }
    }

    private void setLvSelection(int idx, boolean isConsiderScanning) {
        if (isAdded()) {
            if (isConsiderScanning) {
                if (!mAttachedActivity.isMediaScanning()) {
                    lvData.setSelection(idx);
                }
            } else {
                lvData.setSelection(idx);
            }
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
        if (mFrameAnimController != null) {
            mFrameAnimController.destroy();
            mFrameAnimController = null;
        }
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
                Log.i(TAG, "##### ---Forbidden click because of frequency !!!--- #####");
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
                    break;
            }
        }
    }
}