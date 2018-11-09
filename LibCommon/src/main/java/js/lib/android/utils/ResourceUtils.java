package js.lib.android.utils;

import android.content.Context;

/**
 * Resource common methods.
 */
public class ResourceUtils {
    /**
     * 获取资源文件的id
     */
    public static int getId(Context context, String resName) {
        return getResId(context, resName, "id");
    }

    /**
     * 获取资源文件string的id
     */
    public static int getStrId(Context context, String resName) {
        return getResId(context, resName, "string");
    }

    /**
     * 获取资源文件drawable的id
     */
    public static int getDrawableId(Context context, String resName) {
        return getResId(context, resName, "drawable");
    }

    /**
     * 获取资源文件color的id
     */
    public static int getColorId(Context context, String resName) {
        return getResId(context, resName, "color");
    }

    /**
     * 获取资源文件layout的id
     */
    public static int getLayoutId(Context context, String resName) {
        return getResId(context, resName, "layout");
    }

    /**
     * 获取资源文件style的id
     */
    public static int getStyleId(Context context, String resName) {
        return getResId(context, resName, "style");
    }

    /**
     * 获取资源文件dimen的id
     */
    public static int getDimenId(Context context, String resName) {
        return getResId(context, resName, "dimen");
    }

    /**
     * 获取资源文件Animation的id
     */
    public static int getAnimId(Context context, String resName) {
        return getResId(context, resName, "anim");
    }

    /**
     * 获取资源文件ID
     *
     * @param context {@link Context}
     * @param resName e.g. "icon_bg.png" -> "icon_bg"
     * @param defType "drawable" ,"String" ...
     * @return Resource ID.
     */
    public static int getResId(Context context, String resName, String defType) {
        return context.getResources().getIdentifier(resName, defType, context.getPackageName());
    }
}
