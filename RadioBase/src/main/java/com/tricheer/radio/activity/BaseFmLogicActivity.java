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

    public int getLastBand() {
        return PreferUtils.getLastBand(false, -1);
    }

    protected int getLastFreq(int band) {
        return PreferUtils.getLastFreq(false, band, 0);
    }

    protected void execOpenRadio() {
        int lastBand = getLastBand();
        int lastFreq = getLastFreq(lastBand);
        execOpenRadio(lastBand, lastFreq);
    }

    protected void execOpenRadio(int band, int freq) {
        if (!isRadioOpened()) {
            openFm();
        }
        if (isRadioOpened()) {
            //setSt(true);
            //setLoc(true);

            //
            setBand(band);
            if (freq == -1) {
                searchAll();
            } else {
                play(freq);
            }
        }
    }

    protected void execSwitchBand() {
        //Save History
        int band = getCurrBand();
        PreferUtils.getLastFreq(true, band, getCurrFreq());

        //Switch
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
            execOpenRadio();
        }
    }

    protected void openRadioAfterSearchedAll(int band, int freq) {
        //Will close radio after set successfully!!!
        setBand(band);
        PreferUtils.getLastBand(true, band);

        //
        if (closeFm()) {
            if (freq >= getMinFreq() && freq <= getMaxFreq()) {
                execOpenRadio(band, freq);
                PreferUtils.getLastFreq(true, band, freq);
            } else {
                execOpenRadio();
            }
        }
    }

    protected int getSeekBarMax() {
        int maxFreq = getMaxFreq();
        int minFreq = getMinFreq();
        return maxFreq - minFreq;
    }

    protected int getSeekBarMax(int band) {
        int maxFreq = getMaxFreq(band);
        int minFreq = getMinFreq(band);
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
