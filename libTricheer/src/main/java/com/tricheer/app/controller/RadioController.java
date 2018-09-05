package com.tricheer.app.controller;

import js.lib.android.utils.AudioFocusUtil.AudioFocusListener;
import android.content.Context;

import com.tricheer.engine.mcu.radio.BandCateory;
import com.tricheer.engine.mcu.radio.BandType;
import com.tricheer.engine.mcu.radio.RadioStatus;

/**
 * Controller Implement - Radio
 * 
 * @author Jun.Wang
 */
public class RadioController extends Controller {

	public RadioController(Context context) {
		super(context);
	}

	@Override
	public void addAudioFocusControl(AudioFocusListener l) {
		super.addAudioFocusControl(l);
		mAudioFocusUtil.setParameters("kill_others=true");
	}

	@Override
	public void setControlEnable(boolean isEnable) {
		if (mMCUReqUtil != null) {
			mMCUResUtil.registerMCUInfoReceiver(isEnable);
		}
		if (mBtUtil != null) {
			mBtUtil.registerBtStatusReceiver(isEnable);
		}
		if (mSysUtil != null) {
			mSysUtil.registerSystemStatusReceiver(isEnable);
		}
		if (mSysMediaControlUtil != null) {
			mSysMediaControlUtil.registerMediaControlReceiver(isEnable);
		}
	}

	/**
	 * Open Radio
	 */
	public void openRadio() {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.openRadio();
		}
	}

	/**
	 * Get Radio Status
	 */
	public void getRadioStatus() {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.getRadioStatus();
		}
	}

	/**
	 * Switch Band
	 * 
	 * @param band
	 *            : {@link BandType}
	 */
	public void switchBand(int band) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.switchBand(band);
		}
	}

	/**
	 * Scan previous Frequency And Play
	 * 
	 * @param bandType
	 *            : {@link BandCateory}
	 */
	public void scanAndPlayPrevFreq(int bandCatetory) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.scanAndPlayPrevFreq(bandCatetory);
		}
	}

	/**
	 * Scan next Frequency And Play
	 * 
	 * @param bandType
	 *            : {@link BandCateory}
	 */
	public void scanAndPlayNextFreq(int bandCatetory) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.scanAndPlayNextFreq(bandCatetory);
		}
	}

	/**
	 * EXEC Auto Search
	 * 
	 * @param bandType
	 *            : {@link BandCateory}
	 */
	public void search(int bandCatetory) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.search(bandCatetory);
		}
	}

	/**
	 * EXEC Step -0.1 and Play
	 * 
	 * @param bandType
	 *            : {@link BandCateory}
	 */
	public void stepAndPlayPrev(int bandCatetory) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.stepAndPlayPrev(bandCatetory);
		}
	}

	/**
	 * EXEC Step +0.1 and Next
	 * 
	 * @param bandType
	 *            : {@link BandCateory}
	 */
	public void stepAndPlayNext(int bandCatetory) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.stepAndPlayNext(bandCatetory);
		}
	}

	/**
	 * PS
	 * 
	 * @param bandType
	 *            : {@link BandCateory}
	 */
	public void ps(int bandCatetory) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.ps(bandCatetory);
		}
	}

	/**
	 * Set Settings ST Status
	 * 
	 * @param stStatus
	 *            : {@link RadioStatus} ST_*
	 */
	public void setStStatus(int stStatus) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.setStStatus(stStatus);
		}
	}

	/**
	 * Set Settings LOC Status
	 * 
	 * @param stStatus
	 *            : {@link RadioStatus} LOC_*
	 */
	public void setLocStatus(int locStatus) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.setLocStatus(locStatus);
		}
	}

	/**
	 * Set Volume Status
	 * 
	 * @param volStatus
	 *            : {@link RadioStatus} VOL_*
	 */
	public void setVolStatus(int volStatus) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.setVolStatus(volStatus);
		}
	}

	/**
	 * Save current frequency to selected position
	 */
	public void saveCurrFreq(int position) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.saveCurrFreq(position);
		}
	}

	/**
	 * Play Select Position
	 */
	public void playSelectedPos(int selectPos) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.playSelectedPos(selectPos);
		}
	}

	/**
	 * Play Selected Frequency
	 * 
	 * @param bandType
	 *            : {@link BandType}
	 */
	public void playSelectFreq(int bandType, int freq) {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.playSelectFreq(bandType, freq);
		}
	}

	/**
	 * Close Radio
	 */
	public void closeRadio() {
		if (mMCUReqUtil != null) {
			mMCUReqUtil.closeRadio();
		}
	}

	/**
	 * Get Band Category
	 * 
	 * @return {@link BandCateory}
	 */
	public int getBandCategory() {
		if (mMCUResUtil != null) {
			mMCUResUtil.getBandCategory();
		}
		return BandCateory.NONE;
	}
}