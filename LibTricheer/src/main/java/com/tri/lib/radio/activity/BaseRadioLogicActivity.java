package com.tri.lib.radio.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tri.lib.radio.engine.BandCategoryEnum;
import com.tri.lib.radio.utils.RadioPreferUtils;

/**
 * FM logic base activity.
 *
 * @author Jun.Wang
 */
public abstract class BaseRadioLogicActivity extends BaseRadioActivity {
    //TAG
//    private final String TAG = "BaseRadioLogicActivity";

    private MsgHandler mMsgHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMsgHandler = new MsgHandler();
    }

    @Override
    public void onFreqChanged(int freq, BandCategoryEnum band) {
        super.onFreqChanged(freq, band);
        RadioPreferUtils.getLastFreq(true, band, freq);
    }

    public BandCategoryEnum getLastBand() {
        return RadioPreferUtils.getLastBand(false, BandCategoryEnum.FM);
    }

    protected int getLastFreq(BandCategoryEnum band) {
        return RadioPreferUtils.getLastFreq(false, band, 0);
    }

    protected void execOpenRadio() {
        BandCategoryEnum lastBand = getLastBand();
        int lastFreq = getLastFreq(lastBand);
        execOpenRadio(lastBand, lastFreq);
    }

    protected void execOpenRadio(BandCategoryEnum band, int freq) {
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
        BandCategoryEnum band = getCurrBand();
        RadioPreferUtils.getLastFreq(true, band, getCurrFreq());

        //Switch
        switch (band) {
            case FM:
                band = BandCategoryEnum.AM;
                break;
            case AM:
                band = BandCategoryEnum.FM;
                break;
        }

        //Will close radio after set successfully!!!
        setBand(band);
        RadioPreferUtils.getLastBand(true, band);

        //
        if (closeFm()) {
            execOpenRadio();
        }
    }

    protected void openRadioAfterSearchedAll(BandCategoryEnum band, int freq) {
        //Will close radio after set successfully!!!
        setBand(band);
        RadioPreferUtils.getLastBand(true, band);

        //
        if (closeFm()) {
            if (freq >= getMinFreq() && freq <= getMaxFreq()) {
                execOpenRadio(band, freq);
                RadioPreferUtils.getLastFreq(true, band, freq);
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

    protected int getSeekBarMax(BandCategoryEnum band) {
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
