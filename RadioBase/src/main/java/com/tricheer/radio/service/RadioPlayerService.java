package com.tricheer.radio.service;

import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.tricheer.radio.engine.BandInfos.BandType;
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
public class RadioPlayerService extends BaseAudioFocusService implements FmDelegate, FmListener {

    private static Handler mHandler;
    private Set<FmListener> mSetFmListeners;
    private FmUtilV2 mFmUtil;

    /**
     * Get Service Object
     */
    public class LocalBinder extends Binder {
        public RadioPlayerService getService() {
            return RadioPlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        mHandler = new Handler();
        mSetFmListeners = new HashSet<>();
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

    @Override
    public void onAudioFocusTransient() {
        super.onAudioFocusTransient();
        closeFm();
    }

    @Override
    public void onAudioFocusLoss() {
        super.onAudioFocusLoss();
        closeFm();
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
    public void onSeachFreqFail(final int type, final int reason) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (FmListener l : mSetFmListeners) {
                    l.onScanFreqFail(type, reason);
                }
            }
        });
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
    public void setBand(int band) {
        if (mFmUtil != null) {
            mFmUtil.setBand(band);
        }
    }

    @Override
    public int getCurrBand() {
        if (mFmUtil != null) {
            return mFmUtil.getCurrBand();
        }
        return BandType.FM;
    }

    @Override
    public int getMinFreq() {
        if (mFmUtil != null) {
            return mFmUtil.getMinFreq();
        }
        return 8750;
    }

    @Override
    public int getMinFreq(int band) {
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
    public int getMaxFreq(int band) {
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
    public boolean play(int band, int freq) {
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
        registerAudioFocus(2);
        mHandler.removeCallbacksAndMessages(null);
        controlFm(false);
        super.onDestroy();
    }
}
