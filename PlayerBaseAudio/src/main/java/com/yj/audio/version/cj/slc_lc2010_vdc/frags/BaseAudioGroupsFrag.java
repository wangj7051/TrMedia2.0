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
import com.yj.audio.version.cj.slc_lc2010_vdc.adapter.BaseAudioGroupsAdapter;
import com.yj.audio.version.cj.slc_lc2010_vdc.bean.AudioFilter;

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

public abstract class BaseAudioGroupsFrag extends BaseAudioListFrag {
    // TAG
    private static final String TAG = "BaseAudioGroupsFrag";

    //==========Widgets in this Fragment==========
    protected View contentV;
    protected ListView lvData;
    protected ImageView ivLoading;

    //Letter sidebar
    private LetterSideBar letterSidebar;
    private LetterBg letterCircle;

    //==========Variables in this Fragment==========
    //Attached activity of this fragment.
    protected SclLc2010VdcAudioListActivity mAttachedActivity;

    /**
     * Media list
     */
    protected List<?> mListData;
    protected List<AudioFilter> mListFilters;

    /**
     * Data adapter
     */
    protected BaseAudioGroupsAdapter mDataAdapter;

    /**
     * ListView item click event.
     */
    protected LvItemClick mLvItemClick;
    /**
     * Is ListView Item is Clicking
     */
    protected boolean mIsLvItemClicking = false;

    /**
     * ImageView frame animation control
     */
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
        letterCircle = (LetterBg) contentV.findViewById(R.id.letter_circle);
        letterCircle.setVisibility(View.INVISIBLE);
        letterSidebar = (LetterSideBar) contentV.findViewById(R.id.lsb);
        letterSidebar.refreshLetters(null);
        letterSidebar.addCallback(new LetterSideBarCallback());
        letterSidebar.setVisibility(View.VISIBLE);

        //
        ivLoading = (ImageView) contentV.findViewById(R.id.iv_loading);
        mFrameAnimController = new FrameAnimationController();
        mFrameAnimController.setIv(ivLoading);
        mFrameAnimController.setFrameImgResArr(LOADING_RES_ID_ARR);
        showLoading(mAttachedActivity.isMediaScanning());

        //ListView
        initAdapter();
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

    /**
     * Initialize adapter of {@link ListView}
     */
    protected abstract void initAdapter();

    @Override
    public void loadLocalMedias() {
    }

    @Override
    public void refreshData() {
    }

    @Override
    public void refreshPlaying(String mediaUrl) {
        if (!isAdded() || mListData == null) {
            return;
        }

        //Refresh playing
        if (!isAudioList(mListData)) {
            //Refresh
            boolean isFilteredPlaying = false;
            for (Object obj : mListData) {
                AudioFilter filter = (AudioFilter) obj;
                filter.isSelected = false;

                // Filter selected
                if (!isFilteredPlaying && filter.listMedias != null) {
                    for (ProAudio media : filter.listMedias) {
                        if (TextUtils.equals(mediaUrl, media.mediaUrl)) {
                            isFilteredPlaying = true;
                            filter.isSelected = true;
                            break;
                        }
                    }
                }
            }
        }
        mDataAdapter.refreshPlaying(mediaUrl);

        //Switch page.
        int playingPos = getPlayingPosInDataList(mAttachedActivity.getCurrMediaPath(), mListData);
        lvData.setSelection(getPageFirstPos(playingPos));
    }

    @Override
    public void selectNext() {
        if (isAdded()) {
            int nextPos;
            int currPos = mDataAdapter.getSelectPos();
            if (currPos == -1) {
                nextPos = getPlayingPosInDataList(mAttachedActivity.getCurrMediaPath(), mListData);
            } else {
                nextPos = mDataAdapter.getNextPos();
            }

            //
            Log.i(TAG, "nextPos~" + nextPos);
            mDataAdapter.select(nextPos);
            lvData.setSelection(getPageFirstPos(nextPos));
        }
    }

