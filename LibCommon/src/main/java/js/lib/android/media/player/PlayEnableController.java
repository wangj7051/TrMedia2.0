package js.lib.android.media.player;

import android.util.Log;

/**
 * 播放使能标记
 *
 * @author Jun.Wang
 */
public class PlayEnableController {
    // TAG
    private static final String TAG = "PlayEnableController";

    /**
     * ACC state flag
     * <p>1-ACC_ON.</p>
     * <p>2-ACC_OFF.</p>
     * <p>3-ACC_OFF_TRUE.</p>
     */
    private static int mAccFlag = 1;

    /**
     * Car reverse state flag
     * <p>It is not allowed to play when car is reversing.</p>
     */
    private static boolean mIsReverseOn = false;

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
     * If paused by user?
     * <p>Usually caused by touch pause.</p>
     */
    private static boolean mIsPauseByUser = false;

//    public static void init() {
//        Log.i(TAG, "init()");
//        mAccFlag = 1;
//        mIsReverseOn = false;
//        mIsBtCallRunning = false;
//        mIsPauseByUser = false;
//        mIsScreenOff = false;
//    }

    /**
     * Acc State Changed.
     *
     * @param flag <p> 1 -ACC_ON.</p>
     *             <p> 0 -ACC_OFF.</p>
     *             <p>-1 -ACC_OFF_TRUE.</p>
     */
    public static void onAccStateChanged(int flag) {
        mAccFlag = flag;
    }

    /**
     * 暂停 BY 倒车进行中
     *
     * @param isReverseOn 是否倒车进行中
     */
    public static void onReverseStateChanged(boolean isReverseOn) {
        mIsReverseOn = isReverseOn;
    }

    /**
     * 暂停 BY 蓝牙电话进行中
     *
     * @param isBtCallRunning :蓝牙电话是否进行中
     */
    public static void onBtCallStateChanged(boolean isBtCallRunning) {
        mIsBtCallRunning = isBtCallRunning;
    }

    /**
     * 暂停 BY 屏熄灭
     *
     * @param isScreenOff :屏是否熄灭
     */
    public static void onScreenStateChanged(boolean isScreenOff) {
        mIsScreenOff = isScreenOff;
    }

    /**
     * 暂停 BY 用户点击了暂停
     *
     * @param isPauseByUser :用户是否点击了暂停
     */
    public static void pauseByUser(boolean isPauseByUser) {
        mIsPauseByUser = isPauseByUser;
    }

    public static boolean isPauseByUser() {
        return mIsPauseByUser;
    }

    /**
     * 是否可以播放
     * <p>1. ACC_FLAG = 1</p>
     * <p>2. mIsReverseOn == false</p>
     * <p>3. mIsBtCallRunning == false</p>
     * <p>4. mIsPauseByUser == false</p>
     */
    public static boolean isPlayEnable() {
        boolean isPlayEnable = (mAccFlag == 1)
                && !mIsReverseOn
                && !mIsBtCallRunning
                && !mIsPauseByUser;
        Log.i(TAG, "isPlayEnable() - isPlayEnable :: " + isPlayEnable);
        return isPlayEnable;
    }

    /**
     * 是否可以播放
     * <p>1. mIsReverseOn == false</p>
     * <p>2. mIsBtCallRunning == false</p>
     */
    public static boolean isSysAllowPlay() {
        boolean isPlayEnable = !mIsBtCallRunning;
        Log.i(TAG, "isSysAllowPlay() - isPlayEnable :: " + isPlayEnable);
        return isPlayEnable;
    }

    /**
     * Play enable state
     */
    public static String getStateDesc() {
        return "  " +
                ">--------------^--------------" +
                "> mAccFlag ~ " + mAccFlag +
                "> mIsReverseOn ~ " + mIsReverseOn +
                "> mIsBtCallRunning ~ " + mIsBtCallRunning +
                "> mIsPauseByUser ~ " + mIsPauseByUser +
                ">-----------------------------";
    }
}