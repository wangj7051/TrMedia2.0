package js.lib.utils;

import java.text.DecimalFormat;

/**
 * 数字共同类
 * 
 * @author Jun.Wang
 */
public class DigitUtil {
	// File Size
	private static final long K_BYTES = 1024;
	private static final long M_BYTES = 1024 * K_BYTES;
	private static final long G_BYTES = 1024 * M_BYTES;
	private static final long T_BYTES = 1024 * G_BYTES;

	// Format 1
	public static final String FORMAT_11 = "0.00";
	public static final String FORMAT_12 = "0.0";

	// Format 2
	public static final String FORMAT_21 = "0.0B";
	public static final String FORMAT_22 = "0.0KB";
	public static final String FORMAT_23 = "0.0K";
	public static final String FORMAT_24 = "0.0MB";
	public static final String FORMAT_25 = "0.0M";
	public static final String FORMAT_26 = "0.0GB";
	public static final String FORMAT_27 = "0.0G";
	public static final String FORMAT_28 = "0.0TB";
	public static final String FORMAT_29 = "0.0T";

	/**
	 * Covert double to formatted String
	 */
	public static final String format(double num, String strFormat) {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern(strFormat);
		return df.format(num);
	}

	/**
	 * Convert Byte to KB
	 */
	public static double getKFromByte(long byteSize) {
		return ((double) byteSize) / 1024;
	}

	/**
	 * Convert Byte to MB
	 */
	public static double getMFromByte(long byteSize) {
		return getKFromByte(byteSize) / 1024;
	}

	/**
	 * Convert Byte to GB
	 */
	public static double getGFromByte(long byteSize) {
		return getMFromByte(byteSize) / 1024;
	}

	/**
	 * Convert Byte to TB
	 */
	public static double getTFromByte(long byteSize) {
		return getGFromByte(byteSize) / 1024;
	}

	/**
	 * Convert byte to (B | KB | MB | GB | TB)
	 * 
	 * @param byteSize
	 *            : 字节
	 * @param baseLevel
	 *            : 计算等级， <br/>
	 *            baseLevel=0 表示从B开始判断单位； <br/>
	 *            baseLevel=1 表示从KB开始判断单位；<br/>
	 *            baseLevel=2 表示从MB开始判断单位；
	 * @return String : 格式 "1.5B" / 1.5KB / 1.5MB / 1.5GB / 1.5TB
	 */
	public static String getFormatSizeFromByte(long byteSize, int baseLevel) {
		// B
		if (baseLevel == 0 && byteSize < K_BYTES) {
			return format(byteSize, FORMAT_21);
		}

		// KB
		if (baseLevel <= 1 && byteSize < M_BYTES) {
			return format(getKFromByte(byteSize), FORMAT_22);
		}

		// MB
		if (baseLevel <= 2 && byteSize < G_BYTES) {
			return format(getMFromByte(byteSize), FORMAT_24);
		}

		// GB
		if (byteSize < T_BYTES) {
			return format(getGFromByte(byteSize), FORMAT_26);
		}

		// TB
		return format(getTFromByte(byteSize), FORMAT_28);
	}
}
