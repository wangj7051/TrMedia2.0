package com.tricheer.player.version.cj.slc_lc2010_vdc.adapter;

import android.content.Context;
import android.widget.ImageView;

import js.lib.android.adapter.BaseArrayAdapter;

public class BaseAudioAdapter<T> extends BaseArrayAdapter<T> {

    public interface CollectListener {
        void onClickCollectBtn(ImageView ivCollect, int pos);
    }

    public BaseAudioAdapter(Context context, int resource) {
        super(context, resource);
    }
}
