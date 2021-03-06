package com.yj.video.version.cj.slc_lc2010_vdc.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tri.lib.engine.BtCallStateController;
import com.tri.lib.engine.KeyEnum;
import com.tri.lib.receiver.AccReceiver;
import com.tri.lib.receiver.ActionEnum;
import com.tri.lib.receiver.ReverseReceiver;
import com.tri.lib.receiver.VoiceAssistantReceiver;
import com.tri.lib.utils.PanelTouchImpl;
import com.tri.lib.utils.TrVideoPreferUtils;
import com.yj.video.R;
import com.yj.video.engine.PlayerAppManager;
import com.yj.video.engine.PlayerConsts;
import com.yj.video.receiver.TestReceiver;
import com.yj.video.version.base.activity.video.BaseVideoPlayerActivity;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.engine.video.MediaLightModeController;
import js.lib.android.media.player.PlayEnableController;
import js.lib.android.media.player.PlayMode;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.android.utils.gps.GpsImpl;
import js.lib.utils.date.DateFormatUtil;

/**
 * SLC_LC2010_VDC Video Player Activity
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcVideoPlayerActivity extends BaseVideoPlayerActivity
        implements AccReceiver.AccDelegate,
        ReverseReceiver.ReverseDelegate,
        BtCallStateController.BtCallSateDelegate,
        VoiceAssistantReceiver.VoiceAssistantDelegate,
        TestReceiver.TestSendGpsListener {

    //TAG
    private static final String TAG = "video_player";

    //==========Widget in this Activity==========
    private View layoutRoot;
    private View layoutTop;
    private View vCoverPanel;
    private ImageView vArrowRight, vArrowLeft;

    //
    private RelativeLayout rlVvPlayerBorder;
//    private IVideoPlayer vvPlayer;

    private View vControlPanel;
    private TextView tvFolder, tvPosition, tvName;
    private ImageView ivPlayModeSet, ivList;
    private View layoutWarning;

    //==========Variables in this Activity==========
    //Handlers
    private Handler mDelaySetStatusBar = new Handler();
    private Handler mTestSendGpsHandler = new Handler();

    /**
     * 播放器是否已经打开了
     */
    private static boolean mIsPlayerOpened = false;

    //
    private PanelTouchResp mPanelTouchResp;

    private SeekBarOnChange mSeekBarOnChange;

    //Gps listener
    private GpsImpl mGpsImpl;
    private GpsOnChange mGpsOnChange;

    //Media light mode controller
    private MediaLightModeController mLightModeController;

    // BT call state controller
    private BtCallStateController mBtCallStateController;

    /**
     * Voice command cache
     */
    private ActionEnum mVoiceCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scl_lc2010_vdc_activity_video_player);
        mIsPlayerOpened = true;
        PlayerAppManager.addContext(this);
        AccReceiver.register(this);
        ReverseReceiver.register(this);
        VoiceAssistantReceiver.register(this);
        TestReceiver.setTestSendGpsListener(this);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void init() {
        super.init();

        // ----Variables----
        // Light mode controller
        mLightModeController = new MediaLightModeController();
        mLightModeController.addModeListener(new MediaLightModeOnChange());

        //BT call
        mBtCallStateController = new BtCallStateController(this);
        mBtCallStateController.register(this);

        // ----Widgets----
        layoutRoot = findRootView();
        layoutTop = findViewById(R.id.layout_top);
        vArrowLeft = (ImageView) findViewById(R.id.v_arrow_left);
        vArrowRight = (ImageView) findViewById(R.id.v_arrow_right);

        // Cover panel
        PanelTouchImpl mPanelTouchImpl = new PanelTouchImpl();
        mPanelTouchImpl.init(this);
        mPanelTouchImpl.addCallback((mPanelTouchResp = new PanelTouchResp()));

        vCoverPanel = findViewById(R.id.vv_cover);
        vCoverPanel.setOnTouchListener(mPanelTouchImpl);

        //
        vControlPanel = findViewById(R.id.v_control_panel);
        tvFolder = (TextView) findViewById(R.id.v_folder_name);
        tvFolder.setText("");

        tvPosition = (TextView) findViewById(R.id.v_sort);
        tvPosition.setText("");

        tvName = findView(R.id.v_name);
        tvName.setText("");

        rlVvPlayerBorder = (RelativeLayout) findViewById(R.id.rl_vv_border);
        vvPlayer = findView(R.id.vv_player);
        vvPlayer.setPlayStateListener(this);
        vvPlayer.setKeepScreenOn(true);
        vvPlayer.setDrawingCacheEnabled(false);
        setCanPlayAtBg(false);

        tvStartTime = findView(R.id.tv_play_start_time);
        tvEndTime = findView(R.id.tv_play_end_time);
        seekBar = findView(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener((mSeekBarOnChange = new SeekBarOnChange()));
        seekBar.setOnTouchListener(new SeekOnTouch());

        ivPlayPre = findView(R.id.iv_play_pre);
        ivPlayPre.setOnClickListener(mViewOnClick);

        ivPlay = findView(R.id.iv_play);
        ivPlay.setOnClickListener(mViewOnClick);

        ivPlayNext = findView(R.id.iv_play_next);
        ivPlayNext.setOnClickListener(mViewOnClick);

        ivPlayModeSet = (ImageView) findViewById(R.id.iv_play_mode_set);
        ivPlayModeSet.setOnClickListener(mViewOnClick);
        onPlayModeChange();

        ivList = (ImageView) findViewById(R.id.v_list);
        ivList.setOnClickListener(mViewOnClick);

        //Warning
        mGpsImpl = new GpsImpl(this);//Register GPS
        mGpsImpl.setGpsImplListener((mGpsOnChange = new GpsOnChange()));

        layoutWarning = findViewById(R.id.layout_warning);
        layoutWarning.setVisibility(View.GONE);
        layoutWarning.setOnTouchListener(new WarningPageOnTouch());

        // Load Data
        checkingData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        //Resume play
        if (!isPlaying()) {
            resume();
        }
    }

    private void checkingData() {
        String openMethod = getIntent().getStringExtra(PlayerConsts.PlayerOpenMethod.PARAM);
        if (openMethod != null) {
            getIntent().removeExtra(PlayerConsts.PlayerOpenMethod.PARAM);
        }

        //
        if (PlayerConsts.PlayerOpenMethod.VAL_FILE_MANAGER.equalsIgnoreCase(openMethod)) {
            onPlayFromFolder(getIntent());
        } else {
            loadLocalMedias();
        }
    }

    @Override
    public void onPlayFromFolder(Intent data) {
        //Check null
        if (data == null || data.getExtras() == null) {
            return;
        }

        //Media list
        ArrayList<String> listUrls = getIntent().getStringArrayListExtra(PlayerConsts.FILE_LIST);
        getIntent().removeExtra(PlayerConsts.FILE_LIST);
        //Position of target media url
        int index = getIntent().getIntExtra(PlayerConsts.INDEX, 0);
        getIntent().removeExtra(PlayerConsts.INDEX);

        //Construct List<ProVideo>
        if (!EmptyUtil.isEmpty(listUrls)) {
            mListPrograms = new ArrayList<>();
            for (String url : listUrls) {
                File file = new File(url);
                if (file.exists() && file.isFile()) {
                    ProVideo media = new ProVideo();
                    media.title = file.getName();
                    media.mediaUrl = url;
                    mListPrograms.add(media);
                }
            }
        }

        //Play
        if (index < 0 || index >= listUrls.size()) {
            return;
        }
        execPlay(index);
    }

    @Override
    protected void onScanServiceConnected() {
    }

    private void loadLocalMedias() {
        // Resume Light MODE
        mLightModeController.resetLightMode();
        // Play
        playByIntent();
    }

    @SuppressWarnings("unchecked")
    private void playByIntent() {
        String mediaUrl = getIntent().getStringExtra("SELECT_MEDIA_URL");
        Serializable serialListPros = getIntent().getSerializableExtra("MEDIA_LIST");
        if (mediaUrl != null && serialListPros != null) {
            // Play
            mListPrograms = (ArrayList<ProVideo>) serialListPros;
            if (!EmptyUtil.isEmpty(mListPrograms)) {
                execPlay(mediaUrl);
            }
        }
    }

    @Override
    public void onProgressChanged(String mediaUrl, int progress, int duration) {
        Logs.debugI(TAG, "onProgressChanged(" + mediaUrl + "," + progress + "," + duration + ")");
//        final String targetMediaUrl = mediaUrl;
        final int targetProgress = progress;
        final int targetDuration = duration;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 如下2种情况，不执行任何操作
                // (1) 未处于正在播放中
                // (2) SeekBar 正在进行手动拖动进度条
                if (!isPlaying() || mSeekBarOnChange.isTrackingTouch()) {
                    return;
                }

                // 不允许播放
                if (!PlayEnableController.isPlayEnable()) {
                    removePlayRunnable();
                    pause();
                    return;
                }

                // 视频播放 - {正常模式}
                if (targetProgress > targetDuration) {
                    Log.i(TAG, "onProgressChanged :: error progress-playNext()");
                    clearPlayedMediaInfo();
                    playNext();
                } else {
                    seekBar.setProgress(targetProgress);//跟新进度条
                    updateSeekTime(targetProgress, targetDuration);//更新时间
                    savePlayInfo();// 每秒钟保存一次播放信息
                }
            }
        });
    }

    private class PanelTouchResp implements PanelTouchImpl.PanelTouchCallback {

        private Handler mmHandler = new Handler();
        private boolean mIsSeekRunning = false;

        @Override
        public void onActionDown() {
            Log.i(TAG, "onActionDown()");
            cancelRunnable();
        }

        @Override
        public void onActionUp() {
            Log.i(TAG, "onActionUp()");
            if (mIsSeekRunning) {
                delayInvisible();
            }
        }

        @Override
        public void onSingleTapUp() {
            Log.i(TAG, "onSingleTapUp()");
            mLightModeController.switchLightMode();
        }

        @Override
        public void onPrepareAdjustBrightness() {
            Log.i(TAG, "onPrepareAdjustBrightness()");
        }

        @Override
        public void onAdjustBrightness(double rate) {
            Log.i(TAG, "onAdjustBrightness(" + rate + ")");
        }

        @Override
        public void onPrepareAdjustVol() {
            Log.i(TAG, "onPrepareAdjustVol()");
        }

        @Override
        public void onAdjustVol(int vol, int maxVol) {
            Log.i(TAG, "onAdjustVol(" + vol + "," + maxVol + ")");
        }

        @Override
        public void onPrepareAdjustProgress() {
            Log.i(TAG, "onPrepareAdjustProgress()");
        }

        @Override
        public void onAdjustProgress(int direction, int progressDelta) {
            Log.i(TAG, "onAdjustProgress(" + direction + "," + progressDelta + ")");
            switch (direction) {
                case 0:
                    Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    break;
                case 1:
                    Log.i(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                    break;
            }
        }

        @Override
        public void seekProgress(int direction, int progressDelta) {
            Log.i(TAG, "seekProgress(" + direction + "," + progressDelta + ")");
            switch (direction) {
                case 0:
                    //
                    vArrowLeft.setVisibility(View.INVISIBLE);
                    vArrowRight.setVisibility(View.VISIBLE);
                    vArrowRight.invalidate();
                    //
                    long targetProgress = getProgress() + 15 * 1000;
                    if (targetProgress > getDuration()) {
                        targetProgress = getDuration();
                    }
                    seekTo((int) targetProgress);
                    break;
                case 1:
                    //
                    vArrowLeft.setVisibility(View.VISIBLE);
                    vArrowLeft.invalidate();
                    vArrowRight.setVisibility(View.INVISIBLE);
                    //
                    targetProgress = getProgress() - 15 * 1000;
                    if (targetProgress < 0) {
                        targetProgress = 0;
                    }
                    seekTo((int) targetProgress);
                    break;
            }
            mIsSeekRunning = true;
            delayInvisible();
        }

        void delayInvisible() {
            cancelRunnable();
            mmHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsSeekRunning = false;
                    vArrowLeft.setVisibility(View.INVISIBLE);
                    vArrowRight.setVisibility(View.INVISIBLE);
                }
            }, 1000);
        }

        void cancelRunnable() {
            mmHandler.removeCallbacksAndMessages(null);
        }

        void destroy() {
            cancelRunnable();
        }
    }

    /**
     * View Click Event
     */
    private View.OnClickListener mViewOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mLightModeController.resetLightMode();
            if (v == ivPlayPre) {
                execPlayPrevByUser();
            } else if (v == ivPlay) {
                execPlayOrPauseByUser();
            } else if (v == ivPlayNext) {
                execPlayNextByUser();

            } else if (v == ivPlayModeSet) {
                switchPlayMode(53);
            } else if (v == ivList) {
                finishByOperate("EXIT_VIDEO_PLAYER");
            }
        }
    };

    @Override
    public void onPlayModeChange() {
        super.onPlayModeChange();
        Logs.i(TAG, "^^ onPlayModeChange() ^^");
        PlayMode storePlayMode = TrVideoPreferUtils.getPlayMode(false, PlayMode.LOOP);
        Logs.i(TAG, "onPlayModeChange() -> [storePlayMode:" + storePlayMode + "]");
        if (storePlayMode != null) {
            switch (storePlayMode) {
                case LOOP:
                    updateImgRes(ivPlayModeSet, "btn_op_mode_loop_selector");
                    break;
                case SINGLE:
                    updateImgRes(ivPlayModeSet, "btn_op_mode_oneloop_selector");
                    break;
            }
        }
    }

    private void finishByOperate(String flag) {
        Intent data = new Intent();
        data.putExtra("flag", flag);
        setResult(0, data);
        finish();
    }

    @Override
    protected void updatePlayStatus(int flag) {
        super.updatePlayStatus(flag);
        switch (flag) {
            case 1:
                updateImgRes(ivPlay, "btn_op_pause_selector");
                break;
            case 2:
                updateImgRes(ivPlay, "btn_op_play_selector");
                break;
        }
    }

    @Override
    protected void updateSeekTime(int progress, int duration) {
        // super.updateSeekTime(progress, duration);
        // Set Time Display
        if (tvStartTime != null) {
            tvStartTime.setText(DateFormatUtil.getFormatHHmmss(progress));
            Logs.debugI("updateTime", "StartTime --> " + tvStartTime.getText().toString());
        }
        if (tvEndTime != null) {
            tvEndTime.setText(DateFormatUtil.getFormatHHmmss(duration));
            Logs.debugI("updateTime", "EndTime --> " + tvEndTime.getText().toString() + "\n ");
        }
    }

    @Override
    protected void onNotifyPlayState$Play() {
        ProVideo media = getCurrProgram();
        if (media == null) {
            return;
        }

        File file = new File(media.mediaUrl);
        if (file.exists()) {
            //Folder
            File folder = file.getParentFile();
            if (folder != null) {
                tvFolder.setText(folder.getName());
            }

            //Title
            tvName.setText(media.title);

            // Position
            String formatStr = getString(R.string.video_pos_str);
            String currPosStr = String.valueOf((getCurrIdx() + 1));
            String totalCountStr = String.valueOf(getTotalCount());
            tvPosition.setText(String.format(formatStr, currPosStr, totalCountStr));
        }

        //Parse scale information
        scaleScreen(media);
    }

    private void scaleScreen(final ProVideo media) {
        if (media == null) {
            return;
        }
        if (media.width == 0 || media.height == 0) {
            ProVideo.parseMediaScaleInfo(this, media);
        }
        Log.i(TAG, "media :: resolution - " + media.width + "x" + media.height);
        if (media.width == 0 || media.height == 0) {
            return;
        }

        //
        layoutRoot.post(new Runnable() {
            @Override
            public void run() {
                int rootW = layoutRoot.getWidth();
                int rootH = layoutRoot.getHeight();
                Log.i(TAG, "root :: resolution - " + rootW + "x" + rootH);

                //
                int targetW = 0, targetH = 0;
                double mediaRate = ((double) media.width) / media.height;
                double rootRate = ((double) rootW) / rootH;
                if (mediaRate > rootRate) {
                    targetW = rootW;
                    targetH = (int) (targetW / mediaRate);
                    Log.i(TAG, "target1 :: resolution - " + targetW + "x" + targetH);

                } else if (mediaRate < rootRate) {
                    targetH = rootH;
                    targetW = (int) (targetH * mediaRate);
                    Log.i(TAG, "target1 :: resolution - " + targetW + "x" + targetH);
                }

                if (targetW > 0 && targetH > 0) {
                    ViewGroup.LayoutParams lps = rlVvPlayerBorder.getLayoutParams();
                    lps.width = targetW;
                    lps.height = targetH;
                    rlVvPlayerBorder.setLayoutParams(lps);
                }
            }
        });
    }

    @Override
    public void onGetKeyCode(KeyEnum key) {
        Log.i(TAG, "onGetKeyCode(" + key + ")");
        switch (key) {
            case KEYCODE_MEDIA_PREVIOUS:
                execPlayPrevByUser();
                break;
            case KEYCODE_MEDIA_NEXT:
                execPlayNextByUser();
                break;
        }
    }

    @Override
    public void onAccOffTrue() {
        Log.i(TAG, "onAccOffTrue()");
        bindScanService(false);
        PlayerAppManager.exitCurrPlayer();
    }

    @Override
    public void onReverseOn() {
        Log.i(TAG, "onReverseOn()");
        if (isPlaying()) {
            pause();
        }
    }

    @Override
    public void onReverseOff() {
        Log.i(TAG, "onReverseOff()");
        if (!isPlaying()
                && isForeground()
                && isAudioFocusGained()) {
            Log.i(TAG, "onReverseOff -> resume()");
            resume();
        }
    }

    @Override
    public void onBtCallStateChanged(final boolean isBtRunning) {
        Log.i(TAG, "onBtCallStateChanged(" + isBtRunning + ")");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isBtRunning) {
                    Log.i(TAG, "onBtCallStateChanged -> pause()");
                    setStatusBar(1);
                    if (isPlaying()) {
                        pause();
                    }
                } else {
                    Log.i(TAG, "onBtCallStateChanged -> resume()");
                    //TODO 恢复播放由onResume时执行
                    mDelaySetStatusBar.removeCallbacksAndMessages(null);
                    mDelaySetStatusBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setStatusBar(0);
                        }
                    }, 1500);
