package com.tricheer.engine.mcu;

import java.util.List;

import js.lib.android.utils.Logs;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tricheer.engine.mcu.MCUConsts.HandBrakeStatus;
import com.tricheer.engine.mcu.bean.MCUDirection;
import com.tricheer.engine.mcu.bean.MCURadioInfos;
import com.tricheer.engine.mcu.bean.MCURadioSettings;
import com.tricheer.engine.mcu.bean.MCURoot;
import com.tricheer.engine.mcu.keys.KeyPressFlags;
import com.tricheer.engine.mcu.keys.MappingKeys;
import com.tricheer.engine.mcu.radio.BandCateory;
import com.tricheer.engine.mcu.radio.RadioUtil;

/**
 * MCU Response Methods
 * 
 * @author Jun.Wang
 */
public class MCUResUtil {
	// TAG
	private String TAG = "MCUResUtil";
	private Context mContext;

	/**
	 * 波段种类
	 * <p>
	 * {@link BandCateory}
	 */
	private int mBandCategory = BandCateory.NONE;

	/**
	 * Parse MCU Response Listener
	 */
	private ParseMcuRespListener mParseRespListener;

	public interface ParseMcuRespListener {
		/**
		 * MCU 映射键控制指令
		 * 
		 * @param mappingKey
		 *            : {@link MappingKeys}
		 * 
		 * @param pressFlag
		 *            : {@link KeyPressFlags}
		 */
		public void respParsedMappingKey(byte mappingKey, byte pressFlag);

		/**
		 * 手刹状态
		 * 
		 * @param status
		 *            : {@link HandBrakeStatus}
		 * @param flag
		 *            : 0 - CANBOX手刹状态响应
		 *            <p>
		 *            1 - 制动手刹状态响应
		 */
		public void respParsedHandBrakeStatus(int status, int flag);
	}

	/**
	 * Parse Radio Response Listener
	 */
	private ParseRadioRespListener mParseRadioRespListener;

	public interface ParseRadioRespListener extends ParseMcuRespListener {
		/**
		 * @param listFreqs
		 *            : List<Integer>
		 */
		public void respParsedFreqs(List<Integer> listFreqs);

		/**
		 * Settings Status
		 * 
		 * @param settings
		 *            : {@link MCURadioSettings}
		 */
		public void respParsedRadioSettings(MCURadioSettings settings);

		/**
		 * Current Frequency ST Status
		 * 
		 * @param stStatus
		 *            : {@link MCUConsts.RadioStatus} - ST_*
		 */
		public void respParsedCurrFreqStStatus(int stStatus);

		/**
		 * Radio Informations
		 * 
		 * @param radioInfos
		 *            : {@link MCURadioInfos}
		 */
		public void respParsedRadioInfos(MCURadioInfos radioInfos);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            : 上下文
	 * @param l
	 *            : {@link ParseMcuRespListener}
	 */
	public MCUResUtil(Context context, ParseMcuRespListener l) {
		mContext = context;
		mParseRespListener = l;
		if (l instanceof ParseRadioRespListener) {
			mParseRadioRespListener = (ParseRadioRespListener) l;
		}
	}

	public void setLogTAG(String logTAG) {
		TAG += logTAG;
	}

	// MCU Information Receiver
	private BroadcastReceiver mMCUInfoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Logs.i(TAG, "action : " + action);

			// HandBrakeStatus Response from CANBOX
			if (MCUResCmds.ACTION_HANDBRAKE_STATUS.equals(action)) {
				respHandBrakeStatus(intent.getIntExtra("brakeStatus", 0), 0);

				// Frequency List
			} else if (MCUResCmds.ACTION_FREQLIST.equals(action)) {
				respListFreqs(intent.getIntegerArrayListExtra("freqs"));

				// Radio Information
			} else if (MCUResCmds.ACTION_COMEVENT.equals(action)) {
				MCURoot mcur = MCUResParser.parseRoot(intent.getByteArrayExtra("resp"));
				switch (mcur.cmd) {
				// Settings Status
				case MCUResCmds.SETTING_STATUS:
					respSettingsStatus(MCUResParser.parseRadioSettings(mcur.byteArr));
					break;

				// Current Frequency ST Status
				case MCUResCmds.CURR_FREQ_ST_STATUS:
					respCurrFreqStStatus(MCUResParser.parseRadioFreqStStatus(mcur.byteArr));
					break;

				// Radio Informations
				case MCUResCmds.RADIO_INFOS:
					respRadioInfos(MCUResParser.parseRadioInfos(mcur.byteArr));
					break;

				// Direction
				case MCUResCmds.DIRECTION:
					MCUDirection mcud = MCUResParser.parseDirection(mcur.byteArr);
					if (mcud.innerCMD == MCUResCmds.DIRECTION_INNER_CMD) {
						respMappingKey(mcud.mappingKey, mcud.pressFlag);
					}
					break;

				// Radio Informations
				case MCUResCmds.CAR_BRAKE_STATUS:
					respHandBrakeStatus(MCUResParser.parseHandBrakeStatus(mcur.byteArr), 1);
					break;
				}
			}
		}

		private void respListFreqs(List<Integer> listFreqs) {
			if (mParseRadioRespListener != null) {
				mParseRadioRespListener.respParsedFreqs(listFreqs);
			}
		}

		private void respSettingsStatus(MCURadioSettings settings) {
			if (mParseRadioRespListener != null) {
				mParseRadioRespListener.respParsedRadioSettings(settings);
			}
		}

		private void respCurrFreqStStatus(int stStatus) {
			if (mParseRadioRespListener != null) {
				mParseRadioRespListener.respParsedCurrFreqStStatus(stStatus);
			}
		}

		private void respRadioInfos(MCURadioInfos radioInfos) {
			if (mParseRadioRespListener != null) {
				if (RadioUtil.isFMBand(radioInfos.bandType)) {
					mBandCategory = BandCateory.FM;
				} else if (RadioUtil.isAMBand(radioInfos.bandType)) {
					mBandCategory = BandCateory.AM;
				}
				mParseRadioRespListener.respParsedRadioInfos(radioInfos);
			}
		}

		private void respMappingKey(byte mappingKey, byte pressFlag) {
			if (mParseRespListener != null) {
				mParseRespListener.respParsedMappingKey(mappingKey, pressFlag);
			}
		}

		private void respHandBrakeStatus(int status, int flag) {
			if (mParseRespListener != null) {
				mParseRespListener.respParsedHandBrakeStatus(status, flag);
			}
		}
	};

	/**
	 * Register MCU Information Receiver
	 */
	public void registerMCUInfoReceiver(boolean isReg) {
		try {
			if (isReg) {
				IntentFilter ifRadioInfo = new IntentFilter();
				ifRadioInfo.addAction(MCUResCmds.ACTION_FREQLIST);
				ifRadioInfo.addAction(MCUResCmds.ACTION_COMEVENT);
				ifRadioInfo.addAction(MCUResCmds.ACTION_HANDBRAKE_STATUS);
				mContext.registerReceiver(mMCUInfoReceiver, ifRadioInfo);
			} else {
				mContext.unregisterReceiver(mMCUInfoReceiver);
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "registerRadioReceiver(" + isReg + ")", e);
		}
	}

	/**
	 * Get Band Category
	 * 
	 * @return {@link BandCateory}
	 */
	public int getBandCategory() {
		return mBandCategory;
	}
}
