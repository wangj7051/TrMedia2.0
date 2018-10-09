package js.lib.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密
 * 
 * @author Update by Jun.Wang
 */
public class MD5Util {
	//
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * Encrypt String
	 * 
	 * @param srcStr
	 *            : Source string
	 * @return String : MD5 String
	 */
	public static String encrypt(String srcStr) {
		String resStr = "";
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(srcStr.getBytes());
			byte messageDigest[] = digest.digest();
			resStr = toHexString(messageDigest);

			// 表示没有该算法
		} catch (NoSuchAlgorithmException e) {
			resStr = "";
		}

		return resStr;
	}

	private static String toHexString(byte[] b) {
		if (b == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}
}
