package com.tricheer.player.version.cj.slc_lc2010_vdc.activity;

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
import com.tricheer.player.R;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.version.base.activity.video.BaseVideoUIActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.BaseVideoListFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcVideoFoldersFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcVideoNamesFrag;

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
    private static final String TAG = "VideoListActivityImpl";

    /**
     * ==========Widgets in this Activity==========
     */
    private View layoutRoot;
    private View layoutTop;
    private View vRubbishFocus;
    private View[] vItems = new View[2];


    //==========Variables in this Activity==========
    /**
     * Context
     */
    private Context mContext;
    private static Handler mHandler = new Handler();

    /**
     * Flag - If open player automatically?
     * <p>Only execute once in this activity.</p>
     */
    private boolean mIsAutoPlay = true;

    // Flag - If execute scanning when local media data is null ?
    private boolean mIsScanOnLocalIsNull = true;

    // Request Current Playing Media Url
    protected final int M_REQ_WARNING = 2;

    // Current page fragment.
    private BaseVideoListFrag mFragMedias;
    private View mFragItemV;

    /**
     * Warning page controller.
     */
    private WarningController mWarningController;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.scl_lc2010_vdc_activity_video_list);
        PlayerAppManager.putCxt(PlayerCxtFlag.VIDEO_LIST, this);
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
        vItems[0] = findViewById(R.id.v_media_name);
        vItems[0].setOnClickListener(mFilterViewOnClick);

        vItems[1] = findViewById(R.id.v_folder);
        vItems[1].setOnClickListener(mFilterViewOnClick);

        //
        bindScanService(true);
    }

    @Override
    protected void onScanServiceConnected() {
        switchTab(vItems[0], true);
        loadLocalMedias();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWarningController.onResume();
    }

    @Override
    public void onPlayFromFolder(int playPos, List<String> listPlayPaths) {
    }

    @Override
    public void onPlayFromFolder(Intent data) {
    }

    @Override
    protected void loadLocalMedias() {
        Log.i(TAG, "loadLocalMedias()");
        CommonUtil.cancelTask(mLoadLocalMediasTask);
        mLoadLocalMediasTask = new LoadLocalMediasTask();
        mLoadLocalMediasTask.execute(new LoadMediaListener() {

            @Override
            public void afterLoad(String selectMediaUrl) {
                Log.i(TAG, "loadLocalMedias() ->afterLoad(" + selectMediaUrl + ")");
                if (EmptyUtil.isEmpty(mListPrograms)) {
                    if (mIsScanOnLocalIsNull) {
                        mIsScanOnLocalIsNull = false;
                        startScan();
                    }
                } else {
                    refreshData();
                }
            }

            @Override
            public void refreshUI() {
            }
        });
    }

    @Override
    public void onMediaScanningStart() {
        super.onMediaScanningStart();
        Log.i(TAG, "onMediaScanningStart()");
        if (mFragMedias != null) {
            mFragMedias.onMediaScanningStart();
        }
    }

    @Override
    public void onMediaScanningEnd() {
        super.onMediaScanningEnd();
        Log.i(TAG, "onMediaScanningEnd()");
        if (mFragMedias != null) {
            mFragMedias.onMediaScanningEnd();
        }
        loadLocalMedias();
    }

    @Override
    public void onMediaScanningCancel() {
        super.onMediaScanningCancel();
        Log.i(TAG, "onMediaScanningCancel()");
//        if (mFragMedias != null) {
//            mFragMedias.onMediaScanningCancel();
//        }
    }

    @Override
    public void onMediaScanningRefresh(final List<ProVideo> listMedias, boolean isOnUiThread) {
        super.onMediaScanningRefresh(listMedias, isOnUiThread);
        Log.i(TAG, "onMediaScanningRefresh(List<ProAudio>," + isOnUiThread + ")");
        final List<ProVideo> listDeltaMedias = new ArrayList<>(listMedias);
        if (!isOnUiThread) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onMediaScanningRefresh(List<ProVideo>) -2-" + listDeltaMedias.size());
                    if (EmptyUtil.isEmpty(mListPrograms)) {
                        mListPrograms = new ArrayList<>(listDeltaMedias);
                        refreshData();
                    }
                }
            });
        }
    }

    public List<ProVideo> getListMedias() {
        return mListPrograms;
    }

    private void refreshData() {
        //sbLetters.refreshLetters(mListSortLetters.toArray());
        if (mFragMedias != null) {
            mFragMedias.refreshData(mListPrograms, getLastMediaPath());
        }
    }

    private View.OnClickListener mFilterViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == vItems[0]) {
                switchTab(v, true);
            } else {
                switchTab(v, true);
            }
        }
    };

    private void switchTab(View v, boolean isExecLoad) {
        mFragItemV = v;
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

    private void loadFragment(int idx) {
        //Remove Old
        if (mFragMedias != null) {
            FragUtil.removeV4Fragment(mFragMedias, getSupportFragmentManager());
        }

        //Load New
        switch (idx) {
            case 0:
                mFragMedias = new SclLc2010VdcVideoNamesFrag();
                break;
            case 1:
                mFragMedias = new SclLc2010VdcVideoFoldersFrag();
                break;
        }
        if (mFragMedias != null) {
            FragUtil.loadV4Fragment(R.id.layout_frag, mFragMedias, getSupportFragmentManager());
        }
    }

    private void setBg(View v, boolean selected) {
        if (selected) {
            v.setBackgroundResource(getImgResId("bg_title_item_c"));
        } else {
            v.setBackgroundResource(getImgResId("btn_filter_tab_selector"));
        }
    }

    @Override
    public void onGetKeyCode(KeyEnum key) {
        moveFocusToRubbish(vRubbishFocus);
        switch (key) {
            case KEYCODE_MEDIA_PREVIOUS:
                if (mFragMedias != null) {
                    mFragMedias.prev();
                }
                break;
            case KEYCODE_MEDIA_NEXT:
                if (mFragMedias != null) {
                    mFragMedias.next();
                }
                break;
            case KEYCODE_DPAD_LEFT:
                if (mFragMedias != null) {
                    mFragMedias.prev();
                }
                break;
            case KEYCODE_DPAD_RIGHT:
                if (mFragMedias != null) {
                    mFragMedias.next();
                }
                break;
            case KEYCODE_ENTER:
//                if (mFragMedias != null) {
//                    mFragMedias.playSelected();
//                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String flag = data.getStringExtra("flag");
            if ("EXIT_WARNING".equals(flag)) {
                mWarningController.exit();
                if (mFragMedias instanceof SclLc2010VdcVideoNamesFrag) {
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFragMedias.play();
                        }
                    }, 800);
                }
            }
        }
    }

    public boolean isWarningShowing() {
        return mWarningController.isWarningShowing();
    }

    @Override
    public void onBackPressed() {
        if (mFragMedias != null) {
            int backRes = mFragMedias.onBackPressed();
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
    protected void onDestroy() {
        Log.i(TAG, "onDestroy()");
        mHandler.removeCallbacksAndMessages(null);
        bindScanService(false);
        PlayerAppManager.removeCxt(PlayerCxtFlag.VIDEO_LIST);
        super.onDestroy();
    }

    @Override
    public void switchPlayMode(int supportFlag) {
    }

    @Override
    public void onAccOff() {
    }

    @Override
    public void onAccOffTrue() {
        Log.i(TAG, "onAccOffTrue()");
        bindScanService(false);
        PlayerAppManager.exitCurrPlayer();
    }

    @Override
    public void onAccOn() {
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

    public boolean isAutoPlay() {
        if (mIsAutoPlay) {
            mIsAutoPlay = false;
            return true;
        }
        return false;
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
    public void updateThemeToDefault() {
        super.updateThemeToDefault();
        Log.i(TAG, "updateThemeToDefault()");
        //Common
        updateThemeCommon();

        //Fragment
        if (mFragMedias != null) {
            mFragMedias.updateThemeToDefault();
        }
    }

    @Override
    public void updateThemeToIos() {
        Log.i(TAG, "updateThemeToIos()");
        // Top Layout
        // Top items
        for (View v : vItems) {
            ViewGroup.LayoutParams lps = v.getLayoutParams();
            if (lps instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLps = (ViewGroup.MarginLayoutParams) lps;
                marginLps.topMargin = 0;
                marginLps.bottomMargin = 0;
            }
        }

        //Common
        updateThemeCommon();

        //Fragment
        if (mFragMedias != null) {
            mFragMedias.updateThemeToIos();
        }
    }

    private void updateThemeCommon() {
        // Bottom
        layoutRoot.setBackgroundResource(getImgResId("bg_main"));
        // Top Layout
        layoutTop.setBackgroundResource(getImgResId("bg_title"));
        // Top items
        switchTab(mFragItemV, false);
    }
}
