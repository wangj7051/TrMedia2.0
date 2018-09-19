package com.tricheer.player.receiver;

import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothHeadsetClientCall;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.tricheer.app.receiver.PlayerReceiverActionIdxs;
import com.tricheer.app.receiver.PlayerReceiverActions;
import com.tricheer.player.App;
import com.tricheer.player.MusicPlayerActivity;
import com.tricheer.player.VideoPlayerActivity;
import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.engine.PlayerAppManager;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.engine.PlayerConsts;
import com.tricheer.player.engine.PlayerConsts.PlayerOpenMethod;
import com.tricheer.player.engine.PlayerType;
import com.tricheer.player.engine.VersionController;
import com.tricheer.player.service.MusicPlayService;
import com.tricheer.player.utils.PlayerLogicUtils;
import com.tricheer.player.utils.PlayerPreferUtils;
import com.tricheer.player.version.base.activity.music.BaseMusicExtendActionsActivity;
import com.tricheer.player.version.base.activity.music.BaseMusicPlayerActivity;
import com.tricheer.player.version.base.activity.video.BaseVideoExtendActionsActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * Player receiver
 *
 * @author Jun.Wang
 */
public class PlayerReceiver extends PlayerBaseReceiver {
    // LOG TAG
    private final String TAG = "PlayerReceiver";

    // Is BlueTooth Dialing
    public static boolean isBtCalling = false;
    // Is AiSpeech Opened And Showing
    public static boolean isAiSpeechShowing = false;
    // Is Record Camera Opened
    public static boolean isRecordStart = false;

    // Is Device Power Off ING
    public static boolean isPowerOffing = false;

    /**
     * Context
     */
    private static WeakReference<Context> mContext;

    /**
     * Open Music/Video Runnable
     */
    private Runnable mOpenMusicPlayerRunnable, mOpenVideoPlayerRunnable;

