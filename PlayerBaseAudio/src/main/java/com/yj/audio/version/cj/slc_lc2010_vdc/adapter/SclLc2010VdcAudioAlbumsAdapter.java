package com.yj.audio.version.cj.slc_lc2010_vdc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yj.audio.R;
import com.yj.audio.version.cj.slc_lc2010_vdc.bean.AudioFilter;

import js.lib.android.media.bean.MediaBase;

/**
 * Audio groups list adapter - [Album]
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioAlbumsAdapter extends BaseAudioGroupsAdapter {
    // TAG
    private final String TAG = "AudioAlbumsAdapter";

    public SclLc2010VdcAudioAlbumsAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Log.i(TAG, "getView(" + position + ",convertView,parent)");
        return super.getView(position, convertView, parent);
    }

    @Override
    protected void setGroupTypeIcon(@NonNull ImageView ivGroup, boolean isPlayingPos) {
        Log.i(TAG, "setGroupTypeIcon(ivGroup," + isPlayingPos + ")");
        if (isPlayingPos) {
            ivGroup.setImageResource(R.drawable.icon_album_c);
        } else {
            ivGroup.setImageResource(R.drawable.icon_album);
        }
    }

    @Override
    protected void setGroupDesc(@NonNull AudioFilter audioFilter, @NonNull TextView tvDesc) {
        Log.i(TAG, "setGroupTypeIcon(@NonNull AudioFilter audioFilter, @NonNull TextView tvDesc)");
        if (MediaBase.UNKNOWN.equals(audioFilter.album)) {
            tvDesc.setText(R.string.unknown_album);
        } else {
            tvDesc.setText(audioFilter.album);
        }
    }
}
