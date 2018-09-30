package com.tricheer.radio.engine;

import android.content.Context;
import android.os.IBinder;
import android.os.IFmListener;
import android.os.IFmManager;
import android.os.RemoteException;
import android.util.Log;

import com.tricheer.radio.engine.BandInfos.BandType;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Radio Version2
 * <p>
 * Support HAR_LC3110_BAS
 * </p>
 * <p>
 * Support SLC_LC2010_VDC
 * </p>
 *
 * @author Jun.Wang
 */
public class FmUtilV2 implements FmDelegate {
    //TAG
    private final String TAG = "FmUtilV2";

    // private Context mContext;
    private IFmManager mFmManager;
    private FmListener mFmListener;
    private boolean mLocOpen = false;
    private boolean mIsRadioOpened = false;

    public FmUtilV2(Context context) {
        // mContext = context;
        try {
            Log.i(TAG, "FmUtilV2(context)");
            Class<?> servicemanager = Class.forName("android.os.ServiceManager");
            Method getService = servicemanager.getMethod("getService", String.class);
            IBinder ibinder = (IBinder) getService.invoke(null, "fm_manager");
            mFmManager = IFmManager.Stub.asInterface(ibinder);
        } catch (Exception e) {
            Log.i(TAG, "FmUtilV2(context) > " + e.getMessage());
        }
    }

    private final IFmListener.Stub mFmListenerBinder = new IFmListener.Stub() {
        @Override
        public void onFreqChanged(int freq, int type) {
            try {
                Log.i(TAG, "onFreqChanged(" + freq + "," + type + ")");
                if (mFmListener != null) {
                    mFmListener.onFreqChanged(freq, type);
                }
            } catch (Exception e) {
                Log.i(TAG, "onFreqChanged(freq,type) > " + e.getMessage());
            }
        }

        @Override
        public void onSeachAvailableFreq(int currentSeachfreq, int count, int[] freqs, int type) {
            try {
                Log.i(TAG, "onSeachAvailableFreq(" + currentSeachfreq + "," + count + "," + Arrays.toString(freqs) + "," + type + ")");
                if (mFmListener != null) {
                    mFmListener.onSeachAvailableFreq(currentSeachfreq, count, freqs, type);
                }
            } catch (Exception e) {
                Log.i(TAG, "onSeachAvailableFreq(currentSeachfreq,count,freqs,type) > " + e.getMessage());
            }
        }

        @Override
        public void onStChange(boolean show) {
            try {
                Log.i(TAG, "onStChange(" + show + ")");
                if (mFmListener != null) {
                    mFmListener.onStChange(show);
                }
            } catch (Exception e) {
                Log.i(TAG, "onStChange(show) > " + e.getMessage());
            }
        }

        @Override
        public void onSeachFreqStart(int type) {
            try {
                Log.i(TAG, "onSeachFreqStart(" + type + ")");
                if (mFmListener != null) {
                    mFmListener.onSeachFreqStart(type);
                }
            } catch (Exception e) {
                Log.i(TAG, "onSeachFreqStart(type) > " + e.getMessage());
            }
        }

        @Override
        public void onSeachFreqEnd(int type) {
            try {
                Log.i(TAG, "onSeachFreqEnd(" + type + ")");
                if (mFmListener != null) {
                    mFmListener.onSeachFreqEnd(type);
                }
            } catch (Exception e) {
                Log.i(TAG, "onSeachFreqEnd(type) > " + e.getMessage());
            }
        }

        @Override
        public void onSeachFreqFail(int type, int reason) {
            try {
                Log.i(TAG, "onSeachFreqFail(" + type + "," + reason + ")");
                if (mFmListener != null) {
                    mFmListener.onSeachFreqFail(type, reason);
                }
            } catch (Exception e) {
                Log.i(TAG, "onSeachFreqFail(type,reason) > " + e.getMessage());
            }
        }

        @Override
        public void onScanFreqStart(int type) {
            try {
                Log.i(TAG, "onScanFreqStart(" + type + ")");
                if (mFmListener != null) {
                    mFmListener.onScanFreqStart(type);
                }
            } catch (Exception e) {
                Log.i(TAG, "onScanFreqStart(type) > " + e.getMessage());
            }
        }

        @Override
        public void onScanFreqEnd(int type) {
            try {
                Log.i(TAG, "onScanFreqEnd(" + type + ")");
                if (mFmListener != null) {
                    mFmListener.onScanFreqEnd(type);
                }
            } catch (Exception e) {
                Log.i(TAG, "onScanFreqEnd(type) > " + e.getMessage());
            }
        }

        @Override
        public void onScanFreqFail(int type, int reason) {
        }
    };

    @Override
    public void register(FmListener listener) {
        try {
            Log.i(TAG, "register(FmListener)");
            mFmListener = listener;
            mFmManager.regisFmStatusListener(mFmListenerBinder);
        } catch (Exception e) {
            Log.i(TAG, "register(FmListener) > " + e.getMessage());
        }
    }

    @Override
    public void unregister(FmListener listener) {
        try {
            Log.i(TAG, "unregister(FmListener)");
            mFmManager.unregisFmStatusListener(mFmListenerBinder);
        } catch (Exception e) {
            Log.i(TAG, "unregister(FmListener) > " + e.getMessage());
        }
    }

