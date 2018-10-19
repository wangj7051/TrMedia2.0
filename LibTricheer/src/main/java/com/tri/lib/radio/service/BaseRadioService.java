package com.tri.lib.radio.service;

import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.tri.lib.radio.engine.BandCategoryEnum;
import com.tri.lib.radio.engine.FmDelegate;
import com.tri.lib.radio.engine.FmDelegate.FmListener;
import com.tri.lib.radio.engine.FmUtilV2;
import com.tri.lib.radio.utils.RadioPreferUtils;

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
        return mFmUtil != null && mFmUtil.isRadioOpened();
    }

    @Override
    public boolean openFm() {
        if (!isRadioOpened()) {
            mFmUtil.openFm();
        }
        boolean isOpened = isRadioOpened();
        if (isOpened) {
            registerAudioFocus(1);
        }
        return isOpened;
    }

    @Override
    public boolean closeFm() {
        return mFmUtil != null && mFmUtil.closeFm();
    }

    @Override
    public boolean searchAll() {
        return mFmUtil != null && mFmUtil.searchAll();
    }

    @Override
    public int[] getAllAvailableFreqs() {
        if (mFmUtil != null) {
            return mFmUtil.getAllAvailableFreqs();
        }
        return new int[]{};
    }

    @Override
    public boolean preview() {
        return mFmUtil != null && mFmUtil.preview();
    }

    @Override
    public void setBand(BandCategoryEnum band) {
        if (mFmUtil != null) {
            mFmUtil.setBand(band);
        }
    }

    @Override
    public BandCategoryEnum getCurrBand() {
        if (mFmUtil != null) {
            return mFmUtil.getCurrBand();
        }
        return BandCategoryEnum.FM;
    }

    @Override
    public int getMinFreq() {
        if (mFmUtil != null) {
            return mFmUtil.getMinFreq();
        }
        return 8750;
    }

    @Override
    public int getMinFreq(BandCategoryEnum band) {
        if (mFmUtil != null) {
            return mFmUtil.getMinFreq(band);
        }
        return 8750;
    }

    @Override
    public int getMaxFreq() {
        if (mFmUtil != null) {
            return mFmUtil.getMaxFreq();
        }
        return 10800;
    }

    @Override
    public int getMaxFreq(BandCategoryEnum band) {
        if (mFmUtil != null) {
            return mFmUtil.getMaxFreq(band);
        }
        return 10800;
    }

    @Override
    public int getCurrFreq() {
        if (mFmUtil != null) {
            return mFmUtil.getCurrFreq();
        }
        return 8750;
    }

    public boolean play(int freq) {
        return mFmUtil != null && mFmUtil.play(freq);
    }

    @Override
    public boolean play(BandCategoryEnum band, int freq) {
        return mFmUtil != null && mFmUtil.play(band, freq);
    }

    @Override
    public boolean stepPrev() {
        return mFmUtil != null && mFmUtil.stepPrev();
    }

    @Override
    public boolean stepNext() {
        return mFmUtil != null && mFmUtil.stepNext();
    }

    @Override
    public boolean scanAndPlayPrev() {
        return mFmUtil != null && mFmUtil.scanAndPlayPrev();
    }

    @Override
    public boolean scanAndPlayNext() {
        return mFmUtil != null && mFmUtil.scanAndPlayNext();
    }

    @Override
    public boolean setSt(boolean enable) {
        return mFmUtil != null && mFmUtil.setSt(enable);
    }

    @Override
    public boolean setLoc(boolean enable) {
        return mFmUtil.setLoc(enable);
    }

    @Override
    public boolean isLocOpen() {
        return mFmUtil != null && mFmUtil.isLocOpen();
    }

    @Override
    public void onDestroy() {
        removeAudioFocusListener(this);
        registerAudioFocus(2);
        mHandler.removeCallbacksAndMessages(null);
        controlFm(false);
        super.onDestroy();
    }
}
