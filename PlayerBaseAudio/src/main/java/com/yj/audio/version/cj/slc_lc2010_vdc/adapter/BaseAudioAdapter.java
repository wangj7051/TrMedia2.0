package com.yj.audio.version.cj.slc_lc2010_vdc.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.yj.audio.version.base.activity.music.BaseUIActivity;

import js.lib.android.adapter.BaseArrayAdapter;

public class BaseAudioAdapter<T> extends BaseArrayAdapter<T> {

    /**
     * Used to listener your collect operate
     */
    public interface CollectListener {
        void onClickCollectBtn(ImageView ivCollect, int pos);
    }

    public BaseAudioAdapter(Context context, int resource) {
        super(context, resource);
    }

    protected int getImgResId(String imgResName) {
        if (mContext instanceof BaseUIActivity) {
            BaseUIActivity activity = (BaseUIActivity) mContext;
            return activity.getImgResId(imgResName);
        }
        return 0;
    }

}
