package com.tricheer.player.version.cj.slc_lc2010_vdc.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tri.lib.engine.KeyEnum;
import com.tri.lib.receiver.AccReceiver;
import com.tri.lib.receiver.ReverseReceiver;
import com.tri.lib.utils.PanelTouchImpl;
import com.tri.lib.utils.TrVideoPreferUtils;
import com.tricheer.player.R;
import com.tricheer.player.engine.PlayerConsts;
import com.tricheer.player.version.base.activity.video.BaseVideoPlayerActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.engine.video.MediaLightModeController;
import js.lib.android.media.player.PlayEnableController;
import js.lib.android.media.player.PlayMode;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.KillTouch;
import js.lib.android.utils.Logs;
import js.lib.android.utils.gps.GpsImpl;
import js.lib.utils.date.DateFormatUtil;

/**
 * SLC_LC2010_VDC Video Player Activity
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcVideoPlayerActivity extends BaseVideoPlayerActivity
        implements AccReceiver.AccDelegate, ReverseReceiver.ReverseDelegate {

    //TAG
    private static final String TAG = "VdcVideoPlayerActivity";

    //==========Widget in this Activity==========
    private View vCoverPanel;
    private ImageView vArrowRight, vArrowLeft;

    private View vControlPanel;
    private TextView tvFolder, tvPosition, tvName;
    private ImageView ivModeSet;
    private View vList;
    private View layoutWarning;

    //==========Variables in this Activity==========
    private PanelTouchResp mPanelTouchResp;

    private SeekBarOnChange mSeekBarOnChange;
    private GpsImpl mGpsImpl;

    private MediaLightModeController mLightModeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scl_lc2010_vdc_activity_video_player);
        setCurrPlayer(true, this);
        ReverseReceiver.register(this);
        AccReceiver.register(this);
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

        // ----Widgets----
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

        ivModeSet = (ImageView) findViewById(R.id.iv_play_mode_set);
        ivModeSet.setOnClickListener(mViewOnClick);
        onPlayModeChange();

        vList = findViewById(R.id.v_list);
        vList.setOnClickListener(mViewOnClick);

        //Warning
        layoutWarning = findViewById(R.id.layout_warning);
        layoutWarning.setVisibility(View.GONE);
        layoutWarning.setOnTouchListener(new KillTouch());

        //Register GPS
        mGpsImpl = new GpsImpl(this);
        mGpsImpl.setGpsImplListener(new GpsImpl.GpsImplListener() {
            @Override
            public void onGotSpeed(double speed_mPerS, double speed_kmPerH) {
                Log.i(TAG, "onGotSpeed(" + speed_mPerS + "," + speed_kmPerH + ")");
                if (speed_kmPerH >= 10) {
                    layoutWarning.setVisibility(View.VISIBLE);
                    mLightModeController.keepLightOn();
                } else {
                    layoutWarning.setVisibility(View.GONE);
                    mLightModeController.resetLightMode();
                }
            }
        });

        // Load Data
        checkingData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // UI Loaded
        if (isUILoaded()) {
            // Resume Light MODE
            mLightModeController.resetLightMode();
        }
    }

    private void checkingData() {
        String openMethod = getIntent().getStringExtra(PlayerConsts.PlayerOpenMethod.PARAM);
        if (PlayerConsts.PlayerOpenMethod.VAL_FILE_MANAGER.equalsIgnoreCase(openMethod)) {
            onPlayFromFolder(getIntent());
        } else {
            loadLocalMedias();
        }
    }

    @Override
    public void onPlayFromFolder(int playPos, List<String> listPlayPaths) {
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

    @Override
    protected void loadLocalMedias() {
        super.loadLocalMedias();
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

                // 不否允许播放
                if (!PlayEnableController.isPlayEnable()) {
                    removePlayRunnable();
                    pause();
                    return;
                }

                // 视频播放 - {正常模式}
                seekBar.setProgress(targetProgress);
                updateSeekTime(targetProgress, targetDuration);

                // 每秒钟保存一次播放信息
                savePlayInfo();
            }
        });
    }

    private class PanelTouchResp implements PanelTouchImpl.PanelTouchCallback {

        private Handler mmHandler = new Handler();

        @Override
        public void onActionDown() {
            cancelRunnable();
            Log.i(TAG, "onActionDown()");
        }

        @Override
        public void onActionUp() {
            Log.i(TAG, "onActionUp()");
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
                    int targetProgress = getProgress() + 15 * 1000;
                    if (targetProgress > getDuration()) {
                        targetProgress = getDuration();
                    }
                    seekTo(targetProgress);
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
                    seekTo(targetProgress);
                    break;
            }
            delayInvisible();
        }

        void delayInvisible() {
            cancelRunnable();
            mmHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
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
            if (v == ivPlay) {
                execPlayOrPause();

            } else if (v == ivPlayPre) {
                mIsPauseOnNotify = false;
                playPrevBySecurity();

            } else if (v == ivPlayNext) {
                mIsPauseOnNotify = false;
                playNextBySecurity();

            } else if (v == ivModeSet) {
                switchPlayMode(53);
            } else if (v == vList) {
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
                    ivModeSet.setImageResource(R.drawable.btn_op_mode_loop_selector);
                    break;
                case SINGLE:
                    ivModeSet.setImageResource(R.drawable.btn_op_mode_oneloop_selector);
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
        if (flag == 1) {
            ivPlay.setImageResource(R.drawable.btn_op_pause_selector);
        } else if (flag == 2) {
            ivPlay.setImageResource(R.drawable.btn_op_play_selector);
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
        ProVideo program = getCurrProgram();
        if (program != null) {
            File file = new File(program.mediaUrl);
            if (file.exists()) {
                File folder = file.getParentFile();
                if (folder != null) {
                    tvFolder.setText(folder.getName());
                }
                tvName.setText(program.title);
                tvPosition.setText((getCurrIdx() + 1) + "/" + getTotalCount());
            }
        }
    }

    @Override
    public void onGetKeyCode(KeyEnum key) {
        switch (key) {
            case KEYCODE_MEDIA_PREVIOUS:
                mIsPauseOnNotify = false;
                playPrevBySecurity();
                break;
            case KEYCODE_MEDIA_NEXT:
                mIsPauseOnNotify = false;
                playNextBySecurity();
                break;
        }
    }

    @Override
    public boolean isPlayEnable() {
        return false;
    }

    @Override
    public void onAudioFocusLoss() {
        super.onAudioFocusLoss();
    }

    @Override
    public void onAccOn() {
        resume();
    }

    @Override
    public void onAccOff() {
        pause();
    }

    @Override
    public void onAccOffTrue() {
    }

    @Override
    public void onReverseOn() {
        pause();
    }

    @Override
    public void onReverseOff() {
        resume();
    }

    @Override
    protected void onDestroy() {
        ReverseReceiver.unregister(this);
        AccReceiver.unregister(this);

        // 释放播放器
        execRelease();
        setCurrPlayer(false, this);

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

        super.onDestroy();
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
            CommonUtil.setNavigationBar(SclLc2010VdcVideoPlayerActivity.this, 1);
            vControlPanel.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLightOff() {
            CommonUtil.setNavigationBar(SclLc2010VdcVideoPlayerActivity.this, 0);
            vControlPanel.setVisibility(View.GONE);
        }
    }
}