//            if (!isPlaying()
//                    && isForeground()
//                    && isAudioFocusGained()) {
//                Log.i(TAG, "onReverseOff -> resume()");
//                resume();
//            }
                }
            }
        });
    }

    @Override
    public void onVoiceCommand(ActionEnum ae) {
        Log.i(TAG, "onVoiceCommand(" + ae + ")");
        mVoiceCommand = ae;
        if (isAudioFocusGained()) {
            processVoiceCommand();
        }
    }

    private void processVoiceCommand() {
        if (mVoiceCommand != null) {
            Log.i(TAG, "processVoiceCommand() - mVoiceCommand:" + mVoiceCommand);
            switch (mVoiceCommand) {
                case MEDIA_PLAY_PREV:
                    if (isForeground()) {
                        execPlayPrevByUser();
                    }
                    break;
                case MEDIA_PLAY_NEXT:
                    if (isForeground()) {
                        execPlayNextByUser();
                    }
                    break;
                case MEDIA_PLAY:
                    if (isForeground()) {
                        execResumeByUser();
                    }
                    break;
                case MEDIA_PAUSE:
                    if (isPlaying()) {
                        execPauseByUser();
                    }
                    break;
            }
            mVoiceCommand = null;
        }
    }

    @Override
    public void onAudioFocusTransient() {
        super.onAudioFocusTransient();
        Log.i(TAG, "onAudioFocusTransient()");
        if (isPlaying()) {
            pause();
        }
    }

    @Override
    public void onAudioFocusDuck() {
        super.onAudioFocusDuck();
        Log.i(TAG, "onAudioFocusDuck()");
    }

    @Override
    public void onAudioFocusLoss() {
        super.onAudioFocusLoss();
        Log.i(TAG, "onAudioFocusLoss()");
        if (isPlaying()) {
            pause();
        }
    }

    @Override
    public void onAudioFocusGain() {
        super.onAudioFocusGain();
        Log.i(TAG, "onAudioFocusGain()");
        if (mVoiceCommand == null) {
            if (isForeground()) {
                resume();
            }
        } else {
            processVoiceCommand();
        }
    }

    @Override
    public void onAudioFocus(int flag) {
        super.onAudioFocus(flag);
        Log.i(TAG, "onAudioFocus(" + flag + ")");
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Log.i(TAG, "onBackPressed()");
        finish();
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
        AccReceiver.unregister(this);
        ReverseReceiver.unregister(this);
        VoiceAssistantReceiver.unregister(this);
        TestReceiver.setTestSendGpsListener(null);

        //Remove handler runnable
        if (mDelaySetStatusBar != null) {
            mDelaySetStatusBar.removeCallbacksAndMessages(null);
            mDelaySetStatusBar = null;
        }
        if (mTestSendGpsHandler != null) {
            mTestSendGpsHandler.removeCallbacksAndMessages(null);
            mTestSendGpsHandler = null;
        }

        // 释放播放器
        execRelease();

        //
        if (mBtCallStateController != null) {
            mBtCallStateController.unregister();
            mBtCallStateController = null;
        }

        //
        if (mLightModeController != null) {
            mLightModeController.destroy();
            mLightModeController = null;
            CommonUtil.setNavigationBar(this, 1);
        }

        //
        if (mGpsImpl != null) {
            mGpsImpl.destroy();
            mGpsImpl = null;
        }

        //
        if (mPanelTouchResp != null) {
            mPanelTouchResp.destroy();
            mPanelTouchResp = null;
        }

        //
        mIsPlayerOpened = false;
        PlayerAppManager.removeContext(this);
    }

    /**
     * 播放器是否已经打开了
     */
    public static boolean isPlayerOpened() {
        return mIsPlayerOpened;
    }

    /**
     * SeekBar Seek Event
     */
    public class SeekBarOnChange implements SeekBar.OnSeekBarChangeListener {

        int mmProgress = 0;
        boolean mmIsTrackingTouch = false;

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "SeekBarOnChange - onStartTrackingTouch(SeekBar)");
            mmIsTrackingTouch = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "SeekBarOnChange - onStopTrackingTouch(SeekBar)");
            if (mmIsTrackingTouch) {
                mmIsTrackingTouch = false;
                seekTo(mmProgress);
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Logs.debugI(TAG, "SeekBarOnChange - onProgressChanged(SeekBar," + progress + "," + fromUser + ")");
            if (fromUser) {
                mmProgress = progress;
            }
        }

        boolean isTrackingTouch() {
            return mmIsTrackingTouch;
        }
    }

    /**
     * SeekBar Touch Event
     */
    public class SeekOnTouch implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Logs.i(TAG, "seekBar -> SeekOnTouch -> ACTION_DOWN");
                    mLightModeController.keepLightOn();
                    break;
                case MotionEvent.ACTION_UP:
                    Logs.i(TAG, "seekBar -> SeekOnTouch -> ACTION_UP");
                    mLightModeController.resetLightMode();
                    break;
            }
            return false;
        }
    }

    /**
     * {@link js.lib.android.media.engine.video.MediaLightModeController.MediaLightModeListener} implement
     */
    private class MediaLightModeOnChange implements MediaLightModeController.MediaLightModeListener {
        @Override
        public void onLightOn() {
            Log.i(TAG, "onLightOn()");
            vControlPanel.setVisibility(View.VISIBLE);
            setStatusBar(1);
        }

        @Override
        public void onLightOff() {
            Log.i(TAG, "onLightOff()");
            vControlPanel.setVisibility(View.GONE);
            setStatusBar(0);
        }
    }

    private void setStatusBar(int flag) {
        CommonUtil.setNavigationBar(SclLc2010VdcVideoPlayerActivity.this, flag);
        scaleScreen(getCurrProgram());
    }

    /**
     * Warning page touch event.
     */
    private class WarningPageOnTouch implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    testSendGps(0);
                    Log.i(TAG, "WarningPageOnTouch -MotionEvent.ACTION_UP-");
                    if (mGpsOnChange != null) {
                        mGpsOnChange.hideWarning();
                    }
                    break;
            }
            return true;
        }
    }

    /**
     * GPS position change event.
     */
    private class GpsOnChange implements js.lib.android.utils.gps.GpsImpl.GpsImplListener {

        //上一次分组
        private List<Double> mListGroup = new ArrayList<>();
        private final int GROUP_LIMIT = 3;

        //Speed km/h
        private final int WANING_SHOW_SPEED = 10;
        private final int WANING_HIDE_SPEED = 5;

        @Override
        public void onGotSpeed(double speed_mPerS, double speed_kmPerH) {
            Log.i(TAG, "onGotSpeed(" + speed_mPerS + "," + speed_kmPerH + ")");
            showWarning(speed_kmPerH);
        }

        void showWarning(double speed_kmPerH) {
            Log.i(TAG, "showWarning(" + speed_kmPerH + ")");
            //判断是否已经取到了 GROUP_LIMIT 次值
            //并添加新采样
            int groupSize = mListGroup.size();
            if (groupSize >= GROUP_LIMIT) {
                mListGroup.remove(0);
            }
            mListGroup.add(speed_kmPerH);

            //判断警告页是否已经打开了
            boolean isWarningPageVisible = (layoutWarning.getVisibility() == View.VISIBLE);
            Log.i(TAG, "isWarningPageVisible : " + isWarningPageVisible);
            if (!isWarningPageVisible) {
                //如果采样数大于等于 GROUP_LIMIT ,则判断末次 GROUP_LIMIT 个采样是否都大于临界值
                groupSize = mListGroup.size();
                if (groupSize >= GROUP_LIMIT) {
                    for (int idx = GROUP_LIMIT - 1; idx >= 0; idx--) {
                        Double tmpSpeed_kmPerH = mListGroup.get(idx);
                        Log.i(TAG, "tmpSpeed_kmPerH(" + idx + ") : " + tmpSpeed_kmPerH);
                        if (tmpSpeed_kmPerH < WANING_SHOW_SPEED) {
                            return;
                        }
                    }
                    //运行到这里,说明 GROUP_LIMIT 次采样值都大于临界值
                    layoutWarning.setVisibility(View.VISIBLE);
                }
            }
        }

        void hideWarning() {
            int groupSize = mListGroup.size();
            if (groupSize < GROUP_LIMIT) {
                layoutWarning.setVisibility(View.GONE);
            } else {
                //如果采样数大于等于 GROUP_LIMIT ,则判断末次 GROUP_LIMIT 个采样是否都小于临界值
                for (int idx = GROUP_LIMIT - 1; idx >= 0; idx--) {
                    Double tmpSpeed_kmPerH = mListGroup.get(idx);
                    Log.i(TAG, "tmpSpeed_kmPerH(" + idx + ") : " + tmpSpeed_kmPerH);
                    if (tmpSpeed_kmPerH > WANING_HIDE_SPEED) {
                        Log.i(TAG, "hideWarning() -NO-");
                        return;
                    }
                }
                //运行到这里,说明 GROUP_LIMIT 次采样值都小于临界值
                Log.i(TAG, "hideWarning() -Yes-");
                layoutWarning.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void testSendGps(final int flag) {
        switch (flag) {
            case 0:
                mTestSendGpsHandler.removeCallbacksAndMessages(null);
                break;
            case 1:
                mTestSendGpsHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Random random = new Random(System.currentTimeMillis());
                        if (mGpsOnChange != null) {
                            mGpsOnChange.showWarning(random.nextDouble() * 30);
                            testSendGps(flag);
                        }
                    }
                }, 1000);
                break;
        }
    }

    @Override
    public void updateThemeToDefault() {
        Log.i(TAG, "updateThemeToDefault()");
        updateThemeCommon();
    }

    @Override
    public void updateThemeToIos() {
        Log.i(TAG, "updateThemeToIos()");
        updateThemeCommon();
    }

    private void updateThemeCommon() {
        // -- Top Layout --
        layoutTop.setBackgroundResource(getImgResId("bg_title"));
        // Bottom
        layoutRoot.setBackgroundResource(getImgResId("bg_main"));

        // -- Middle Layout --
        //Seek bar
        seekBar.setProgressDrawable(mContext.getDrawable(getImgResId("seekbar_progress_drawable_video")));
        try {//
            Class<?> superclass = seekBar.getClass().getSuperclass().getSuperclass();
            Field mMaxHeight = superclass.getDeclaredField("mMaxHeight");
            mMaxHeight.setAccessible(true);
            mMaxHeight.set(seekBar, 10);

            Field mMinHeight = superclass.getDeclaredField("mMinHeight");
            mMinHeight.setAccessible(true);
            mMinHeight.set(seekBar, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // -- Bottom --
        ivPlayPre.setImageResource(getImgResId("btn_op_prev_selector"));

        Object objTag = ivPlay.getTag();
        if (objTag == null) {
            updateImgRes(ivPlay, "btn_op_play_selector");
        } else {
            updateImgRes(ivPlay, String.valueOf(objTag));
        }

        ivPlayNext.setImageResource(getImgResId("btn_op_next_selector"));

        objTag = ivPlayModeSet.getTag();
        if (objTag == null) {
            updateImgRes(ivPlayModeSet, "btn_op_mode_loop_selector");
        } else {
            updateImgRes(ivPlayModeSet, String.valueOf(objTag));
        }

        ivList.setImageResource(getImgResId("btn_op_list_selector"));
    }
}
