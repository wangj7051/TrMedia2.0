package com.tricheer.player.version.cj.slc_lc2010_vdc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tricheer.player.R;
import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.engine.Keys;
import com.tricheer.player.engine.PlayerConsts.PlayMode;
import com.tricheer.player.engine.db.DBManager;
import com.tricheer.player.utils.PlayerLogicUtils;
import com.tricheer.player.utils.PlayerPreferUtils;
import com.tricheer.player.version.base.activity.music.BaseMusicPlayerActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.utils.date.DateFormatUtil;

/**
 * SLC_LC2010_VDC Music Player Activity
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioPlayerActivity extends BaseMusicPlayerActivity {
    // TAG
    private static final String TAG = "MusicPlayerActivityImpl";

    //==========Widgets in this Activity==========
    private TextView tvName, tvArtist, tvAlbum;
    private ImageView ivMusicCover;
    private TextView tvStartTime, tvEndTime;
    private SeekBar seekBar;
    private ImageView ivPlayPre, ivPlay, ivPlayNext, ivFavor, ivPlayModeSet, ivList;

    /**
     * ==========Variables in this Activity==========
     */
    private Context mContext;
    private SeekBarOnChange mSeekBarOnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scl_lc2010_vdc_activity_audio_player);
        setCurrPlayer(true, this);
        init();
    }

    @Override
    protected void init() {
        super.init();
        // Initialize Variables
        mContext = this;

        // Initialize Widgets
        tvName = findView(R.id.tv_name);
        tvName.setText("");

        tvArtist = findView(R.id.tv_artist);
        tvArtist.setText("");

        tvAlbum = findView(R.id.tv_album);
        tvAlbum.setText("");

        tvStartTime = findView(R.id.tv_play_start_time);
        tvEndTime = findView(R.id.tv_play_end_time);
        seekBar = findView(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener((mSeekBarOnChange = new SeekBarOnChange()));

        ivMusicCover = findView(R.id.iv_music_cover);
        ivPlayPre = findView(R.id.iv_play_pre);
        ivPlayPre.setOnClickListener(mViewOnClick);

        ivPlay = findView(R.id.iv_play);
        ivPlay.setOnClickListener(mViewOnClick);

        ivPlayNext = findView(R.id.iv_play_next);
        ivPlayNext.setOnClickListener(mViewOnClick);

        ivFavor = findView(R.id.v_favor);
        ivFavor.setOnClickListener(mViewOnClick);

        ivPlayModeSet = findView(R.id.iv_play_mode_set);
        ivPlayModeSet.setOnClickListener(mViewOnClick);

        ivList = findView(R.id.v_list);
        ivList.setOnClickListener(mViewOnClick);

        //
        mController.setControlEnable(true);
        bindAndCreatePlayService(2);
    }

    @Override
    public void onPlayFromFolder(int playPos, List<String> listPlayPaths) {
        super.onPlayFromFolder(playPos, listPlayPaths);
    }

    @Override
    public void onPlayFromFolder(Intent data) {
    }

    @Override
    protected void onPlayServiceConnected() {
        super.onPlayServiceConnected();
        loadLocalMedias();
    }

    @Override
    protected void loadLocalMedias() {
        playByIntent();
    }

    private void playByIntent() {
        String mediaUrl = getIntent().getStringExtra("SELECT_MEDIA_URL");
        Serializable serialListPros = getIntent().getSerializableExtra("MEDIA_LIST");
        if (mediaUrl != null && serialListPros != null) {
            // Play
            mListPrograms = (ArrayList<ProMusic>) serialListPros;
            if (!EmptyUtil.isEmpty(mListPrograms)) {
                //Show playing information
                if (isPlaying() && isPlayingSameMedia(mediaUrl)) {
                    refreshCurrMediaInfo();
                    refreshUIOfPlayBtn(1);
                    refreshSeekBar(true);

                    //Play selected media
                } else {
                    setPlayList(mListPrograms);
                    setPlayPosition(mediaUrl);
                    execPlay(mediaUrl);
                }
            }
        }
    }

    @Override
    protected void execPlay(String mediaUrl) {
        if (isPlaying()) {
            if (!isPlayingSameMedia(mediaUrl)) {
                super.execPlay(mediaUrl);
            }
        } else {
            super.execPlay(mediaUrl);
        }
    }

    /**
     * View Click Event
     */
    private View.OnClickListener mViewOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == ivPlayModeSet) {
                execPlayModeSet();
            } else if (v == ivPlayPre) {
                playPrevBySecurity();
            } else if (v == ivPlay) {
                execPlayOrPause();
            } else if (v == ivPlayNext) {
                playNextBySecurity();
            } else if (v == ivList) {
                finish();
            }
        }
    };


    @Override
    public void onProgressChange(String mediaUrl, int progress, int duration, boolean isPerSecond) {
        super.onProgressChange(mediaUrl, progress, duration, isPerSecond);
        //Update current media duration
        ProMusic program = getCurrProgram();
        if (program != null && program.duration <= 0) {
            program.duration = duration;
            DBManager.updateMusicInfo(program);
        }

        // Update
        refreshFrameTime(false);

        // Save Play Info Per Second
        execSavePlayInfo();
    }

    @Override
    protected void refreshCurrMediaInfo() {
        final ProMusic program = getCurrProgram();
        if (program != null) {
            // Media Cover
            PlayerLogicUtils.setMediaCover(ivMusicCover, program, getImageLoader());
            // Artist
            tvArtist.setText(PlayerLogicUtils.getUnKnowOnNull(mContext, program.artist));
            // Title
            tvName.setText(PlayerLogicUtils.getMediaTitle(mContext, -1, program, true));
        }
    }

    @Override
    protected void refreshUIOfPlayBtn(int flag) {
        switch (flag) {
            case 1:
                ivPlay.setImageResource(R.drawable.btn_op_pause_selector);
                break;
            case 2:
                ivPlay.setImageResource(R.drawable.btn_op_play_selector);
                break;
        }
    }

    @Override
    protected void refreshSeekBar(boolean isInit) {
        Log.i(TAG, "refreshSeekBar(" + isInit + ")");
        seekBar.setMax(getDuration());
        if (isInit) {
            seekBar.setEnabled(true);
            seekBar.setProgress(0);
        } else {
            seekBar.setProgress(getProgress());
        }
    }

    @Override
    protected void refreshFrameTime(boolean isInit) {
        if (isInit) {
            tvStartTime.setText(R.string.fillstr_time_1);
            tvEndTime.setText(R.string.fillstr_time_1);
        } else {
            //Get media duration and current position
            int duration = getDuration();
            int currProgress = getProgress();

            //Set SeekBar-Progress
            if (mSeekBarOnChange != null && !mSeekBarOnChange.isTrackingTouch()) {
                if (currProgress > seekBar.getMax()) {
                    seekBar.setMax(duration);
                }
                seekBar.setProgress(currProgress);
            }

            // Set Start/End Time
            tvStartTime.setText(DateFormatUtil.getFormatHHmmss(currProgress));
            tvEndTime.setText(DateFormatUtil.getFormatHHmmss(duration));
        }
    }

    @Override
    public void onPlayModeChange() {
        super.onPlayModeChange();
        Logs.i(TAG, "^^ onPlayModeChange() ^^");
        int storePlayMode = PlayerPreferUtils.getMusicPlayMode(false, PlayMode.LOOP);
        Logs.i(TAG, "onPlayModeChange() -> [storePlayMode:" + storePlayMode + "]");
        switch (storePlayMode) {
            case PlayMode.LOOP:
                ivPlayModeSet.setImageResource(R.drawable.btn_op_mode_loop_selector);
                break;
            case PlayMode.SINGLE:
                ivPlayModeSet.setImageResource(R.drawable.btn_op_mode_oneloop_selector);
                break;
            case PlayMode.RANDOM:
                ivPlayModeSet.setImageResource(R.drawable.btn_op_mode_random_selector);
                break;
            // case PlayMode.ORDER:
            // ivPlayModeSet.setImageResource(R.drawable.btn_op_mode_order_selector);
            // break;
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
            case Keys.KeyVals.KEYCODE_DPAD_LEFT:
            case Keys.KeyVals.KEYCODE_DPAD_RIGHT:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mController.setControlEnable(false);
        bindAndCreatePlayService(3);
        setCurrPlayer(false, this);
        super.onDestroy();
    }

    private final class SeekBarOnChange implements SeekBar.OnSeekBarChangeListener {

        int mmProgress;
        boolean mmIsTracking = false;

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mmIsTracking = true;
            Log.i(TAG, "SeekBarOnChange --onStartTrackingTouch(SeekBar)--");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "SeekBarOnChange --onStopTrackingTouch(SeekBar)--");
            if (mmIsTracking) {
                mmIsTracking = false;
                seekTo(mmProgress);
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Logs.debugI(TAG, "SeekBarOnChange --onProgressChanged(SeekBar," + progress + "," + fromUser + ")--");
            if (fromUser) {
                mmProgress = progress;
            }
        }

        boolean isTrackingTouch() {
            return mmIsTracking;
        }
    }
}