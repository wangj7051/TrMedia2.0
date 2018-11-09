package com.tricheer.player.version.cj.slc_lc2010_vdc.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.tricheer.player.version.base.activity.music.BaseAudioUIActivity;

import js.lib.android.adapter.BaseArrayAdapter;

public class BaseAudioAdapter<T> extends BaseArrayAdapter<T> {

    public interface CollectListener {
        void onClickCollectBtn(ImageView ivCollect, int pos);
    }

    public BaseAudioAdapter(Context context, int resource) {
        super(context, resource);
    }

    protected int getImgResId(String imgResName) {
        if (mContext instanceof BaseAudioUIActivity) {
            BaseAudioUIActivity activity = (BaseAudioUIActivity) mContext;
            return activity.getImgResId(imgResName);
        }
        return 0;
    }

}
