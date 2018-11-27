package com.yj.video.version.cj.slc_lc2010_vdc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yj.video.R;
import com.yj.video.version.cj.slc_lc2010_vdc.bean.VideoFilter;

import java.io.File;

public class SclLc2010VdcVideoFoldersAdapter extends BaseVideoFolderFoldersAdapter {
    // TAG
    private final String TAG = "VideoFoldersAdapter";

    public SclLc2010VdcVideoFoldersAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    protected void setGroupTypeIcon(@NonNull ImageView ivGroup, boolean isPlayingPos) {
        Log.i(TAG, "setGroupTypeIcon(ivGroup," + isPlayingPos + ")");
        if (isPlayingPos) {
            ivGroup.setImageResource(R.drawable.icon_folder_c);
        } else {
            ivGroup.setImageResource(R.drawable.icon_video);
        }
    }

    @Override
    protected void setGroupDesc(@NonNull VideoFilter videoFilter, @NonNull TextView tvDesc) {
        Log.i(TAG, "setGroupTypeIcon(@NonNull VideoFilter videoFilter, @NonNull TextView tvDesc)");
        try {
            String folderPath = videoFilter.folderPath;
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
