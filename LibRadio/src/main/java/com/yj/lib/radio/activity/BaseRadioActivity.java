package com.yj.lib.radio.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.yj.lib.radio.engine.BandCategoryEnum;
import com.yj.lib.radio.engine.FmDelegate;
import com.yj.lib.radio.engine.FmDelegate.FmListener;
import com.yj.lib.radio.service.BaseRadioService;
import com.yj.lib.radio.service.BaseRadioService.LocalBinder;

import js.lib.android.activity.BaseFragActivity;

/**
 * Base FM Activity
 * <p>
 * Implemented {@link FmDelegate} & {@link FmListener}
 * </p>
 *
 * @author Jun.Wang
 */
public abstract class BaseRadioActivity extends BaseFragActivity implements FmDelegate, FmListener {

    protected BaseRadioService mControlService;
    protected ServiceConnection mControlServiceConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mControlService = ((LocalBinder) binder).getService();
            onServiceStatusChanged(mControlService, true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mControlService = null;
            onServiceStatusChanged(null, false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract void onServiceStatusChanged(Service service, boolean isConnected);

    /**
     * Callback by {@link FmListener}
     */
    @Override
    public void onFreqChanged(int freq, BandCategoryEnum band) {
    }

    /**
     * Callback by {@link FmListener}
     */
    @Override
    public void onSearchAvailableFreq(int currentSeachfreq, int count, int[] freqs, BandCategoryEnum band) {
    }

    /**
     * Callback by {@link FmListener}
     */
    @Override
    public void onStChange(boolean show) {
    }

    /**
     * Callback by {@link FmListener}
     */
    @Override
    public void onSearchFreqEnd(BandCategoryEnum band) {
    }

    /**
     * Callback by {@link FmListener}
     */
    @Override
    public void onSearchFreqStart(BandCategoryEnum band) {
    }

    @Override
    public void onSearchFreqFail(BandCategoryEnum band, int reason) {
    }

    /**
     * Callback by {@link FmListener}
     */
    @Override
    public void onScanFreqStart(BandCategoryEnum band) {
    }

    /**
     * Callback by {@link FmListener}
     */
    @Override
    public void onScanFreqEnd(BandCategoryEnum band) {
    }

    @Override
    public void onScanFreqFail(BandCategoryEnum band, int reason) {
    }

    @Override
    public void onScanStrongFreqLeftStart(BandCategoryEnum band) {
    }

    @Override
    public void onScanStrongFreqLeftEnd(BandCategoryEnum band) {
    }

    @Override
    public void onScanStrongFreqLeftFail(BandCategoryEnum band, int reason) {
    }

    @Override
    public void onScanStrongFreqRightStart(BandCategoryEnum band) {
    }

    @Override
    public void onScanStrongFreqRightEnd(BandCategoryEnum band) {
    }

    @Override
    public void onScanStrongFreqRightFail(BandCategoryEnum band, int reason) {
    }

    @Override
    public void register(FmListener l) {
        if (mControlService != null) {
            mControlService.register(l);
        }
    }

    @Override
    public void unregister(FmListener l) {
        if (mControlService != null) {
            mControlService.unregister(l);
        }
    }

    @Override
    public boolean openFm() {
        return mControlService != null && mControlService.openFm();
    }

    @Override
    public boolean isRadioOpened() {
        return mControlService != null && mControlService.isRadioOpened();
    }

    @Override
    public boolean closeFm() {
        return mControlService != null && mControlService.closeFm();
    }

    @Override
    public boolean searchAll() {
        return mControlService != null && mControlService.searchAll();
    }

    @Override
    public int[] getAllAvailableFreqs() {
        if (mControlService != null) {
            return mControlService.getAllAvailableFreqs();
        }
        return null;
    }

    @Override
    public boolean preview() {
        return mControlService != null && mControlService.preview();
    }

    @Override
    public void setBand(BandCategoryEnum band) {
        if (mControlService != null) {
            mControlService.setBand(band);
        }
    }

    @Override
    public BandCategoryEnum getCurrBand() {
        if (mControlService != null) {
            return mControlService.getCurrBand();
        }
        return BandCategoryEnum.FM;
    }

    @Override
    public int getMinFreq() {
        if (mControlService != null) {
            return mControlService.getMinFreq();
        }
        return 0;
    }

    @Override
    public int getMinFreq(BandCategoryEnum band) {
        if (mControlService != null) {
            return mControlService.getMinFreq(band);
        }
        return 0;
    }

    @Override
    public int getMaxFreq() {
        if (mControlService != null) {
            return mControlService.getMaxFreq();
        }
        return 0;
    }

    @Override
    public int getMaxFreq(BandCategoryEnum band) {
        if (mControlService != null) {
            return mControlService.getMaxFreq(band);
        }
        return 0;
    }

    @Override
    public int getCurrFreq() {
        if (mControlService != null) {
            return mControlService.getCurrFreq();
        }
        return 0;
    }

    @Override
    public boolean play(int freq) {
        return mControlService != null && mControlService.play(freq);
    }

    @Override
    public boolean play(BandCategoryEnum band, int freq) {
        return mControlService != null && mControlService.play(band, freq);
    }

    @Override
    public boolean stepPrev() {
        return mControlService != null && mControlService.stepPrev();
    }

    @Override
    public boolean stepNext() {
        return mControlService != null && mControlService.stepNext();
    }

    @Override
    public boolean scanAndPlayPrev() {
        return mControlService != null && mControlService.scanAndPlayPrev();
    }

    @Override
    public boolean scanAndPlayNext() {
        return mControlService != null && mControlService.scanAndPlayNext();
    }

    @Override
    public boolean setSt(boolean enable) {
        return mControlService != null && mControlService.setSt(enable);
    }

    @Override
    public boolean setLoc(boolean enable) {
        return mControlService != null && mControlService.setLoc(enable);
    }

    @Override
    public boolean isLocOpen() {
        return mControlService != null && mControlService.isLocOpen();
    }

    public boolean isAudioFocusRegistered() {
        return mControlService != null && mControlService.isAudioFocusRegistered();
    }

    @Override
    protected void onDestroy() {
        unregister(this);
        super.onDestroy();
    }
}
