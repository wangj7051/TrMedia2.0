package com.yj.video.version.cj.slc_lc2010_vdc.adapter;

import android.content.Context;

import com.yj.video.version.base.activity.video.BaseVideoUIActivity;

import js.lib.android.adapter.BaseArrayAdapter;

public class BaseVideoAdapter<T> extends BaseArrayAdapter<T> {

    public BaseVideoAdapter(Context context, int resource) {
        super(context, resource);
    }

    protected int getImgResId(String imgResName) {
        if (mContext instanceof BaseVideoUIActivity) {
            BaseVideoUIActivity activity = (BaseVideoUIActivity) mContext;
            return activity.getImgResId(imgResName);
        }
        return 0;
    }
}
