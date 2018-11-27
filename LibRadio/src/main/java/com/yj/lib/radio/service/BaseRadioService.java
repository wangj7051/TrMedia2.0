package com.yj.lib.radio.service;

import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.yj.lib.radio.engine.BandCategoryEnum;
import com.yj.lib.radio.engine.FmDelegate;
import com.yj.lib.radio.engine.FmDelegate.FmListener;
import com.yj.lib.radio.engine.FmUtilV2;
import com.yj.lib.radio.utils.RadioPreferUtils;

import java.util.HashSet;
import java.util.Set;

import js.lib.android.media.player.audio.service.BaseAudioFocusService;

/**
 * Base Radio operate service
 * <p>
 * 1. Control Radio Play
 * </p>
 *
 * @author Jun.Wang
 */
public abstract class BaseRadioService extends BaseAudioFocusService implements FmDelegate, FmListener {
    //TAG
    private final String TAG = "BaseRadioService";

    //
    private static Handler mHandler;
    private Set<FmListener> mSetFmListeners;
    private FmUtilV2 mFmUtil;

    /**
     * Get Service Object
     */
    public class LocalBinder extends Binder {
        public BaseRadioService getService() {
            return BaseRadioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setAudioFocusListener(this);
        init();
    }

    private void init() {
        RadioPreferUtils.init(getApplicationContext());
        mHandler = new Handler();
        mSetFmListeners = new HashSet<>();
        controlFm(true);
    }

