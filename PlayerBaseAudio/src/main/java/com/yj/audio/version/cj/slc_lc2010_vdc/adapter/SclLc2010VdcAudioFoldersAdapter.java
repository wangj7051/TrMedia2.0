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

import java.io.File;

/**
 * Audio groups list adapter - [folders]
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioFoldersAdapter extends BaseAudioGroupsAdapter {
    // TAG
    private final String TAG = "AudioFoldersAdapter";

    public SclLc2010VdcAudioFoldersAdapter(Context context) {
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
            ivGroup.setImageResource(R.drawable.icon_folder_c);
        } else {
            ivGroup.setImageResource(R.drawable.icon_folder);
        }
    }

    @Override
    protected void setGroupDesc(@NonNull AudioFilter audioFilter, @NonNull TextView tvDesc) {
        Log.i(TAG, "setGroupTypeIcon(@NonNull AudioFilter audioFilter, @NonNull TextView tvDesc)");
        try {
            String folderPath = audioFilter.folderPath;
            Log.i(TAG, "folderPath:|" + folderPath + "|");
            File folder = new File(folderPath);
            File parentFolder = folder.getParentFile();
            //根目录显示为"/"
            if ("storage".equals(parentFolder.getName()) && folder.getName().startsWith("udisk")) {
                tvDesc.setText("/");
                //显示真实的名称
            } else {
                tvDesc.setText(folder.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
