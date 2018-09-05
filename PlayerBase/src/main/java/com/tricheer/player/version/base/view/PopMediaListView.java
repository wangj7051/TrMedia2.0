package com.tricheer.player.version.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tricheer.player.R;
import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.bean.Program;
import com.tricheer.player.utils.PlayerLogicUtils;

import java.util.List;

import js.lib.android.adapter.BaseArrayAdapter;
import js.lib.android.utils.Logs;

/**
 * Show Media List View
 *
 * @author Jun.Wang
 */
@SuppressLint("InflateParams")
public class PopMediaListView extends PopupWindow {
    // TAG
    private final String TAG = "PopMediaListView -> ";

    /**
     * ==========Widget in this Activity==========
     */
    /**
     * Media ListView
     */
    private ListView lvMedias;

    /**
     * ==========Variable in this Activity==========
     */
    //
    private Context mContext;
    private Resources mResources;

    //
    private int mPlayingPos = -1;

    // Handler Delay Time, 300MS
    protected final int M_DEFAULT_DELAY_TIME = 300;

    /**
     * Media List Adapter
     */
    private MediaListAdapter mListAdapter;

    /**
     * Listener play selected music
     */
    private PlayMediaListListener mPlayMediaListListener;

    public interface PlayMediaListListener {
        public void onPlayFixedPos(int pos);
    }

    public PopMediaListView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.mResources = mContext.getResources();

        //
        View vList = LayoutInflater.from(mContext).inflate(R.layout.zpt_lv8918_rja_v_media_list, null);

        //
        mListAdapter = new MediaListAdapter(mContext, R.layout.zpt_lv8918_rja_v_media_list_item);
        mListAdapter.notifyDataSetChanged();

        lvMedias = (ListView) vList.findViewById(R.id.lv_media_list);
        lvMedias.setAdapter(mListAdapter);
        lvMedias.setOnItemClickListener(new LvOnItemClick());
        lvMedias.addOnLayoutChangeListener(new LvOnLayoutChange());

        //
        setContentView(vList);
    }

    /**
     * ListView Item Click Event
     */
    private class LvOnItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            dismiss();
            if (mPlayMediaListListener != null) {
                mPlayMediaListListener.onPlayFixedPos(position);
            }
        }
    }

    /**
     * ListView Layout Change Event
     */
    private class LvOnLayoutChange implements View.OnLayoutChangeListener {

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
                                   int oldBottom) {
            if (mPlayingPos < mListAdapter.getCount() && mPlayingPos >= 0) {
                lvMedias.setSelection(mPlayingPos);
            }
        }
    }

    /**
     * Set PlayMediaListListener
     */
    public void setPlayMusicListListener(PlayMediaListListener l) {
        this.mPlayMediaListListener = l;
    }

    /**
     * Refresh Playing Position
     */
    public void refreshMediaList(final int playingPos) {
        try {
            if (playingPos >= 0) {
                mPlayingPos = playingPos;
                mListAdapter.refreshDatas(mPlayingPos);
            }

        } catch (Exception e) {
            Logs.printStackTrace(TAG + "refreshMediaList(int)", e);
        }
    }

    /**
     * Refresh Media List
     */
    public void refreshMediaList(List<Program> listMedias, final int playingPos) {
        try {
            if (playingPos >= 0) {
                mPlayingPos = playingPos;
                mListAdapter.refreshDatas(listMedias, mPlayingPos);
            } else {
                mListAdapter.refreshDatas(listMedias);
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "refreshMediaList(list,int)", e);
        }
    }

    /**
     * Media List Adapter
     */
    private class MediaListAdapter extends BaseArrayAdapter<Program> {
        private List<Program> mListMedias;
        private int mPlayingPos;

        public MediaListAdapter(Context context, int resource) {
            super(context, resource);
            this.mContext = context;
            this.mInflater = LayoutInflater.from(mContext);
            this.mResID = resource;
        }

        public void refreshDatas(List<Program> listMedias, int playingPos) {
            mListMedias = listMedias;
            mPlayingPos = playingPos;
            notifyDataSetChanged();
        }

        public void refreshDatas(List<Program> listMedias) {
            mListMedias = listMedias;
            notifyDataSetChanged();
        }

        public void refreshDatas(int playingPos) {
            mPlayingPos = playingPos;
            notifyDataSetChanged();
        }

        @Override
        public Program getItem(int position) {
            if (mListMedias == null || mListMedias.size() == 0) {
                return null;
            }

            return mListMedias.get(position);
        }

        @Override
        public int getCount() {
            if (mListMedias == null) {
                return 0;
            }

            return mListMedias.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(mResID, null);

                holder = new ViewHolder();
                holder.tvMediaTitle = (TextView) convertView.findViewById(R.id.tv_media_title);
                holder.tvMediaPlayer = (TextView) convertView.findViewById(R.id.tv_media_player);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Program media = mListMedias.get(position);
            holder.tvMediaTitle.setText(((position + 1) + ". ") + PlayerLogicUtils.getStrOrUnKnow(mContext, media.title));
            if (media instanceof ProMusic) {
                holder.tvMediaPlayer.setText(PlayerLogicUtils.getStrOrUnKnow(mContext, ((ProMusic) media).artist));
            } else if (media instanceof ProVideo) {
                holder.tvMediaPlayer.setText("");
            }

            //
            if (mPlayingPos == position) {
                holder.tvMediaTitle.setTextColor(mResources.getColor(R.color.paint_color_hl_blue));
                holder.tvMediaPlayer.setTextColor(mResources.getColor(R.color.paint_color_hl_blue));
            } else {
                holder.tvMediaTitle.setTextColor(mResources.getColor(android.R.color.white));
                holder.tvMediaPlayer.setTextColor(mResources.getColor(R.color.lightgray));
            }

            return convertView;
        }

        /**
         * View Holder
         */
        private final class ViewHolder {
            public TextView tvMediaTitle, tvMediaPlayer;
        }
    }
}
