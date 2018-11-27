package js.lib.android.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.text.TextUtils;

/**
 * Empty Check Common methods
 * 
 * @author Jun.Wang
 */
public class EmptyUtil {
	/**
	 * Check whether Object is NUll
	 */
	public static boolean isNull(Object obj) {
		return (obj == null);
	}

	/**
	 * Check whether [the String Object] is empty
	 * <p>
	 * Like : null，" "，""
	 */
	public static boolean isEmpty(String str) {
		if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * Check whether [the List Object] is empty
	 * 
	 * @param <T>
	 */
	public static <T> boolean isEmpty(List<T> list) {
		if (list == null || list.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Check whether [the Map Object] is empty
	 * 
	 * @param <K>
	 * @param <V>
	 */
	public static <K, V> boolean isEmpty(Map<K, V> map) {
		if (map == null || map.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Check whether [the Set Object] is empty
	 * 
	 * @param <K>
	 * @param <V>
	 */
	public static <T> boolean isEmpty(Set<T> set) {
		if (set == null || set.size() == 0) {
			return true;
		}
		return false;
	}


}