    @Override
    public void selectPrev() {
        if (isAdded()) {
            int prevPos;
            int currPos = mDataAdapter.getSelectPos();
            if (currPos == -1) {
                prevPos = getPlayingPosInDataList(mAttachedActivity.getCurrMediaPath(), mListData);
            } else {
                prevPos = mDataAdapter.getPrevPos();
            }

            //
            Log.i(TAG, "nextPos~" + prevPos);
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

    /**
     * @param listData {@link AudioFilter} list or {@link ProAudio} list.
     * @return true-{@link ProAudio} list
     */
    protected boolean isAudioList(List<?> listData) {
        if (!EmptyUtil.isEmpty(listData)) {
            Object item = listData.get(0);
            return (item instanceof ProAudio);
        }
        return false;
    }

    /**
     * Get first select position.
     * <p>Default is the playing position.</p>
     *
     * @param playMediaUrl current playing media url.
     * @param listData     {@link AudioFilter} list or {@link ProAudio} list.
     * @return int-Playing position.
     */
    protected int getPlayingPosInDataList(String playMediaUrl, List<?> listData) {
        Log.i(TAG, "getFirstSelectPos()");
        int currPos = -1;
        if (!EmptyUtil.isEmpty(listData)) {
            //Media list
            if (isAudioList(listData)) {
                currPos = getPlayingPosInMediaList(playMediaUrl, listData);
                //Group list
            } else {
                currPos = getPlayingPosInGroupList(playMediaUrl, listData);
            }
            if (currPos == -1) {
                currPos = 0;
            }
        }
        return currPos;
    }

    protected int getPlayingPosInMediaList(String playMediaUrl, List<?> listData) {
        int currPos = -1;
        if (listData != null) {
            int loop = listData.size();
            for (int idx = 0; idx < loop; idx++) {
                ProAudio media = (ProAudio) listData.get(idx);
                if (TextUtils.equals(playMediaUrl, media.mediaUrl)) {
                    currPos = idx;
                    break;
                }
            }
        }
        return currPos;
    }

    protected int getPlayingPosInGroupList(String playMediaUrl, List<?> listData) {
        int currPos = -1;
        if (listData != null) {
            int loop = listData.size();
            for (int idx = 0; idx < loop; idx++) {
                AudioFilter af = (AudioFilter) listData.get(idx);
                if (af.listMedias != null) {
                    int loopJ = af.listMedias.size();
                    for (int jdx = 0; jdx < loopJ; jdx++) {
                        ProAudio media = af.listMedias.get(jdx);
                        if (TextUtils.equals(playMediaUrl, media.mediaUrl)) {
                            currPos = idx;
                            break;
                        }
                    }
                }
                if (currPos != -1) {
                    break;
                }
            }
        }
        return currPos;
    }

    /**
     * Refresh filter data list.
     *
     * @param listFilters {@link AudioFilter} list
     */
    @SuppressWarnings("unchecked")
    protected void refreshFilters(List<AudioFilter> listFilters) {
        if (listFilters != null) {
            mListData = listFilters;
        } else {
            mListData = new ArrayList<>();
        }
    }

    @Override
    public int onBackPressed() {
        if (EmptyUtil.isEmpty(mListData)) {
            return 0;
        }

        if (isAudioList(mListData)) {
            refreshFilters(mListFilters);
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void onDestroy() {
        if (mLvItemClick != null) {
            mLvItemClick.destroy();
            mLvItemClick = null;
        }
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
    @SuppressWarnings("unchecked")
    private class LvItemClick implements AdapterView.OnItemClickListener {

        private Handler mmHandler = new Handler();

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Logs.i(TAG, "LvItemClick -> onItemClick(AdapterView," + position + ",id)");
            execItemClick(position);
        }

        private void execItemClick(int position) {
            Object objItem = mDataAdapter.getItem(position);
            if (objItem == null) {
                return;
            }

            //Click filter group
            if (objItem instanceof AudioFilter) {
                // Refresh list
                refreshMedias((AudioFilter) objItem);

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

                //
                ProAudio program = (ProAudio) objItem;
                mAttachedActivity.playAndOpenPlayerActivity(position, program.mediaUrl, (List<ProAudio>) mListData);
            }
        }

        private Runnable mmDelayResetClickingFlagRunnable = new Runnable() {

            @Override
            public void run() {
                mIsLvItemClicking = false;
            }
        };

        private void destroy() {
            mmHandler.removeCallbacksAndMessages(null);
        }
    }

    protected void refreshMedias(AudioFilter audioFilter) {
        if (audioFilter != null) {
            mListData = audioFilter.listMedias;
        } else {
            mListData = new ArrayList<>();
        }
    }

    /**
     * Collect button call back.
     */
    class CollectBtnCallback implements BaseAudioAdapter.CollectListener {
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
}