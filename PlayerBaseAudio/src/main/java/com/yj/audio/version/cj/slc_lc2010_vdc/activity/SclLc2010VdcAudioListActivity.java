package com.yj.audio.version.cj.slc_lc2010_vdc.activity;

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
import com.tri.lib.engine.MediaPageState;
import com.tri.lib.engine.MediaTypeEnum;
import com.tri.lib.receiver.AccReceiver;
import com.tri.lib.receiver.ActionEnum;
import com.tri.lib.receiver.VoiceAssistantReceiver;
import com.tri.lib.utils.SettingsSysUtil;
import com.yj.audio.R;
import com.yj.audio.engine.PlayerAppManager;
import com.yj.audio.version.base.activity.music.BaseUIActivity;
import com.yj.audio.version.cj.slc_lc2010_vdc.frags.BaseAudioListFrag;
import com.yj.audio.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioAlbumsFrag;
import com.yj.audio.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioArtistsFrag;
import com.yj.audio.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioCollectsFrag;
import com.yj.audio.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioFoldersFrag;
import com.yj.audio.version.cj.slc_lc2010_vdc.frags.SclLc2010VdcAudioNamesFrag;
import com.yj.audio.view.ToastMsg;

import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.MediaBase;
import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.mediabtn.MediaBtnController;
import js.lib.android.media.engine.mediabtn.MediaBtnReceiver;
import js.lib.android.media.player.PlayState;
import js.lib.android.media.player.audio.utils.AudioSortUtils;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.FragUtil;
import js.lib.android.utils.Logs;

