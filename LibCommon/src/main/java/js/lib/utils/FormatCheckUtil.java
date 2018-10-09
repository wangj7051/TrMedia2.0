package js.lib.utils;

import java.util.regex.Pattern;

/**
 * Format Check Methods
 * 
 * @author Jun.Wang
 */
public class FormatCheckUtil {
	/**
	 * 正则表达式 - 手机号码
	 * <p>
	 * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
	 * </p>
	 * <p>
	 * 联通号码段:130、131、132、136、185、186、145
	 * </p>
	 * <p>
	 * 电信号码段:133、153、180、189
	 * </p>
	 */
	private static final String REGEXP_PHONE = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";

	/**
	 * 正则表达式 - 邮箱
	 */
	private static final String REGEXP_EMAIL = "^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$";

	/**
	 * 检测 手机格式
	 * 
	 * @param srcStr
	 *            : 源字符串, 格式如 "13895676685"
	 * @return boolean : true 表示是正确格式的手机号码
	 */
	public static boolean isCellPhone(String srcStr) {
		return Pattern.compile(REGEXP_PHONE).matcher(srcStr).matches();
	}

	/**
	 * 检测 邮箱格式
	 * 
	 * @param srcStr
	 *            : 源字符串, 格式如 "xxx@163.com"
	 * @return boolean : true 表示是正确格式的邮箱
	 */
	public static boolean isEmail(String srcStr) {
		return Pattern.compile(REGEXP_EMAIL).matcher(srcStr).matches();
	}

	/**
	 * 检测 密码格式
	 * 
	 * @param srcStr
	 *            :源字符串
	 * @param minLen
	 *            :最小长度
	 * @param maxLen
	 *            :最大长度
	 * @return boolean : true 表示是正确格式的密码
	 */
	public static boolean isPwd(String srcStr, int minLen, int maxLen) {
		return srcStr.length() >= minLen && srcStr.length() <= maxLen;
	}
}
