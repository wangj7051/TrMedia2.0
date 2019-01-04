package js.lib.android.utils;

import android.content.Context;
import android.util.Log;

import java.lang.Thread.UncaughtExceptionHandler;

public class AppCrashHandler implements Thread.UncaughtExceptionHandler {
    // TAG
    private static final String TAG = "AppCrashHandler";

    private Context mContext;
    private UncaughtExceptionHandler mDefaultHandler;

    private AppCrashHandler() {
    }

    private static class InstanceHolder {
        public static final AppCrashHandler INSTANCE = new AppCrashHandler();
    }

    public static AppCrashHandler instance() {
        return InstanceHolder.INSTANCE;
    }

    public void init(Context cxt) {
        this.mContext = cxt;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.i(TAG, "uncaughtException(Thread,Throwable)");
        ex.printStackTrace();
        if (!handleException(ex) && mDefaultHandler != null) {
            Log.i(TAG, "...PROCESS By system....");
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            Log.i(TAG, "...KILL MySelf...");
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        // if (ex == null) {
        // return true;
        // }
        // final String msg = ex.getLocalizedMessage();
        // TODO 收集设备信息
        // 保存错误报告文件
        return true;
    }
}
