package com.tricheer.player.version.cj.slc_lc2010_vdc.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tricheer.player.R;
import com.tricheer.player.bean.ProMusic;

import java.util.List;

import js.lib.android.adapter.BaseArrayAdapter;
import js.lib.android.utils.EmptyUtil;

public class SclLc2010VdcAudioNamesAdapter extends BaseArrayAdapter<ProMusic> implements SectionIndexer {
    // TAG
    private final String TAG = "SlcLc2010VdcMusicListAdapter";

    private int mSelectedPos = 0;
    private String mSelectedMediaUrl = "";
    private List<ProMusic> mListDatas;

    @IdRes
    private int mSelectFontColor, mNotSelectedColor;

    /**
     * {@link CollectListener} object
     */
    private CollectListener mCollectListener;

    public interface CollectListener {
        void onClickCollectBtn(ImageView ivCollect, int pos);
    }

    public SclLc2010VdcAudioNamesAdapter(Context context, int resource) {
        super(context, resource);
        this.mResID = R.layout.scl_lc2010_vdc_activity_audio_list_item;
        mNotSelectedColor = context.getResources().getColor(android.R.color.white);
        mSelectFontColor = context.getResources().getColor(R.color.music_item_selected);
    }

    public void setCollectListener(CollectListener l) {
        mCollectListener = l;
    }

    public void setListDatas(List<ProMusic> listDatas) {
        this.mListDatas = listDatas;
    }

    public void setSelect(String mediaUrl) {
        this.mSelectedMediaUrl = mediaUrl;
    }

    public void refreshDatas(int pos) {
        ProMusic item = getItem(pos);
        if (item != null) {
            refreshDatas(item.mediaUrl);
        }
    }

    public void refreshDatas(List<ProMusic> listDatas) {
        this.mListDatas = listDatas;
        refreshDatas();
    }

    public void refreshDatas(String selectedMediaUrl) {
        this.mSelectedMediaUrl = selectedMediaUrl;
        refreshDatas();
    }

    public void refreshDatas(List<ProMusic> listDatas, String selectedMediaUrl) {
        this.mListDatas = listDatas;
        this.mSelectedMediaUrl = selectedMediaUrl;
        refreshDatas();
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
    public ProMusic getItem(int position) {
        if (EmptyUtil.isEmpty(mListDatas)) {
            return null;
        }
        return mListDatas.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResID, null);
            holder.ivSelected = (ImageView) convertView.findViewById(R.id.iv_start);
            holder.tvSongName = (TextView) convertView.findViewById(R.id.tv_item);
            holder.ivCollect = (ImageView) convertView.findViewById(R.id.iv_end);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //
        ProMusic item = getItem(position);
        holder.tvSongName.setText((position + 1) + "    " + item.title);

        if (TextUtils.equals(mSelectedMediaUrl, item.mediaUrl)) {
            mSelectedPos = position;
            holder.tvSongName.setTextColor(mSelectFontColor);
            holder.ivSelected.setVisibility(View.VISIBLE);
            convertView.setBackgroundResource(R.drawable.bg_lv_item_selected);
        } else {
            holder.tvSongName.setTextColor(mNotSelectedColor);
            holder.ivSelected.setVisibility(View.INVISIBLE);
            convertView.setBackgroundResource(0);
        }

        //Collect
        switch (item.isCollected) {
            case 1:
                holder.ivCollect.setImageResource(R.drawable.favor_c);
                break;
            case 0:
                holder.ivCollect.setImageResource(R.drawable.favor_c_n);
                break;
        }
        holder.ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCollectListener != null) {
                    mCollectListener.onClickCollectBtn((ImageView) v, position);
                }
            }
        });

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
        ImageView ivSelected;
        TextView tvSongName;
        ImageView ivCollect;
    }
}
