package com.tricheer.player.version.base.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.Toast;

import com.lib.view.OperateDialog;
import com.lib.view.RequestProcessDialog;
import com.lib.view.contacts.CharacterParser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tricheer.player.R;
import com.tricheer.player.utils.PlayerFileUtils;
import com.tricheer.player.utils.PlayerLogicUtils;

import js.lib.android.utils.AudioManagerUtil;
import js.lib.android.utils.CommonUtil;
import js.lib.android.utils.ImageLoaderUtils;
import js.lib.android.utils.Logs;

/**
 * Base FragmentActivity
 *
 * @author Jun.Wang
 */
public abstract class BaseFragActivity extends FragmentActivity {
    // TAG
    private final String TAG = "BaseFragActivity";

    /**
     * ==========Variable in this Activity==========
     */
    /**
     * Context
     */
    protected Context mContext;

    /**
     * Thread handler
     */
    protected Handler mHandler = new Handler();
    /**
     * Handler Delay Time, 300MS
     */
    protected final int M_DEFAULT_DELAY_TIME = 300;

    /**
     * Activity UI Load End Flag
     */
    private boolean mIsActUILoadEnd = false;

    /**
     * 图片加载器
     */
    private ImageLoader mImageLoader;

    /**
     * Chinese to Spelling Covert Class
     */
    protected CharacterParser mCharacterParser;

    /**
     * Request Processing Dialog
     */
    protected RequestProcessDialog mProcessDialog;

    /**
     * Operate Dialog
     */
    protected OperateDialog mOperateDialog;

