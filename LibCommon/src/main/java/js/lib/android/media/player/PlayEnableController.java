package js.lib.android.media.player;

import android.util.Log;

/**
 * 播放使能标记
 *
 * @author Jun.Wang
 */
public class PlayEnableController {
    // TAG
    private static final String TAG = "PlayEnableFlag";

    /**
     * If paused by user?
     * <p>Usually caused by touch pause.</p>
     */
    private static boolean mIsPauseByUser = false;

    /**
     * If Bluetooth call is running?
     * <p>It is not allowed to play when Bluetooth call is running.</p>
     */
    private static boolean mIsBtCallRunning = false;

    /**
     * If device screen is off?
     * <p>Usually it is not allowed to play when screen is off.</p>
     */
    private static boolean mIsScreenOff = false;

    /**
     * ACC state flag
     * <p>1-ACC_ON.</p>
     * <p>2-ACC_OFF.</p>
     * <p>3-ACC_OFF_TRUE.</p>
     */
    private static int mAccFlag = 1;

    public static void init() {
        Log.i(TAG, "init()");
        mAccFlag = 1;
        mIsPauseByUser = false;
        mIsScreenOff = false;
        mIsBtCallRunning = false;
    }

    /**
     * 暂停 BY 用户点击了暂停
     *
     * @param isPauseByUser :用户是否点击了暂停
     */
    public static void pauseByUser(boolean isPauseByUser) {
        mIsPauseByUser = isPauseByUser;
        print();
    }

    public static boolean isPauseByUser() {
        return mIsPauseByUser;
    }

    /**
     * Acc State Changed.
     *
     * @param flag <p>1-ACC_ON.</p>
     *             <p>2-ACC_OFF.</p>
     *             <p>3-ACC_OFF_TRUE.</p>
     */
    public static void onAccStateChanged(int flag) {
        mAccFlag = flag;
        print();
    }

    /**
     * 暂停 BY 蓝牙电话进行中
     *
     * @param isBtCallRunning :蓝牙电话是否进行中
     */
    public static void onBtCallRunning(boolean isBtCallRunning) {
        mIsBtCallRunning = isBtCallRunning;
        print();
    }

    /**
     * 暂停 BY 屏熄灭
     *
     * @param isScreenOff :屏是否熄灭
     */
    public static void onScreenStateChanged(boolean isScreenOff) {
        mIsScreenOff = isScreenOff;
        print();
    }

    /**
     * 是否可以播放
     */
    public static boolean isPlayEnable() {
        boolean isPlayEnable = (mAccFlag == 1)
                || !mIsPauseByUser
                || !mIsScreenOff
                || !mIsBtCallRunning;
        Log.i(TAG, "isPlayEnable :: " + isPlayEnable);
        return isPlayEnable;
    }

    /**
     * 打印当前标记
     */
    public static void print() {
        Log.i(TAG, "  ");
        Log.i(TAG, ">--------------^--------------");
        Log.i(TAG, "> mAccFlag ~ " + mAccFlag);
        Log.i(TAG, "> mIsPauseByUser ~ " + mIsPauseByUser);
        Log.i(TAG, "> mIsScreenOff ~ " + mIsScreenOff);
        Log.i(TAG, "> mIsBtCallRunning ~ " + mIsBtCallRunning);
        Log.i(TAG, ">-----------------------------");
        Log.i(TAG, "  ");
    }
}