package js.lib.android.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Screen Informations
 *
 * @author Jun.Wang
 */
public class ScreenInfoUtil {
    /**
     * Constructor
     */
    private ScreenInfoUtil() {
    }

    /**
     * DisplayMetrics 对象 <br/>
     * 包含了分辨率相关信息
     */
    private static DisplayMetrics displayMetrics;

    /**
     * 屏幕密度
     */
    public static float density;
    /**
     * 字体缩放比例
     */
    public static float scaledDensity;

    /**
     * 屏幕密度表示为每英寸点数
     */
    public static int densityDpi;
    /**
     * 屏幕密度表示为字符串
     * <p>
     * {@link DensityStr}
     */
    public static String densityStr = "";

    public interface DensityStr {
        /**
         * drawable-ldpi (dpi=120, density=0.75)
         */
        public String LDPI = "ldpi";
        /**
         * drawable-mdpi (dpi=160, density=1)
         */
        public String MDPI = "mdpi";
        /**
         * drawable-hdpi (dpi=240, density=1.5)
         */
        public String HDPI = "hdpi";
        /**
         * drawable-xhdpi (dpi=320, density=2)
         */
        public String XHDPI = "xhdpi";
        /**
         * drawable-xxhdpi (dpi=480, density=3)
         */
        public String XXHDPI = "xxhdpi";
    }

    /**
     * 屏宽 / 屏高
     */
    public static int width, height;

    /**
     * Initialize
     */
    public static void init(Context context) {
        displayMetrics = context.getResources().getDisplayMetrics();

        density = displayMetrics.density;
        scaledDensity = displayMetrics.scaledDensity;

        densityDpi = displayMetrics.densityDpi;
        densityStr = getDensityStr(densityDpi);

        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    /**
     * 获取当前手机DPI的字符串表述
     *
     * @param densityDPI : 分辨率DPI,即"屏幕密度表示为每英寸点数"
     * @return {@link DensityStr}
     */
    private static String getDensityStr(int densityDPI) {

        String strDpi = "";

        if (densityDPI <= 120) {
            strDpi = "LDPI";

        } else if (densityDPI <= 160) {
            strDpi = "MDPI";

        } else if (densityDPI <= 240) {
            strDpi = "HDPI";

        } else if (densityDPI <= 320) {
            strDpi = "XHDPI";

        } else if (densityDPI <= 480) {
            strDpi = "XXHDPI";
        }

        return strDpi;
    }
}
