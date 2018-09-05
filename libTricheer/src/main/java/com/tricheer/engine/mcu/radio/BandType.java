package com.tricheer.engine.mcu.radio;

/**
 * Band Type
 *
 * @author Jun.Wang
 */
public interface BandType {
    /**
     * Band None
     */
    int NONE = -1;

    /**
     * FM - AM
     */
    int FM = 0, AM = 1;

    /**
     * FM1/FM2/FM3 - AM1/AM2
     */
    int BAND_FM1 = 0, BAND_FM2 = 1, BAND_FM3 = 2, BAND_AM1 = 3, BAND_AM2 = 4, BAND_AM3 = 5;
}
