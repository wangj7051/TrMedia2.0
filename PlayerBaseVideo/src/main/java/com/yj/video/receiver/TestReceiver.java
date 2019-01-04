package com.yj.video.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tri.lib.receiver.ActionEnum;
import com.tri.lib.utils.TrVideoPreferUtils;
import com.yj.video.engine.PlayerAppManager;
import com.yj.video.engine.PlayerConsts;

import java.util.ArrayList;

import js.lib.android.utils.Logs;

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
    private static final String TEST_OPEN_VIDEO = "com.tri.test.OPEN_VIDEO";
    private static final String TEST_OPEN_SELECTED_VIDEOS = "com.tri.test.OPEN_SELECTED_VIDEOS";
    private static final String TEST_SEND_GPS = "com.tri.test.SEND_GPS";

    /**
     * Gps test Handler
     */
    private static TestSendGpsListener mTestSendGpsListener;

    public interface TestSendGpsListener {
        void testSendGps(int flag);
    }

    public static void setTestSendGpsListener(TestSendGpsListener l) {
        mTestSendGpsListener = l;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action : " + action);
        //发送命令,强制退出播放器.
        if (TEST_EXIT_PLAYER.equals(action)) {
            PlayerAppManager.exitCurrPlayer();

            //解除无U盘提示,即无U盘也可以打开视频播放器.
        } else if (TEST_OPEN_VIDEO.equals(action)) {
            TrVideoPreferUtils.getNoUDiskToastFlag(true);

            //模拟打开视频列表
        } else if (TEST_OPEN_SELECTED_VIDEOS.equals(action)) {
            testSendVideoList(context);

            //模拟发送GPS数据
        } else if (TEST_SEND_GPS.equals(action)) {
            //1 开始模拟发送
            //0 取消模拟发送
            int flag = intent.getIntExtra("flag", 0);
            Log.i(TAG, "flag : " + flag);
            if (mTestSendGpsListener != null) {
                mTestSendGpsListener.testSendGps(flag);
            }
        }
    }

    private void testSendVideoList(Context context) {
        try {
            ArrayList<String> listUrls = new ArrayList<>();
            listUrls.add("/storage/emulated/0/Music/maozhuxideguanghui.mkv");
            listUrls.add("/storage/emulated/0/Music/shenghua.mkv");
            listUrls.add("/storage/emulated/0/Music/taomagan.mkv");
            listUrls.add("/storage/emulated/0/Music/huohuodeguniang.flv");
            listUrls.add("/storage/emulated/0/Music/fenshengbang-huakaihualuo.flv");
            listUrls.add("/storage/emulated/0/Music/daoguangjianying.mkv");

            //Send broadcast
            Intent data = new Intent(ActionEnum.PLAY_VIDEO_BY_FILEMANAGER.getAction());
            data.putExtra(PlayerConsts.FILE_LIST, listUrls);
            data.putExtra(PlayerConsts.INDEX, 0);
            context.sendBroadcast(data);
        } catch (Exception e) {
            Logs.debugI(TAG, "testSendVideoList");
        }
    }
}
