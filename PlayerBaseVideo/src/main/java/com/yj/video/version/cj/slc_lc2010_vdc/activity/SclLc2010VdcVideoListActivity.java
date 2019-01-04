package com.yj.video.version.cj.slc_lc2010_vdc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.tri.lib.engine.KeyEnum;
import com.tri.lib.receiver.AccReceiver;
import com.tri.lib.utils.TrVideoPreferUtils;
import com.yj.video.R;
import com.yj.video.engine.PlayerAppManager;
import com.yj.video.version.base.activity.video.BaseVideoUIActivity;
import com.yj.video.version.cj.slc_lc2010_vdc.frags.BaseVideoListFrag;
import com.yj.video.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcVideoFoldersFrag;
import com.yj.video.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcVideoNamesFrag;
import com.yj.video.view.ToastMsg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.FragUtil;

/**
 * SLC_LC2010_VDC Video List Activity
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcVideoListActivity extends BaseVideoUIActivity implements AccReceiver.AccDelegate {
    // TAG
    private static final String TAG = "video_list";

    /**
     * ==========Widgets in this Activity==========
     */
    //Root view
    private View layoutRoot;

    //垃圾聚焦控件: 该控件主要是在其他空间不需要聚焦时，将聚焦转移到此。
    private View vRubbishFocus;

    //Top layout root view
    private View layoutTop;
    private View vItemFocused;
    private View[] vItems = new View[2];

    // Content fragment
    private BaseVideoListFrag fragCategory;
    private final int CATEGORY_TITLE = 0;
    private final int CATEGORY_FOLDER = 1;

    //==========Variables in this Activity==========
    /**
     * Context
     */
    private Context mContext;

    //如果本地媒体未搜索到，执行一次全盘扫描
    private boolean mIsScanWhenLocalMediaIsEmpty = true;

    //自动播放
    private Handler mDelayPlayHandler = new Handler();
    private boolean mIsAllowAutoPlay = true;

    // Request Current Playing Media Url
    protected final int M_REQ_WARNING = 2;

    /**
     * Warning page controller.
     */
    private WarningController mWarningController;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.scl_lc2010_vdc_activity_video_list);
        Log.i(TAG, "onCreate");
        PlayerAppManager.addContext(this);
        AccReceiver.register(this);
        init();
    }

    @Override
    protected void init() {
        super.init();
        //
        mContext = this;
        mWarningController = new WarningController();

        // -- Widgets --
        layoutRoot = findRootView();
        layoutTop = findViewById(R.id.layout_top);
        vRubbishFocus = findViewById(R.id.v_rubbish_focus);

        //
        vItems[CATEGORY_TITLE] = findViewById(R.id.v_media_name);
        vItems[CATEGORY_TITLE].setOnClickListener(mFilterViewOnClick);

        vItems[CATEGORY_FOLDER] = findViewById(R.id.v_folder);
        vItems[CATEGORY_FOLDER].setOnClickListener(mFilterViewOnClick);

        //
        bindScanService(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWarningController.onResume();
    }

    @Override
    protected void onScanServiceConnected() {
        Log.i(TAG, "onScanServiceConnected()");
        switchTab(vItems[CATEGORY_TITLE], true);
        loadLocalMedias();
    }

    /**
     * Load medias stored in database of the application.
     */
    private void loadLocalMedias() {
        Log.i(TAG, "loadLocalMedias()");
        showLoading(true);
        CommonUtil.cancelTask(mLoadLocalMediasTask);
        mLoadLocalMediasTask = new LoadLocalMediasTask();
        mLoadLocalMediasTask.execute(new LoadMediaListener() {

            @Override
            public void afterLoad(String selectMediaUrl) {
                Log.i(TAG, "loadLocalMedias() ->afterLoad(" + selectMediaUrl + ")");
                showLoading(false);
                if (EmptyUtil.isEmpty(mListPrograms)) {
                    //Only execute once.
                    if (mIsScanWhenLocalMediaIsEmpty) {
                        mIsScanWhenLocalMediaIsEmpty = false;
                        Log.i(TAG, "loadLocalMedias() -> startScan()");
                        startScan();
                    }
                } else {
                    autoPlay();
                    if (fragCategory != null) {
                        fragCategory.loadLocalMedias();
                    }
                }
            }

            @Override
            public void refreshUI() {
            }
        });
    }

    /**
     * Automatically play.
     */
    private void autoPlay() {
        Log.i(TAG, "autoPlay()");
        if (isForeground() && mIsAllowAutoPlay) {
            Log.i(TAG, "autoPlay() -EXEC-");
            mIsAllowAutoPlay = false;
            openVideoPlayerActivity(getLastMediaPath(), mListPrograms);
        }
    }

    @Override
    public void onMediaScanningStart() {
        super.onMediaScanningStart();
        Log.i(TAG, "onMediaScanningStart()");
        if (fragCategory != null) {
            fragCategory.onMediaScanningStart();
        }
    }

    @Override
    public void onMediaScanningEnd(boolean isHasMedias) {
        Log.i(TAG, "onMediaScanningEnd(" + isHasMedias + ")");
        showLoading(false);
        if (isHasMedias) {
            loadLocalMedias();
        } else {
            ToastMsg.show(this, getString(R.string.toast_no_videos));
        }
    }

    @Override
    public void onMediaScanningCancel() {
        super.onMediaScanningCancel();
        Log.i(TAG, "onMediaScanningCancel()");
        if (fragCategory != null) {
            fragCategory.onMediaScanningCancel();
        }
    }

    @Override
    public void onMediaScanningRefresh(final List<ProVideo> listMedias, boolean isOnUiThread) {
        super.onMediaScanningRefresh(listMedias, isOnUiThread);
        Log.i(TAG, "onMediaScanningRefresh(List<ProAudio>," + isOnUiThread + ")");
        if (EmptyUtil.isEmpty(mListPrograms)) {
            mListPrograms = new ArrayList<>(listMedias);
            sortMediaList(mListPrograms);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (fragCategory != null) {
                        fragCategory.loadLocalMedias();
                    }
                }
            });
        } else {
            mListPrograms.addAll(listMedias);
            sortMediaList(mListPrograms);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (fragCategory != null) {
                        fragCategory.refreshData();
                    }
                }
            });
        }
    }

    //TODO 预留
    @Override
    public void onPlayFromFolder(Intent data) {
    }

    /**
     * Filter item click event.
     */
    private View.OnClickListener mFilterViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switchTab(v, true);
        }
    };

    /**
     * Switch filter item.
     *
     * @param v          Filter item that focused.
     * @param isExecLoad true : Load fragment focused.
     */
    private void switchTab(View v, boolean isExecLoad) {
        vItemFocused = v;
        //Switch TAB
        final int loop = vItems.length;
        for (int idx = 0; idx < loop; idx++) {
            View item = vItems[idx];
            if (item == v) {
                item.setFocusable(true);
                item.requestFocus();
                setBg(item, true);
                if (isExecLoad) {
                    loadFragment(idx);
                }
            } else {
                item.setFocusable(false);
                item.clearFocus();
                setBg(item, false);
            }
        }
    }

    /**
     * Set background of filter items.
     *
     * @param vFocused Filter item that focused.
     * @param selected Focused or not.
     */
    private void setBg(View vFocused, boolean selected) {
        if (selected) {
            vFocused.setBackgroundResource(getImgResId("bg_title_item_c"));
        } else {
            vFocused.setBackgroundResource(getImgResId("btn_filter_tab_selector"));
        }
    }

    /**
     * Load fragment content.
     *
     * @param idx The idx of fragment
     */
    private void loadFragment(int idx) {
        //Remove old
        if (fragCategory != null) {
            FragUtil.removeV4Fragment(fragCategory, getSupportFragmentManager());
        }

        //Load New
        switch (idx) {
            case CATEGORY_TITLE:
                fragCategory = new SclLc2010VdcVideoNamesFrag();
                break;
            case CATEGORY_FOLDER:
                fragCategory = new SclLc2010VdcVideoFoldersFrag();
                break;
        }
        if (fragCategory != null) {
            FragUtil.loadV4Fragment(R.id.layout_frag, fragCategory, getSupportFragmentManager());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String flag = data.getStringExtra("flag");
            if ("EXIT_WARNING".equals(flag)) {
                mWarningController.exit();
                if (fragCategory instanceof SclLc2010VdcVideoNamesFrag) {
                    Log.i(TAG, "-EXEC auto play on EXIT_WARNING-");
                    mDelayPlayHandler.removeCallbacksAndMessages(null);
                    mDelayPlayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autoPlay();
                        }
                    }, 300);
                }
            }
        }
    }


    public void openVideoPlayerActivity(String mediaUrl, List<ProVideo> listPrograms) {
        try {
            boolean isMediasEmpty = EmptyUtil.isEmpty(listPrograms);
            boolean isPlayerOpened = SclLc2010VdcVideoPlayerActivity.isPlayerOpened();
            boolean isWarningShowing = mWarningController.isWarningShowing();
            Log.i(TAG, "isMediasEmpty : " + isMediasEmpty
                    + "\n isPlayerOpened:" + isPlayerOpened
                    + "\n isWarningShowing:" + isWarningShowing);
            if (isMediasEmpty || isPlayerOpened || isWarningShowing) {
                return;
            }

            //Open player.
            Log.i(TAG, "==== Open player page =====");
            Intent playerIntent = new Intent(this, SclLc2010VdcVideoPlayerActivity.class);
            playerIntent.putExtra("SELECT_MEDIA_URL", mediaUrl);
            playerIntent.putExtra("MEDIA_LIST", (Serializable) listPrograms);
            startActivityForResult(playerIntent, 1);
        } catch (Exception e) {
            Log.i(TAG, "openVideoPlayerActivity :: ERROR - " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onGetKeyCode(KeyEnum key) {
        Log.i(TAG, "onGetKeyCode(" + key + ")");
        moveFocusToRubbish(vRubbishFocus);
        switch (key) {
            case KEYCODE_MEDIA_PREVIOUS:
                if (fragCategory != null) {
                    fragCategory.selectPrev();
                }
                break;
            case KEYCODE_MEDIA_NEXT:
                if (fragCategory != null) {
                    fragCategory.selectNext();
                }
                break;
            case KEYCODE_DPAD_LEFT:
                if (fragCategory != null) {
                    fragCategory.selectPrev();
                }
                break;
            case KEYCODE_DPAD_RIGHT:
                if (fragCategory != null) {
                    fragCategory.selectNext();
                }
                break;
            case KEYCODE_ENTER:
                if (fragCategory != null) {
                    fragCategory.playSelected();
                }
                break;
        }
    }

    /**
     * Move window focus to rubbish position where not useful.
     *
     * @param vRubbish Rubbish view.
     */
    private void moveFocusToRubbish(View vRubbish) {
        View focusedV = getCurrentFocus();
        if (focusedV != vRubbish && vRubbish != null) {
            vRubbish.setFocusable(true);
            vRubbish.requestFocus();
        }
    }

    @Override
    public void onAccOffTrue() {
        Log.i(TAG, "onAccOffTrue()");
        bindScanService(false);
        PlayerAppManager.exitCurrPlayer();
    }

    @Override
    public void onAudioFocusDuck() {
    }

    @Override
    public void onAudioFocusTransient() {
    }

    @Override
    public void onAudioFocusGain() {
    }

    @Override
    public void onAudioFocusLoss() {
    }

    @Override
    public void onAudioFocus(int flag) {
    }

    @Override
    public void onBackPressed() {
        if (fragCategory != null) {
            int backRes = fragCategory.onBackPressed();
            switch (backRes) {
                case 1:
                    break;
                default:
                    super.onBackPressed();
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause()");
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    public void finish() {
        Log.i(TAG, "finish()");
        clearActivity();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy()");
//        clearActivity();
        super.onDestroy();
    }

    public void clearActivity() {
        //
        bindScanService(false);
        if (fragCategory != null) {
            fragCategory.onDestroy();
        }

        //
        PlayerAppManager.removeContext(this);
    }

    private void showLoading(boolean isShow) {
        if (fragCategory != null) {
            fragCategory.showLoading(isShow);
        }
    }

    public List<ProVideo> getListMedias() {
        return mListPrograms;
    }

    @Override
    public void updateThemeToDefault() {
        super.updateThemeToDefault();
        Log.i(TAG, "updateThemeToDefault()");
        //Common
        updateThemeCommon();

        //Fragment
        if (fragCategory != null) {
            fragCategory.updateThemeToDefault();
        }
    }

    @Override
    public void updateThemeToIos() {
        Log.i(TAG, "updateThemeToIos()");
        //Common
        updateThemeCommon();

        //Fragment
        if (fragCategory != null) {
            fragCategory.updateThemeToIos();
        }
    }

    private void updateThemeCommon() {
        // Top Layout
        // Top items
        for (View v : vItems) {
            ViewGroup.LayoutParams lps = v.getLayoutParams();
            if (lps instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLps = (ViewGroup.MarginLayoutParams) lps;
                marginLps.setMargins(0, 0, 0, 0);
            }
        }

        // Bottom
        layoutRoot.setBackgroundResource(getImgResId("bg_main"));
        // Top Layout
        layoutTop.setBackgroundResource(getImgResId("bg_title"));
        // Top items
        switchTab(vItemFocused, false);
    }

    /**
     * Warning Page Controller
     */
    private class WarningController {
        private boolean mmIsFirstOpen = true;
        private boolean mmIsWarningShowing = false;

        void onResume() {
            if (mmIsFirstOpen) {
                mmIsFirstOpen = false;
                checkAndShowWarning();

            } else if (mmIsWarningShowing) {
                openWarningPage();
            }
        }

        void exit() {
            mmIsWarningShowing = false;
        }

        private void checkAndShowWarning() {
            int flag = TrVideoPreferUtils.getVideoWarningFlag(false, 0);
            switch (flag) {
                case 1:
                    mmIsWarningShowing = true;
                    openWarningPage();
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }

        private void openWarningPage() {
            Intent warningIntent = new Intent(mContext, SclLc2010VdcVideoWarningActivity.class);
            startActivityForResult(warningIntent, M_REQ_WARNING);
        }

        boolean isWarningShowing() {
            return mmIsWarningShowing;
        }
    }
}
