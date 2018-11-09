package com.tricheer.radio;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tri.lib.engine.KeyEnum;
import com.tri.lib.receiver.AccReceiver;
import com.tri.lib.receiver.ReverseReceiver;
import com.tri.lib.utils.SettingsSysUtil;
import com.tricheer.radio.activity.BaseKeyEventActivity;
import com.tricheer.radio.frags.TabFreqCollectFragment;
import com.tricheer.radio.service.RadioPlayerService;
import com.tricheer.radio.utils.FreqFormatUtil;
import com.tricheer.radio.utils.TrRadioPreferUtils;
import com.tricheer.radio.view.ToastView;
import com.yj.lib.radio.engine.BandCategoryEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import js.lib.android.adapter.VPFragStateAdapter;
import js.lib.android.fragment.BaseAppV4Fragment;
import js.lib.android.media.engine.mediabtn.MediaBtnController;
import js.lib.android.media.engine.mediabtn.MediaBtnReceiver;
import js.lib.android.media.player.PlayEnableController;
import js.lib.android.view.SeekBarImpl;
import js.lib.android.view.ViewPagerImpl;

/**
 * FM Main Page
 *
 * @author Jun.Wang
 */
public class MainActivity extends BaseKeyEventActivity
        implements AccReceiver.AccDelegate, ReverseReceiver.ReverseDelegate,
        MediaBtnReceiver.MediaBtnListener {
    // TAG
    private static final String TAG = "RadioMain";

    //==========Widgets in this Activity==========
    //Top
    private ViewPagerImpl viewPager;
    private RelativeLayout vPointsContainer;
    private ImageView ivArrow;
    private LinearLayout vBgPoints;
    private ImageView vSelectedPoint;

    //Center
    private TextView tvFreq;
    private SeekBar seekBarFreq;
    private SeekBarImpl seekBarSearchingAll;

    private View layoutTower;
    private ImageView ivTower;
    private TextView tvBand;

    //Bottom
    private ImageView ivPrev, ivExit, ivNext;
    private TextView tvUpdate;

    //==========Variables in this Activity==========
    private ViewPagerOnChange mViewPageOnChange;
    private VPFragStateAdapter mFragAdapter;

    private ObjectAnimator mAnimTowerRotate;

    private static Handler mHandler = new Handler();
    private SearchingController mSearchingController;
    private ScanningController mScanningController;
    private FmStateController mFmStateController;

    private RadioPlayerService mRadioService;

    // Media Button Controller
    private MediaBtnController mMediaBtnController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        mSearchingController = new SearchingController();
        mScanningController = new ScanningController();

        mFmStateController = new FmStateController();
        mFmStateController.onCreate(this);

        //
        ReverseReceiver.register(this);
        AccReceiver.register(this);
        MediaBtnReceiver.setListener(this);
        init();
    }

    private void init() {
        //---- Variables ----
        mMediaBtnController = new MediaBtnController(this);

        //---- Widgets ----
        //Top
        vPointsContainer = (RelativeLayout) findViewById(R.id.v_points_container);
        vPointsContainer.setOnClickListener(mViewOnClick);

        viewPager = (ViewPagerImpl) findViewById(R.id.vpager);
        viewPager.setAdapter(mFragAdapter = new VPFragStateAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener((mViewPageOnChange = new ViewPagerOnChange()));

        //Center
        tvFreq = (TextView) findViewById(R.id.v_freq);
        tvFreq.setText("");

        seekBarSearchingAll = (SeekBarImpl) findViewById(R.id.seekbar_freqs_searching_all);
        seekBarSearchingAll.setCanSeek(false);
        seekBarFreq = (SeekBar) findViewById(R.id.seekbar_freqs);
        seekBarFreq.setOnSeekBarChangeListener(new SeekBarOnChange());

        layoutTower = findViewById(R.id.layout_tower);
        tvBand = (TextView) findViewById(R.id.v_band);
        tvBand.setText("");
        ivTower = (ImageView) findViewById(R.id.v_radio_tower);
        ivTower.setOnClickListener(mViewOnClick);

        //Bottom
        ivPrev = (ImageView) findViewById(R.id.iv_play_pre);
        ivPrev.setOnClickListener(mViewOnClick);

        ivNext = (ImageView) findViewById(R.id.iv_play_next);
        ivNext.setOnClickListener(mViewOnClick);

        ivExit = (ImageView) findViewById(R.id.iv_exit);
        ivExit.setOnClickListener(mViewOnClick);

        tvUpdate = (TextView) findViewById(R.id.v_update);
        tvUpdate.setOnClickListener(mViewOnClick);

        //---- Variables ----

        // Bind Service
        bindAndCreateControlService(1, 2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaBtnController.register();
        if (mRadioService != null && !isRadioOpened()) {
            execOpenRadio();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        openRadioOnNewIntent();
    }

    @Override
    public boolean openFm() {
        boolean res = super.openFm();
        mFmStateController.onFmStateChanged();
        return res;
    }

    @Override
    public boolean closeFm() {
        boolean res = super.closeFm();
        mFmStateController.onFmStateChanged();
        return res;
    }

    private void openRadioOnNewIntent() {
        if (mSearchingController.isSearching()) {
            return;
        }

        //Get parameters
        int paramBandVal = getIntent().getIntExtra("TARGET_BAND", -1);
        BandCategoryEnum paramBand = BandCategoryEnum.get(paramBandVal);
        getIntent().removeExtra("TARGET_BAND");
        Log.i(TAG, "openRadioOnNewIntent() > paramBand:" + paramBand);


        //Check and Play
        if (paramBand == BandCategoryEnum.NONE) {
            if (!isRadioOpened()) {
                execOpenRadio();
            }
        } else {
            if (isRadioOpened()) {
                BandCategoryEnum currBand = getCurrBand();
                if (paramBand != currBand) {
                    execSwitchBand();
                    return;
                }
            }
            execOpenRadio(paramBand, getLastFreq(paramBand));
        }
    }

    @Override
    protected void onServiceStatusChanged(Service service, boolean isConnected) {
        super.onServiceStatusChanged(service, isConnected);
        if (service != null) {
            mRadioService = (RadioPlayerService) service;
            refreshCollectViews();
            register(this);
            openRadioOnInit();
        }
    }

    private void openRadioOnInit() {
        final boolean isFirstTimeOpen = TrRadioPreferUtils.isFirstOpen();
        if (isFirstTimeOpen) {
            ToastView.show(this, R.string.first_time_searching_toast);
            initSeekBar(true);
        } else {
            initSeekBar(false);
        }

        //Get parameters
        final int paramBandVal = getIntent().getIntExtra("TARGET_BAND", -1);
        final BandCategoryEnum paramBand = BandCategoryEnum.get(paramBandVal);
        getIntent().removeExtra("TARGET_BAND");
        Log.i(TAG, "openRadioOnInit() > paramBand:" + paramBand);

        //Check and Play
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (paramBand == BandCategoryEnum.NONE) {
                    execOpenRadio();
                    if (isFirstTimeOpen) {
                        mSearchingController.prepare();
                    }
                } else {
                    execOpenRadio(paramBand, getLastFreq(paramBand));
                }
            }
        }, 1000);
    }

    private void refreshCollectViews() {
        //Points view
        vPointsContainer.removeAllViews();
        switch (getLastBand()) {
            case FM:
                View vFMRoot = getLayoutInflater().inflate(R.layout.v_points_fm, vPointsContainer);
                ivArrow = (ImageView) vFMRoot.findViewById(R.id.v_arrow_to_right);
                vBgPoints = (LinearLayout) vFMRoot.findViewById(R.id.v_bg_points);
                vSelectedPoint = (ImageView) vFMRoot.findViewById(R.id.v_select_point);
                break;
            case AM:
                View vAMRoot = getLayoutInflater().inflate(R.layout.v_points_am, vPointsContainer);
                ivArrow = (ImageView) vAMRoot.findViewById(R.id.v_arrow_to_right);
                vBgPoints = (LinearLayout) vAMRoot.findViewById(R.id.v_bg_points);
                vSelectedPoint = (ImageView) vAMRoot.findViewById(R.id.v_select_point);
                break;
        }

        //ViewPager
        List<BaseAppV4Fragment> listFrags = new ArrayList<BaseAppV4Fragment>();
        int loop = getPageSum();
        for (int idx = 0; idx < loop; idx++) {
            TabFreqCollectFragment frag = new TabFreqCollectFragment();
            frag.setPageIdx(idx);
            listFrags.add(frag);
        }
        mFragAdapter.refresh(listFrags, true);
    }

    private void initSeekBar(boolean isFirstTime) {
        seekBarFreq.setMax(isFirstTime ? getSeekBarMax(BandCategoryEnum.FM) : getSeekBarMax());
        seekBarFreq.setProgress(0);
    }

    @Override
    public void onFreqChanged(int freq, BandCategoryEnum band) {
        super.onFreqChanged(freq, band);
        int currProgress = freq - getMinFreq();
        seekBarFreq.setProgress(currProgress);
        setFreqInfo(freq, band);

        //
        Fragment frag = mFragAdapter.getItem(mViewPageOnChange.getPageIdx());
        if (frag != null) {
            ((TabFreqCollectFragment) frag).refreshItemsBgByCurrFreq();
        }

        //Set searching all progress
        if (seekBarSearchingAll.isEnabled()) {
            int tempProgress = currProgress + mSearchingController.getBaseProgress();
            if (tempProgress >= seekBarSearchingAll.getProgress()) {
                seekBarSearchingAll.setProgress(tempProgress);
            }
        }
    }

    private void setFreqInfo(int freq, BandCategoryEnum band) {
        if (tvFreq == null || tvBand == null) {
            return;
        }

        //Set Band/Frequency
        String txtBand = "";
        String txtFreq = "";
        switch (band) {
            case FM:
                txtBand = getString(R.string.band_fm);
//                txtFreq = txtBand + FreqFormatUtil.getFmFreqStr(freq);
                //TODO ???
                txtFreq = txtBand + FreqFormatUtil.getFmFreqStr(freq >= 10790 ? 10800 : freq);
                break;
            case AM:
                txtBand = getString(R.string.band_am);
//                txtFreq = txtBand + FreqFormatUtil.getAmFreqStr(freq);
                //TODO ???
                txtFreq = txtBand + FreqFormatUtil.getAmFreqStr(freq >= 1611 ? 1620 : freq);
                break;
        }
        tvBand.setText(txtBand);
        tvFreq.setText(txtFreq);
    }

    private View.OnClickListener mViewOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == vPointsContainer) {
                showCollectsNextPage();
            } else if (v == ivTower) {
                execSwitchBand();
            } else if (v == ivPrev) {
                scanAndPlayPrev();
            } else if (v == ivNext) {
                scanAndPlayNext();

                //
            } else if (v == ivExit) {
                if (mScanningController.isScanningPrev()) {
                    Log.i(TAG, "isScanningPrev - Stop scanning, execute play.");
                    scanAndPlayPrev();
                } else if (mScanningController.isScanningNext()) {
                    Log.i(TAG, "isScanningNext - Stop scanning, execute play.");
                    scanAndPlayNext();
                } else if (mSearchingController.isSearching()) {
                    Log.i(TAG, "isSearching - Stop searching, execute play.");
                    mSearchingController.resumeOrigin();
                } else {
                    closeFm();
                    finish();
                }

                //
            } else if (v == tvUpdate) {
                mSearchingController.start();
//                if (mSearchingController.isSearching()) {
//                    mSearchingController.resumeOrigin();
//                }
            }
        }
    };

    @Override
    protected void execSwitchBand() {
        Log.i(TAG, "execSwitchBand()");
        if (mAnimTowerRotate == null) {
            mAnimTowerRotate = ObjectAnimator.ofFloat(ivTower, "rotationY", 0, 180);
            mAnimTowerRotate.setInterpolator(new LinearInterpolator());
            mAnimTowerRotate.setDuration(300);
            mAnimTowerRotate.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mViewPageOnChange.reset();
                    viewPager.setCurrentItem(0, true);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //Switch
                    MainActivity.super.execSwitchBand();
                    //UI
                    refreshCollectViews();
                    initSeekBar(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
        if (!mAnimTowerRotate.isRunning()) {
            mAnimTowerRotate.start();
        }
    }

    @Override
    public void onScanStrongFreqLeftStart(BandCategoryEnum band) {
        super.onScanStrongFreqLeftStart(band);
        Log.i(TAG, "onScanStrongFreqLeftStart(" + band + ")");
        mScanningController.startPrev();
    }

    @Override
    public void onScanStrongFreqLeftEnd(BandCategoryEnum band) {
        super.onScanStrongFreqLeftEnd(band);
        Log.i(TAG, "onScanStrongFreqLeftEnd(" + band + ")");
        mScanningController.endPrev();
    }

    @Override
    public void onScanStrongFreqLeftFail(BandCategoryEnum band, int reason) {
        super.onScanStrongFreqLeftFail(band, reason);
        Log.i(TAG, "onScanStrongFreqLeftFail(" + band + "," + reason + ")");
        mScanningController.failPrev();
    }

    @Override
    public void onScanStrongFreqRightStart(BandCategoryEnum band) {
        super.onScanStrongFreqRightStart(band);
        Log.i(TAG, "onScanStrongFreqRightStart(" + band + ")");
        mScanningController.startNext();
    }

    @Override
    public void onScanStrongFreqRightEnd(BandCategoryEnum band) {
        super.onScanStrongFreqRightEnd(band);
        Log.i(TAG, "onScanStrongFreqRightEnd(" + band + ")");
        mScanningController.endNext();
    }

    @Override
    public void onScanStrongFreqRightFail(BandCategoryEnum band, int reason) {
        super.onScanStrongFreqRightFail(band, reason);
        Log.i(TAG, "onScanStrongFreqRightFail(" + band + "," + reason + ")");
        mScanningController.failNext();
    }

    @Override
    public void onSearchFreqStart(BandCategoryEnum band) {
        super.onSearchFreqStart(band);
        Log.i(TAG, "onSearchFreqStart(" + band + ")");
    }

    @Override
    public void onSearchFreqEnd(BandCategoryEnum band) {
        super.onSearchFreqEnd(band);
        Log.i(TAG, "onSeachFreqEnd(" + band + ")");
        TrRadioPreferUtils.saveSearchedFreqs(band, getAllAvailableFreqs());
        mSearchingController.onLastSearched(band);
    }

    @Override
    public void onSearchFreqFail(BandCategoryEnum band, int reason) {
        super.onSearchFreqFail(band, reason);
        Log.i(TAG, "onSeachFreqFail(" + band + "," + reason + ")");
        mSearchingController.onSearchFailed();
    }

    private void refreshPageOnScanning(boolean isScanning) {
        Log.i(TAG, "refreshPageOnScanning(" + isScanning + ")");
        //Refresh top
        viewPager.setScrollEnable(!isScanning);
        Fragment frag = mFragAdapter.getItem(mViewPageOnChange.getPageIdx());
        if (frag != null) {
            ((TabFreqCollectFragment) frag).refreshPageOnScanning(isScanning);
        }

        if (ivArrow != null) {
            ivArrow.setImageResource(isScanning ? R.drawable.arrow_disable : R.drawable.arrow);
        }
        if (vSelectedPoint != null) {
            vSelectedPoint.setImageResource(isScanning ? R.drawable.tab_point_current_disable : R.drawable.tab_point_current);
        }
        if (vBgPoints != null) {
            int childCount = vBgPoints.getChildCount();
            for (int idx = 0; idx < childCount; idx++) {
                View childV = vBgPoints.getChildAt(idx);
                if (childV instanceof ImageView) {
                    ((ImageView) childV).setImageResource(isScanning ? R.drawable.tab_point_bg_disable : R.drawable.tab_point_bg);
                }
            }
        }

        //Refresh SeekBar
        seekBarFreq.setEnabled(!isScanning);
        seekBarSearchingAll.setEnabled(isScanning);
        seekBarSearchingAll.setProgress(0);
        if (isScanning) {
            int fmMax = getSeekBarMax(BandCategoryEnum.FM);
            int amMax = getSeekBarMax(BandCategoryEnum.AM);
            seekBarSearchingAll.setMax(fmMax + amMax);
            seekBarSearchingAll.setVisibility(View.VISIBLE);
        } else {
            seekBarSearchingAll.setVisibility(View.GONE);
        }

        //Refresh tower
        layoutTower.setVisibility(isScanning ? View.INVISIBLE : View.VISIBLE);

        //Refresh operate
        ivPrev.setEnabled(!isScanning);
        ivPrev.setImageResource(isScanning ? R.drawable.op_prev_disable : R.drawable.btn_op_prev_selector);

        ivExit.setEnabled(!isScanning);
        ivExit.setImageResource(isScanning ? R.drawable.op_power_disable : R.drawable.btn_op_exit_selector);

        ivNext.setEnabled(!isScanning);
        ivNext.setImageResource(isScanning ? R.drawable.op_next_disable : R.drawable.btn_op_next_selector);

//        tvUpdate.setEnabled(!isScanning);
        tvUpdate.setText(isScanning ? R.string.cancel_update : R.string.radio_update);
        tvUpdate.setTextColor(isScanning ? getResources().getColor(R.color.red) : getResources().getColor(R.color.white));
        tvUpdate.setBackgroundResource(isScanning ? R.drawable.bg_border_red : R.drawable.bg_border_white);
    }

    @Override
    public void onAudioFocusDuck() {
        Log.i(TAG, "onAudioFocusGain()");
    }

    @Override
    public void onAudioFocusTransient() {
        Log.i(TAG, "onAudioFocusGain()");
    }

    @Override
    public void onAudioFocusGain() {
        Log.i(TAG, "onAudioFocusGain()");
        // closeFm() executed in service
        // Call back in activity
        if (mSearchingController.isSearching()) {
            Log.i(TAG, "onAudioFocusGain() -Resume-");
            mSearchingController.resumeOrigin();
        } else if (!isRadioOpened()) {
            Log.i(TAG, "onAudioFocusGain() -Open-");
            execOpenRadio();
        }
    }

    @Override
    public void onAudioFocusLoss() {
        Log.i(TAG, "onAudioFocusLoss()");
        mMediaBtnController.unregister();

        //Reset Radio status
        refreshPageOnScanning(false);
        mFmStateController.onFmStateChanged();
    }

    @Override
    public void onAccOn() {
        Log.i(TAG, "onAccOn()");
        if (!isRadioOpened()
                && PlayEnableController.isPlayEnable()
                && isAudioFocusRegistered()) {
            execOpenRadio();
        }
    }

    @Override
    public void onAccOff() {
        Log.i(TAG, "onAccOff()");
        closeFm();
    }

    @Override
    public void onAccOffTrue() {
        Log.i(TAG, "onAccOffTrue()");
        closeFm();
    }

    @Override
    public void onReverseOn() {
        Log.i(TAG, "onReverseOn()");
        closeFm();
    }

    @Override
    public void onReverseOff() {
        Log.i(TAG, "onReverseOff()");
        if (!isRadioOpened()
                && PlayEnableController.isPlayEnable()
                && isAudioFocusRegistered()) {
            execOpenRadio();
        }
    }

    @Override
    public void onGotMediaKeyCode(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            KeyEnum key = KeyEnum.getKey(event.getKeyCode());
            switch (key) {
                case KEYCODE_MEDIA_PREVIOUS:
                    if (!isForeground()) {
                        scanAndPlayPrev();
                    }
                    break;
                case KEYCODE_MEDIA_NEXT:
                    if (!isForeground()) {
                        scanAndPlayNext();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onGetKeyCode(int keyCode) {
        //super.onGetKeyCode(keyCode);
        KeyEnum ke = KeyEnum.getKey(keyCode);
        Log.i(TAG, "onGetKeyCode(" + ke + "-" + keyCode + ")");
        switch (ke) {
            case KEYCODE_VOLUME_UP:
                break;
            case KEYCODE_VOLUME_DOWN:
                break;
            case KEYCODE_VOLUME_MUTE:
                break;

            case KEYCODE_RADIO:
                if (mSearchingController.isSearching()) {
                    mSearchingController.resumeOrigin();
                } else {
                    execSwitchBand();
                }
                break;

            case KEYCODE_MEDIA_PREVIOUS:
                if (isForeground()) {
                    scanAndPlayPrev();
                }
                break;
            case KEYCODE_MEDIA_NEXT:
                if (isForeground()) {
                    scanAndPlayNext();
                }
                break;

            case KEYCODE_DPAD_LEFT:
                stepPrev();
                break;
            case KEYCODE_DPAD_RIGHT:
                stepNext();
                break;

            case KEYCODE_ENTER:
                break;
            case KEYCODE_HOME:
                break;
            case KEYCODE_BACK:
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        //
        if (mAnimTowerRotate != null) {
            if (mAnimTowerRotate.isRunning()) {
                mAnimTowerRotate.cancel();
                mAnimTowerRotate.end();
            }
            mAnimTowerRotate = null;
        }

        //
        MediaBtnReceiver.setListener(null);
        mMediaBtnController.unregister();

        //
        ReverseReceiver.unregister(this);
        AccReceiver.unregister(this);

        //
        closeFm();
        unregister(this);
        bindAndCreateControlService(3, 4);

        //
        mFmStateController.onDestroy();
        super.onDestroy();
    }

    private final class FmStateController {
        private Context mmContext;

        void onCreate(Context context) {
            mmContext = context;
            Log.i(TAG, "FmStateController.onCreate(Context) > status:" + 1);
            SettingsSysUtil.setFmState(mmContext, 1);
        }

        void onFmStateChanged() {
            int status = isRadioOpened() ? 2 : 1;
            Log.i(TAG, "FmStateController.onFmStateChanged() > status:" + status);
            SettingsSysUtil.setFmState(mmContext, status);
        }

        void onDestroy() {
            Log.i(TAG, "FmStateController.onDestroy() > status:" + 0);
            SettingsSysUtil.setFmState(mmContext, 0);
        }
    }

    private final class ScanningController {
        boolean mmIsScanningPrev = false;
        boolean mmIsScanningNext = false;

        void startPrev() {
            mmIsScanningPrev = true;
        }

        void endPrev() {
            mmIsScanningPrev = false;
        }

        void failPrev() {
            mmIsScanningPrev = false;
        }

        boolean isScanningPrev() {
            return mmIsScanningNext;
        }

        void startNext() {
            mmIsScanningNext = true;
        }

        void endNext() {
            mmIsScanningNext = false;
        }

        void failNext() {
            mmIsScanningNext = false;
        }

        boolean isScanningNext() {
            return mmIsScanningNext;
        }
    }

    /**
     * Class for Searching all bands.
     */
    private class SearchingController {
        //
        BandCategoryEnum mmOriginBand = BandCategoryEnum.NONE;
        int mmOriginFreq = 0;

        //
        int mmBaseProgress = 0;
        boolean mmIsSearching = false;
        BandCategoryEnum mmLastSearchedBand = BandCategoryEnum.NONE;
        Set<BandCategoryEnum> mmSetSearchedBand = new HashSet<>();

        void prepare() {
            //
            mmOriginBand = getCurrBand();
            mmOriginFreq = getCurrFreq();

            //
            mmIsSearching = true;
            refreshPageOnScanning(true);
        }

        void start() {
            prepare();
            searchAll();
        }

        void onLastSearched(BandCategoryEnum lastBand) {
            //Record searched.
            mmBaseProgress += getSeekBarMax(lastBand);
            mmLastSearchedBand = lastBand;
            mmSetSearchedBand.add(lastBand);

            //Execute next search
            BandCategoryEnum nextBand = nextBand();
            if (nextBand != BandCategoryEnum.NONE) {
                setBand(nextBand);
                initSeekBar(false);
                searchAll();
                //Search ended
            } else {
                resumeOrigin();
            }
        }

        void onSearchFailed() {
            resumeOrigin();
        }

        BandCategoryEnum nextBand() {
            BandCategoryEnum next = null;
            switch (mmLastSearchedBand) {
                case FM:
                    next = BandCategoryEnum.AM;
                    break;
                case AM:
                    next = BandCategoryEnum.FM;
                    break;
            }

            if (mmSetSearchedBand.contains(next)) {
                next = BandCategoryEnum.NONE;
            }
            return next;
        }

        boolean isSearching() {
            return mmIsSearching;
        }

        int getBaseProgress() {
            return mmBaseProgress;
        }

        void resumeOrigin() {
            refreshPageOnScanning(false);
            //TODO 需求: 定位到搜索到的第一个频率
            int firstCollectedFreq = TrRadioPreferUtils.getCollect(false, mmOriginBand, 0, 0, 0);
            if (firstCollectedFreq == -1) {
                Log.i(TAG, "mmOriginFreq : " + mmOriginFreq);
                openRadioAfterSearchedAll(mmOriginBand, mmOriginFreq);
            } else {
                Log.i(TAG, "firstCollectedFreq : " + firstCollectedFreq);
                openRadioAfterSearchedAll(mmOriginBand, firstCollectedFreq);
            }

            //Reset origin
            mmOriginBand = BandCategoryEnum.NONE;
            mmOriginFreq = 0;

            mmBaseProgress = 0;
            mmIsSearching = false;
            mmLastSearchedBand = BandCategoryEnum.NONE;
            mmSetSearchedBand.clear();
        }
    }

    @Override
    protected void openRadioAfterSearchedAll(BandCategoryEnum band, int freq) {
        super.openRadioAfterSearchedAll(band, freq);
        //UI
        refreshCollectViews();
        initSeekBar(false);
        ivTower.setEnabled(true);
    }

    /**
     * Show next page
     */
    private void showCollectsNextPage() {
        int pageIdx = mViewPageOnChange.getPageIdx();
        pageIdx++;
        if (pageIdx >= getPageSum()) {
            pageIdx = 0;
        }
        viewPager.setCurrentItem(pageIdx);
    }

    /**
     * View Pager adapter
     */
    private class ViewPagerOnChange implements ViewPager.OnPageChangeListener {

        /**
         * 记录上一次页面索引
         */
        private int mmLastPageIdx = 0;

        /**
         * @param pos 1 开始滑动 2 滑动完毕 0 保持不变
         */
        @Override
        public void onPageScrollStateChanged(int pos) {
            // Log.i(TAG, "TabFragOnPageChange> onPageScrollStateChanged(" + pos + ")");
        }

        /**
         * @param pos             表示的当前屏幕显示的左边页面的position
         * @param posOffset       表示的当前屏幕显示的左边页面偏移的百分比
         * @param posOffsetPixels 向右滑动到头再向左滑动到头变化规律
         */
        @Override
        public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) {
            Log.i(TAG, "TabFragOnPageChange> onPageScrolled(" + pos + "," + posOffset + "," + posOffsetPixels + ")");
        }

        /**
         * @param pos 滑动停止后调用，表示当前选中页面的索引
         */
        @Override
        public void onPageSelected(int pos) {
            Log.i(TAG, "TabFragOnPageChange> onPageSelected(" + pos + ")");
            // 如果圆点背景视图未加载
            if (vBgPoints == null) {
                return;
            }

            // 多圆点背景宽
            int layoutW = vBgPoints.getWidth();
            // 单个圆点宽
            int pointW = vSelectedPoint.getWidth();
            // 背景有几个圆点
            int pointSum = vBgPoints.getChildCount();
            // 背景圆点间距
            int spaceOfTwoPoints = (layoutW - pointSum * pointW) / (pointSum - 1);

            // 旧位置
            int lastSeat = mmLastPageIdx * (pointW + spaceOfTwoPoints);
            // 新位置
            int newSeat = pos * (pointW + spaceOfTwoPoints);

            // 实心圆点起始位置
            // 向右
            transPoint(lastSeat, newSeat);
            if (pos > mmLastPageIdx) {
                Log.i(TAG, ">>>>>>>>>>>>");
                // 向左
            } else if (pos < mmLastPageIdx) {
                Log.i(TAG, "<<<<<<<<<<<<");
            }

            // 记录上一次页面索引
            mmLastPageIdx = pos;
        }

        private void transPoint(final float fromXDelta, final float toXDelta) {
            if (vSelectedPoint != null) {
                ObjectAnimator objAnim = ObjectAnimator.ofFloat(vSelectedPoint, "x", fromXDelta, toXDelta);
                objAnim.setInterpolator(new LinearInterpolator());
                objAnim.setDuration(300);
                objAnim.start();
            }
        }

        int getPageIdx() {
            return mmLastPageIdx;
        }

        void reset() {
            mmLastPageIdx = 0;
        }
    }

    /**
     * FM has 6 pages. AM has 3 pages.
     *
     * @return int
     */
    private int getPageSum() {
        final BandCategoryEnum lastBand = getLastBand();
        switch (lastBand) {
            case FM:
                return 6;
            case AM:
                return 2;
        }
        return 6;
    }

    /**
     * {@link SeekBar} listener
     */
    private class SeekBarOnChange implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "onStartTrackingTouch");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "onStopTrackingTouch");
            int targetFreq = getMinFreq() + seekBar.getProgress();
            play(targetFreq);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setFreqInfo(getMinFreq() + progress, getCurrBand());
        }

    }

    /**
     * Play collected frequency
     */
    public void playCollected(int freq) {
        play(freq);
    }
}