    public static void init(Context context) {
        mContext = new WeakReference<>(context);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action: " + action);

        ActionEnum ae = ActionEnum.getByAction(action);
        Log.i(TAG, "action-ae: " + ae);
        if (ae != null) {
            switch (ae) {
                //ACC
                case ACC_ON:
                    break;
                case ACC_OFF:
                    break;
                case ACC_OFF_TRUE:
                    PlayerPreferUtils.getVideoWarningFlag(true, 1);
                    break;

                //Test
                case TEST_OPEN_VIDEO_LIST:
                    testSendVideoList();
                    break;
                case TEST_OPEN_VIDEO:
                    Intent videoI = new Intent(context, VideoPlayerActivity.class);
                    videoI.putExtra("IS_TEST", true);
                    videoI.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(videoI);
                    break;
                case TEST_OPEN_AUDIO:
                    Intent audioI = new Intent(context, MusicPlayerActivity.class);
                    audioI.putExtra("IS_TEST", true);
                    audioI.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(audioI);
                    break;
                case TEST_EXIT_PLAYER:
                    PlayerAppManager.exitCurrPlayer();
                    break;
            }
            return;
        }

        int actionType = PlayerReceiverActions.getActionIdx(action);
        switch (actionType) {
            // ### System Power Off/On ###
            case PlayerReceiverActionIdxs.SYS_AUDIO_NOISY:
                isPowerOffing = true;
                break;
            case PlayerReceiverActionIdxs.SYS_BOOT_COMPLETED:
                isPowerOffing = false;
                isBtCalling = false;
                // 清空缓存的 [音乐] 播放状态,这个播放状态是用来给屏保显示当前媒体播放信息的
                PlayerLogicUtils.cachePlayerState(context.getContentResolver(), PlayerCxtFlag.MUSIC_PLAYER, 0);
                break;

            // ### ACC OFF/ON ###
            case PlayerReceiverActionIdxs.SYS_SHUTDOWN:
                if (VersionController.isCanAutoResume()) {
                    PlayerReceiverListener currPlayer = PlayerAppManager.getCurrPlayer();
                    if (currPlayer != null && currPlayer.isCacheOnAccOff()) {
                        int currPlayerFlag = PlayerAppManager.getCurrPlayerFlag();
                        Log.i(TAG, "ACC OFF -> [currPlayerFlag:" + currPlayerFlag + "]");
                        PlayerPreferUtils.getPlayerFlagOfShutdown(true, currPlayerFlag);
                    }
                }
                PlayerAppManager.exitCurrPlayer();
                break;
            case PlayerReceiverActionIdxs.SELF_BOOT_COMPLETED:
                if (VersionController.isCanAutoResume()) {
                    int cachedPlayerFlag = PlayerPreferUtils.getPlayerFlagOfShutdown(false, PlayerCxtFlag.NONE);
                    PlayerPreferUtils.getPlayerFlagOfShutdown(true, PlayerCxtFlag.NONE);
                    Log.i(TAG, "ACC ON -> [cachedPlayerFlag:" + cachedPlayerFlag + "]");
                    PlayerAppManager.startPlayer(context, cachedPlayerFlag);
                }
                break;

            // ### AIS Window Status ###
            case PlayerReceiverActionIdxs.AIS_STATUS:
                String param = intent.getStringExtra("SmallWindow");
                Log.i("SmallWindow", "SmallWindow:" + param);
                if ("Open".equals(param)) {
                    isAiSpeechShowing = true;
                    notifyOperate(null, ReceiverOperates.AIS_OPEN);
                } else if ("Close".equals(param)) {
                    isAiSpeechShowing = false;
                    notifyOperate(null, ReceiverOperates.AIS_EXIT);
                }
                break;
            case PlayerReceiverActionIdxs.AIOS_OPERATE:
                // Search Result,格式如下:
                // ---START---
                // {id='0', title='逆流成河', artist='金南玲', album='unknown',
                // duration=125203, size=5009368,
                // url='/storage/sdcard1/Song/逆流成河-金南玲.mp3', local='逆流成河-金南玲'}
                // ---END---
                String jsonSearchRes = intent.getStringExtra("SEARCH_RESULT");
                Log.i(TAG, "|<<*jsonSearchRes*: { " + jsonSearchRes + " }>>|");
                if (!TextUtils.isEmpty(jsonSearchRes)) {
                    try {
                        JSONArray jaValues = new JSONArray(jsonSearchRes);
                        JSONObject joValue = jaValues.optJSONObject(0);
                        String path = joValue.optString("url", "");
                        if (PlayerType.isMusic()) {
                            notifyPlayMedia(path);
                        } else {
                            Intent data = new Intent();
                            data.putExtra("targetPath", path);
                            App.openMusicPlayer(mContext.get(), "", data);
                        }
                    } catch (Exception e) {
                        Logs.printStackTrace(TAG + ">  jsonSearchRes", e);
                    }
                    return;
                }

                // Operate
                String operate = intent.getStringExtra("media_action");
                Log.i(TAG, "|<<*operate*: { " + operate + " }>>|");
                if (!TextUtils.isEmpty(operate)) {
                    // 播放音乐 / 打开音乐 / 继续播放
                    if ("RESUME".equals(operate) || "PLAY".equals(operate)) {
                        notifyOperate(null, ReceiverOperates.RESUME);
                        // 音乐暂停 / 视频暂停
                    } else if ("PAUSE".equals(operate)) {
                        notifyOperate(null, ReceiverOperates.PAUSE);
                        // 随机播歌 / 随便来首歌
                    } else if ("RANDOM".equals(operate)) {
                        doOperatePlayer(intent, ReceiverOperates.MUSIC_RANDOM);

                        // 随机模式 / 随机播放
                    } else if ("MODE_RANDOM".equals(operate)) {
                        notifyOperate(null, ReceiverOperates.MUSIC_MODE_RANDOM);
                        // 顺序模式 / 顺序播放
                    } else if ("MODE_ORDER".equals(operate)) {
                        notifyOperate(null, ReceiverOperates.MUSIC_MODE_ORDER);
                        // 单曲模式 / 单曲循环
                    } else if ("MODE_REPEAT".equals(operate)) {
                        notifyOperate(null, ReceiverOperates.MUSIC_MODE_SIGLE);
                        // 循环模式 / 循环播放
                    } else if ("MODE_LOOP".equals(operate)) {
                        notifyOperate(null, ReceiverOperates.MUSIC_MODE_LOOP);

                        // 上一首/上一个/上一曲
                    } else if ("PLAY_PRE".equals(operate)) {
                        notifyOperate(null, ReceiverOperates.PREVIOUS);
                        // 下一首/下一个/下一曲
                    } else if ("PLAY_NEXT".equals(operate)) {
                        notifyOperate(null, ReceiverOperates.NEXT);

                    } else if ("FORWARD".equals(operate) && PlayerType.isVideo()) {
                    } else if ("REWIND".equals(operate) && PlayerType.isVideo()) {

                        // 音乐列表上一页 / 视频列表上一页
                    } else if ("PAGE_PRE".equals(operate)) {
                        notifyOperate(null, ReceiverOperates.PREV_PAGE);
                        // 音乐列表下一页 / 视频列表下一页
                    } else if ("PAGE_NEXT".equals(operate)) {
                        notifyOperate(null, ReceiverOperates.NEXT_PAGE);
                    }
                    return;
                }
                break;

            // ### Open Player ###
            case PlayerReceiverActionIdxs.OPEN_MUSIC_PLAYER:
                doOpenMusicPlayer();
                break;
            case PlayerReceiverActionIdxs.OPEN_VIDEO_PLAYER:
                doOpenVideoPlayer();
                break;

            // ### Close Player ###
            case PlayerReceiverActionIdxs.EXIT_MUSIC:
                if (PlayerType.isMusic()) {
                    PlayerAppManager.closeMusicPlayer();
                }
                break;
            case PlayerReceiverActionIdxs.EXIT_VIDEO:
                if (PlayerType.isVideo()) {
                    PlayerAppManager.closeVideoPlayer();
                }
                break;
            case PlayerReceiverActionIdxs.OPEN_BT_MUSIC:
            case PlayerReceiverActionIdxs.OPEN_KUWO_MUSIC:
                PlayerAppManager.exitCurrPlayer();
                break;

            // ### Click FileManager Media to Play ###
            case PlayerReceiverActionIdxs.PLAY_MUSIC_BY_FILEMANAGER:
                doPlayMusicFromFileManager(intent);
                break;
            case PlayerReceiverActionIdxs.PLAY_VIDEO_BY_FILEMANAGER:
                doPlayVideoFromFileManager(intent);
                break;

            // ### Click [PLAY/PAUSE] on the Application ICON to Play ###
            case PlayerReceiverActionIdxs.PLAY_MUSIC_BY_APPICON_ACT:
                doPlayMusicFromLauncher(context, intent);
                break;

            // ### AiSpeech [Search List]/[Send Searched List]/[Play Selected Music]###
            case PlayerReceiverActionIdxs.AIS_SEARCH_MUSIC:
                doSearchMediaList(context, intent);
                break;
            case PlayerReceiverActionIdxs.AIS_PLAY_SEARCHED_MUSIC:
                doPlaySelectedMusic(intent);
                break;

            // ### AiSpeech Common Commands ###
            case PlayerReceiverActionIdxs.AIS_PAUSE:
                notifyOperate(null, ReceiverOperates.PAUSE);
                break;
            case PlayerReceiverActionIdxs.AIS_RESUME:
                notifyOperate(null, ReceiverOperates.RESUME);
                break;
            case PlayerReceiverActionIdxs.AIS_PREV:
                notifyOperate(null, ReceiverOperates.PREVIOUS);
                break;
            case PlayerReceiverActionIdxs.AIS_NEXT:
                notifyOperate(null, ReceiverOperates.NEXT);
                break;

            // ### AiSpeech Operate Music Commands ###
            case PlayerReceiverActionIdxs.MUSIC_MODE:
                if (PlayerType.isMusic()) {
                    String mode = intent.getStringExtra("mode");
                    if ("random".equals(mode)) {
                        notifyOperate(null, ReceiverOperates.MUSIC_MODE_RANDOM);
                    } else if ("repeat".equals(mode)) {
                        notifyOperate(null, ReceiverOperates.MUSIC_MODE_SIGLE);
                    } else if ("sequence".equals(mode)) {
                        notifyOperate(null, ReceiverOperates.MUSIC_MODE_LOOP);
                    }
                }
                break;
            case PlayerReceiverActionIdxs.MUSIC_AIS_RANDOM:
                doOperatePlayer(intent, ReceiverOperates.MUSIC_RANDOM);
                break;

            // ### AiSpeech Operate Video Commands ###
            // Video Pause/Resume/Previous/Next
            case PlayerReceiverActionIdxs.VIDEO_PAUSE:
                if (PlayerType.isVideo()) {
                    notifyOperate(null, ReceiverOperates.PAUSE);
                }
                break;
            case PlayerReceiverActionIdxs.VIDEO_RESUME:
                if (PlayerType.isVideo()) {
                    notifyOperate(null, ReceiverOperates.RESUME);
                }
                break;
            case PlayerReceiverActionIdxs.VIDEO_PREV:
                if (PlayerType.isVideo()) {
                    notifyOperate(null, ReceiverOperates.PREVIOUS);
                }
                break;
            case PlayerReceiverActionIdxs.VIDEO_NEXT:
                if (PlayerType.isVideo()) {
                    notifyOperate(null, ReceiverOperates.NEXT);
                }
                break;
            // Video Play Speed
            case PlayerReceiverActionIdxs.VIDEO_PLAY_NORMAL:
                if (PlayerType.isVideo()) {
                    notifyOperate(null, ReceiverOperates.VIDEO_NORMAL);
                }
                break;
            case PlayerReceiverActionIdxs.VIDEO_PLAY_FORWARD:
                if (PlayerType.isVideo()) {
                    notifyOperate(null, ReceiverOperates.VIDEO_FORWARD);
                }
                break;
            case PlayerReceiverActionIdxs.VIDEO_PLAY_BACKWARD:
                if (PlayerType.isVideo()) {
                    notifyOperate(null, ReceiverOperates.VIDEO_BACKWARD);
                }
                break;
            // Video Screen Size
            case PlayerReceiverActionIdxs.VIDEO_FULL:
                if (PlayerType.isVideo()) {
                    notifyOperate(null, ReceiverOperates.VIDEO_SCREEN_FULL);
                }
                break;
            case PlayerReceiverActionIdxs.VIDEO_RESIZE:
                if (PlayerType.isVideo()) {
                    param = intent.getStringExtra("VIEW_TYPE");
                    if ("bigger".equals(param)) {
                        notifyOperate(null, ReceiverOperates.VIDEO_SCREEN_BIGGER);
                    } else if ("smaller".equals(param)) {
                        notifyOperate(null, ReceiverOperates.VIDEO_SCREEN_SMALLER);
                    } else if ("4to3".equals(param)) {
                        notifyOperate(null, ReceiverOperates.VIDEO_SCREEN_4_3);
                    } else if ("16to9".equals(param)) {
                        notifyOperate(null, ReceiverOperates.VIDEO_SCREEN_16_9);
                    } else if ("21to9".equals(param)) {
                        notifyOperate(null, ReceiverOperates.VIDEO_SCREEN_21_9);
                    }
                }
                break;
            // Video Show/Close PlayList
            case PlayerReceiverActionIdxs.VIDEO_SHOW_LIST:
                break;
            case PlayerReceiverActionIdxs.VIDEO_CLOSE_LIST:
                break;

            // ### BlueTooth Commands ###
            // BT Call Connect State Changed
            case PlayerReceiverActionIdxs.BTCALL_SYS_CONN_STATE_CHANGED:
                Log.i(TAG, "[isPowerOffing1 : " + isPowerOffing + "]");
                if (!isPowerOffing) {
                    int hfpState = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                    Log.i(TAG, "BTCALL_CONN_STATE_CHANGED -> [hfpState:" + hfpState + "]");
                    if (hfpState == BluetoothProfile.STATE_CONNECTED) {
                        Log.i(TAG, "BTCALL : STATE_CONNECTED");
                        // doSelectMedia(true);
                    } else if (hfpState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.i(TAG, "BTCALL : STATE_DISCONNECTED");
                        isBtCalling = false;
                        notifyOperate(null, ReceiverOperates.BTCALL_END);
                    }
                }
                break;
            // BT Call State Changed
            case PlayerReceiverActionIdxs.BTCALL_SYS_CHANGED:
                BluetoothHeadsetClientCall bhcc = (BluetoothHeadsetClientCall) intent
                        .getParcelableExtra(BluetoothHeadsetClient.EXTRA_CALL);
                int callState = bhcc.getState();
                Log.i(TAG, "BTCALL_CHANGED -> [callState:" + callState + "]");
                if (callState == BluetoothHeadsetClientCall.CALL_STATE_TERMINATED) {
                    Log.i(TAG, "BTCALL : CALL_STATE_TERMINATED");
                    isBtCalling = false;
                    notifyOperate(null, ReceiverOperates.BTCALL_END);
                }
                break;
            // BT Call IDLE
            case PlayerReceiverActionIdxs.BTCALL_IDLE_IN:
            case PlayerReceiverActionIdxs.BTCALL_IDLE_OUT:
                Log.i(TAG, "[isPowerOffing2 : " + isPowerOffing + "]");
                if (!isPowerOffing) {
                    isBtCalling = false;
                    notifyOperate(null, ReceiverOperates.BTCALL_END);
                }
                break;
            // BT Calling
            case PlayerReceiverActionIdxs.BT_RINGING_TRICHEER_DIAL:
                String callNumber = intent.getStringExtra("number");
                Log.i(TAG, "[number:" + callNumber + "]");
                if (!TextUtils.isEmpty(callNumber)) {
                    isBtCalling = true;
                    notifyOperate(null, ReceiverOperates.BTCALL_RUNING);
                }
                break;
            case PlayerReceiverActionIdxs.BT_RINGING_IN_SPECIAL:
            case PlayerReceiverActionIdxs.BT_RINGING_IN:
            case PlayerReceiverActionIdxs.BT_RINGING_OUT:
                isBtCalling = true;
                notifyOperate(null, ReceiverOperates.BTCALL_RUNING);
                break;

            // ### Record State ###
            case PlayerReceiverActionIdxs.RECORD_STATECHANGE:
                if (PlayerType.isVideo()) {
                    isRecordStart = intent.getBooleanExtra("state", false);
                    Log.i(TAG, "isRecordStart:" + isRecordStart);
                    notifyOperate(null, isRecordStart ? ReceiverOperates.RECORD_STATE_START : ReceiverOperates.RECORD_STATE_END);
                }
                break;

            // ### Cloud E-Dog ###
            case PlayerReceiverActionIdxs.E_DOG_PLAY_START:
                notifyOperate(null, ReceiverOperates.PAUSE_ON_E_DOG_START);
                break;
            case PlayerReceiverActionIdxs.E_DOG_PLAY_END:
                notifyOperate(null, ReceiverOperates.RESUME_ON_E_DOG_END);
                break;

            // ### Screen ###
            // Screen Status
            case PlayerReceiverActionIdxs.SCREEN_OFF:
            case PlayerReceiverActionIdxs.SCREEN_SLEEP:
                if (PlayerType.isVideo()) {
                    notifyOperate(null, ReceiverOperates.VIDEO_PAUSE_ON_SCREEN_OFF);
                }
                break;
            case PlayerReceiverActionIdxs.SCREEN_ON:
                if (PlayerType.isVideo()) {
                    notifyOperate(null, ReceiverOperates.VIDEO_RESUME_ON_SCREEN_ON);
                }
                break;
            // Screen Saver
            case PlayerReceiverActionIdxs.SCREEN_SAVER_EXIT:
                if (PlayerType.isVideo()) {
                    notifyOperate(null, ReceiverOperates.VIDEO_RESUME_ON_MASKAPP_EXIT);
                }
                break;

            // ### System Temperature Mode ###
            case PlayerReceiverActionIdxs.SYS_TEMP_LOW:
                break;
            case PlayerReceiverActionIdxs.SYS_TEMP_HIGH:
                if (PlayerType.isVideo()) {
                    notifyOperate(null, ReceiverOperates.SYS_TEMP_HIGH);
                }
                break;

            // ### Open Logs ###
            case PlayerReceiverActionIdxs.OPEN_LOGS:
                Logs.setPrintLog(intent.getBooleanExtra("IS_OPEN", false));
                break;
        }
    }

