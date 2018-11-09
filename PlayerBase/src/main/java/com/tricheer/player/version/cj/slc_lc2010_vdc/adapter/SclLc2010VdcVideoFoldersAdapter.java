package com.tricheer.player.version.cj.slc_lc2010_vdc.adapter;

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

import com.tricheer.player.R;
import com.tricheer.player.version.cj.slc_lc2010_vdc.bean.VideoFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.adapter.BaseArrayAdapter;
import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.engine.scan.MediaScanService;
import js.lib.android.utils.Logs;

public class SclLc2010VdcVideoFoldersAdapter<T> extends BaseArrayAdapter<T> implements SectionIndexer {
    // TAG
    private final String TAG = "SlcLc2010VdcMusicListAdapter";

    private int mSelectedPos = 0;
    private String mSelectedMediaUrl = "";
    private List<T> mListData;

    public SclLc2010VdcVideoFoldersAdapter(Context context, int resource) {
        super(context, resource);
        this.mResID = R.layout.scl_lc2010_vdc_activity_video_list_item;
    }

    public void setListData(List<T> listData) {
        if (listData != null) {
            this.mListData = new ArrayList<>(listData);
        } else {
            this.mListData = new ArrayList<>();
        }
    }

    public void setSelect(String mediaUrl) {
        this.mSelectedMediaUrl = mediaUrl;
    }

    public void refreshData(int pos) {
        T item = getItem(pos);
        if (item instanceof ProVideo) {
            ProVideo media = (ProVideo) item;
            refreshData(media.mediaUrl);
        }
    }

    public void refreshData(String selectedMediaUrl) {
        setSelect(selectedMediaUrl);
        refreshData();
    }

    public void refreshData(List<T> listData) {
        setListData(listData);
        refreshData();
    }


    public void refreshData(List<T> listData, String selectedMediaUrl) {
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
    public T getItem(int position) {
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
            holder.vOpPlay = (ImageView) convertView.findViewById(R.id.iv_op_play);
            holder.vName = (TextView) convertView.findViewById(R.id.v_name);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //
        T tItem = getItem(position);
        if (tItem != null) {
            if (tItem instanceof ProVideo) {
                holder.vOpPlay.setVisibility(View.VISIBLE);
                //
                ProVideo item = (ProVideo) tItem;
                holder.vName.setText(item.title);

                //Cover
                try {
                    String coverPicFilePath = MediaScanService.getCoverBitmapPath(item, 1);
                    Log.i("coverPicFile", "coverPicFile: " + coverPicFilePath);
                    File coverPicFile = new File(coverPicFilePath);
                    if (coverPicFile.exists()) {
                        holder.vCover.setImageURI(Uri.parse(coverPicFilePath));
                    } else {
                        holder.vCover.setImageResource(R.color.video_item_cover);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Selected
                if (TextUtils.equals(mSelectedMediaUrl, item.mediaUrl)) {
                    mSelectedPos = position;
                    convertView.setBackgroundResource(R.color.video_item_bg);
                    //Not selected
                } else {
                    convertView.setBackgroundResource(android.R.color.transparent);
                }
            } else if (tItem instanceof VideoFilter) {
                holder.vOpPlay.setVisibility(View.INVISIBLE);
                VideoFilter item = (VideoFilter) tItem;
                //Folder Name
                File file = new File(item.folderPath);
                holder.vName.setText(file.getName());
                holder.vCover.setImageResource(R.drawable.bg_cover_video_folder);
                convertView.setBackgroundResource(android.R.color.transparent);
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

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        int position = -1;
        try {
            for (int idx = 0; idx < getCount(); idx++) {
                T item = getItem(idx);
                if (item == null) {
                    continue;
                }

                //
                if (item instanceof ProVideo) {
                    ProVideo media = (ProVideo) item;
                    char firstChar = media.sortLetter.toUpperCase().charAt(0);
                    if (firstChar == sectionIndex) {
                        position = idx;
                        break;
                    }
                } else if (item instanceof VideoFilter) {
                    VideoFilter filter = (VideoFilter) item;
                    char firstChar = filter.sortLetter.toUpperCase().charAt(0);
                    if (firstChar == sectionIndex) {
                        position = idx;
                        break;
                    }
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
        ImageView vCover, vOpPlay;
        TextView vName;
    }
}
