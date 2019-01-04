package com.yj.audio.version.base.activity.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.tri.lib.receiver.ActionEnum;
import com.yj.audio.engine.ThemeController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.EmptyUtil;

public abstract class BaseUIActivity extends BaseExtendActionsActivity
        implements ThemeController.ThemeChangeDelegate {
    //TAG
    private static final String TAG = "audio_base_ui";

    private Context mContext;
    private Handler mHandler = new Handler();

    /**
     * {@link js.lib.android.media.player.audio.service.AudioPlayService} object
     */
    private Service mAudioService;

    //Controllers
    private VoiceCmdFromBroadcastController mVoiceCmdFromBroadcastController;
    private VoiceCmdFromIntentController mVoiceCmdFromIntentController;
    private ThemeController mThemeController;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //Initialize
        mContext = this;
        mThemeController = new ThemeController(this, mHandler);
        mThemeController.addCallback(this);

        //Voice command from intent
        mVoiceCmdFromIntentController = new VoiceCmdFromIntentController();
        //Voice command from broadcast
        mVoiceCmdFromBroadcastController = new VoiceCmdFromBroadcastController();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //
        mThemeController.onResume();
        mVoiceCmdFromIntentController.parseCmd(getIntent());
    }

    @Override
    protected void onAudioServiceConnChanged(Service service) {
        super.onAudioServiceConnChanged(service);
        Log.i(TAG, "onAudioServiceConnChanged(" + service + ")");
        mAudioService = service;
    }

    protected boolean isAudioServiceConned() {
        return (mAudioService != null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void clearActivity() {
        if (mThemeController != null) {
            mThemeController.onDestroy();
            mThemeController = null;
        }
        if (mVoiceCmdFromIntentController != null) {
            mVoiceCmdFromIntentController.destroy();
            mVoiceCmdFromIntentController = null;
        }
        if (mVoiceCmdFromBroadcastController != null) {
            mVoiceCmdFromBroadcastController.destroy();
            mVoiceCmdFromBroadcastController = null;
        }
    }

    /**
     * 根据资源文件名称，获取资源文件ID
     *
     * @param imgResName 如"bg_title"
     * @return '0' means 'no resource'; '>0' means 'Resource ID of this Image'.
     */
    public int getImgResId(String imgResName) {
        if (mThemeController != null) {
            return mThemeController.getImgResId(imgResName);
        }
        return 0;
    }

    /**
     * Update {@link ImageView} resource.
     *
     * @param iv      Target {@link ImageView}
     * @param resName ImageView resource name, such as 'bg_title'
     */
    protected void updateImgRes(ImageView iv, String resName) {
        iv.setTag(resName);
        iv.setImageResource(getImgResId(resName));
    }

    /**
     * Processing voice assistant command.
     */
    protected boolean processingVoiceCmdFromIntent() {
        if (isAudioServiceConned()
                && mVoiceCmdFromIntentController != null
                && mVoiceCmdFromIntentController.hasCmd()) {
            mVoiceCmdFromIntentController.processCmd();
            return true;
        }
        return false;
    }

    /**
     * Process voice command from intent.
     */
    private final class VoiceCmdFromIntentController {
        // 1==》打开并播放歌曲==>"我要听歌"
        // 2==》随机播放一首歌曲
        // 3==》播放指定歌手的歌曲。歌手的名称：请看artist参数值; 歌曲路径看path参数值（如果path没值，播放器自己定义播放这个歌手的哪首歌曲）
        // 4==》播放指定歌名的歌曲。“我要听华阴老腔一声喊”     歌曲路径看path参数值（必须要有path）;
        // 5==》播放指定歌手指定歌名的歌曲。歌手的名称:请看artist参数值（必须要有artist）; 歌曲路径看path参数值（必须要有path）
        final String KEY_TYPE = "type";
        // 歌曲名 / 歌手 / 歌曲路径
        final String KEY_TITLE = "musicName", KEY_ARTIST = "artist", KEY_PATH = "path";

        // Variable - Voice command type.
        int mmType = -1;
        // Variable - Music title/artist/path
        String mmTitle = "", mmArtist = "", mmPath = "";

        /**
         *
         */
        private Thread mmParseAndPlayThread;

        void parseCmd(Intent data) {
            try {
                mmType = data.getIntExtra(KEY_TYPE, -1);
                if (mmType > 0) {
                    mmTitle = data.getStringExtra(KEY_TITLE);
                    mmArtist = data.getStringExtra(KEY_ARTIST);
                    mmPath = data.getStringExtra(KEY_PATH);
                    Log.i(TAG, "VoiceCmdController -onResume(Intent data)-"
                            + "\nmmType: |" + mmType + "|"
                            + "\nmmTitle: |" + mmTitle + "|"
                            + "\nmmArtist: |" + mmArtist + "|"
                            + "\nmmPath: |" + mmPath + "|");
                }

                //Clear all parameters to ensure that they are only used once.
                data.removeExtra(KEY_TYPE);
                data.removeExtra(KEY_TITLE);
                data.removeExtra(KEY_ARTIST);
                data.removeExtra(KEY_PATH);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        boolean hasCmd() {
            return mmType != -1;
        }

        void processCmd() {
            Log.i(TAG, "VoiceCmdFromIntentController -processCmd()-");
            switch (mmType) {
                //1==》打开并播放歌曲==>"我要听歌"
                //(1) OnResume - 如果播放服务已经连接，则执行恢复；
                //    否则，则表示服务还没有连接，只要执行正常的打开并自动播放流程即可了。
                case 1:
                    if (isAudioServiceConned()) {
                        //将所有媒体源纳入到播放列表
                        setPlayList(getListSrcMedias());
                        if (!isPlaying()) {
                            execResumeByUser();
                        }
                    }
                    mmType = -1;
                    break;
                //2==》随机播放一首歌曲
                //(1) 在服务连接的情况下，随机挑选一首歌进行播放。
                case 2:
                    if (isAudioServiceConned()) {
                        //将所有媒体源纳入到播放列表
                        setPlayList(getListSrcMedias());
                        execPlayRandomByUser();
                        mmType = -1;
                    }
                    break;
                //3==》播放指定歌手的歌曲。歌手的名称：请看artist参数值; 歌曲路径看path参数值（如果path没值，播放器自己定义播放这个歌手的哪首歌曲）
                case 3:
                    playFromCmd();
                    break;
                //4==》播放指定歌名的歌曲。“我要听华阴老腔一声喊”     歌曲路径看path参数值（必须要有path）;
                case 4:
                    playFromCmd();
                    break;
                //5==》播放指定歌手指定歌名的歌曲。歌手的名称:请看artist参数值（必须要有artist）; 歌曲路径看path参数值（必须要有path）
                case 5:
                    playFromCmd();
                    break;
                default:
                    break;
            }
        }

        /**
         * <p>
         * 4==》播放指定歌名的歌曲。“我要听华阴老腔一声喊”     歌曲路径看path参数值（必须要有path）;
         * </p>
         * <p>
         * 3==》播放指定歌手的歌曲。歌手的名称：请看artist参数值; 歌曲路径看path参数值（如果path没值，播放器自己定义播放这个歌手的哪首歌曲）
         * </p>
         */
        private void playFromCmd() {
            Log.i(TAG, "VoiceCmdFromIntentController -playFromCmd() - "
                    + "\n[mmType:" + mmType + "]"
                    + "\n[mmTitle:" + mmTitle + "]"
                    + "\n[mmArtist:" + mmArtist + "]"
                    + "\n[mmPath:" + mmPath + "]");

            //检测服务是否已连接
            if (isAudioServiceConned()) {
                //Play the music driver selected according music path.
                if (mmType == 4) {
                    if (EmptyUtil.isEmpty(mmPath)) {
                        Log.i(TAG, "<..> Bad news , your file is empty -1- <..>");
                    } else {
                        File audioFile = new File(mmPath);
                        if (audioFile.exists() && audioFile.isFile()) {
                            ProAudio media = AudioDBManager.instance().getMedia(mmPath);
                            //文件不在列表中
                            if (media == null) {
                                Log.i(TAG, "<^-^> Play after parsing. -1- !!! <^-^>");
                                execParseAndPlay();
                                //文件在列表中
                            } else {
                                Log.i(TAG, "<^-^> Play selected !!! <^-^>");
                                setPlayList(getListSrcMedias());
                                execPlay(mmPath);
                            }
                        } else {
                            Log.i(TAG, "<..> Sorry. The music you selected is not exist -1- <..>");
                        }
                    }

                    //Play the music according to title and artist.
                } else {
                    List<ProAudio> targetAudios = AudioDBManager.instance().getListMusics(mmTitle, mmArtist);
                    //列表中查询不到指定[歌名/歌手]的歌曲
                    if (EmptyUtil.isEmpty(targetAudios)) {
                        Log.i(TAG, "====---- Query Failed ---====");
                        if (EmptyUtil.isEmpty(mmPath)) {
                            Log.i(TAG, "<..> Bad news , your file is empty -2- <..>");
                        } else {
                            File file = new File(mmPath);
                            if (file.exists() && file.isFile()) {
                                Log.i(TAG, "<^-^> Play after parsing. -2- !!! <^-^>");
                                execParseAndPlay();
                            } else {
                                Log.i(TAG, "<..> Sorry. The music you selected is not exist -2- <..>");
                            }
                        }
                    } else {
                        Log.i(TAG, "====---- Query successfully <^-^> ---====");
                        //刷新播放列表
                        setPlayList(getListSrcMedias());

                        //No path
                        if (TextUtils.isEmpty(mmPath)) {
                            try {
                                //获取 当前播放的歌曲 在 查询到的列表 中的位置
                                String currMediaPath = getCurrMediaPath();
                                int sizeOfNewList = targetAudios.size(), posInNewList = -1;
                                for (int idx = 0; idx < sizeOfNewList; idx++) {
                                    ProAudio tmp = targetAudios.get(idx);
                                    if (TextUtils.equals(tmp.mediaUrl, currMediaPath)) {
                                        posInNewList = idx;
                                        break;
                                    }
                                }

                                //获取一个与当前播放位置不同的播放位置,这是为了每次获取的歌曲有变化
                                int randomPos = CommonUtil.getRandomNum(posInNewList, sizeOfNewList);
                                String randomPath = targetAudios.get(randomPos).mediaUrl;
                                Log.i(TAG, "will play -1- |" + randomPath + "|");
                                execPlay(randomPath);
                            } catch (Exception e) {
                                Log.i(TAG, "e:" + e.getMessage());
                                e.printStackTrace();
                            }

                            //Already playing
                        } else if (TextUtils.equals(getCurrMediaPath(), mmPath)) {
                            Log.i(TAG, "The song you chose is already playing !!! -1- ");

                            //Play according by path.
                        } else {
                            // 获取当前正在播放的媒体
                            try {
                                ProAudio mediaFromVoiceAssistant = AudioDBManager.instance().getMedia(mmPath);
                                Log.i(TAG, "mediaFromVoiceAssistant - " + mediaFromVoiceAssistant + "|");
                                ProAudio currMedia = (ProAudio) getCurrMedia();
                                Log.i(TAG, "currMedia - " + currMedia + "|");
                                if (TextUtils.equals(mediaFromVoiceAssistant.artist, currMedia.artist)
                                        && TextUtils.equals(mediaFromVoiceAssistant.title, currMedia.title)) {
                                    Log.i(TAG, "The song you chose is already playing !!! -2- ");
                                } else {
                                    Log.i(TAG, "will play -2- |" + mmPath + "|");
                                    execPlay(mmPath);
                                }
                            } catch (Exception e) {
                                Log.i(TAG, "e:" + e.getMessage() + "\nwill play -3- |" + mmPath + "|");
                                execPlay(mmPath);
                            }
                        }
                    }
                }
            }

            mmType = -1;
        }

        /**
         * Execute parse and play select media.
         */
        private void execParseAndPlay() {
            Log.i(TAG, "execParseAndPlay()");
            if (mmParseAndPlayThread != null) {
                mmParseAndPlayThread = null;
            }
            mmParseAndPlayThread = new ParseAndPlayThread(mmPath);
            mmParseAndPlayThread.start();
        }

        class ParseAndPlayThread extends Thread {
            String mmmPath;

            ParseAndPlayThread(String path) {
                mmmPath = path;
            }

            @Override
            public void run() {
                super.run();
                //Parse and add to list.
                ProAudio media = new ProAudio(mmPath);
                ProAudio.parseMedia(mContext, media);
                final List<ProAudio> mediaList = new ArrayList<>();
                mediaList.add(media);
                //Play
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setPlayList(mediaList);
                        execPlay(0);
                    }
                });
            }
        }

        void destroy() {
            Log.i(TAG, "VoiceCmdController -onDestroy()-");
            mmType = -1;
            mmTitle = "";
            mmArtist = "";
            mmPath = "";
        }
    }

    /**
     * Parse voice command from broadcast
     */
    protected void parseVoiceCmdFromBroadcast(ActionEnum ae) {
        if (mVoiceCmdFromBroadcastController != null) {
            mVoiceCmdFromBroadcastController.parseCmd(ae);
        }
    }

    /**
     * Processing voice command from broadcast
     */
    protected boolean processingVoiceCmdFromBroadcast() {
        if (mVoiceCmdFromBroadcastController != null
                && mVoiceCmdFromBroadcastController.hasCmd()) {
            mVoiceCmdFromBroadcastController.processCmd();
            return true;
        }
        return false;
    }

    /**
     * Voice command controller.
     */
    private final class VoiceCmdFromBroadcastController {
        /**
         * Voice command cache
         */
        private ActionEnum mmVoiceCommand;

        void parseCmd(ActionEnum ae) {
            Log.i(TAG, "VoiceCmdFromBroadcastController -parseCmd(" + ae + ")-");
            mmVoiceCommand = ae;
        }

        boolean hasCmd() {
            return (mmVoiceCommand != null);
        }

        void processCmd() {
            Log.i(TAG, "VoiceCmdFromBroadcastController -processCmd()- mmVoiceCommand:" + mmVoiceCommand);
            if (mmVoiceCommand != null) {
                switch (mmVoiceCommand) {
                    case MEDIA_PLAY_PREV:
                        execPlayPrevByUser();
                        break;
                    case MEDIA_PLAY_NEXT:
                        execPlayNextByUser();
                        break;
                    case MEDIA_PLAY:
                    case MEDIA_AUDIO_OPEN_AND_PLAY:
                        execResumeByUser();
                        break;
                    case MEDIA_PAUSE:
                        execPauseByUser();
                        break;
                }
                mmVoiceCommand = null;
            }
        }

        void destroy() {
            Log.i(TAG, "VoiceCmdFromBroadcastController -destroy()-");
            mmVoiceCommand = null;
        }
    }
}