    /**
     * 操作播放器
     *
     * @param data   : 数据
     * @param opFlag : 要执行的动作标记
     */
    private void doOperatePlayer(Intent data, String opFlag) {
        // 获取当前播放器
        PlayerReceiverListener currPlayer = PlayerAppManager.getCurrPlayer();
        // 语音中带有关键字 "歌"or"音乐"， 则打开音乐播放器
        String voiceStr = data.getStringExtra("order");
        if (isNeedOpenMusicPlayer(voiceStr)) {
            if (currPlayer == null) {
                App.openMusicPlayer(mContext.get(), "", null);
            } else if (PlayerType.isMusic()) {
                notifyOperate(currPlayer, opFlag);
            }

            // Just operate
        } else if (currPlayer != null) {
            notifyOperate(currPlayer, opFlag);
        }

        // 视频列表页面
        Context cxtVideoList = PlayerAppManager.getCxt(PlayerCxtFlag.VIDEO_LIST);
        if (cxtVideoList != null) {
            if (cxtVideoList instanceof BaseVideoExtendActionsActivity) {
                ((BaseVideoExtendActionsActivity) cxtVideoList).onNotifyOperate(opFlag);
            }
        }

        // 音频列表页面
        Context cxtMusicList = PlayerAppManager.getCxt(PlayerCxtFlag.MUSIC_LIST);
        if (cxtMusicList != null) {
            if (cxtMusicList instanceof BaseMusicExtendActionsActivity) {
                ((BaseMusicExtendActionsActivity) cxtMusicList).onNotifyOperate(opFlag);
            }
        }
    }

