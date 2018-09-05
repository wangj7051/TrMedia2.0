package com.tricheer.radio.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tricheer.radio.engine.BandInfos.BandType;
import com.tricheer.radio.utils.PreferUtils;

/**
 * FM logic base activity.
 *
 * @author Jun.Wang
 */
public abstract class BaseFmLogicActivity extends BaseFmActivity {
    //TAG
    private final String TAG = "BaseFmLogicActivity";

    private MsgHandler mMsgHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMsgHandler = new MsgHandler();
    }

    @Override
    public void onFreqChanged(int freq, int band) {
        super.onFreqChanged(freq, band);
        PreferUtils.getLastFreq(true, band, freq);
    }

    protected int getLastBand() {
        return PreferUtils.getLastBand(false, -1);
    }

    protected int getLastFreq(int band) {
        return PreferUtils.getLastFreq(false, band, 0);
    }

    protected void execOpenRadio() {
        openFm();
        if (isRadioOpened()) {
            //setSt(true);
            //setLoc(true);
            int lastBand = getLastBand();
            int lastFreq = getLastFreq(lastBand);

            //
            setBand(lastBand);
            if (lastFreq == -1) {
                scanAll();
            } else {
                play(lastFreq);
            }
        }
    }

    protected void execSwitchBand() {
        int band = getCurrBand();
        switch (band) {
            case BandType.FM:
                band = BandType.AM;
                break;
            case BandType.AM:
                band = BandType.FM;
                break;
        }

        //Will close radio after set successfully!!!
        setBand(band);
        PreferUtils.getLastBand(true, band);

        //
        if (closeFm()) {
            mMsgHandler.removeCallbacksAndMessages(null);
            mMsgHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    execOpenRadio();
                }
            }, 100);
        }
    }

    protected int getSeekBarMax() {
        int maxFreq = getMaxFreq();
        int minFreq = getMinFreq();
        return maxFreq - minFreq;
    }

    @Override
    protected void onDestroy() {
        mMsgHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private static class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
