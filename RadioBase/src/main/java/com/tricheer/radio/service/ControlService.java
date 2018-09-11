package com.tricheer.radio.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.tricheer.radio.engine.FmDelegate;
import com.tricheer.radio.engine.FmDelegate.FmListener;
import com.tricheer.radio.engine.FmUtilV2;

import java.util.HashSet;
import java.util.Set;

/**
 * Control operate service
 * <p>
 * 1. Control Radio Play
 * </p>
 *
 * @author Jun.Wang
 */
public class ControlService extends Service implements FmDelegate, FmListener {

    private static Handler mHandler;
    private Set<FmListener> mSetFmListeners;
    private FmUtilV2 mFmUtil;

    /**
     * Get Service Object
     */
    public class LocalBinder extends Binder {
        public ControlService getService() {
            return ControlService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        mHandler = new Handler();
        mSetFmListeners = new HashSet<FmListener>();
        controlFm(true);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return new LocalBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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
    public void onFreqChanged(final int freq, final int type) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (FmListener l : mSetFmListeners) {
                    l.onFreqChanged(freq, type);
                }
            }
        });
    }

    @Override
    public void onSeachAvailableFreq(int currentSeachfreq, int count, int[] freqs, int type) {
        for (FmListener l : mSetFmListeners) {
            l.onSeachAvailableFreq(currentSeachfreq, count, freqs, type);
        }
    }

    @Override
    public void onStChange(boolean show) {
        for (FmListener l : mSetFmListeners) {
            l.onStChange(show);
        }
    }

    @Override
    public void onSeachFreqStart(final int type) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (FmListener l : mSetFmListeners) {
                    l.onSeachFreqStart(type);
                }
            }
        });
    }

    @Override
    public void onSeachFreqEnd(final int type) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (FmListener l : mSetFmListeners) {
                    l.onSeachFreqEnd(type);
                }
            }
        });
    }


    @Override
    public void onSeachFreqFail(int type, int reason) {
    }

    // 预览
    @Override
    public void onScanFreqStart(int type) {
        for (FmListener l : mSetFmListeners) {
            l.onScanFreqStart(type);
        }
    }

    @Override
    public void onScanFreqEnd(int type) {
        for (FmListener l : mSetFmListeners) {
            l.onScanFreqEnd(type);
        }
    }

    @Override
    public void onScanFreqFail(int type, int reason) {
    }

    @Override
    public void register(FmListener l) {
        if (l != null) {
            mSetFmListeners.add(l);
        }
    }

    @Override
    public void unregister(FmListener l) {
        if (l != null) {
            mSetFmListeners.remove(l);
        }
    }

    @Override
    public boolean openFm() {
        return mFmUtil.openFm();
    }

    @Override
    public boolean closeFm() {
        return mFmUtil.closeFm();
    }

    @Override
    public boolean searchAll() {
        return mFmUtil.searchAll();
    }

    @Override
    public int[] getAllAvailableFreqs() {
        return mFmUtil.getAllAvailableFreqs();
    }

    @Override
    public boolean preview() {
        return mFmUtil.preview();
    }

    @Override
    public void setBand(int band) {
        mFmUtil.setBand(band);
    }

    @Override
    public int getCurrBand() {
        return mFmUtil.getCurrBand();
    }

    @Override
    public int getMinFreq() {
        return mFmUtil.getMinFreq();
    }

    @Override
    public int getMinFreq(int band) {
        return mFmUtil.getMinFreq(band);
    }

    @Override
    public int getMaxFreq() {
        return mFmUtil.getMaxFreq();
    }

    @Override
    public int getMaxFreq(int band) {
        return mFmUtil.getMaxFreq(band);
    }

    @Override
    public int getCurrFreq() {
        return mFmUtil.getCurrFreq();
    }

    public boolean play(int freq) {
        return mFmUtil.play(freq);
    }

    @Override
    public boolean play(int band, int freq) {
        return mFmUtil.play(band, freq);
    }

    @Override
    public boolean stepPrev() {
        return mFmUtil.stepPrev();
    }

    @Override
    public boolean stepNext() {
        return mFmUtil.stepNext();
    }

    @Override
    public boolean scanAndPlayPrev() {
        return mFmUtil.scanAndPlayPrev();
    }

    @Override
    public boolean scanAndPlayNext() {
        return mFmUtil.scanAndPlayNext();
    }

    @Override
    public boolean setSt(boolean enable) {
        return mFmUtil.setSt(enable);
    }

    @Override
    public boolean setLoc(boolean enable) {
        return mFmUtil.setLoc(enable);
    }

    @Override
    public boolean isLocOpen() {
        return mFmUtil.isLocOpen();
    }

    @Override
    public void onDestroy() {
        controlFm(false);
        super.onDestroy();
    }
}
