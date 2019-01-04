package com.yj.audio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tri.lib.utils.TrAudioPreferUtils;
import com.yj.audio.App;
import com.yj.audio.engine.PlayerAppManager;

/**
 * Test Receiver
 * <p>Used to test some case.</p>
 *
 * @author Jun.Wang
 */
public class TestReceiver extends BroadcastReceiver {
    //TAG
    private static final String TAG = "TestReceiver";

    // ENUM - Test Actions
    private static final String TEST_EXIT_PLAYER = "com.tri.test.EXIT_PLAYER";
    private static final String TEST_OPEN_AUDIO = "com.tri.test.OPEN_AUDIO";
    private static final String TEST_OPEN_AUDIO_FROM_VOICE = "com.tri.test.OPEN_AUDIO_FROM_VOICE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action : " + action);
        //发送命令,强制退出播放器.
        if (TEST_EXIT_PLAYER.equals(action)) {
            PlayerAppManager.exitCurrPlayer(true);

            //解除无U盘提示,即无U盘也可以打开视频播放器.
        } else if (TEST_OPEN_AUDIO.equals(action)) {
            TrAudioPreferUtils.getNoUDiskToastFlag(true);

            //模拟语音命令
            //am broadcast -a com.tri.test.OPEN_AUDIO_FROM_VOICE --ei "type" 1/2/3/4/5
        } else if (TEST_OPEN_AUDIO_FROM_VOICE.equals(action)) {
            //包名：com.yj.audio
            //主类名：com.yj.audio.WelcomeActivity
            //Intent参数：
            //type: int型
            //1==》打开并播放歌曲==>"我要听歌"
            //2==》随机播放一首歌曲
            //3==》播放指定歌手的歌曲。歌手的名称：请看artist参数值; 歌曲路径看path参数值（如果path没值，播放器自己定义播放这个歌手的哪首歌曲）
            //4==》播放指定歌名的歌曲。“我要听华阴老腔一声喊”     歌曲路径看path参数值（必须要有path）;
            //5==》播放指定歌手指定歌名的歌曲。歌手的名称:请看artist参数值（必须要有artist）; 歌曲路径看path参数值（必须要有path）
            //artist：String型  歌手名称
            //musicName：String型	"华阴老腔一声喊"
            //path: String型	"/storage/emulated/0/Music/test/谭维维-华阴老腔一声喊.mp3"
            int type = intent.getIntExtra("type", 1);
            switch (type) {
                case 3:
                    intent.putExtra("artist", "叶振");
                    break;
                case 4:
                    intent.putExtra("musicName", "火");
                    break;
                case 5:
                    intent.putExtra("artist", "李孝利");
                    intent.putExtra("musicName", "Anyclub");
                    intent.putExtra("path", "/storage/emulated/0/Music/lixiaoli - Anyclub.mp3");
                    break;
            }
            App.openMusicPlayer(context, "", intent);
        }
    }
}
