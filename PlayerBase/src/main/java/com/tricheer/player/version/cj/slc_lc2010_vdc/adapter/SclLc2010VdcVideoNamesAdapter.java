package com.tricheer.player.version.cj.slc_lc2010_vdc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tricheer.player.R;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.utils.PlayerLogicUtils;

import java.util.List;

import js.lib.android.adapter.BaseArrayAdapter;
import js.lib.android.utils.EmptyUtil;

public class SclLc2010VdcVideoNamesAdapter extends BaseArrayAdapter<ProVideo> implements SectionIndexer {
    // TAG
    private final String TAG = "SlcLc2010VdcMusicListAdapter";

    private int mSelectedPos = 0;
    private String mSelectedMediaUrl = "";
    private List<ProVideo> mListDatas;

    public SclLc2010VdcVideoNamesAdapter(Context context, int resource) {
        super(context, resource);
        this.mResID = R.layout.scl_lc2010_vdc_activity_video_list_item;
    }

    public void setListDatas(List<ProVideo> listDatas) {
        this.mListDatas = listDatas;
    }

    public void setSelect(String mediaUrl) {
        this.mSelectedMediaUrl = mediaUrl;
    }

    public void refreshDatas(List<ProVideo> listDatas) {
        this.mListDatas = listDatas;
        refreshDatas();
    }

    public void refreshDatas(int pos) {
        ProVideo item = getItem(pos);
        if (item != null) {
            refreshDatas(item.mediaUrl);
        }
    }

    public void refreshDatas(String selectedMediaUrl) {
        this.mSelectedMediaUrl = selectedMediaUrl;
        refreshDatas();
    }

    public void refreshDatas(List<ProVideo> listDatas, String selectMediaUrl) {
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
    public ProVideo getItem(int position) {
        if (EmptyUtil.isEmpty(mListDatas)) {
            return null;
        }
        return mListDatas.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResID, null);
            holder.vCoverBg = convertView.findViewById(R.id.rl_cover);
            holder.vCover = (ImageView) convertView.findViewById(R.id.v_cover);
            holder.vName = (TextView) convertView.findViewById(R.id.v_name);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //
        ProVideo item = getItem(position);
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
        ImageView vCover;
        TextView vName;
    }
}
