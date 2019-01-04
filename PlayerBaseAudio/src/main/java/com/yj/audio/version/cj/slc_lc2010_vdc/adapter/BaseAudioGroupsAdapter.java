package com.yj.audio.version.cj.slc_lc2010_vdc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yj.audio.R;
import com.yj.audio.utils.PlayerLogicUtils;
import com.yj.audio.version.cj.slc_lc2010_vdc.bean.AudioFilter;

import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.utils.Logs;

/**
 * Audio groups list adapter - [Song name]
 * <p>Folder/ Artist/ Album</p>
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioGroupsAdapter<T> extends BaseAudioAdapter<T> implements SectionIndexer {
    // TAG
    private final String TAG = "BaseAudioGroupsAdapter";

    /**
     * Data list
     */
    private List<T> mListData;

    /**
     * Select position, not always equal to playing position.
     */
    private int mSelectedPos = -1;

    /**
     * Playing media url.
     */
    private String mPlayingMediaUrl = "";

    /**
     * Color of item font.
     * <P>Playing : mHlFontColor</P>
     * <P>Normal : mNormalFontColor</P>
     */
    private int mHlFontColor, mNormalFontColor;

    /**
     * {@link CollectListener} object.
     */
    private CollectListener mCollectListener;

    public BaseAudioGroupsAdapter(Context context) {
        super(context, 0);
        //Initialize
        mResID = R.layout.scl_lc2010_vdc_activity_audio_list_item;
        mNormalFontColor = context.getResources().getColor(android.R.color.white);
        mHlFontColor = context.getResources().getColor(R.color.music_item_selected_font);
    }

    public void setCollectListener(CollectListener l) {
        mCollectListener = l;
    }

    private void setListData(List<T> listData) {
        if (listData == null) {
            this.mListData = new ArrayList<>();
        } else {
            this.mListData = new ArrayList<>(listData);
        }
    }

    private void setPlayingUrl(String mediaUrl) {
        this.mPlayingMediaUrl = mediaUrl;
    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    public void refreshData(List<T> listData, String mediaUrl) {
        synchronized (this) {
            setPlayingUrl(mediaUrl);
            setListData(listData);
            refreshData();
        }
    }

    public void refreshData(List<T> listData) {
        synchronized (this) {
            setListData(listData);
            refreshData();
        }
    }

    public void refreshData(int pos) {
        T item = getItem(pos);
        if (item instanceof ProAudio) {
            refreshPlaying(((ProAudio) item).mediaUrl);
        } else if (item instanceof AudioFilter) {
            AudioFilter selectedAf = (AudioFilter) item;
            for (T t : mListData) {
                AudioFilter af = (AudioFilter) t;
                af.isSelected = TextUtils.equals(selectedAf.artist, af.artist);
            }
            refreshData();
        }
    }

    public void refreshPlaying(String mediaUrl) {
        synchronized (this) {
            setPlayingUrl(mediaUrl);
            refreshData();
        }
    }

    public void select(int pos) {
        synchronized (this) {
            mSelectedPos = pos;
            refreshData();
        }
    }

    public int getSelectPos() {
        return mSelectedPos;
    }

    public void resetSelect() {
        mSelectedPos = -1;
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

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResID, parent, false);
            holder.ivStart = (ImageView) convertView.findViewById(R.id.iv_start);
            holder.tvIdx = (TextView) convertView.findViewById(R.id.tv_idx);
            holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
            holder.vEnd = convertView.findViewById(R.id.v_end);
            holder.ivEnd = (ImageView) convertView.findViewById(R.id.iv_end);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //
        T tItem = getItem(position);
        //ProAudio
        if (tItem instanceof ProAudio) {
            //
            ProAudio item = (ProAudio) tItem;
            holder.ivStart.setImageResource(R.drawable.icon_item_selected);
            holder.tvIdx.setText(String.valueOf((position + 1)));
            holder.tvDesc.setText(PlayerLogicUtils.getMediaTitle(mContext, -1, item, true));

            //Playing
            if (TextUtils.equals(mPlayingMediaUrl, item.mediaUrl)) {
                holder.tvIdx.setTextColor(mHlFontColor);
                holder.tvDesc.setTextColor(mHlFontColor);
                holder.ivStart.setVisibility(View.VISIBLE);
            } else {
                holder.tvIdx.setTextColor(mNormalFontColor);
                holder.tvDesc.setTextColor(mNormalFontColor);
                holder.ivStart.setVisibility(View.INVISIBLE);
            }

            //Collect
            holder.ivEnd.setVisibility(View.VISIBLE);
            holder.vEnd.setOnClickListener(new CollectOnClick(holder.ivEnd, position));
            switch (item.isCollected) {
                case 1:
                    holder.ivEnd.setImageResource(R.drawable.favor_c);
                    break;
                case 0:
                    holder.ivEnd.setImageResource(R.drawable.favor_c_n);
                    break;
            }

            //Media Artist
        } else if (tItem instanceof AudioFilter) {
            AudioFilter item = (AudioFilter) tItem;
            holder.ivStart.setVisibility(View.VISIBLE);
            holder.tvIdx.setText(String.valueOf((position + 1)));
            setGroupDesc(item, holder.tvDesc);
            holder.ivEnd.setVisibility(View.INVISIBLE);
            if (item.isSelected) {
                setGroupTypeIcon(holder.ivStart, true);
                holder.tvIdx.setTextColor(mHlFontColor);
                holder.tvDesc.setTextColor(mHlFontColor);
            } else {
                setGroupTypeIcon(holder.ivStart, false);
                holder.tvIdx.setTextColor(mNormalFontColor);
                holder.tvDesc.setTextColor(mNormalFontColor);
            }
        }

        //Item background
        if (mSelectedPos == position) {
            convertView.setBackgroundResource(getImgResId("bg_lv_item_selected"));
        } else {
            convertView.setBackgroundResource(0);
        }

        return convertView;
    }

    protected abstract void setGroupTypeIcon(@NonNull ImageView ivGroup, boolean isPlayingPos);

    protected abstract void setGroupDesc(@NonNull AudioFilter audioFilter, @NonNull TextView tvDesc);

    /**
     * Collect icon click event
     */
    private class CollectOnClick implements View.OnClickListener {
        private ImageView ivCollect;
        private int mmPosition;

        CollectOnClick(ImageView iv, int position) {
            ivCollect = iv;
            mmPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mCollectListener != null) {
                mCollectListener.onClickCollectBtn(ivCollect, mmPosition);
            }
        }
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
        TextView tvIdx, tvDesc;

        //Collect
        View vEnd;
        ImageView ivEnd;
    }
}