    private void controlFm(boolean isControl) {
        if (isControl) {
            mFmUtil = new FmUtilV2(this);
            mFmUtil.register(this);
        } else if (mFmUtil != null) {
            mFmUtil.unregister(null);
            mFmUtil = null;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAudioFocusDuck() {
        Log.i(TAG, "onAudioFocusDuck()");
    }

    @Override
    public void onAudioFocusTransient() {
        Log.i(TAG, "onAudioFocusTransient()");
        closeFm();
    }

    @Override
    public void onAudioFocusGain() {
        Log.i(TAG, "onAudioFocusGain()");
    }

    @Override
    public void onAudioFocusLoss() {
        Log.i(TAG, "onAudioFocusLoss()");
        closeFm();
    }

    @Override
    public void onAudioFocus(int flag) {
    }

    @Override
    public void onFreqChanged(final int freq, final BandCategoryEnum band) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (FmListener l : mSetFmListeners) {
                    l.onFreqChanged(freq, band);
                }
            }
        });
    }

    @Override
    public void onSearchAvailableFreq(int currentSeachfreq, int count, int[] freqs, BandCategoryEnum band) {
        for (FmListener l : mSetFmListeners) {
            l.onSearchAvailableFreq(currentSeachfreq, count, freqs, band);
        }
    }

    @Override
    public void onStChange(boolean show) {
        for (FmListener l : mSetFmListeners) {
            l.onStChange(show);
        }
    }

    @Override
    public void onSearchFreqStart(final BandCategoryEnum band) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (FmListener l : mSetFmListeners) {
                    l.onSearchFreqStart(band);
                }
            }
        });
    }

    @Override
    public void onSearchFreqEnd(final BandCategoryEnum band) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (FmListener l : mSetFmListeners) {
                    l.onSearchFreqEnd(band);
                }
            }
        });
    }


    @Override
    public void onSearchFreqFail(final BandCategoryEnum band, final int reason) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (FmListener l : mSetFmListeners) {
                    l.onSearchFreqFail(band, reason);
                }
            }
        });
    }

    // 预览
    @Override
    public void onScanFreqStart(BandCategoryEnum band) {
        for (FmListener l : mSetFmListeners) {
            l.onScanFreqStart(band);
        }
    }

    @Override
    public void onScanFreqEnd(BandCategoryEnum band) {
        for (FmListener l : mSetFmListeners) {
            l.onScanFreqEnd(band);
        }
    }

    @Override
    public void onScanFreqFail(BandCategoryEnum band, int reason) {
        for (FmListener l : mSetFmListeners) {
            l.onScanFreqFail(band, reason);
        }
    }

    @Override
    public void onScanStrongFreqLeftStart(BandCategoryEnum band) {
        for (FmListener l : mSetFmListeners) {
            l.onScanStrongFreqLeftStart(band);
        }
    }

    @Override
    public void onScanStrongFreqLeftEnd(BandCategoryEnum band) {
        for (FmListener l : mSetFmListeners) {
            l.onScanStrongFreqLeftEnd(band);
        }
    }

    @Override
    public void onScanStrongFreqLeftFail(BandCategoryEnum band, int reason) {
        for (FmListener l : mSetFmListeners) {
            l.onScanStrongFreqLeftFail(band, reason);
        }
    }

    @Override
    public void onScanStrongFreqRightStart(BandCategoryEnum band) {
        for (FmListener l : mSetFmListeners) {
            l.onScanStrongFreqRightStart(band);
        }
    }

    @Override
    public void onScanStrongFreqRightEnd(BandCategoryEnum band) {
        for (FmListener l : mSetFmListeners) {
            l.onScanStrongFreqRightEnd(band);
        }
    }

    @Override
    public void onScanStrongFreqRightFail(BandCategoryEnum band, int reason) {
        for (FmListener l : mSetFmListeners) {
            l.onScanStrongFreqRightFail(band, reason);
        }
    }

    @Override
    public void register(FmListener l) {
        if (l != null) {
            setAudioFocusListener(l);
            mSetFmListeners.add(l);
        }
    }

    @Override
    public void unregister(FmListener l) {
        if (l != null) {
            removeAudioFocusListener(l);
            mSetFmListeners.remove(l);
        }
    }

    @Override
    public boolean isRadioOpened() {
        try {
            return mFmUtil.isRadioOpened();
        } catch (Exception e) {
            Log.i(TAG, "isRadioOpened() Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean openFm() {
        boolean isOpened = false;
        try {
            if (!isRadioOpened()) {
                mFmUtil.openFm();
            }

            isOpened = isRadioOpened();
            if (isOpened) {
                registerAudioFocus(1);
            }
        } catch (Exception e) {
            Log.i(TAG, "openFm() Exception **** ");
            e.printStackTrace();
        }
        return isOpened;
    }

    @Override
    public boolean closeFm() {
        try {
            return mFmUtil.closeFm();
        } catch (Exception e) {
            Log.i(TAG, "closeFm() Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean searchAll() {
        try {
            return mFmUtil.searchAll();
        } catch (Exception e) {
            Log.i(TAG, "searchAll() Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int[] getAllAvailableFreqs() {
        try {
            return mFmUtil.getAllAvailableFreqs();
        } catch (Exception e) {
            Log.i(TAG, "getAllAvailableFreqs() Exception **** ");
            e.printStackTrace();
            return new int[]{};
        }
    }

    @Override
    public boolean preview() {
        try {
            return mFmUtil.preview();
        } catch (Exception e) {
            Log.i(TAG, "preview() Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setBand(BandCategoryEnum band) {
        try {
            mFmUtil.setBand(band);
        } catch (Exception e) {
            Log.i(TAG, "preview() Exception **** ");
            e.printStackTrace();
        }
    }

    @Override
    public BandCategoryEnum getCurrBand() {
        try {
            return mFmUtil.getCurrBand();
        } catch (Exception e) {
            Log.i(TAG, "getCurrBand() Exception **** ");
            e.printStackTrace();
            return BandCategoryEnum.FM;
        }
    }

    @Override
    public int getMinFreq() {
        try {
            return mFmUtil.getMinFreq();
        } catch (Exception e) {
            Log.i(TAG, "getMinFreq() Exception **** ");
            e.printStackTrace();
            return 8750;
        }
    }

    @Override
    public int getMinFreq(BandCategoryEnum band) {
        try {
            return mFmUtil.getMinFreq(band);
        } catch (Exception e) {
            Log.i(TAG, "getMinFreq(" + band + ") Exception **** ");
            e.printStackTrace();
            return 8750;
        }
    }

    @Override
    public int getMaxFreq() {
        try {
            return mFmUtil.getMaxFreq();
        } catch (Exception e) {
            Log.i(TAG, "getMaxFreq() Exception **** ");
            e.printStackTrace();
            return 10800;
        }
    }

    @Override
    public int getMaxFreq(BandCategoryEnum band) {
        try {
            return mFmUtil.getMaxFreq(band);
        } catch (Exception e) {
            Log.i(TAG, "getMaxFreq(" + band + ") Exception **** ");
            e.printStackTrace();
            return 10800;
        }
    }

    @Override
    public int getCurrFreq() {
        try {
            return mFmUtil.getCurrFreq();
        } catch (Exception e) {
            Log.i(TAG, "getCurrFreq() Exception **** ");
            e.printStackTrace();
            return 8750;
        }
    }

    public boolean play(int freq) {
        try {
            return mFmUtil.play(freq);
        } catch (Exception e) {
            Log.i(TAG, "play(" + freq + ") Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean play(BandCategoryEnum band, int freq) {
        try {
            return mFmUtil.play(band, freq);
        } catch (Exception e) {
            Log.i(TAG, "play(" + band + "," + freq + ") Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean stepPrev() {
        try {
            return mFmUtil.stepPrev();
        } catch (Exception e) {
            Log.i(TAG, "stepPrev() Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean stepNext() {
        try {
            return mFmUtil.stepNext();
        } catch (Exception e) {
            Log.i(TAG, "stepNext() Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean scanAndPlayPrev() {
        try {
            return mFmUtil.scanAndPlayPrev();
        } catch (Exception e) {
            Log.i(TAG, "scanAndPlayPrev() Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean scanAndPlayNext() {
        try {
            return mFmUtil.scanAndPlayNext();
        } catch (Exception e) {
            Log.i(TAG, "scanAndPlayNext() Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setSt(boolean enable) {
        try {
            return mFmUtil.setSt(enable);
        } catch (Exception e) {
            Log.i(TAG, "setSt(" + enable + ") Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setLoc(boolean enable) {
        try {
            return mFmUtil.setLoc(enable);
        } catch (Exception e) {
            Log.i(TAG, "setLoc(" + enable + ") Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isLocOpen() {
        try {
            return mFmUtil.isLocOpen();
        } catch (Exception e) {
            Log.i(TAG, "isLocOpen() Exception **** ");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isAudioFocusRegistered() {
        return super.isAudioFocusRegistered();
    }

    @Override
    public void onDestroy() {
        removeAudioFocusListener(this);
        mHandler.removeCallbacksAndMessages(null);
        controlFm(false);
        super.onDestroy();
    }
}
