package js.lib.android.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * SIM 卡工具类
 * <p>
 * 需要权限 : &lt;uses-permission android:name="android.permission.READ_PHONE_STATE"/&gt;
 * </p>
 * 
 * @author Jun.Wang
 */
public class SIMUtil {
	/**
	 * 运营商
	 */
	public interface SIMProvider {
		/**
		 * 未知
		 */
		public final int NA = -1;
		/**
		 * 中国移动
		 */
		public final int CHINA_MOBILE = 1;
		/**
		 * 中国联通
		 */
		public final int CHINA_UNION = 2;
		/**
		 * 中国电信
		 */
		public final int CHINA_TELECOM = 3;
	}

	/**
	 * 获取SIM卡 IMSI(International Mobile Subscriber Identification Number, 即: 国际移动用户识别码)
	 * <p>
	 * (1)是区别移动用户的标志 ， 储存在SIM卡中，可用于区别移动用户的有效信息, IMSI由MCC、MNC、MSIN组成.
	 * 
	 * 其中MCC为移动国家号码，由3位数字组成， 唯一地识别移动客户所属的国家，我国为460;
	 * 
	 * MNC为网络id，由2位数字组成， 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；
	 * 
	 * MSIN为移动客户识别码，采用等长11位数字构成。
	 * </p>
	 */
	public static String getIMSI(Context cxt) {
		TelephonyManager telManager = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
		return telManager.getSubscriberId();
	}

	/**
	 * 获取运营商
	 * 
	 * @param imsi
	 *            : {@link SIMUtil#getIMSI}
	 * 
	 * @return {@link SIMProvider}
	 */
	public static int getSIMProvider(String imsi) {
		// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号 //中国移动
		if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
			return SIMProvider.CHINA_MOBILE;
			// 中国联通
		} else if (imsi.startsWith("46001")) {
			return SIMProvider.CHINA_UNION;
			// 中国电信
		} else if (imsi.startsWith("46003")) {
			return SIMProvider.CHINA_TELECOM;
		}
		return SIMProvider.NA;
	}

	/**
	 * 返回的IMEI / MEID的设备
	 * <p>
	 * 如果该设备是GSM设备 返回IMEI
	 * </p>
	 * <p>
	 * 如果该设备是一个CDMA设备, 返回MEID.
	 * </p>
	 */
	public String getIMEI(Context cxt) {
		TelephonyManager telManager = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
		return telManager.getDeviceId();
	}

	/**
	 * 返回SIM卡的序列号
	 */
	public String getICCID(Context cxt) {
		TelephonyManager telManager = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
		return telManager.getSimSerialNumber();
	}

	/**
	 * 获取电话号码
	 * <p>
	 * Returns the phone number string for line 1, for example, the MSISDN for a GSM phone. Return
	 * null if it is unavailable.
	 * </p>
	 */
	public String getPhoneNumber(Context cxt) {
		TelephonyManager telManager = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
		return telManager.getLine1Number();
	}
}
