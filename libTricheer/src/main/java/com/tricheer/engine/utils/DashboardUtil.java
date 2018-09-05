package com.tricheer.engine.utils;

import android.content.Context;
import android.content.Intent;

import com.tricheer.engine.mcu.radio.BandType;

/**
 * DashBoard Methods
 * 
 * @author Jun.Wang
 */
public class DashboardUtil {
	/**
	 * 
	 * Notify Radio Informations to DashBoard
	 * 
	 * @param context
	 *            : 上下文
	 * 
	 * @param bandType
	 *            : {@link BandType}
	 * 
	 * @param bandAreaType
	 *            : bandAreaType==0 ~ (FM,AM)
	 *            <p>
	 *            bandAreaType==1 ~ (FM1~FM3 / AM1~AM2)}
	 * 
	 * @param objInfos
	 *            :
	 *            <p>
	 *            <li>objInfos[0] openStatus
	 *            <li>objInfos[1] currentFreq
	 *            <li>objInfos[2] currentFreqPosAtList
	 *            <li>objInfos[3] currentFreqStStatus
	 *            <li>objInfos[4] settingsStStatus
	 *            <li>objInfos[5] settingsLocStatus
	 *            </p>
	 */
	public static void notifyRadioInfo(Context context, int bandType, int bandAreaType, Object[] objInfos) {
		if (objInfos != null) {
			String strBandType = "";
			// FM/AM
			if (bandAreaType == 0) {
				switch (bandType) {
				case BandType.FM:
					strBandType = "FM";
					break;
				case BandType.AM:
					strBandType = "AM";
					break;
				}
				// (FM1~FM3 / AM1~AM2)
			} else if (bandAreaType == 1) {
				switch (bandType) {
				case BandType.BAND_FM1:
					strBandType = "FM1";
					break;
				case BandType.BAND_FM2:
					strBandType = "FM2";
					break;
				case BandType.BAND_FM3:
					strBandType = "FM3";
					break;
				case BandType.BAND_AM1:
					strBandType = "AM1";
					break;
				case BandType.BAND_AM2:
					strBandType = "AM2";
					break;
				}
			}

			// 通知仪表盘信息
			Intent dashboardIntent = new Intent("com.tricheer.radio.info");
			// 波段类型
			dashboardIntent.putExtra("bandType", strBandType);

			// 打开状态
			if (objInfos[0] != null) {
				dashboardIntent.putExtra("status", Integer.valueOf(objInfos[0].toString()));
			}
			// 当前频率
			if (objInfos[1] != null) {
				dashboardIntent.putExtra("freq", Integer.valueOf(objInfos[1].toString()));
			}
			// 当前频率在预置频道列表中的位置
			if (objInfos[2] != null) {
				dashboardIntent.putExtra("freqIdx", Integer.valueOf(objInfos[2].toString()));
			}
			// 当前频率ST状态
			if (objInfos[3] != null) {
				dashboardIntent.putExtra("freqSt", Integer.valueOf(objInfos[3].toString()));
			}
			// ST/LOC 设置状态
			if (objInfos[4] != null) {
				dashboardIntent.putExtra("settingSt", Integer.valueOf(objInfos[4].toString()));
			}
			if (objInfos[5] != null) {
				dashboardIntent.putExtra("settingLoc", Integer.valueOf(objInfos[5].toString()));
			}
			context.sendBroadcast(dashboardIntent);
		}
	}
}
