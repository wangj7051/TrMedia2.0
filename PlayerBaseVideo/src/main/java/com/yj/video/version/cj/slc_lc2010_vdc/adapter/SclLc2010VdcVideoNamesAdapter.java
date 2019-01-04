package com.yj.video.version.cj.slc_lc2010_vdc.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yj.video.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.utils.Logs;
import js.lib.android_media.scan.video.controller.VideoScanController;

public class SclLc2010VdcVideoNamesAdapter extends BaseVideoAdapter<ProVideo> implements SectionIndexer {
    // TAG
    private final String TAG = "VideoNamesAdapter";

    private boolean mIsScrolling = false;
    private int mSelectedPos = -1;
    private String mSelectedMediaUrl = "";
    private List<ProVideo> mListData;

    public SclLc2010VdcVideoNamesAdapter(Context context) {
        super(context, 0);
        this.mResID = R.layout.scl_lc2010_vdc_activity_video_list_frag_names_item;
    }

    public void setScrollState(boolean isScrolling) {
        mIsScrolling = isScrolling;
        if (!isScrolling) {
            refreshData();
        }
    }

    private void setListData(List<ProVideo> listData) {
        if (listData != null) {
            this.mListData = new ArrayList<>(listData);
        } else {
            this.mListData = new ArrayList<>();
        }
    }

    public void setSelect(String mediaUrl) {
        this.mSelectedMediaUrl = mediaUrl;
    }

    public void refreshData(List<ProVideo> listData) {
        setListData(listData);
        refreshData();
    }

    public void select(int pos) {
        Log.i(TAG, "select(" + pos + ")");
        mSelectedPos = pos;
        ProVideo item = getItem(pos);
        if (item != null) {
            refreshData(item.mediaUrl);
        }
    }

    public void refreshData(String selectedMediaUrl) {
        this.mSelectedMediaUrl = selectedMediaUrl;
        setSelect(selectedMediaUrl);
        refreshData();
    }

    public void refreshData(List<ProVideo> listData, String selectedMediaUrl) {
        setSelect(selectedMediaUrl);
        setListData(listData);
        notifyDataSetChanged();
    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mListData == null) {
            return 0;
        }
        return mListData.size();
    }

    @Override
    public ProVideo getItem(int position) {
        try {
            return mListData.get(position);
        } catch (Exception e) {
            return null;
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResID, null);
            holder.vCover = (ImageView) convertView.findViewById(R.id.v_cover);
            holder.vName = (TextView) convertView.findViewById(R.id.v_name);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //
        ProVideo item = getItem(position);
        if (item != null) {
            holder.vName.setText(item.title);
            //Set video image resource
            //Cover
            if (!mIsScrolling) {
                try {
                    String coverPicFilePath = VideoScanController.getCoverBitmapPath(item);
                    Log.i("coverAdapter", "coverPicFile: " + coverPicFilePath);
                    File coverPicFile = new File(coverPicFilePath);
                    if (coverPicFile.exists()) {
                        holder.vCover.setImageURI(Uri.parse(coverPicFilePath));
                    } else {
                        holder.vCover.setImageResource(R.color.video_item_cover);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Selected
            if (TextUtils.equals(mSelectedMediaUrl, item.mediaUrl)) {
                convertView.setBackgroundResource(getColorResId("video_item_bg"));
                //Not selected
            } else {
                convertView.setBackgroundResource(0);
            }
        }

        return convertView;
    }

    public int getNextPos() {
        int loop = getCount();
        int nextPos = mSelectedPos + 1;
        if (nextPos >= loop) {
            nextPos = 0;
        }
        return nextPos;
    }

    public int getPrevPos() {
        int prevPos = mSelectedPos - 1;
        if (prevPos < 0) {
            prevPos = getCount() - 1;
        }
        return prevPos;
    }

    public int getSelectPos() {
        return mSelectedPos;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        int position = -1;
        try {
            int loop = getCount();
            for (int idx = 0; idx < loop; idx++) {
                ProVideo item = getItem(idx);
                if (item == null) {
                    continue;
                }

                //
                char firstChar = item.sortLetter.toUpperCase().charAt(0);
                if (firstChar == sectionIndex) {
                    position = idx;
                    break;
                }
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getPositionForSection()", e);
        }
        return position;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    private final class ViewHolder {
        ImageView vCover;
        TextView vName;
    }
}
