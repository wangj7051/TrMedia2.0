package com.yj.audio.version.cj.slc_lc2010_vdc.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tri.lib.engine.KeyEnum;
import com.tri.lib.engine.MediaPageState;
import com.tri.lib.engine.MediaTypeEnum;
import com.tri.lib.utils.SettingsSysUtil;
import com.yj.audio.R;
import com.yj.audio.engine.PlayerAppManager;
import com.yj.audio.utils.PlayerLogicUtils;
import com.yj.audio.version.base.activity.music.BasePlayerActivity;

import js.lib.android.media.bean.MediaBase;
import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.media.player.PlayMode;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.utils.date.DateFormatUtil;

/**
 * SLC_LC2010_VDC Music Player Activity
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioPlayerActivity extends BasePlayerActivity {
    // TAG
    private static final String TAG = "audio_player";

    //==========Widgets in this Activity==========
    private View layoutRoot;
    private View layoutTop;
    private TextView tvName, tvArtist, tvAlbum;
    private ImageView ivMusicCover;
    private TextView tvStartTime, tvEndTime;
    private RelativeLayout layoutSeekbar;
    private SeekBar seekBar;
    private ImageView ivPlayPre, ivPlay, ivPlayNext, ivCollect, ivPlayModeSet, ivList;
    private ImageView ivSeparateH1;

    //==========Variables in this Activity==========
    private Context mContext;

    //拖动条监听事件
    private SeekBarOnChange mSeekBarOnChange;

    //延迟刷新当前媒体信息
    //有时候发现无法获取到媒体信息，这样是为了保证能够轮循拿到媒体信息
    private Handler mDelayRefreshRunnable = new Handler();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scl_lc2010_vdc_activity_audio_player);
        init();
    }

    private void init() {
        // Initialize Variables
        mContext = this;

        // Initialize Widgets
        layoutRoot = findRootView();
        layoutTop = findViewById(R.id.layout_top);

        tvName = (TextView) findViewById(R.id.tv_name);
        tvName.setText("");
        tvName.setSelected(false);
        tvName.setOnClickListener(mViewOnClick);

        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvArtist.setText("");
        tvArtist.setOnClickListener(mViewOnClick);

        tvAlbum = (TextView) findViewById(R.id.tv_album);
        tvAlbum.setText("");
        tvAlbum.setOnClickListener(mViewOnClick);

        layoutSeekbar = (RelativeLayout) findViewById(R.id.rl_seek_bar);
        tvStartTime = (TextView) findViewById(R.id.tv_play_start_time);
        tvEndTime = (TextView) findViewById(R.id.tv_play_end_time);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener((mSeekBarOnChange = new SeekBarOnChange()));

        ivMusicCover = (ImageView) findViewById(R.id.iv_music_cover);
        ivSeparateH1 = (ImageView) findViewById(R.id.iv_separate_h1);
        ivPlayPre = (ImageView) findViewById(R.id.iv_play_pre);
        ivPlayPre.setOnClickListener(mViewOnClick);

        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivPlay.setOnClickListener(mViewOnClick);

        ivPlayNext = (ImageView) findViewById(R.id.iv_play_next);
        ivPlayNext.setOnClickListener(mViewOnClick);

        ivCollect = (ImageView) findViewById(R.id.v_favor);
        ivCollect.setOnClickListener(mViewOnClick);

        ivPlayModeSet = (ImageView) findViewById(R.id.iv_play_mode_set);
        onPlayModeChange();
        ivPlayModeSet.setOnClickListener(mViewOnClick);

        ivList = (ImageView) findViewById(R.id.v_list);
        ivList.setOnClickListener(mViewOnClick);

        //Initialize execute
        bindAndCreatePlayService(2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        PlayerAppManager.addContext(this);
        SettingsSysUtil.setLastMediaType(this, MediaTypeEnum.MUSIC);
        SettingsSysUtil.setMediaPageState(this, MediaPageState.MUSIC_FOREGROUND);

        //Register Audio focus.
        boolean isAudioServiceConned = isAudioServiceConned();
        Log.i(TAG, "onResume() -isAudioServiceConned:" + isAudioServiceConned + "-");
        if (isAudioServiceConned) {
            //Register Audio focus when service binded.
            registerAudioFocus(1);

            //是否处理语音命令,该语音命令通过Intent传入
            boolean isProcessingVoiceCmd = processingVoiceCmdFromIntent();
            Log.i(TAG, "onResume() - isProcessingVoiceCmd : " + isProcessingVoiceCmd);
            if (!isProcessingVoiceCmd && !isPlaying()) {
                resume();
            }
        }
    }

    @Override
    protected void onScanServiceConnected() {
    }

    @Override
    protected void onAudioServiceConnChanged(Service service) {
        super.onAudioServiceConnChanged(service);
        Log.i(TAG, "onAudioServiceConnChanged(" + service + ")");
        if (service != null) {
            loadLocalMedias();
        }
    }

    /**
     * Load current media information.
     */
    private void loadLocalMedias() {
        refreshCurrMediaInfo();
        refreshUIOfPlayBtn(isPlaying() ? 1 : 2);
        refreshFrameTime(true);
        refreshSeekBar(true);
    }

    @Override
    protected void refreshCurrMediaInfo() {
        Log.i(TAG, "refreshCurrMediaInfo()");
        final ProAudio media = getCurrProgram();
        if (media != null) {
            setMediaInformation(media);
        } else {
            Log.i(TAG, "refreshCurrMediaInfo() -Delay Refresh-");
            mDelayRefreshRunnable.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshCurrMediaInfo();
                }
            }, 300);
        }
    }

    private void setMediaInformation(ProAudio media) {
        // Media Cover
        PlayerLogicUtils.setMediaCover(ivMusicCover, media);
        // Title
        String strTitle = PlayerLogicUtils.getMediaTitle(mContext, -1, media, true);
        if (ProAudio.UNKNOWN.equals(strTitle)) {
            tvName.setText(R.string.unknown_title);
        } else {
            tvName.setText(strTitle);
        }
        //Artist
        String strArtist = media.artist;
        if (ProAudio.UNKNOWN.equals(strArtist) || EmptyUtil.isEmpty(strArtist)) {
            tvArtist.setText(R.string.unknown_artist);
        } else {
            tvArtist.setText(strArtist);
        }
        //Album
        String strAlbum = media.album;
        if (ProAudio.UNKNOWN.equals(strAlbum) || EmptyUtil.isEmpty(strAlbum)) {
            tvAlbum.setText(R.string.unknown_album);
        } else {
            tvAlbum.setText(strAlbum);
        }

        //Collect status
        switch (media.isCollected) {
            case 0:
                updateImgRes(ivCollect, "btn_op_favor_selector");
                break;
            case 1:
                updateImgRes(ivCollect, "btn_op_favored_selector");
                break;
        }
    }

    /**
     * View Click Event
     */
    private View.OnClickListener mViewOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //Mode
            if (v == ivPlayModeSet) {
                switchPlayMode(12);

                //Play
            } else if (v == ivPlayPre) {
                execPlayPrevByUser();
            } else if (v == ivPlay) {
                execPlayOrPauseByUser();
            } else if (v == ivPlayNext) {
                execPlayNextByUser();

                //Collect
            } else if (v == ivCollect) {
                collect();
            } else if (v == ivList) {
                execFinish("PLAYER_FINISH_ON_CLICK_LIST", null);

                //Click music information
            } else if (v == tvName) {
                ProAudio media = getCurrProgram();
                if (media != null) {
                    execFinish("PLAYER_FINISH_ON_CLICK_TITLE", new String[]{media.title});
                }
            } else if (v == tvArtist) {
                ProAudio media = getCurrProgram();
                if (media != null) {
                    execFinish("PLAYER_FINISH_ON_CLICK_ARTIST", new String[]{media.artist});
                }
            } else if (v == tvAlbum) {
                ProAudio media = getCurrProgram();
                if (media != null) {
                    execFinish("PLAYER_FINISH_ON_CLICK_ALBUM", new String[]{media.album});
                }
            }
        }

        void collect() {
            ProAudio media = getCurrProgram();
            if (media != null) {
                switch (media.isCollected) {
                    case 1:
                        media.isCollected = 0;
                        updateImgRes(ivCollect, "btn_op_favor_selector");
                        break;
                    case 0:
                        media.isCollected = 1;
                        updateImgRes(ivCollect, "btn_op_favored_selector");
                        break;
                }
                AudioDBManager.instance().updateMediaCollect(media);
            }
        }
    };

    /**
     * Execute finish methods.
     */
    private void execFinish(String flag, String[] values) {
        //
        Intent data = new Intent();
        data.putExtra("flag", flag);
        if (values != null) {
            data.putExtra("values", values);
        }
        setResult(0, data);
        finish();
    }

    @Override
    public void onProgressChanged(String mediaUrl, int progress, int duration) {
        super.onProgressChanged(mediaUrl, progress, duration);
        Logs.debugI(TAG, "onProgressChanged(" + mediaUrl + "," + progress + "," + duration + ")");
        // Update
        refreshFrameTime(false);
    }

    @Override
    protected void refreshFrameTime(boolean isInit) {
        //Get media progress and duration.
        int currProgress = (int) getProgress();
        int duration = (int) getDuration();

        //Set SeekBar-Progress
        if (!mSeekBarOnChange.isTrackingTouch()) {
            if (currProgress > seekBar.getMax()) {
                seekBar.setMax(duration);
            }
            seekBar.setProgress(currProgress);
        }

        // Set Start/End Time
        tvStartTime.setText(DateFormatUtil.getFormatHHmmss(currProgress));
        tvEndTime.setText(DateFormatUtil.getFormatHHmmss(duration));
    }

    @Override
    protected void refreshUIOfPlayBtn(int flag) {
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
    protected void refreshSeekBar(boolean isInit) {
        Log.i(TAG, "refreshSeekBar(" + isInit + ")");
        seekBar.setMax((int) getDuration());
        seekBar.setProgress((int) getProgress());
        if (isInit) {
            seekBar.setEnabled(true);
        }
    }

    @Override
    public long getDuration() {
        MediaBase mediaBase = getCurrMedia();
        if (mediaBase instanceof ProAudio) {
            ProAudio media = (ProAudio) mediaBase;
            if (media.duration <= 0) {
                media.duration = super.getDuration();
                AudioDBManager.instance().updateMusicInfo(media);
            }
            return media.duration;
        }
        return super.getDuration();
    }

    @Override
    public void onPlayModeChange() {
        super.onPlayModeChange();
        Logs.i(TAG, "^^ onPlayModeChange() ^^");
        PlayMode storePlayMode = getPlayMode();
        Logs.i(TAG, "onPlayModeChange() -> [storePlayMode:" + storePlayMode + "]");
        if (storePlayMode != null) {
            switch (storePlayMode) {
                case LOOP:
                    updateImgRes(ivPlayModeSet, "btn_op_mode_loop_selector");
                    break;
                case SINGLE:
                    updateImgRes(ivPlayModeSet, "btn_op_mode_oneloop_selector");
                    break;
                case RANDOM:
                    updateImgRes(ivPlayModeSet, "btn_op_mode_random_selector");
                    break;
                // case PlayMode.ORDER:
                // ivPlayModeSet.setImageResource(R.drawable.btn_op_mode_order_selector);
                // break;
            }
        }
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
    public void onGotKeyCode(KeyEnum key) {
        switch (key) {
            case KEYCODE_DPAD_LEFT:
            case KEYCODE_DPAD_RIGHT:
                execFinish("PLAYER_FINISH_ON_GET_KEY", null);
                break;
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause()");
        SettingsSysUtil.setMediaPageState(this, MediaPageState.MUSIC_BACKGROUND);
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

    @Override
    protected void clearActivity() {
        super.clearActivity();
        mDelayRefreshRunnable.removeCallbacksAndMessages(null);
        bindAndCreatePlayService(3);
        PlayerAppManager.removeContext(this);
    }

    private final class SeekBarOnChange implements SeekBar.OnSeekBarChangeListener {

        int mmProgress;
        boolean mmIsTracking = false;

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "SeekBarOnChange - onStartTrackingTouch(SeekBar)");
            mmIsTracking = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "SeekBarOnChange - onStopTrackingTouch(SeekBar)");
            if (mmIsTracking) {
                mmIsTracking = false;
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
            return mmIsTracking;
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
        ivMusicCover.setBackgroundResource(getImgResId("bg_cover_border"));

        //Seek bar
        layoutSeekbar.setBackgroundResource(getImgResId("bg_corners_seekbar"));
        seekBar.setProgressDrawable(mContext.getDrawable(getImgResId("seekbar_progress_drawable_audio")));

        // -- Bottom --
        ivPlayPre.setImageResource(getImgResId("btn_op_prev_selector"));

        Object objTag = ivPlay.getTag();
        if (objTag == null) {
            updateImgRes(ivPlay, "btn_op_play_selector");
        } else {
            updateImgRes(ivPlay, String.valueOf(objTag));
        }

        ivPlayNext.setImageResource(getImgResId("btn_op_next_selector"));

        objTag = ivCollect.getTag();
        if (objTag == null) {
            updateImgRes(ivCollect, "btn_op_favor_selector");
        } else {
            updateImgRes(ivCollect, String.valueOf(objTag));
        }

        objTag = ivPlayModeSet.getTag();
        if (objTag == null) {
            updateImgRes(ivPlayModeSet, "btn_op_mode_loop_selector");
        } else {
            updateImgRes(ivPlayModeSet, String.valueOf(objTag));
        }

        ivList.setImageResource(getImgResId("btn_op_list_selector"));
        ivSeparateH1.setImageResource(getImgResId("separate_line_h"));
    }
}