    /**
     * Is Click Home Key , And Application is Running Background
     */
    private boolean mIsHomeClicked = false;
    /**
     * Home Key Receiver
     */
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logs.i(TAG, "mHomeKeyEventReceiver -> [action : " + action);
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    Logs.i(TAG, "----Home Key Click----");
                    mIsHomeClicked = true;
                    onHomeKeyClick();
                } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
                    Logs.i(TAG, "----Home Key LONG Click----");
                    onHomeKeyLongClick();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        init();
    }

    private void init() {
        this.mContext = this;
        this.mCharacterParser = CharacterParser.getInstance();
        this.mImageLoader = ImageLoaderUtils.getImageLoader(PlayerFileUtils.getMusicPicPath(""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsHomeClicked = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!mIsActUILoadEnd && hasFocus) {
            mIsActUILoadEnd = true;
            onUILoadEnd();
        }
    }

    /**
     * On UI Load END
     */
    protected void onUILoadEnd() {
        Logs.i(TAG, "onUILoadEnd() -> \n****" + this.toString() + "****");
    }

    /**
     * Is UiLoaded
     */
    protected boolean isUILoaded() {
        return mIsActUILoadEnd;
    }

    /**
     * Is Activity at background on Home Clicked
     */
    protected boolean isHomeClicked() {
        return mIsHomeClicked;
    }

    /**
     * Get ImageLoader
     */
    public ImageLoader getImageLoader() {
        if (mImageLoader == null || !mImageLoader.isInited()) {
            mImageLoader = ImageLoaderUtils.getImageLoader(PlayerFileUtils.getMusicPicPath(""));
        }
        return mImageLoader;
    }

    /**
     * 是否正在通话中
     *
     * @param isToast : 是否提示通话...
     */
    public boolean isBtCalling(boolean isToast) {
        if (isBtCalling()) {
            if (isToast) {
                Toast.makeText(mContext, R.string.dialing_toast, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    /**
     * 蓝牙通话是否正在进行中
     */
    public boolean isBtCalling() {
        return PlayerLogicUtils.isBtCalling(mContext);
    }

    /**
     * Show or Hide Progress Dialog
     */
    public void showProgress(boolean isShow, int msgResID) {
        showProgress(isShow, mContext.getString(msgResID));
    }

    /**
     * Show or Hide Progress Dialog
     */
    public void showProgress(boolean isShow, String msg) {
        if (mProcessDialog == null) {
            mProcessDialog = new RequestProcessDialog(mContext, getColorByResID(android.R.color.white));
            mProcessDialog.setCancelable(false);
            mProcessDialog.setCanceledOnTouchOutside(false);
        }

        //
        if (isShow) {
            if (!mProcessDialog.isShowing()) {
                mProcessDialog.setMsgInfo(msg);
                mProcessDialog.show();
            }

            //
        } else {
            if (mProcessDialog.isShowing()) {
                mProcessDialog.dismiss();
            }
        }
    }

    /**
     * Get Color
     */
    protected int getColorByResID(int colorResID) {
        return getResources().getColor(colorResID);
    }

    /**
     * Hide SoftKeyBoard
     */
    protected void hideSoftKeyBoard(boolean isNeedWait) {
        if (isNeedWait) {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    hideSoftKeyBoard(false);
                }
            }, 100);
        } else {
            CommonUtil.hideSoftKeyBoard(mContext, getCurrentFocus());
        }
    }

    /**
     * Register "HOME" key BroadReceiver
     */
    protected void registerHomeKeyReciver(boolean isReg) {
        try {
            if (isReg) {
                IntentFilter ifHomeKey = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                registerReceiver(mHomeKeyEventReceiver, ifHomeKey);
            } else {
                unregisterReceiver(mHomeKeyEventReceiver);
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "registerHomeKeyReciver()", e);
        }
    }

    /**
     * Home Key Click Event
     */
    protected void onHomeKeyClick() {
    }

    /**
     * Home Key Long Click Event
     */
    protected void onHomeKeyLongClick() {
    }

    /**
     * Trim Memory
     * <p>
     * ----Android 4.0 Add----
     * <p>
     * (1)TRIM_MEMORY_UI_HIDDEN：表示应用程序的 所有UI界面 被隐藏了，即用户点击了Home键或者Back键导致应用的UI界面不可见．这时候应该释放一些资源
     * <p>
     * (2)TRIM_MEMORY_BACKGROUND : 表示手机目前内存已经很低了，系统准备开始根据LRU缓存来清理进程。这个时候我们的程序在LRU缓存列表的最近位置
     * ，是不太可能被清理掉的，但这时去释放掉一些比较容易恢复的资源能够让手机的内存变得比较充足
     * ，从而让我们的程序更长时间地保留在缓存当中，这样当用户返回我们的程序时会感觉非常顺畅，而不是经历了一次重新启动的过程。
     * <p>
     * (3) TRIM_MEMORY_MODERATE :
     * 表示手机目前内存已经很低了，并且我们的程序处于LRU缓存列表的中间位置，如果手机内存还得不到进一步释放的话，那么我们的程序就有被系统杀掉的风险了。
     * <p>
     * (4) TRIM_MEMORY_COMPLETE 表示手机目前内存已经很低了，并且我们的程序处于LRU缓存列表的最边缘位置，系统会最优先考虑杀掉我们的应用程序
     * ，在这个时候应当尽可能地把一切可以释放的东西都进行释放。
     * <p>
     * ----Android 4.1 Add----
     * <p>
     * (5) TRIM_MEMORY_RUNNING_MODERATE :
     * 表示应用程序正常运行，并且不会被杀掉。但是目前手机的内存已经有点低了，系统可能会开始根据LRU缓存规则来去杀死进程了。
     * <p>
     * (6)TRIM_MEMORY_RUNNING_LOW : 表示应用程序正常运行，并且不会被杀掉。但是目前手机的内存已经非常低了，我们应该去释放掉一些不必要的资源以提升系统的性能
     * ，同时这也会直接影响到我们应用程序的性能。
     * <p>
     * (7)TRIM_MEMORY_RUNNING_CRITICAL 表示应用程序仍然正常运行，但是系统已经根据LRU缓存规则杀掉了大部分缓存的进程了
     * 。这个时候我们应当尽可能地去释放任何不必要的资源，不然的话系统可能会继续杀掉所有缓存中的进程 ，并且开始杀掉一些本来应当保持运行的进程，比如说后台运行的服务。
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    /**
     * Must Be used in onBackPressed()
     */
    protected void onIBackPressed() {
    }

    /**
     * Must Be used in onDestroy()
     */
    protected void onIDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        showProgress(false, "");
    }

    /**
     * Replace Layout with Fragment
     */
    protected void replaceFragContainer(FragmentManager fragManager, int resID, Fragment frag) {
        FragmentTransaction fragTrasAct = fragManager.beginTransaction();
        fragTrasAct.replace(resID, frag);
        fragTrasAct.commit();
    }

    /**
     * (1)强迫退出其他媒体，这里采用了在列表中注册焦点的方式，强制其他注册了焦点时间的媒体退出<br/>
     * (2)视频列表不需要进行声音焦点控制，所以在短暂的获取焦点后，要释放，否则会造成与视频播放器的争抢
     */
    protected void forceQuitOthers() {
        AudioManagerUtil.requestMusicGain(mContext, null);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                AudioManagerUtil.abandon(mContext, null);
            }
        }, 50);
    }

    @SuppressWarnings("unchecked")
    public <T> T findView(int vResID) {
        return (T) findViewById(vResID);
    }
}
