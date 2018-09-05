package com.tricheer.radio.engine;

/**
 * Band Category
 * <p>
 * 波段大类
 *
 * @author Jun.Wang
 */
public interface BandInfos {
    /**
     * Band Category
     * <p>
     * 波段大类
     */
    interface BandCateory {
        /**
         * Band None
         */
        int NONE = -1;

        /**
         * FM - AM
         */
        int FM = 0, AM = 1;
    }

    /**
     * Band Type
     *
     * @author Jun.Wang
     */
    interface BandType {
        /**
         * Band None
         */
        int NONE = -1;

        /**
         * FM - AM
         */
        int FM = 0, AM = 1;
    }

    /**
     * Radio FM/AM Unit
     */
    interface BandUnit {
        String UNIT_FM = "MHz", UNIT_AM = "KHz";
    }
}
