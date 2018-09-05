package com.tricheer.engine.mcu;

/**
 * MCU CONSTS
 *
 * @author Jun.Wang
 */
public class MCUConsts {
    /**
     * 上一次设备掉电了
     */
    public static final int DEVICE_POWER_DOWN = 1;

    /**
     * MCU Volume Type
     */
    public interface MCUVol {
        // Volume Types
        int TYPE_RADIO = 0x00;
        int YPE_MUSIC = 0x01;
        int TYPE_VIDEO = 0x03;
        int TYPE_AUX = 0x04;
        int TYPE_AVOFF = 0x05;

        // Status
        int ST_OPEN = 0, ST_CLOSE = 1;
    }

    /**
     * HandBrake Status
     */
    public interface HandBrakeStatus {
        /**
         * BRAKE / NO BRAKE
         */
        byte ON = 0x01, OFF = 0x00;
    }
}
