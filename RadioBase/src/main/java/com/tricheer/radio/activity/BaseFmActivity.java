package com.tricheer.radio.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.tricheer.radio.engine.BandInfos.BandType;
import com.tricheer.radio.engine.FmDelegate;
import com.tricheer.radio.engine.FmDelegate.FmListener;
import com.tricheer.radio.service.ControlService;
import com.tricheer.radio.service.ControlService.LocalBinder;

/**
 * Base FM Activity
 * <p>
 * Implemented {@link FmDelegate} & {@link FmListener}
 * </p>
 *
 * @author Jun.Wang
 */
public abstract class BaseFmActivity extends BaseFragActivity implements FmDelegate, FmListener {

    private boolean mIsRadioOpened = false;
    private ControlService mControlService;
    private ServiceConnection mControlServiceConn = new ServiceConnection() {

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
    public void onFreqChanged(int freq, int band) {
    }

    /**
     * Callback by {@link FmListener}
     */
    @Override
    public void onSeachAvailableFreq(int currentSeachfreq, int count, int[] freqs, int type) {
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
    public void onSeachFreqEnd(int type) {
    }

    /**
     * Callback by {@link FmListener}
     */
    @Override
    public void onSeachFreqStart(int type) {
    }

    /**
     * Callback by {@link FmListener}
     */
    @Override
    public void onScanFreqStart(int type) {
    }

    /**
     * Callback by {@link FmListener}
     */
    @Override
    public void onScanFreqEnd(int type) {
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
        mIsRadioOpened = mControlService != null && mControlService.openFm();
        return mIsRadioOpened;
    }

    public boolean isRadioOpened() {
        return mIsRadioOpened;
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
    public void setBand(int band) {
        if (mControlService != null) {
            mControlService.setBand(band);
        }
    }

    @Override
    public int getCurrBand() {
        if (mControlService != null) {
            return mControlService.getCurrBand();
        }
        return BandType.FM;
    }

    @Override
    public int getMinFreq() {
        if (mControlService != null) {
            return mControlService.getMinFreq();
        }
        return 0;
    }

    @Override
    public int getMinFreq(int band) {
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
    public int getMaxFreq(int band) {
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
    public boolean play(int band, int freq) {
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

    /**
     * Bind and create control service
     *
     * @param flags 1 start; 2 bind; 3 unbind; 4 stop
     */
    protected void bindAndCreateControlService(int... flags) {
        try {
            for (int flag : flags) {
                Intent serviceIntent = new Intent(this, ControlService.class);
                switch (flag) {
                    case 1:
                        startService(serviceIntent);
                        break;
                    case 2:
                        bindService(serviceIntent, mControlServiceConn, BIND_AUTO_CREATE);
                        break;
                    case 3:
                        if (mControlService != null) {
                            unbindService(mControlServiceConn);
                        }
                        break;
                    case 4:
                        if (mControlService != null) {
                            stopService(serviceIntent);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        unregister(this);
        super.onDestroy();
    }
}
