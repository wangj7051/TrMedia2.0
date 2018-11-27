package com.yj.audio.engine;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.tri.lib.utils.SettingsSysUtil;

import js.lib.android.utils.ResourceUtils;

public class ThemeController {
    //TAG
    private static final String TAG = "ThemeController";

    /**
     * {@link Context}
     */
    private Context mContext;

    /**
     * {@link SettingsThemeChangeContentObserver} object.
     */
    private SettingsThemeChangeContentObserver mThemeObserver;

    /**
     * 当前主题字符串
     * <p>"" - Android</p>
     * <p>"ios_" - Android</p>
     */
    private String mCurrThemeStr = "";

    /**
     * {@link ThemeChangeDelegate} object
     */
    private ThemeChangeDelegate mThemeChangeDelegate;

    public interface ThemeChangeDelegate {
        void updateThemeToIos();

        void updateThemeToDefault();
    }

    public ThemeController(Context context, Handler handler) {
        mContext = context;
        registerUiObserver(handler);
    }

    private void registerUiObserver(Handler handler) {
        try {
            Uri themeSettingsUri = Settings.System.getUriFor("theme_setting");
            mThemeObserver = new SettingsThemeChangeContentObserver(handler);
            mContext.getContentResolver().registerContentObserver(themeSettingsUri, true, mThemeObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SettingsThemeChangeContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        SettingsThemeChangeContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.i(TAG, "onChange(" + selfChange + ")");
            checkAndUpdateTheme();
        }
    }

    public void destroy() {
        try {
            mThemeChangeDelegate = null;
            mContext.getContentResolver().unregisterContentObserver(mThemeObserver);
            mThemeObserver = null;
            mContext = null;
            mCurrThemeStr = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkAndUpdateTheme() {
        String newThemeStr = getThemeStr();
        if (!TextUtils.equals(mCurrThemeStr, newThemeStr)) {
            mCurrThemeStr = newThemeStr;
            if ("ios_".equals(mCurrThemeStr)) {
                if (mThemeChangeDelegate != null) {
                    mThemeChangeDelegate.updateThemeToIos();
                }
            } else {
                if (mThemeChangeDelegate != null) {
                    mThemeChangeDelegate.updateThemeToDefault();
                }
            }
        }
    }

    private String getThemeStr() {
        int themeVal = SettingsSysUtil.getThemeVal(mContext);
        Log.i(TAG, "getThemeStr() - themeVal:" + themeVal + ")");
        switch (themeVal) {
            case 1:
                return "ios_";
            case 0:
            default:
                return "";
        }
    }

    public void addCallback(ThemeChangeDelegate delegate) {
        mThemeChangeDelegate = delegate;
    }

    public int getImgResId(String imgResName) {
        String targetImgResName = mCurrThemeStr + imgResName;
        return ResourceUtils.getDrawableId(mContext, targetImgResName);
    }
}