    /**
     * Notify Play Media
     */
    private void notifyPlayMedia(String mediaPath) {
        PlayerReceiverListener l = PlayerAppManager.getCurrPlayer();
        if (l != null) {
            l.onNotifyPlayMedia(mediaPath);
        }
    }

    /**
     * Start Music Play Service
     */
    private void startMusicPlayService(Context cxt, boolean isStart) {
        Intent playServiceIntent = new Intent(cxt, MusicPlayService.class);
        if (isStart) {
            playServiceIntent.putExtra(PlayerConsts.IS_JUST_OPEN_SERVICE, true);
            cxt.startService(playServiceIntent);
        } else {
            cxt.stopService(playServiceIntent);
        }
    }

    /**
     * Open Video Player
     */
    private void doOpenMusicPlayer() {
        if (PlayerType.isMusic()) {
            App.openMusicPlayer(mContext.get(), "", null);
            if (VersionController.isCjVersion()) {
                notifyOperate(null, ReceiverOperates.RESUME);
            }
        } else {
            if (mOpenMusicPlayerRunnable == null) {
                mOpenMusicPlayerRunnable = new Runnable() {

                    @Override
                    public void run() {
                        App.openMusicPlayer(mContext.get(), "", null);
                    }
                };
            } else {
                mHandler.removeCallbacks(mOpenMusicPlayerRunnable);
            }
            PlayerAppManager.exitCurrPlayer();
            mHandler.postDelayed(mOpenMusicPlayerRunnable, M_DEFAULT_DELAY_TIME);
        }
    }

