package com.tricheer.engine.mcu.radio;

/**
 * Radio Status
 */
public interface RadioStatus {
	/**
	 * Radio Opened/Closed
	 */
	public final int RADIO_CLOSED = 0, RADIO_OPENED = 1;

	/**
	 * ST Closed/Opened
	 */
	public final int ST_CLOSED = 0, ST_OPENED = 1;

	/**
	 * LOC Closed/Opened
	 */
	public final int LOC_CLOSED = 0, LOC_OPENED = 1;

	/**
	 * Search Start/End
	 */
	public final int SEARCH_START = 0x01, SEARCH_END = 0x00;

	/**
	 * PS Start/End
	 */
	public final int PS_START = 0x01, PS_END = 0x00;
}
