package com.tri.lib.radio.engine;

import android.support.annotation.Nullable;

import js.lib.android.media.engine.IAudioFocusListener;

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
    interface FmListener extends IAudioFocusListener {
        void onFreqChanged(int freq, BandCategoryEnum band);

        void onSearchAvailableFreq(int currentSeachfreq, int count, int[] freqs, BandCategoryEnum band);

        void onStChange(boolean show);

        /**
         * 搜索所有可以播放的电台-Start
         */
        void onSearchFreqStart(BandCategoryEnum band);

        /**
         * 搜索所有可以播放的电台-END
         */
        void onSearchFreqEnd(BandCategoryEnum band);

        /**
         * 搜索所有可以播放的电台-FAIL
         */
        void onSearchFreqFail(BandCategoryEnum band, int reason);

        /**
         * 扫描预览-Start
         */
        void onScanFreqStart(BandCategoryEnum band);

        /**
         * 扫描预览-END
         */
        void onScanFreqEnd(BandCategoryEnum band);

        /**
         * 扫描预览-FAIL
         */
        void onScanFreqFail(BandCategoryEnum band, int reason);

        /**
         * 搜索上一个可以播放的电台并播放-START
         */
        void onScanStrongFreqLeftStart(BandCategoryEnum band);

        /**
         * 搜索上一个可以播放的电台并播放-END
         */
        void onScanStrongFreqLeftEnd(BandCategoryEnum band);

        /**
         * 搜索上一个可以播放的电台并播放-FAIL
         */
        void onScanStrongFreqLeftFail(BandCategoryEnum band, int reason);

        /**
         * 搜索下一个可以播放的电台并播放-START
         */
        void onScanStrongFreqRightStart(BandCategoryEnum band);

        /**
         * 搜索下一个可以播放的电台并播放-END
         */
        void onScanStrongFreqRightEnd(BandCategoryEnum band);

        /**
         * 搜索下一个可以播放的电台并播放-FAIL
         */
        void onScanStrongFreqRightFail(BandCategoryEnum band, int reason);
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
     * @param band {@link BandCategoryEnum#FM} or {@link BandCategoryEnum#AM}
     */
    void setBand(BandCategoryEnum band);

    /**
     * Get current band
     *
     * @return int {@link BandCategoryEnum#FM} or {@link BandCategoryEnum#AM}
     */
    BandCategoryEnum getCurrBand();

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
     * @param band {@link BandCategoryEnum}
     * @return int
     */
    int getMinFreq(BandCategoryEnum band);

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
     * @param band {@link BandCategoryEnum}
     * @return int
     */
    int getMaxFreq(BandCategoryEnum band);

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
     * @param band {@link BandCategoryEnum#FM} or {@link BandCategoryEnum#AM}
     * @param freq Frequency value
     * @return true - Successfully; false - Failed.
     */
    boolean play(BandCategoryEnum band, int freq);

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