    /**
     * Open Video Player
     */
    private void doOpenVideoPlayer() {
        if (PlayerType.isVideo()) {
            App.openVideoPlayer(mContext.get(), "", null);
            if (VersionController.isCjVersion()) {
                notifyOperate(null, ReceiverOperates.RESUME);
            }
        } else {
            if (mOpenVideoPlayerRunnable == null) {
                mOpenVideoPlayerRunnable = new Runnable() {

                    @Override
                    public void run() {
                        App.openVideoPlayer(mContext.get(), "", null);
                    }
                };
            } else {
                mHandler.removeCallbacks(mOpenVideoPlayerRunnable);
            }
            PlayerAppManager.exitCurrPlayer();
            mHandler.postDelayed(mOpenVideoPlayerRunnable, M_DEFAULT_DELAY_TIME);
        }
    }

    /**
     * Play Music From Launcher
     */
    private void doPlayMusicFromLauncher(Context context, Intent intent) {
        int active = intent.getIntExtra("action", -1);
        Log.i(TAG, "doPlayMusicFromLauncher(context,intent) -> [active:" + active);
        PlayerReceiverListener currPlayer = PlayerAppManager.getCurrPlayer();
        Log.i(TAG, "[currPlayer:" + currPlayer);
        if (currPlayer == null || !PlayerType.isMusic()) {
            if (active == 1) {
                startMusicPlayService(context, true);
            }
        } else if (currPlayer instanceof MusicPlayService) {
            if (active == 0) {
                startMusicPlayService(context, false);
            }
        } else if (currPlayer instanceof BaseMusicPlayerActivity) {
            if (active == 1) {
                doOperatePlayer(intent, ReceiverOperates.RESUME);
            } else if (active == 0) {
                doOperatePlayer(intent, ReceiverOperates.PAUSE);
            }
        }
    }