/**
 * SCL_LC2010_VDC Music List Activity
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioListActivity extends BaseUIActivity
        implements AccReceiver.AccDelegate,
        MediaBtnReceiver.MediaBtnListener,
        BtCallStateController.BtCallSateDelegate,
        VoiceAssistantReceiver.VoiceAssistantDelegate {

    // TAG
    private static final String TAG = "audio_list";

    //==========Widgets in this Activity==========
    //Root view
    private View layoutRoot;

    //垃圾聚焦控件: 该控件主要是在其他空间不需要聚焦时，将聚焦转移到此。
    private View vRubbishFocus;

    //Top layout root view
    private View layoutTop;
    //Items
    private View[] vItems = new View[5];
    private View vItemFocused;
    private ImageView ivRhythmAnim;

    // Content fragment
    private BaseAudioListFrag fragCategory;
    private final int CATEGORY_COLLECT = 0;
    private final int CATEGORY_FOLDER = 1;
    private final int CATEGORY_TITLE = 2;
    private final int CATEGORY_ARTIST = 3;
    private final int CATEGORY_ALBUM = 4;

    //==========Variables in this Activity==========
    //Activity context
    private Context mContext;

    //如果本地媒体未搜索到，执行一次全盘扫描
    private boolean mIsScanWhenLocalMediaIsEmpty = true;

    //Handler
    //注册媒体按键
    private Handler mRegMediaBtnHandler = new Handler();
    //自动打开播放器
    private Handler mAutoOpenPlayerHandler = new Handler();

    // Media Button Controller
    private MediaBtnController mMediaBtnController;
    // Bluetooth call state controller
    private BtCallStateController mBtCallStateController;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.scl_lc2010_vdc_activity_audio_list);

        //
        SettingsSysUtil.setAudioState(this, 1);
        AccReceiver.register(this);
        VoiceAssistantReceiver.register(this);

        //
        init();
    }

    private void init() {
        // -- Variables --
        mContext = this;
        //Media button
        mMediaBtnController = new MediaBtnController(this);
        //BT call
        mBtCallStateController = new BtCallStateController(this);
        mBtCallStateController.register(this);

        // -- Widgets --
        layoutRoot = findRootView();
        vRubbishFocus = findViewById(R.id.v_rubbish_focus);

        // Top layout root view
        layoutTop = findViewById(R.id.layout_top);
        // Item0
        vItems[CATEGORY_COLLECT] = findViewById(R.id.v_my_favorite);
        vItems[CATEGORY_COLLECT].setOnClickListener(mFilterViewOnClick);
        // Item1
        vItems[CATEGORY_FOLDER] = findViewById(R.id.v_folder);
        vItems[CATEGORY_FOLDER].setOnClickListener(mFilterViewOnClick);
        // Item2
        vItems[CATEGORY_TITLE] = findViewById(R.id.v_music_name);
        vItems[CATEGORY_TITLE].setOnClickListener(mFilterViewOnClick);
        // Item3
        vItems[CATEGORY_ARTIST] = findViewById(R.id.v_artist);
        vItems[CATEGORY_ARTIST].setOnClickListener(mFilterViewOnClick);
        // Item4
        vItems[CATEGORY_ALBUM] = findViewById(R.id.v_album);
        vItems[CATEGORY_ALBUM].setOnClickListener(mFilterViewOnClick);
        // animation View
        ivRhythmAnim = (ImageView) findViewById(R.id.v_rate);
        ivRhythmAnim.setOnClickListener(mFilterViewOnClick);

        //Initialize execute
        bindScanService(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        PlayerAppManager.addContext(this);
        SettingsSysUtil.setRememberPlayFlag(this, true);
        SettingsSysUtil.setLastMediaType(this, MediaTypeEnum.MUSIC);
        SettingsSysUtil.setMediaPageState(this, MediaPageState.MUSIC_FOREGROUND);

        //Register Audio focus.
        boolean isAudioServiceConned = isAudioServiceConned();
        Log.i(TAG, "onResume() -isAudioServiceConned:" + isAudioServiceConned + "-");
        if (isAudioServiceConned) {
            //Register Audio focus when service binded.
            registerAudioFocus(1);

            //Update playing rhythm
            boolean isPlaying = isPlaying();
            activeAnimRhythm(isPlaying);

            //是否处理语音命令,该语音命令通过Intent传入
            boolean isProcessingVoiceCmd = processingVoiceCmdFromIntent();
            Log.i(TAG, "onResume() - isProcessingVoiceCmd : " + isProcessingVoiceCmd);
            //当未处理语音命令，并且未在播放
            if (!isProcessingVoiceCmd && !isPlaying) {
                resume();
            }
        }
    }

    @Override
    protected void onScanServiceConnected() {
        if (isActActive()) {
            Log.i(TAG, "onScanServiceConnected()");
            switchFilterTab(vItems[CATEGORY_TITLE], true);
            bindAndCreatePlayService(1, 2);
        }
    }

    @Override
    protected void onAudioServiceConnChanged(Service service) {
        if (isActActive()) {
            super.onAudioServiceConnChanged(service);
            Log.i(TAG, "onAudioServiceConnChanged(" + service + ")");
            if (service != null) {
                setPlayListener(this);
                registerAudioFocus(1);
                loadLocalMedias();
            }
        }
    }

    /**
     * Load medias stored in database of the application.
     */
    private void loadLocalMedias() {
        showLoading(true);
        CommonUtil.cancelTask(mLoadLocalMediasTask);
        mLoadLocalMediasTask = new LoadLocalMediasTask(new LoadMediaListener() {

            @Override
            public void afterLoaded(List<ProAudio> mediasToPlay) {
                if (isActActive()) {
                    Log.i(TAG, "loadLocalMedias() ->afterLoad(List<ProAudio>)");
                    showLoading(false);
                    if (EmptyUtil.isEmpty(mediasToPlay)) {
                        //Only execute once.
                        if (mIsScanWhenLocalMediaIsEmpty) {
                            mIsScanWhenLocalMediaIsEmpty = false;
                            Log.i(TAG, "loadLocalMedias() -> startScan()");
                            startScan();
                        }
                    } else {
                        setListSrcMedias(mediasToPlay);
                        setPlayList(mediasToPlay);
                        autoPlay(getLastMediaPath());
                        if (fragCategory != null) {
                            fragCategory.loadLocalMedias();
                        }
                    }
                }
            }
        });
        mLoadLocalMediasTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Automatically play.
     *
     * @param targetMediaUrl Target media url to play.
     */
    private void autoPlay(final String targetMediaUrl) {
        Log.i(TAG, "autoPlay(targetMediaUrl)");
        if (isAudioFocusGained()) {
            boolean isProcessingVoiceCmd = processingVoiceCmdFromIntent();
            if (isProcessingVoiceCmd) {
                Log.i(TAG, "autoPlay() -Processing voice command from Intent-");
            } else if (isPlaying()) {
                Log.i(TAG, "autoPlay() -Already playing-");
            } else {
                Log.i(TAG, "autoPlay() -EXEC auto play-");
                play(targetMediaUrl);
            }
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
        super.onMediaScanningEnd(isHasMedias);
        Log.i(TAG, "onMediaScanningEnd(" + isHasMedias + ")");
        showLoading(false);
        if (isHasMedias) {
            loadLocalMedias();
        } else {
            ToastMsg.show(this, getString(R.string.toast_no_audios));
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
    public void onMediaScanningRefresh(final List<ProAudio> listDeltaMedias, boolean isOnUiThread) {
        super.onMediaScanningRefresh(listDeltaMedias, isOnUiThread);
        Log.i(TAG, "onMediaScanningRefresh(List<ProAudio>," + isOnUiThread + ")");
        List<ProAudio> listSrcMedias = getListSrcMedias();
        if (EmptyUtil.isEmpty(listSrcMedias)) {
            final List<ProAudio> mediasToPlay = new ArrayList<>(listDeltaMedias);
            AudioSortUtils.sortByTitle(mediasToPlay);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onMediaScanningRefresh(List<ProAudio>) -2-" + mediasToPlay.size());
                    setListSrcMedias(mediasToPlay);
                    setPlayList(mediasToPlay);
                    autoPlay(getLastMediaPath());
                    if (fragCategory != null) {
                        fragCategory.loadLocalMedias();
                    }
                }
            });
        } else {
            listSrcMedias.addAll(listDeltaMedias);
            final List<ProAudio> targetSrcMedias = listSrcMedias;
            AudioSortUtils.sortByTitle(targetSrcMedias);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onMediaScanningRefresh(List<ProAudio>) -2-" + targetSrcMedias.size());
                    setListSrcMedias(targetSrcMedias);
                    if (fragCategory != null) {
                        fragCategory.refreshData();
                    }
                }
            });
        }
    }

    /**
     * 从文件夹打开媒体播放器
     * <p>目前该方法预留</p>
     *
     * @param playPos       : target play position
     * @param listPlayPaths : target media URL list
     */
    //TODO
    @Override
    public void onPlayFromFolder(int playPos, List<String> listPlayPaths) {
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        autoOpenPlayer(false);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Filter item click event.
     */
    private View.OnClickListener mFilterViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == ivRhythmAnim) {
                List<ProAudio> playingList = getListMedias();
                if (!EmptyUtil.isEmpty(playingList)) {
                    openPlayerActivity();
                }

                //Click favorite/folder/name/artist/album
            } else {
                switchFilterTab(v, true);
            }
        }
    };

    /**
     * Open player activity
     */
    private void openPlayerActivity() {
        Log.i(TAG, "openPlayerActivity()");
        List<ProAudio> playingList = getListMedias();
        if (!EmptyUtil.isEmpty(playingList)) {
            Log.i(TAG, "openPlayerActivity() -EXEC-");
            Intent playerIntent = new Intent(mContext, SclLc2010VdcAudioPlayerActivity.class);
            startActivityForResult(playerIntent, 1);
        }
    }

    /**
     * Switch filter item.
     *
     * @param v          Filter item that focused.
     * @param isExecLoad true : Load fragment focused.
     */
    private void switchFilterTab(View v, boolean isExecLoad) {
        vItemFocused = v;
        //Switch
        final int loop = vItems.length;
        for (int idx = 0; idx < loop; idx++) {
            View item = vItems[idx];
            if (item == v) {
                item.setFocusable(true);
                item.requestFocus();
                setBg(item, true);
                if (isExecLoad) {
                    loadFragment(idx, "");
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
     * @param idx    The idx of fragment
     * @param params [ALBUM / ARTIST / TITLE] value
     */
    private void loadFragment(int idx, String... params) {
        //Remove old
        if (fragCategory != null) {
            FragUtil.removeV4Fragment(fragCategory, getSupportFragmentManager());
        }

        //Load New
        switch (idx) {
            case CATEGORY_COLLECT:
                fragCategory = new SclLc2010VdcAudioCollectsFrag();
                break;
            case CATEGORY_FOLDER:
                fragCategory = new SclLc2010VdcAudioFoldersFrag();
                break;
            case CATEGORY_TITLE:
                fragCategory = new SclLc2010VdcAudioNamesFrag();
                break;
            case CATEGORY_ARTIST:
                fragCategory = new SclLc2010VdcAudioArtistsFrag();
                break;
            case CATEGORY_ALBUM:
                fragCategory = new SclLc2010VdcAudioAlbumsFrag();
                break;
        }
        if (fragCategory != null) {
            fragCategory.setParam(params);
            FragUtil.loadV4Fragment(R.id.layout_frag, fragCategory, getSupportFragmentManager());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String flag = data.getStringExtra("flag");
            if ("PLAYER_FINISH_ON_CLICK_LIST".equals(flag)) {
                if (fragCategory != null) {
                    fragCategory.refreshData();
                }
            } else if ("PLAYER_FINISH_ON_GET_KEY".equals(flag)) {
                if (fragCategory != null) {
                    fragCategory.refreshData();
                }
                autoOpenPlayer(true);
            } else if ("PLAYER_FINISH_ON_CLICK_TITLE".equals(flag)) {
                switchFilterTab(vItems[2], false);
                String[] values = data.getStringArrayExtra("values");
                if (values != null && values.length > 0) {
                    loadFragment(CATEGORY_TITLE, values[0]);
                }
            } else if ("PLAYER_FINISH_ON_CLICK_ARTIST".equals(flag)) {
                switchFilterTab(vItems[3], false);
                String[] values = data.getStringArrayExtra("values");
                if (values != null && values.length > 0) {
                    loadFragment(CATEGORY_ARTIST, values[0]);
                }
            } else if ("PLAYER_FINISH_ON_CLICK_ALBUM".equals(flag)) {
                switchFilterTab(vItems[4], false);
                String[] values = data.getStringArrayExtra("values");
                if (values != null && values.length > 0) {
                    loadFragment(CATEGORY_ALBUM, values[0]);
                }
            }
        }
    }

    /**
     * Demand : Automatically open player after 5s.
     *
     * @param isAutoOpen <p>true: Automatically open player after 5s;</p>
     *                   <p>false:
     *                   (1)Cancel on touch.
     *                   (2)Cancel on receive key event from seek+/seek-
     *                   (3)Cancel on activity execute {@link #moveTaskToBack(boolean)}
     *                   </p>
     */
    private void autoOpenPlayer(boolean isAutoOpen) {
        Log.i(TAG, "autoOpenPlayer(" + isAutoOpen + ")");
        if (isAutoOpen) {
            mAutoOpenPlayerHandler.removeCallbacksAndMessages(null);
            mAutoOpenPlayerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    openPlayerActivity();
                }
            }, 5000);
        } else {
            mAutoOpenPlayerHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onGotMediaKeyCode(KeyEvent event) {
        Log.i(TAG, "onGotMediaKeyCode(KeyEvent)");
        if (event.getAction() == KeyEvent.ACTION_UP) {
            moveFocusToRubbish(vRubbishFocus);
            KeyEnum key = KeyEnum.getKey(event.getKeyCode());
            Log.i(TAG, "onGotMediaKeyCode(" + key + ")");
            switch (key) {
                case KEYCODE_MEDIA_PREVIOUS:
                    execPlayPrevByUser();
                    break;
                case KEYCODE_MEDIA_NEXT:
                    execPlayNextByUser();
                    break;
            }
        }
    }

    @Override
    public void onGotKeyCode(KeyEnum key) {
        Log.i(TAG, "onGotKeyCode(" + key + ")");
        moveFocusToRubbish(vRubbishFocus);
        switch (key) {
            case KEYCODE_DPAD_LEFT:
                autoOpenPlayer(false);
                if (fragCategory != null) {
                    fragCategory.selectPrev();
                }
                break;
            case KEYCODE_DPAD_RIGHT:
                autoOpenPlayer(false);
                if (fragCategory != null) {
                    fragCategory.selectNext();
                }
                break;
            case KEYCODE_ENTER:
                autoOpenPlayer(false);
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
        PlayerAppManager.exitCurrPlayer(true);
    }

    @Override
    public void onBtCallStateChanged(boolean isBtRunning) {
        Log.i(TAG, "onBtCallStateChanged(" + isBtRunning + ")");
        if (isBtRunning) {
            if (isPlaying()) {
                Log.i(TAG, "Audio :: pause()");
                pause();
            }
        } else {
            boolean isAudioFocusGained = isAudioFocusGained();
            Log.i(TAG, "isAudioFocusGained: " + isAudioFocusGained);
            if (!isPlaying() && isAudioFocusGained) {
                Log.i(TAG, "Audio :: resume()");
                resume();
            }
        }
    }

    @Override
    public void onVoiceCommand(ActionEnum ae) {
        Log.i(TAG, "onVoiceCommand(" + ae + ")");
        parseVoiceCmdFromBroadcast(ae);
        if (isAudioFocusGained()) {
            processingVoiceCmdFromBroadcast();
        }
    }

    @Override
    public void onAudioFocusDuck() {
        Log.i(TAG, "onAudioFocusDuck()");
    }

    @Override
    public void onAudioFocusTransient() {
        Log.i(TAG, "onAudioFocusTransient()");
    }

    @Override
    public void onAudioFocusLoss() {
        Log.i(TAG, "onAudioFocusLoss()");
        registerMediaBtn(false);
        SettingsSysUtil.setRememberPlayFlag(this, false);
    }

    @Override
    public void onAudioFocusGain() {
        Log.i(TAG, "onAudioFocusGain()");
        registerMediaBtn(true);

        //Process voice command from broadcast
        boolean isProcessingVoiceCmdFromBroadcast = processingVoiceCmdFromBroadcast();
        Log.i(TAG, "isProcessingVoiceCmdFromBroadcast: " + isProcessingVoiceCmdFromBroadcast);
        if (!isProcessingVoiceCmdFromBroadcast) {
            //Resume play if no voice command.
            if (!isPlaying()) {
                resume();
            }
        }
    }

    /**
     * Callback when player call method: registerAudioFocus
     */
    @Override
    public void onAudioFocus(int flag) {
        Log.i(TAG, "onAudioFocus(" + flag + ")");
        switch (flag) {
            case 1:
                registerMediaBtn(true);
                break;
            case 2:
                registerMediaBtn(false);
                break;
        }
    }

    /**
     * 注册媒体按键事件
     * <P>为了防止注册失败，需要延迟补注册</P>
     */
    private void registerMediaBtn(final boolean isReg) {
        Log.i(TAG, "registerMediaBtn(" + isReg + ")");
        if (mMediaBtnController != null) {
            mRegMediaBtnHandler.removeCallbacksAndMessages(null);
            if (isReg) {
                MediaBtnReceiver.setListener(this);
                mMediaBtnController.register();
                //推迟几秒钟再次注册一下，这是为了防止有时候注册不成功的情况。
                mRegMediaBtnHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "registerMediaBtn -Delay Reg-");
                        mMediaBtnController.register();
                    }
                }, 3000);
            } else {
                mMediaBtnController.unregister();
                MediaBtnReceiver.setListener(null);
            }
        }
    }

    @Override
    public void onPlayStateChanged(final PlayState playState) {
        if (isActActive()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    SclLc2010VdcAudioListActivity.super.onPlayStateChanged(playState);
                    Log.i(TAG, "onPlayStateChanged(" + playState + ")");
                    cachePlayState(playState);
                    switch (playState) {
                        case PLAY:
                            activeAnimRhythm(true);
                            break;
                        case PREPARED:
                            activeAnimRhythm(true);
                            if (fragCategory != null) {
                                fragCategory.refreshPlaying(getCurrMediaPath());
                            }
                            break;
                        case SEEK_COMPLETED:
                        case REFRESH_UI:
                            break;
                        default:
                            activeAnimRhythm(false);
                            break;
                    }
                }
            });
        }
    }

    /**
     * Active playing rhythm
     */
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
    public void onBackPressed() {
//        super.onBackPressed();
        if (fragCategory != null) {
            int backRes = fragCategory.onBackPressed();
            switch (backRes) {
                case 1:
                    break;
                default:
                    autoOpenPlayer(false);
                    moveTaskToBack(true);
                    break;
            }
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
//        if (!isActActive()) {
//            clearActivity();
//        }
        super.onDestroy();
    }

    @Override
    protected void clearActivity() {
        super.clearActivity();
        //Unbind service.
        registerAudioFocus(2);
        bindScanService(false);
        bindAndCreatePlayService(3, 4);

        //Cancel all task or runnable.
        cancelAllTasks();
        mRegMediaBtnHandler.removeCallbacksAndMessages(null);
        mAutoOpenPlayerHandler.removeCallbacksAndMessages(null);

        //Remove controllers
        registerMediaBtn(false);
        if (mBtCallStateController != null) {
            mBtCallStateController.unregister();
            mBtCallStateController = null;
        }

        //Remove object register.
        removePlayListener(this);
        AccReceiver.unregister(this);
        VoiceAssistantReceiver.unregister(this);

        //Update state.
        SettingsSysUtil.setAudioState(this, 0);
        // Remove control
        PlayerAppManager.removeContext(this);
    }

    /**
     * Show UI loading.
     */
    private void showLoading(boolean isShow) {
        if (fragCategory != null) {
            fragCategory.showLoading(isShow);
        }
    }

    /**
     * Open player
     *
     * @param mediaUrl   The media url to play.
     * @param listMedias The media list to play.
     */
    public void playAndOpenPlayerActivity(int position, String mediaUrl, List<? extends MediaBase> listMedias) {
        try {
            //Execute play
            execPlay(position, mediaUrl, listMedias);
            //Open player
            openPlayerActivity();
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "openPlayerActivity()", e);
        }
    }

    /**
     * Cache Program Information
     * <p>
     * This method used to set playing status for Screen/Launcher
     */
    private void cachePlayState(PlayState playState) {
        Log.i(TAG, "cachePlayState(" + playState + ")");
        //0-未打开
        //1-打开
        //2-播放
        switch (playState) {
            case PLAY:
            case PREPARED:
                SettingsSysUtil.setAudioState(this, 2);
                break;
            case REFRESH_UI:
            case SEEK_COMPLETED:
                break;
            default:
                SettingsSysUtil.setAudioState(this, 1);
                break;
        }
    }

    @Override
    public void updateThemeToDefault() {
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
        switchFilterTab(vItemFocused, false);
    }
}