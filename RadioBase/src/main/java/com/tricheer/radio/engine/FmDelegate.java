package com.tricheer.radio.engine;

import android.support.annotation.Nullable;

import com.tricheer.radio.engine.BandInfos.BandType;

/**
 * Radio delegate
 * <p>Used to control radio.</p>
 * <p>{@link FmListener} - The callback of radio.</p>
 *
 * @author Jun.Wang
 */
public interface FmDelegate {

    /**
     * Callback
     */
    interface FmListener {
        void onFreqChanged(int freq, int type);

        void onSeachAvailableFreq(int currentSeachfreq, int count, int[] freqs, int type);

        void onStChange(boolean show);

        void onSeachFreqEnd(int type);

        void onSeachFreqStart(int type);

        void onSeachFreqFail(int type, int reason);

        void onScanFreqStart(int type);

        void onScanFreqEnd(int type);

        void onScanFreqFail(int type, int reason);
    }

    /**
     * Register FM status listener
     * <p>
     * Please call the method before {@link #openFm()}
     * </P>
     *
     * @param l {@link FmListener}
     */
    void register(FmListener l);

    /**
     * Unregister FM status listener
     *
     * @param l {@link FmListener}
     */
    void unregister(@Nullable FmListener l);

    /**
     * Open Status
     */
    boolean isRadioOpened();

    /**
     * Open FM
     *
     * @return true - Open successfully; false - Open failed.
     */
    boolean openFm();

    /**
     * Close FM
     *
     * @return true - close successfully; false - close failed.
     */
    boolean closeFm();

    /**
     * Search all available frequencies
     *
     * @return true - Successfully; false - Failed.
     */
    boolean searchAll();

    /**
     * Get all available frequencies
     *
     * @return int[]
     */
    int[] getAllAvailableFreqs();

    /**
     * Preview all available frequencies
     * <p>
     * Search and play 10 seconds. Loop till all available frequencies
     * </p>
     *
     * @return true - opened; false - not opened.
     */
    boolean preview();

    /**
     * Set band
     * <p>Will close radio after set successfully!!!</p>
     *
     * @param band {@link BandType#FM} or {@link BandType#AM}
     */
    void setBand(int band);

    /**
     * Get current band
     *
     * @return int {@link BandType#FM} or {@link BandType#AM}
     */
    int getCurrBand();

    /**
     * Get minimum frequency of support area
     * <p>
     * e.g. If your country support [80~108], function will return 80;
     * </p>
     *
     * @return int
     */
    int getMinFreq();

    /**
     * Get minimum frequency of support area by band
     *
     * @param band {@link BandType}
     * @return int
     */
    int getMinFreq(int band);

    /**
     * Get maximum frequency of support area
     * <p>
     * e.g. If your country support [80~108], function will return 108;
     * </p>
     *
     * @return int
     */
    int getMaxFreq();

    /**
     * Get maximum frequency of support area by band
     *
     * @param band {@link BandType}
     * @return int
     */
    int getMaxFreq(int band);

    /**
     * Get current frequency
     *
     * @return int
     */
    int getCurrFreq();

    /**
     * Play selected frequency
     *
     * @param freq Selected frequency
     * @return true - Successfully; false - Failed.
     */
    boolean play(int freq);

    /**
     * Play selected frequency and band
     *
     * @param band {@link BandType#FM} or {@link BandType#AM}
     * @param freq Frequency value
     * @return true - Successfully; false - Failed.
     */
    boolean play(int band, int freq);

    /**
     * Step to previous
     *
     * @return true - Successfully; false - Failed.
     */
    boolean stepPrev();

    /**
     * Step to next
     *
     * @return true - Successfully; false - Failed.
     */
    boolean stepNext();

    /**
     * Scan previous exist frequency and play
     *
     * @return true - Successfully; false - Failed.
     */
    boolean scanAndPlayPrev();

    /**
     * Scan next exist frequency and play
     *
     * @return true - Successfully; false - Failed.
     */
    boolean scanAndPlayNext();

    /**
     * Set ST status
     *
     * @param enable Status
     * @return true - Successfully; false - Failed.
     */
    boolean setSt(boolean enable);

    /**
     * Set LOC status
     *
     * @param enable Status
     * @return true - Successfully; false - Failed.
     */
    boolean setLoc(boolean enable);

    /**
     * Get LOC status
     *
     * @return true - opened; false - not opened.
     */
    boolean isLocOpen();
}
