package com.tricheer.player.version.cj.slc_lc2010_vdc.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.tricheer.player.R;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.engine.Keys;
import com.tricheer.player.engine.PlayerConsts;
import com.tricheer.player.version.base.activity.video.BaseVideoPlayerActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.utils.date.DateFormatUtil;

/**
 * SLC_LC2010_VDC Video Player Activity
 *
 * @author Jun.Wang
 */
public class SlcLc2010VdcVideoPlayerActivity extends BaseVideoPlayerActivity {

    /**
     * ==========Widget in this Activity==========
     */
    private View vControlPanel;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slc_lc2010_vdc_activity_video_player);
        setCurrPlayer(true, this);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void init() {
        super.init();

        // ----Widgets----
        vControlPanel = findView(R.id.v_control_panel);

        tvName = findView(R.id.v_name);
        tvName.setText("");

        vvPlayer = findView(R.id.vv_player);
        vvPlayer.setPlayStateListener(this);
        vvPlayer.setKeepScreenOn(true);
        vvPlayer.setDrawingCacheEnabled(false);
        vvPlayer.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switchLightMode();
                return false;
            }
        });
        setCanPlayAtBg(false);

        tvStartTime = findView(R.id.tv_play_start_time);
        tvEndTime = findView(R.id.tv_play_end_time);
        seekBar = findView(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekOnChange());
        seekBar.setOnTouchListener(new SeekOnTouch());

        ivPlayPre = findView(R.id.iv_play_pre);
        ivPlayPre.setOnClickListener(mViewOnClick);

        ivPlay = findView(R.id.iv_play);
        ivPlay.setOnClickListener(mViewOnClick);

        ivPlayNext = findView(R.id.iv_play_next);
        ivPlayNext.setOnClickListener(mViewOnClick);

        // Load Data
        checkingData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // UI Loaded
        if (isUILoaded()) {
            // Resume 手刹状态
            // resumeHandBrakeStatus(tvTitle);
            // Resume Light MODE
            setLightMode(VideoLightMode.ON);
            resetLightMode();
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
    protected void loadLocalMedias() {
        super.loadLocalMedias();
        // Resume Light MODE
        setLightMode(VideoLightMode.ON);
        resetLightMode();
        playByIntent();
    }

    @SuppressWarnings("unchecked")
    private boolean playByIntent() {
        String mediaUrl = getIntent().getStringExtra("SELECT_MEDIA_URL");
        Serializable serialListPros = getIntent().getSerializableExtra("MEDIA_LIST");
        if (mediaUrl != null && serialListPros != null) {
            // Play
            mListPrograms = (ArrayList<ProVideo>) serialListPros;
            if (!EmptyUtil.isEmpty(mListPrograms)) {
                execPlay(mediaUrl);
                return true;
            }
        }
        return false;
    }

    /**
     * View Click Event
     */
    private View.OnClickListener mViewOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            resetLightMode();
            if (v == ivPlay) {
                doPlayOrPause();
            } else if (v == ivPlayPre) {
                doSecPlayPre();
            } else if (v == ivPlayNext) {
                doSecPlayNext();
            }
        }
    };

    private void doPlayOrPause() {
        execPlayOrPause();
    }

    private void doSecPlayPre() {
        mIsPauseOnNotify = false;
        playPrevBySecurity();
    }

    private void doSecPlayNext() {
        mIsPauseOnNotify = false;
        playNextBySecurity();
    }

    @Override
    protected void setLightMode(int lightMode) {
        super.setLightMode(lightMode);
        if (isLightOn()) {
            vControlPanel.setVisibility(View.VISIBLE);
        } else if (lightMode == VideoLightMode.OFF) {
            vControlPanel.setVisibility(View.GONE);
        }
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
//        super.onNotifyPlayState$Play();
        ProVideo program = getCurrProgram();
        if (program != null) {
            tvName.setText(program.title);
        }
    }

    @Override
    public void onGetKeyCode(int keyCode) {
        switch (keyCode) {
            case Keys.KeyVals.KEYCODE_PREV:
                playPrevBySecurity();
                break;
            case Keys.KeyVals.KEYCODE_NEXT:
                playNextBySecurity();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onIDestroy();
    }

    @Override
    protected void onIDestroy() {
        super.onIDestroy();
        setCurrPlayer(false, this);
    }
}
