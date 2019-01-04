package js.lib.android.utils;

import android.util.Log;


/**
 * 日志输出共同类
 * <p>
 * 该类依赖于 PreferenceHelper 的初始化
 *
 * @author Jun.Wang
 */
public class Logs {
    /**
     * IS Log Print Enable
     */
    private static boolean mIsPrintLog = false;

    /**
     * Set Print Log Flag
     */
    public static void switchEnable(boolean isBootCompleted) {
        mIsPrintLog = PreferenceHelper.isOpenLogs(false, false);
        if (!isBootCompleted) {
            mIsPrintLog = !mIsPrintLog;
            PreferenceHelper.isOpenLogs(true, mIsPrintLog);
        }
    }

    /**
     * The same as {@link Log#i(String, String)}
     */
    public static void i(String tag, String msg) {
        if (msg != null) {
            Log.i(tag, msg);
        }
    }

    /**
     * Debug log, control by flag {@link #mIsPrintLog}
     */
    public static void debugI(String tag, String msg) {
        if (mIsPrintLog) {
            i(tag, msg);
        }
    }

    /**
     * The same as {@link Log#e(String, String)}
     */
    public static void e(String tag, String msg) {
        if (msg != null) {
            Log.e(tag, msg);
        }
    }

    /**
     * Debug log, control by flag {@link #mIsPrintLog}
     */
    public static void debugE(String tag, String msg) {
        if (mIsPrintLog) {
            e(tag, msg);
        }
    }

    /**
     * The same as {@link Log#d(String, String)}
     */
    public static void d(String tag, String msg) {
        if (msg != null) {
            Log.d(tag, msg);
        }
    }

    /**
     * Debug log, control by flag {@link #mIsPrintLog}
     */
    public static void debugD(String tag, String msg) {
        if (mIsPrintLog) {
            d(tag, msg);
        }
    }

    /**
     * The same as {@link Log#w(String, String)}
     */
    public static void w(String tag, String msg) {
        if (msg != null) {
            Log.w(tag, msg);
        }
    }

    /**
     * Debug log, control by flag {@link #mIsPrintLog}
     */
    public static void debugW(String tag, String msg) {
        if (mIsPrintLog) {
            w(tag, msg);
        }
    }

    /**
     * The same as {@link Log#v(String, String)}
     */
    public static void v(String tag, String msg) {
        if (msg != null) {
            Log.v(tag, msg);
        }
    }

    /**
     * Debug log, control by flag {@link #mIsPrintLog}
     */
    public static void debugV(String tag, String msg) {
        if (mIsPrintLog) {
            v(tag, msg);
        }
    }

    /**
     * Print {@link Throwable}
     */
    public static void printStackTrace(String tag, Throwable e) {
        if (e != null) {
            String msg = e.getMessage();
            if (msg != null) {
                i("ERROR_LOG", " ");
                i("ERROR_LOG", "--XX-->>| " + tag + " |<<--XX--");
                i("ERROR_LOG", msg);
            }
        }
    }
}