    /**
     * Notify Play Music From FileManager
     * <p>
     * Click media to play
     */
    private void doPlayMusicFromFileManager(Intent intent) {
        PlayerReceiverListener currPlayer = PlayerAppManager.getCurrPlayer();
        if (EmptyUtil.isNull(currPlayer) || !PlayerType.isMusic()) {
            App.openMusicPlayer(mContext.get(), PlayerOpenMethod.VAL_FILE_MANAGER, intent);
        } else {
            App.openMusicPlayer(mContext.get(), "", null);
            // Play Selected Medias
            Bundle bundle = intent.getExtras();
            int playPos = bundle.getInt(PlayerConsts.INDEX);
            ArrayList<String> listPaths = bundle.getStringArrayList(PlayerConsts.FILE_LIST);
            notifyPlayFromFolder(currPlayer, playPos, listPaths);
        }
    }

    /**
     * Notify Play Music From FileManager
     * <p>Execute when you are clicking the media in a folder!</p>
     * <p>(1) Should contain list used to storage media urls.</p>
     * <p>(2) Should contain index used to notify the position of target media url.</p>
     */
    private void doPlayVideoFromFileManager(Intent intent) {
        //It is no meanings to open a player with no contents.
        if (intent.getExtras() == null) {
            return;
        }

        //Try to get player
        PlayerReceiverListener currPlayer = PlayerAppManager.getCurrPlayer();
        //Open a new player to play media
        if (EmptyUtil.isNull(currPlayer) || !PlayerType.isVideo()) {
            App.openVideoPlayerByFileManager(mContext.get(), intent);

            //Notify exist player to play media
        } else {
            App.openVideoPlayer(mContext.get(), "", null);
            notifyPlayFromFolder(currPlayer, intent);
        }
    }

