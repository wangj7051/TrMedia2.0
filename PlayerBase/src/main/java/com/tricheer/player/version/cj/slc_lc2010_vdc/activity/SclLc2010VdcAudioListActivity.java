package com.tricheer.player.version.cj.slc_lc2010_vdc.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tri.lib.engine.BtCallStateController;
import com.tri.lib.engine.KeyEnum;
import com.tri.lib.receiver.AccReceiver;
import com.tri.lib.receiver.ActionEnum;
import com.tri.lib.receiver.ReverseReceiver;
import com.tri.lib.receiver.VoiceAssistantReceiver;
import com.tri.lib.utils.SettingsSysUtil;
import com.tricheer.player.R;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.version.base.activity.music.BaseAudioUIActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.BaseAudioListFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioAlbumsFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioArtistsFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioCollectsFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioFoldersFrag;
import com.tricheer.player.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioNamesFrag;

import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.MediaBase;
import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.mediabtn.MediaBtnController;
import js.lib.android.media.engine.mediabtn.MediaBtnReceiver;
import js.lib.android.media.player.PlayEnableController;
import js.lib.android.media.player.PlayState;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.FragUtil;
import js.lib.android.utils.Logs;

/**
 * SCL_LC2010_VDC Music List Activity
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioListActivity extends BaseAudioUIActivity
        implements AccReceiver.AccDelegate,
        ReverseReceiver.ReverseDelegate,
        MediaBtnReceiver.MediaBtnListener,
        BtCallStateController.BtCallSateDelegate,
        VoiceAssistantReceiver.VoiceAssistantDelegate {

    // TAG
    private static final String TAG = "MusicListActivity";

    //==========Widgets in this Activity==========
    private View layoutRoot;
    private View layoutTop;
    private View vRubbishFocus;
    private ImageView ivRhythmAnim;
    private View[] vItems = new View[5];

    //==========Variables in this Activity==========
    //
    private Context mContext;
    private Handler mHandler = new Handler();

    //
    private boolean mIsScanOnLocalIsNull = true;

    // Request Current Playing Media Url
    private BaseAudioListFrag mFragMedias;
    private View mFragItemV;

    // Media Button Controller
    private MediaBtnController mMediaBtnController;

    // BT call state controller
    private BtCallStateController mBtCallStateController;

    /**
     * Voice command cache
     */
    private ActionEnum mVoiceCommand;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.scl_lc2010_vdc_activity_audio_list);

        //
        PlayerAppManager.putCxt(PlayerCxtFlag.MUSIC_LIST, this);
        MediaBtnReceiver.setListener(this);
        AccReceiver.register(this);
        ReverseReceiver.register(this);
        VoiceAssistantReceiver.register(this);
        SettingsSysUtil.setAudioState(this, 1);

        //
        init();
    }

    @Override
    protected void init() {
        super.init();
        // -- Variables --
        mContext = this;
        mMediaBtnController = new MediaBtnController(this);

        //BT call
        mBtCallStateController = new BtCallStateController(this);
        mBtCallStateController.register(this);

        // -- Widgets --
        layoutRoot = findRootView();
        layoutTop = findViewById(R.id.layout_top);
        vRubbishFocus = findViewById(R.id.v_rubbish_focus);

        // Switch items
        vItems[0] = findViewById(R.id.v_my_favorite);
        vItems[0].setOnClickListener(mFilterViewOnClick);

        vItems[1] = findViewById(R.id.v_folder);
        vItems[1].setOnClickListener(mFilterViewOnClick);


        vItems[2] = findViewById(R.id.v_music_name);
        vItems[2].setOnClickListener(mFilterViewOnClick);

        vItems[3] = findViewById(R.id.v_artist);
        vItems[3].setOnClickListener(mFilterViewOnClick);

        vItems[4] = findViewById(R.id.v_album);
        vItems[4].setOnClickListener(mFilterViewOnClick);

        ivRhythmAnim = (ImageView) findViewById(R.id.v_rate);
        ivRhythmAnim.setOnClickListener(mFilterViewOnClick);

        //
        bindScanService(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaBtnController.register();
        if (isPlaying()) {
            activeAnimRhythm(true);
        } else if (!PlayEnableController.isPauseByUser()) {
            resume();
        }
    }

    @Override
    protected void onScanServiceConnected() {
        switchTab(vItems[2], true);
        bindAndCreatePlayService(1, 2);
    }

    @Override
    protected void onPlayServiceConnected(Service service) {
        super.onPlayServiceConnected(service);
        setPlayListener(this);
        loadLocalMedias();
    }

    @Override
    protected void loadLocalMedias() {
        super.loadLocalMedias();
        CommonUtil.cancelTask(mLoadLocalMediasTask);
        mLoadLocalMediasTask = new LoadLocalMediasTask(new LoadMediaListener() {

            @Override
            public void afterLoaded(List<ProAudio> listMedias) {
                if (EmptyUtil.isEmpty(listMedias)) {
                    if (mIsScanOnLocalIsNull) {
                        mIsScanOnLocalIsNull = false;
                        startScan();
                    }
                } else {
                    setListSrcMedias(listMedias);
                    refreshDataList();
                }
            }
        });
        mLoadLocalMediasTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
    public void onMediaScanningRefresh(final List<ProAudio> listMedias, boolean isOnUiThread) {
        super.onMediaScanningRefresh(listMedias, isOnUiThread);
        Log.i(TAG, "onMediaScanningRefresh(List<ProAudio>," + isOnUiThread + ")");
        final List<ProAudio> listDeltaMedias = new ArrayList<>(listMedias);
        if (!isOnUiThread) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onMediaScanningRefresh(List<ProAudio>) -2-" + listDeltaMedias.size());
                    List<ProAudio> currListSrcMedias = getListSrcMedias();
                    if (EmptyUtil.isEmpty(currListSrcMedias)) {
                        currListSrcMedias = new ArrayList<>(listDeltaMedias);
                        setListSrcMedias(currListSrcMedias);
                    } else {
                        currListSrcMedias.addAll(listDeltaMedias);
                    }
                    refreshDataList();
                }
            });
        }
    }

    @Override
    public void onPlayFromFolder(int playPos, List<String> listPlayPaths) {
    }

    @Override
    public void onPlayFromFolder(Intent data) {
    }

    /**
     * 加载列表-媒体
     */
    private void refreshDataList() {
        // 刷新媒体列表
        if (mFragMedias != null) {
            mFragMedias.loadDataList();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        cancelOpenPlayer();
        return super.dispatchTouchEvent(ev);
    }

    private View.OnClickListener mFilterViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == ivRhythmAnim) {
                if (isPlaying()) {
                    openPlayerActivity(getLastMediaPath(), getListMedias());
                }

                //Click favorite/folder/name/artist/album
            } else {
                switchTab(v, true);
            }
        }
    };

    /**
     * Exec play
     *
     * @param mediaUrl     The media url to play.
     * @param listPrograms The media list to play.
     */
    public void execPlay(String mediaUrl, List<? extends MediaBase> listPrograms) {
        //Set play list.
        setPlayList(listPrograms);
        //Check if execute play "mediaUrl"
        if (isPlayingSameMedia(mediaUrl)) {
            Log.i(TAG, "### The media to play is playing now. ###");
        } else {
            execPlay(mediaUrl);
        }
    }

    /**
     * Open player
     *
     * @param mediaUrl   The media url to play.
     * @param listMedias The media list to play.
     */
    public void openPlayerActivity(String mediaUrl, List<? extends MediaBase> listMedias) {
        if (PlayEnableController.isPlayEnable()) {
            try {
                //Execute play
                execPlay(mediaUrl, listMedias);

                //Open player
                Intent playerIntent = new Intent(mContext, SclLc2010VdcAudioPlayerActivity.class);
                startActivityForResult(playerIntent, 1);
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "openPlayerActivity()", e);
            }
        }
    }

    private void switchTab(View v, boolean isExecLoad) {
        mFragItemV = v;
        //Switch
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
                mFragMedias = new SclLc2010VdcAudioCollectsFrag();
                break;
            case 1:
                mFragMedias = new SclLc2010VdcAudioFoldersFrag();
                break;
            case 2:
                mFragMedias = new SclLc2010VdcAudioNamesFrag();
                break;
            case 3:
                mFragMedias = new SclLc2010VdcAudioArtistsFrag();
                break;
            case 4:
                mFragMedias = new SclLc2010VdcAudioAlbumsFrag();
                break;
        }
        FragUtil.loadV4Fragment(R.id.layout_frag, mFragMedias, getSupportFragmentManager());
    }

    private void setBg(View v, boolean selected) {
        if (selected) {
            v.setBackgroundResource(getImgResId("bg_title_item_c"));
        } else {
            v.setBackgroundResource(getImgResId("btn_filter_tab_selector"));
        }
    }

    @Override
    public void onGotMediaKeyCode(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            moveFocusToRubbish(vRubbishFocus);
            KeyEnum key = KeyEnum.getKey(event.getKeyCode());
            switch (key) {
                case KEYCODE_MEDIA_PREVIOUS:
                    if (!isForeground()) {
                        playPrev();
                        if (mFragMedias != null) {
                            mFragMedias.selectPrev();
                        }
                    }
                    break;
                case KEYCODE_MEDIA_NEXT:
                    if (!isForeground()) {
                        playNext();
                        if (mFragMedias != null) {
                            mFragMedias.selectNext();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onGetKeyCode(KeyEnum key) {
        moveFocusToRubbish(vRubbishFocus);
        switch (key) {
            case KEYCODE_MEDIA_PREVIOUS:
                if (isForeground()) {
                    playPrev();
                    if (mFragMedias != null) {
                        mFragMedias.selectPrev();
                    }
                }
                break;
            case KEYCODE_MEDIA_NEXT:
                if (isForeground()) {
                    playNext();
                    if (mFragMedias != null) {
                        mFragMedias.selectNext();
                    }
                }
                break;

            case KEYCODE_DPAD_LEFT:
                cancelOpenPlayer();
                if (mFragMedias != null) {
                    mFragMedias.selectPrev();
                }
                break;
            case KEYCODE_DPAD_RIGHT:
                cancelOpenPlayer();
                if (mFragMedias != null) {
                    mFragMedias.selectNext();
                }
                break;
            case KEYCODE_ENTER:
                cancelOpenPlayer();
                if (mFragMedias != null) {
                    mFragMedias.playSelected();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String flag = data.getStringExtra("flag");
            if ("PLAYER_FINISH_ON_CLICK_LIST".equals(flag)) {
                if (mFragMedias != null) {
                    mFragMedias.refreshDataList();
                }
            } else if ("PLAYER_FINISH_ON_GET_KEY".equals(flag)) {
                if (mFragMedias != null) {
                    mFragMedias.refreshDataList();
                }
                autoOpenPlayer();
            }
        }
    }

    @Override
    public void onAccOn() {
        Log.i(TAG, "onAccOn()");
        if (!isPlaying() && isAudioFocusRegistered()) {
            Log.i(TAG, "onAccOffTrue -> resume()");
            resume();
        }
    }

    @Override
    public void onAccOff() {
        Log.i(TAG, "onAccOff()");
        if (isPlaying()) {
            pause();
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
        if (!isPlaying() && isAudioFocusRegistered()) {
            Log.i(TAG, "onReverseOff -> resume()");
            resume();
        }
    }

    @Override
    public void onBtCallStateChanged(boolean isBtRunning) {
        Log.i(TAG, "onBtCallStateChanged(" + isBtRunning + ")");
        if (isBtRunning) {
            Log.i(TAG, "onBtCallStateChanged -> pause()");
            if (isPlaying()) {
                pause();
            }
        } else if (!isPlaying() && isAudioFocusRegistered()) {
            Log.i(TAG, "onBtCallStateChanged -> resume()");
            resume();
        }
    }

    @Override
    public void onVoiceCommand(ActionEnum ae) {
        Log.i(TAG, "onVoiceCommand(" + ae + ")");
        mVoiceCommand = ae;
        if (isAudioFocusRegistered()) {
            processVoiceCommand();
        }
    }

    private void processVoiceCommand() {
        if (mVoiceCommand != null) {
            Log.i(TAG, "processVoiceCommand() - mVoiceCommand:" + mVoiceCommand);
            switch (mVoiceCommand) {
                case MEDIA_PLAY_PREV:
                    execPlayPrevByUser();
                    break;
                case MEDIA_PLAY_NEXT:
                    execPlayNextByUser();
                    break;
                case MEDIA_PLAY:
                    execResumeByUser();
                    break;
                case MEDIA_PAUSE:
                    execPauseByUser();
                    break;
            }
            mVoiceCommand = null;
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
        Log.i(TAG, "onAudioFocusGain()");
        processVoiceCommand();
    }

    @Override
    public void onAudioFocusLoss() {
        mMediaBtnController.unregister();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (mFragMedias != null) {
            int backRes = mFragMedias.onBackPressed();
            switch (backRes) {
                case 1:
                    break;
                default:
                    cancelOpenPlayer();
                    moveTaskToBack(true);
                    break;
            }
        }
    }

    @Override
    public void onPlayStateChanged(PlayState playState) {
        super.onPlayStateChanged(playState);
        Log.i(TAG, "playState -> " + playState);
        switch (playState) {
            case PLAY:
            case PREPARED:
                activeAnimRhythm(true);
                if (mFragMedias != null) {
                    mFragMedias.refreshPlaying(getCurrMediaPath());
                }
                break;
            default:
                activeAnimRhythm(false);
                break;
        }
    }

    private void activeAnimRhythm(boolean isActive) {
        Drawable drawable = ivRhythmAnim.getDrawable();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animDrawable = (AnimationDrawable) drawable;
            if (isActive) {
                if (!animDrawable.isRunning()) {
                    animDrawable.start();
                }
            } else {
                if (animDrawable.isRunning()) {
                    animDrawable.stop();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        //
        mMediaBtnController.unregister();
        MediaBtnReceiver.setListener(null);

        //
        if (mBtCallStateController != null) {
            mBtCallStateController.unregister();
            mBtCallStateController = null;
        }

        //
        removePlayListener(this);
        AccReceiver.unregister(this);
        ReverseReceiver.unregister(this);
        VoiceAssistantReceiver.unregister(this);

        //
        cancelAllTasks();
        mHandler.removeCallbacksAndMessages(null);

        //
        bindScanService(false);
        bindAndCreatePlayService(3, 4);

        //
        SettingsSysUtil.setAudioState(this, 0);
        PlayerAppManager.removeCxt(PlayerCxtFlag.MUSIC_LIST);
        super.onDestroy();
    }

    /**
     * Demand : Automatically open player after 5s.
     */
    private void autoOpenPlayer() {
        if (isPlaying()) {
            cancelOpenPlayer();
            Log.i(TAG, "autoOpenPlayer() -EXEC-");
            mHandler.postDelayed(mmDelayOpenAudioRunnable, 5000);
        }
    }

    private void cancelOpenPlayer() {
        Log.i(TAG, "autoOpenPlayer() -CANCEL-");
        mHandler.removeCallbacks(mmDelayOpenAudioRunnable);
    }

    private Runnable mmDelayOpenAudioRunnable = new Runnable() {
        @Override
        public void run() {
            if (mFragMedias != null) {
                mFragMedias.playSelected(getLastMediaPath());
            }
        }
    };

    @Override
    public void switchPlayMode(int supportFlag) {
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
        Log.i(TAG, "updateThemeToDefault()");
        // Top Layout
        // Top items
        int marginTop = getResources().getDimensionPixelSize(R.dimen.audio_top_item_margin_top);
        int marginBottom = getResources().getDimensionPixelSize(R.dimen.audio_top_item_margin_top);
        for (View v : vItems) {
            ViewGroup.LayoutParams lps = v.getLayoutParams();
            if (lps instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLps = (ViewGroup.MarginLayoutParams) lps;
                marginLps.setMargins(0, marginTop, 0, marginBottom);
            }
        }

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
                marginLps.setMargins(0, 0, 0, 0);
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