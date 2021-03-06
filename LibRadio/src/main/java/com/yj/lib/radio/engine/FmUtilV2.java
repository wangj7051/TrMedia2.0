package com.yj.lib.radio.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.IBinder;
import android.os.IFmListener;
import android.os.IFmManager;
import android.os.RemoteException;
import android.util.Log;

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

    /**
     * LOC status
     */
    private boolean mLocOpen = false;

    /**
     * Radio open status
     */
    private boolean mIsRadioOpened = false;

    /**
     * Constructor
     *
     * @param context-{@link Context}
     */
    public FmUtilV2(Context context) {
        // mContext = context;
        try {
            Log.i(TAG, "FmUtilV2(context)");
            @SuppressLint("PrivateApi")
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
                    mFmListener.onFreqChanged(freq, BandCategoryEnum.get(type));
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
                    mFmListener.onSearchAvailableFreq(currentSeachfreq, count, freqs, BandCategoryEnum.get(type));
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
                    mFmListener.onSearchFreqStart(BandCategoryEnum.get(type));
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
                    mFmListener.onSearchFreqEnd(BandCategoryEnum.get(type));
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
                    mFmListener.onSearchFreqFail(BandCategoryEnum.get(type), reason);
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
                    mFmListener.onScanFreqStart(BandCategoryEnum.get(type));
                }
            } catch (Exception e) {
                Log.i(TAG, "onScanFreqStart(type) > " + e.getMessage());
            }
        }

        @Override
        public void onScanFreqEnd(int type) {
            try {
                BandCategoryEnum bce = BandCategoryEnum.get(type);
                Log.i(TAG, "onScanFreqEnd(" + bce + ")");
                if (mFmListener != null) {
                    mFmListener.onScanFreqEnd(bce);
                }
            } catch (Exception e) {
                Log.i(TAG, "onScanFreqEnd(type) > " + e.getMessage());
            }
        }

        @Override
        public void onScanFreqFail(int type, int reason) {
            try {
                BandCategoryEnum bce = BandCategoryEnum.get(type);
                Log.i(TAG, "onScanFreqFail(" + bce + "," + reason + ")");
                if (mFmListener != null) {
                    mFmListener.onScanFreqFail(bce, reason);
                }
            } catch (Exception e) {
                Log.i(TAG, "onScanFreqFail(type,reason) > " + e.getMessage());
            }
        }

        @Override
        public void onScanStrongFreqLeftStart(int type) {
            try {
                BandCategoryEnum bce = BandCategoryEnum.get(type);
                Log.i(TAG, "onScanStrongFreqLeftStart(" + bce + ")");
                if (mFmListener != null) {
                    mFmListener.onScanStrongFreqLeftStart(bce);
                }
            } catch (Exception e) {
                Log.i(TAG, "onScanStrongFreqLeftStart(type) > " + e.getMessage());
            }
        }

        @Override
        public void onScanStrongFreqLeftEnd(int type) {
            try {
                BandCategoryEnum bce = BandCategoryEnum.get(type);
                Log.i(TAG, "onScanStrongFreqLeftEnd(" + bce + ")");
                if (mFmListener != null) {
                    mFmListener.onScanStrongFreqLeftEnd(bce);
                }
            } catch (Exception e) {
                Log.i(TAG, "onScanStrongFreqLeftEnd(type) > " + e.getMessage());
            }
        }

        @Override
        public void onScanStrongFreqLeftFail(int type, int reason) {
            try {
                BandCategoryEnum bce = BandCategoryEnum.get(type);
                Log.i(TAG, "onScanStrongFreqLeftFail(" + bce + "," + reason + ")");
                if (mFmListener != null) {
                    mFmListener.onScanStrongFreqLeftFail(bce, reason);
                }
            } catch (Exception e) {
                Log.i(TAG, "onScanStrongFreqLeftFail(type) > " + e.getMessage());
            }
        }

        @Override
        public void onScanStrongFreqRightStart(int type) {
            try {
                BandCategoryEnum bce = BandCategoryEnum.get(type);
                Log.i(TAG, "onScanStrongFreqRightStart(" + bce + ")");
                if (mFmListener != null) {
                    mFmListener.onScanStrongFreqRightStart(bce);
                }
            } catch (Exception e) {
                Log.i(TAG, "onScanStrongFreqRightStart(type) > " + e.getMessage());
            }
        }

        @Override
        public void onScanStrongFreqRightEnd(int type) {
            try {
                BandCategoryEnum bce = BandCategoryEnum.get(type);
                Log.i(TAG, "onScanStrongFreqRightEnd(" + bce + ")");
                if (mFmListener != null) {
                    mFmListener.onScanStrongFreqRightEnd(bce);
                }
            } catch (Exception e) {
                Log.i(TAG, "onScanStrongFreqRightEnd(type) > " + e.getMessage());
            }
        }

        @Override
        public void onScanStrongFreqRightFail(int type, int reason) {
            try {
                BandCategoryEnum bce = BandCategoryEnum.get(type);
                Log.i(TAG, "onScanStrongFreqRightFail(" + bce + "," + reason + ")");
                if (mFmListener != null) {
                    mFmListener.onScanStrongFreqRightFail(bce, reason);
                }
            } catch (Exception e) {
                Log.i(TAG, "onScanStrongFreqRightFail(type) > " + e.getMessage());
            }
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
            Log.i(TAG, "searchAll()");
            return mFmManager.startSearchAvailableFreq();
        } catch (Exception e) {
            Log.i(TAG, "searchAll() > " + e.getMessage());
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
    public void setBand(BandCategoryEnum band) {
        try {
            Log.i(TAG, "setBand(" + band + ")");
            mFmManager.setSwitchType(band.getVal());
        } catch (Exception e) {
            Log.i(TAG, "setBand(band) > " + e.getMessage());
        }
    }

    @Override
    public BandCategoryEnum getCurrBand() {
        try {
            Log.i(TAG, "getCurrBand()");
            return BandCategoryEnum.get(getSwitchType());
        } catch (Exception e) {
            Log.i(TAG, "getCurrBand() > " + e.getMessage());
            return BandCategoryEnum.FM;
        }
    }

    private int getSwitchType() {
        try {
            Log.i(TAG, "getSwitchType()");
            return mFmManager.getSwitchType();
        } catch (Exception e) {
            Log.i(TAG, "getSwitchType() > " + e.getMessage());
            return 0;
        }
    }

    @Override
    public int getMinFreq() {
        try {
            Log.i(TAG, "getMinFreq()");
            return mFmManager.getFmMinSearchFreq(getSwitchType());
        } catch (Exception e) {
            Log.i(TAG, "getMinFreq() > " + e.getMessage());
            return 8750;
        }
    }

    @Override
    public int getMinFreq(BandCategoryEnum band) {
        switch (band) {
            case FM:
                return 8750;
            case AM:
                return 522;
        }
        return 0;
    }

    @Override
    public int getMaxFreq() {
        try {
            Log.i(TAG, "getMaxFreq()");
            return mFmManager.getFmMaxSearchFreq(getSwitchType());
        } catch (Exception e) {
            Log.i(TAG, "getMaxFreq() > " + e.getMessage());
            return 10800;
        }
    }

    @Override
    public int getMaxFreq(BandCategoryEnum band) {
        switch (band) {
            case FM:
                return 10800;
            case AM:
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
    public boolean play(BandCategoryEnum band, int freq) {
        try {
            Log.i(TAG, "play(" + band + "," + freq + ")");
            mFmManager.setSwitchType(band.getVal());
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
