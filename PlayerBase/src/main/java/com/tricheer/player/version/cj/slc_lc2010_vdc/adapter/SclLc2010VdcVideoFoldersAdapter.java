package com.tricheer.player.version.cj.slc_lc2010_vdc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tricheer.player.R;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.utils.PlayerLogicUtils;
import com.tricheer.player.version.cj.slc_lc2010_vdc.bean.VideoFilter;

import java.io.File;
import java.util.List;

import js.lib.android.adapter.BaseArrayAdapter;
import js.lib.android.utils.EmptyUtil;

public class SclLc2010VdcVideoFoldersAdapter<T> extends BaseArrayAdapter<T> implements SectionIndexer {
    // TAG
    private final String TAG = "SlcLc2010VdcMusicListAdapter";

    private int mSelectedPos = 0;
    private String mSelectedMediaUrl = "";
    private List<T> mListDatas;

    public SclLc2010VdcVideoFoldersAdapter(Context context, int resource) {
        super(context, resource);
        this.mResID = R.layout.scl_lc2010_vdc_activity_video_list_item;
    }

    public void setListDatas(List<T> listDatas) {
        this.mListDatas = listDatas;
    }

    public void setSelect(String mediaUrl) {
        this.mSelectedMediaUrl = mediaUrl;
    }

    public void refreshDatas(List<T> listDatas) {
        this.mListDatas = listDatas;
        refreshDatas();
    }

    public void refreshDatas(int pos) {
        T item = getItem(pos);
        if (item != null && item instanceof ProVideo) {
            ProVideo media = (ProVideo) item;
            refreshDatas(media.mediaUrl);
        }
    }

    public void refreshDatas(String selectedMediaUrl) {
        this.mSelectedMediaUrl = selectedMediaUrl;
        refreshDatas();
    }

    public void refreshDatas(List<T> listDatas, String selectMediaUrl) {
        this.mListDatas = listDatas;
        this.mSelectedMediaUrl = selectMediaUrl;
        notifyDataSetChanged();
    }

    public void refreshDatas() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mListDatas == null) {
            return 0;
        }
        return mListDatas.size();
    }

    @Override
    public T getItem(int position) {
        if (EmptyUtil.isEmpty(mListDatas)) {
            return null;
        }
        return mListDatas.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResID, null);
            holder.vCoverBg = convertView.findViewById(R.id.rl_cover);
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
                PlayerLogicUtils.setMediaCover(holder.vCover, item, false);

                //Selected
                if (TextUtils.equals(mSelectedMediaUrl, item.mediaUrl)) {
                    mSelectedPos = position;
                    holder.vCoverBg.setBackgroundResource(R.color.video_item_bg);
                    //Not selected
                } else {
                    holder.vCoverBg.setBackgroundResource(android.R.color.transparent);
                }
            } else if (tItem instanceof VideoFilter) {
                holder.vOpPlay.setVisibility(View.INVISIBLE);
                VideoFilter item = (VideoFilter) tItem;
                //Folder Name
                File file = new File(item.folderPath);
                holder.vName.setText(file.getName());
                holder.vCover.setImageResource(R.drawable.bg_cover_video);
                holder.vCoverBg.setBackgroundResource(android.R.color.transparent);
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
        if (prevPos <= 0) {
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
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    private final class ViewHolder {
        View vCoverBg;
        ImageView vCover, vOpPlay;
        TextView vName;
    }
}
