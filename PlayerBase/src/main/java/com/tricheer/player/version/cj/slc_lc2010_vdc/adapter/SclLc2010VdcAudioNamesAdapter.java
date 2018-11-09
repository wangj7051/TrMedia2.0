package com.tricheer.player.version.cj.slc_lc2010_vdc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tricheer.player.R;
import com.tricheer.player.utils.PlayerLogicUtils;

import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.utils.Logs;

/**
 * Audio - [Song name]
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioNamesAdapter extends BaseAudioAdapter<ProAudio> implements SectionIndexer {
    // TAG
    private final String TAG = "SlcLc2010VdcMusicListAdapter";

    //Data list
    private List<ProAudio> mListData;

    // ---- Selected ----
    private static int mSelectedPos = -1;

    // ---- Focused ----
    //A single color value in the form 0xAARRGGBB
    private int mHlFontColor, mNormalFontColor;
    private String mPlayingMediaUrl = "";

    /**
     * Media collect operate callback listener
     * <p>{@link CollectListener}</p>
     */
    private CollectListener mCollectListener;

    public SclLc2010VdcAudioNamesAdapter(Context context, int resource) {
        super(context, resource);
        //Font color
        mHlFontColor = context.getResources().getColor(R.color.music_item_selected_font);
        mNormalFontColor = context.getResources().getColor(android.R.color.white);
    }

    public void setCollectListener(CollectListener l) {
        mCollectListener = l;
    }

    public void setListData(List<ProAudio> listData) {
        if (listData != null) {
            this.mListData = new ArrayList<>(listData);
        } else {
            this.mListData = new ArrayList<>();
        }
    }

    public void setPlayingUrl(String mediaUrl) {
        this.mPlayingMediaUrl = mediaUrl;
    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    public void refreshData(List<ProAudio> listData, String mediaUrl) {
        setPlayingUrl(mediaUrl);
        setListData(listData);
        refreshData();
    }

    public void refreshPlaying(String mediaUrl) {
        setPlayingUrl(mediaUrl);
        refreshData();
    }

    public void select(int pos) {
        mSelectedPos = pos;
        refreshData();
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
    public ProAudio getItem(int position) {
        try {
            return mListData.get(position);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.scl_lc2010_vdc_activity_audio_list_item, null);
            holder.ivPlaying = (ImageView) convertView.findViewById(R.id.iv_start);
            holder.tvIdx = (TextView) convertView.findViewById(R.id.tv_idx);
            holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
            holder.vEnd = convertView.findViewById(R.id.v_end);
            holder.ivEnd = (ImageView) convertView.findViewById(R.id.iv_end);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //
        ProAudio item = getItem(position);
        if (item != null) {
            //
            holder.tvIdx.setText(String.valueOf((position + 1)));
            holder.tvDesc.setText(PlayerLogicUtils.getMediaTitle(mContext, -1, item, true));

            //Playing
            if (TextUtils.equals(mPlayingMediaUrl, item.mediaUrl)) {
                holder.tvIdx.setTextColor(mHlFontColor);
                holder.tvIdx.setVisibility(View.GONE);
                holder.tvDesc.setTextColor(mHlFontColor);
                holder.ivPlaying.setVisibility(View.VISIBLE);
            } else {
                holder.tvIdx.setTextColor(mNormalFontColor);
                holder.tvIdx.setVisibility(View.VISIBLE);
                holder.tvDesc.setTextColor(mNormalFontColor);
                holder.ivPlaying.setVisibility(View.INVISIBLE);
            }

            //Collect
            holder.vEnd.setOnClickListener(new CollectOnClick(holder.ivEnd, position));
            switch (item.isCollected) {
                case 1:
                    holder.ivEnd.setImageResource(R.drawable.favor_c);
                    break;
                case 0:
                    holder.ivEnd.setImageResource(R.drawable.favor_c_n);
                    break;
            }

            //Item background
            if (mSelectedPos == position) {
                convertView.setBackgroundResource(getImgResId("bg_lv_item_selected"));
            } else {
                convertView.setBackgroundResource(0);
            }
        }

        return convertView;
    }

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
                ProAudio media = getItem(idx);
                if (media == null) {
                    continue;
                }

                //
                char firstChar = media.sortLetter.toUpperCase().charAt(0);
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
        int section = -1;
        try {
            ProAudio media = getItem(position);
            if (media != null) {
                section = media.sortLetter.charAt(0);
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getSectionForPosition()", e);
        }
        return section;
    }

    private final class ViewHolder {
        ImageView ivPlaying;
        TextView tvIdx, tvDesc;

        //
        View vEnd;
        ImageView ivEnd;
    }
}