    /**
     * Do Search Media List
     */
    private void doSearchMediaList(Context context, Intent intent) {
        try {
            // Get parameters
            String strJsonParam = intent.getStringExtra("params");
            Log.i(TAG, "doSearchMediaList(context,intent)----params----");
            Log.i("callback_JSON", strJsonParam);
            JSONObject joParam = new JSONObject(strJsonParam);
            String title = joParam.optString(PlayerConsts.TITLE, "");
            String artist = joParam.optString(PlayerConsts.ARTIST, "");
            // Get Player and Start Search
            PlayerReceiverListener currPlayer = PlayerAppManager.getCurrPlayer();
            if (currPlayer == null || !PlayerType.isMusic()) {
                Intent data = new Intent();
                data.putExtra(PlayerConsts.TITLE, title);
                data.putExtra(PlayerConsts.ARTIST, artist);
                App.openMusicPlayer(context, PlayerOpenMethod.VAL_ONLINE_MEDIA, data);
            } else {
                currPlayer.onNotifySearchMediaList(title, artist);
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + ">  doSearchMediaList()", e);
        }
    }

    /**
     * Notify Play Searched Result
     * <p>
     * Bundle[{params={"artist":"张学友"}, musicList=[{"artist":"张学友","duration":0,"id"
     * :"620023","size":0,"title":"一路上有你","url":""}]}]
     */
    private void doPlaySelectedMusic(Intent intent) {
        try {
            PlayerReceiverListener currPlayer = PlayerAppManager.getCurrPlayer();
            if (currPlayer != null && PlayerType.isMusic()) {
                // Get parameters
                String strJsonParams = intent.getStringExtra("musicList");
                Log.i(TAG, "doPlaySelectedMusic(intent)----musicList----");
                Log.i("callback_JSON", strJsonParams);
                // Play Selected
                JSONArray jaParams = new JSONArray(strJsonParams);
                JSONObject joParam = jaParams.optJSONObject(0);
                ProMusic program = new ProMusic();
                program.id = joParam.optInt("id", -1);
                program.title = joParam.optString("title", "");
                program.artist = joParam.optString("artist", "");
                program.mediaUrl = joParam.optString("url", "");
                currPlayer.onNotifyPlaySearchedMusic(program);
            }
        } catch (Throwable e) {
            Logs.printStackTrace(TAG + ">  doPlaySelectedMusic()", e);
        }
    }

    private void testSendVideoList() {
        try {
            ArrayList<String> listUrls = new ArrayList<>();
            listUrls.add("/storage/emulated/0/Music/maozhuxideguanghui.mkv");
            listUrls.add("/storage/emulated/0/Music/shenghua.mkv");
            listUrls.add("/storage/emulated/0/Music/taomagan.mkv");
            listUrls.add("/storage/emulated/0/Music/huohuodeguniang.flv");
            listUrls.add("/storage/emulated/0/Music/fenshengbang-huakaihualuo.flv");
            listUrls.add("/storage/emulated/0/Music/daoguangjianying.mkv");

            Intent data = new Intent(PlayerReceiverActions.PLAY_VIDEO_BY_FILEMANAGER);
            data.putExtra(PlayerConsts.FILE_LIST, listUrls);
            data.putExtra(PlayerConsts.INDEX, 0);

            mContext.get().sendBroadcast(data);
        } catch (Exception e) {
            Logs.debugI(TAG, "testSendVideoList");
        }
    }
}
