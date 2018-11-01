package com.tricheer.player.version.cj.slc_lc2010_vdc.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tricheer.player.R;
import com.tricheer.player.utils.PlayerLogicUtils;
import com.tricheer.player.version.cj.slc_lc2010_vdc.bean.AudioFilter;

import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.utils.Logs;

public class SclLc2010VdcAudioAlbumsAdapter<T> extends BaseAudioAdapter<T> implements SectionIndexer {
    // TAG
    private final String TAG = "SlcLc2010VdcMusicListAdapter";

    private int mSelectedPos = 0;
    private String mSelectedMediaUrl = "";
    private List<T> mListData;

    @IdRes
    private int mSelectFontColor, mNotSelectedColor;

    /**
     * {@link CollectListener} object
     */
    private CollectListener mCollectListener;

    public SclLc2010VdcAudioAlbumsAdapter(Context context, int resource) {
        super(context, resource);
        this.mResID = R.layout.scl_lc2010_vdc_activity_audio_list_item;
        mNotSelectedColor = context.getResources().getColor(android.R.color.white);
        mSelectFontColor = context.getResources().getColor(R.color.music_item_selected_font);
    }

    public void setCollectListener(CollectListener l) {
        mCollectListener = l;
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
        if (item instanceof ProAudio) {
            refreshData(((ProAudio) item).mediaUrl);
        } else if (item instanceof AudioFilter) {
            AudioFilter selectedAf = (AudioFilter) item;
            for (T t : mListData) {
                AudioFilter af = (AudioFilter) t;
                af.isSelected = TextUtils.equals(selectedAf.album, af.album);
            }
            refreshData();
        }
    }

    public void refreshData(List<T> listData) {
        setListData(listData);
        refreshData();
    }

    public void refreshData(String selectedMediaUrl) {
        setSelect(selectedMediaUrl);
        refreshData();
    }

    public void refreshData(List<T> listData, String selectedMediaUrl) {
        setSelect(selectedMediaUrl);
        setListData(listData);
        refreshData();
    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    public int getSelectPos() {
        return mSelectedPos;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResID, null);
            holder.ivStart = (ImageView) convertView.findViewById(R.id.iv_start);
            holder.tvItem = (TextView) convertView.findViewById(R.id.tv_desc);
            holder.ivEnd = (ImageView) convertView.findViewById(R.id.iv_end);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //
        T tItem = getItem(position);
        if (tItem != null) {
            //ProAudio
            if (tItem instanceof ProAudio) {
                ProAudio item = (ProAudio) tItem;
                String showName = PlayerLogicUtils.getMediaTitle(mContext, -1, item, true);
                holder.tvItem.setText((position + 1) + "    " + showName);
                if (TextUtils.equals(mSelectedMediaUrl, item.mediaUrl)) {
                    mSelectedPos = position;
                    holder.tvItem.setTextColor(mSelectFontColor);
                    holder.ivStart.setImageResource(R.drawable.icon_item_selected);
                    holder.ivStart.setVisibility(View.VISIBLE);
                    convertView.setBackgroundResource(R.drawable.bg_lv_item_selected);
                } else {
                    holder.tvItem.setTextColor(mNotSelectedColor);
                    holder.ivStart.setVisibility(View.INVISIBLE);
                    convertView.setBackgroundResource(0);
                }

                //Collect
                holder.ivEnd.setVisibility(View.VISIBLE);
                switch (item.isCollected) {
                    case 1:
                        holder.ivEnd.setImageResource(R.drawable.favor_c);
                        break;
                    case 0:
                        holder.ivEnd.setImageResource(R.drawable.favor_c_n);
                        break;
                }
                holder.ivEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCollectListener != null) {
                            mCollectListener.onClickCollectBtn((ImageView) v, position);
                        }
                    }
                });

                //MediaArtist
            } else if (tItem instanceof AudioFilter) {
                AudioFilter item = (AudioFilter) tItem;
                holder.tvItem.setText((position + 1) + "    " + item.album);
                holder.ivEnd.setVisibility(View.INVISIBLE);
                if (item.isSelected) {
                    mSelectedPos = position;
                    holder.ivStart.setImageResource(R.drawable.icon_singer_c);
                    holder.tvItem.setTextColor(mSelectFontColor);
                    convertView.setBackgroundResource(R.drawable.bg_lv_item_selected);
                } else {
                    holder.ivStart.setImageResource(R.drawable.icon_singer);
                    holder.tvItem.setTextColor(mNotSelectedColor);
                    convertView.setBackgroundResource(0);
                }
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
                if (item instanceof ProAudio) {
                    ProAudio media = (ProAudio) item;
                    char firstChar = media.sortLetter.toUpperCase().charAt(0);
                    if (firstChar == sectionIndex) {
                        position = idx;
                        break;
                    }
                } else if (item instanceof AudioFilter) {
                    AudioFilter filter = (AudioFilter) item;
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
        ImageView ivStart;
        TextView tvItem;
        ImageView ivEnd;
    }
}