    @Override
    public boolean isRadioOpened() {
        return mIsRadioOpened;
    }

    @Override
    public boolean openFm() {
        try {
            Log.i(TAG, "openFm()");
            return (mIsRadioOpened = mFmManager.OpenFm());
        } catch (Exception e) {
            Log.i(TAG, "openFm() > " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean closeFm() {
        try {
            Log.i(TAG, "closeFm()");
            boolean isClosed = mFmManager.closeFm();
            if (isClosed) {
                mIsRadioOpened = false;
            }
            return isClosed;
        } catch (Exception e) {
            Log.i(TAG, "closeFm() > " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean searchAll() {
        try {
            Log.i(TAG, "scanAll()");
            return mFmManager.startSearchAvailableFreq();
        } catch (Exception e) {
            Log.i(TAG, "scanAll() > " + e.getMessage());
            return false;
        }
    }

    @Override
    public int[] getAllAvailableFreqs() {
        try {
            Log.i(TAG, "getAllAvailableFreqs()");
            return mFmManager.getSearchFreqs();
        } catch (Exception e) {
            Log.i(TAG, "getAllAvailableFreqs() > " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean preview() {
        try {
            Log.i(TAG, "preview()");
            return mFmManager.startScanFreq();
        } catch (Exception e) {
            Log.i(TAG, "preview() > " + e.getMessage());
            return false;
        }
    }

    @Override
    public void setBand(int band) {
        try {
            Log.i(TAG, "setBand(" + band + ")");
            mFmManager.setSwitchType(band);
        } catch (Exception e) {
            Log.i(TAG, "setBand(band) > " + e.getMessage());
        }
    }

    @Override
    public int getCurrBand() {
        try {
            Log.i(TAG, "getCurrBand()");
            return mFmManager.getSwitchType();
        } catch (Exception e) {
            Log.i(TAG, "getCurrBand() > " + e.getMessage());
            return BandType.FM;
        }
    }

    @Override
    public int getMinFreq() {
        try {
            Log.i(TAG, "getMinFreq()");
            return mFmManager.getFmMinSearchFreq();
        } catch (Exception e) {
            Log.i(TAG, "getMinFreq() > " + e.getMessage());
            return 7600;
        }
    }

    @Override
    public int getMinFreq(int band) {
        switch (band) {
            case BandType.FM:
                return 7600;
            case BandType.AM:
                return 522;
        }
        return 0;
    }

    @Override
    public int getMaxFreq() {
        try {
            Log.i(TAG, "getMaxFreq()");
            return mFmManager.getFmMaxSearchFreq();
        } catch (Exception e) {
            Log.i(TAG, "getMaxFreq() > " + e.getMessage());
            return 10800;
        }
    }

    @Override
    public int getMaxFreq(int band) {
        switch (band) {
            case BandType.FM:
                return 10800;
            case BandType.AM:
                return 1620;
        }
        return 0;
    }

    @Override
    public int getCurrFreq() {
        try {
            Log.i(TAG, "getCurrFreq()");
            return mFmManager.getFmCurrentFreq();
        } catch (RemoteException e) {
            Log.i(TAG, "getCurrFreq() > " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean play(int freq) {
        try {
            Log.i(TAG, "play(" + freq + ")");
            return mFmManager.setFmCurrentFreq(freq);
        } catch (Exception e) {
            Log.i(TAG, "play(freq) > " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean play(int band, int freq) {
        try {
            Log.i(TAG, "play(" + band + "," + freq + ")");
            mFmManager.setSwitchType(band);
            return mFmManager.setFmCurrentFreq(freq);
        } catch (Exception e) {
            Log.i(TAG, "play(band,freq) > " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean stepPrev() {
        try {
            Log.i(TAG, "stepPrev()");
            return mFmManager.onStepLeft();
        } catch (Exception e) {
            Log.i(TAG, "stepPrev() > " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean stepNext() {
        try {
            Log.i(TAG, "stepNext()");
            return mFmManager.onStepRight();
        } catch (Exception e) {
            Log.i(TAG, "stepNext() > " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean scanAndPlayPrev() {
        try {
            Log.i(TAG, "scanAndPlayPrev()");
            return mFmManager.onLongPressScanStrongFreqLeft();
        } catch (Exception e) {
            Log.i(TAG, "scanAndPlayPrev() > " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean scanAndPlayNext() {
        try {
            Log.i(TAG, "scanAndPlayNext()");
            return mFmManager.onLongPressScanStrongFreqRight();
        } catch (Exception e) {
            Log.i(TAG, "scanAndPlayNext() > " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean setSt(boolean enable) {
        try {
            Log.i(TAG, "setSt(" + enable + ")");
            return mFmManager.setSt(enable);
        } catch (Exception e) {
            Log.i(TAG, "setSt(boolean) > " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean setLoc(boolean enable) {
        try {
            Log.i(TAG, "setLoc(" + enable + ")");
            mLocOpen = enable;
            return mFmManager.setLoc(enable);
        } catch (Exception e) {
            Log.i(TAG, "setLoc(boolean) > " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isLocOpen() {
        return mLocOpen;
    }
}
