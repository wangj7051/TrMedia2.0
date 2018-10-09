package com.tricheer.player.version.cj.slc_lc2010_vdc.frags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.js.sidebar.LetterSideBar;
import com.tricheer.player.R;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioListActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.activity.SclLc2010VdcAudioPlayerActivity;
import com.tricheer.player.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcAudioArtistAdapter;
import com.tricheer.player.version.cj.slc_lc2010_vdc.adapter.SclLc2010VdcAudioNamesAdapter;
import com.tricheer.player.version.cj.slc_lc2010_vdc.bean.AudioFilter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import js.lib.android.media.audio.db.AudioDBManager;
import js.lib.android.media.bean.ProAudio;
import js.lib.android.utils.Logs;

/**
 * SCL_LC2010_VDC - Music collects fragment
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioArtistsFrag extends BaseAudioListFrag {
    // TAG
    private static final String TAG = "MusicArtistsFrag";


    //==========Widgets in this Fragment==========
    private View contentV;
    private ListView lvDatas;
    private LetterSideBar lsb;

    //==========Variables in this Fragment==========
    private SclLc2010VdcAudioListActivity mAttachedActivity;
    private static Handler mHandler = new Handler();

    /**
     * Is ListView Item is Clicking
     */
    private boolean mIsLvItemClicking = false;

    /**
     * Media list
     */
    private List<?> mListDatas;

    /**
     * Data adapter
     */
    private SclLc2010VdcAudioArtistAdapter mDataAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAttachedActivity = (SclLc2010VdcAudioListActivity) activity;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentV = inflater.inflate(R.layout.scl_lc2010_vdc_activity_audio_list_frag_artists, null);
        return contentV;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        //----Widgets----
        //Side bar
        lsb = (LetterSideBar) contentV.findViewById(R.id.lsb);
        lsb.refreshLetters(null);
        lsb.addCallback(new LetterSideBarCallback());
        lsb.setVisibility(View.VISIBLE);

        //ListView
        mDataAdapter = new SclLc2010VdcAudioArtistAdapter(mAttachedActivity, 0);
        mDataAdapter.setCollectListener(new CollectBtnCallback());

        lvDatas = (ListView) contentV.findViewById(R.id.lv_datas);
        lvDatas.setAdapter(mDataAdapter);
        lvDatas.setOnItemClickListener(new LvItemClick());
        refreshDatas(mAttachedActivity.getListMedias(), mAttachedActivity.getLastMediaPath());
    }

    @Override
    public void refreshDatas(List<ProAudio> listMedias, String targetMediaUrl) {
        if (isAdded()) {
            //Check NULL
            if (listMedias == null) {
                return;
            }

            //Filter collected
            Map<String, AudioFilter> mapDatas = new HashMap<>();
            for (ProAudio media : listMedias) {
                //Folder
                String folderPath = "";
                File file = new File(media.mediaUrl);
                if (file.exists()) {
                    File parentFile = file.getParentFile();
                    if (parentFile != null) {
                        folderPath = parentFile.getPath();
                    }
                }

                //
                AudioFilter audioFilter = mapDatas.get(media.artist);
                if (audioFilter == null) {
                    audioFilter = new AudioFilter();
                    audioFilter.folderPath = folderPath;
                    audioFilter.artist = media.artist;
                    audioFilter.artistPinYin = media.artistPinYin;
                    audioFilter.listMedias = new ArrayList<>();
                    audioFilter.listMedias.add(media);
                    mapDatas.put(media.artist, audioFilter);
                } else {
                    audioFilter.listMedias.add(media);
                }

                //
                if (!audioFilter.isSelected) {
                    audioFilter.isSelected = TextUtils.equals(targetMediaUrl, media.mediaUrl);
                }
            }

            //Refresh UI
            mListDatas = new ArrayList<>(mapDatas.values());
            AudioFilter.sortByArtist((List<AudioFilter>) mListDatas);
            mDataAdapter.refreshDatas(mListDatas, targetMediaUrl);
        }
    }

    @Override
    public void refreshDatas(String targetMediaUrl) {
        if (isAdded() && mListDatas != null) {
            for (Object obj : mListDatas) {
                if (obj instanceof AudioFilter) {
                    AudioFilter filter = (AudioFilter) obj;
                    if (filter.listMedias != null) {
                        for (ProAudio media : filter.listMedias) {
                            filter.isSelected = TextUtils.equals(targetMediaUrl, media.mediaUrl);
                            if (filter.isSelected) {
                                break;
                            }
                        }
                    }
                    if (filter.isSelected) {
                        break;
                    }
                } else {
                    break;
                }
            }
            mDataAdapter.refreshDatas(targetMediaUrl);
        }
    }

    @Override
    public void next() {
        if (isAdded()) {
            mDataAdapter.refreshDatas(mDataAdapter.getNextPos());
        }
    }

    @Override
    public void prev() {
        if (isAdded()) {
            mDataAdapter.refreshDatas(mDataAdapter.getPrevPos());
        }
    }

    @Override
    public void playSelectMedia(String mediaUrl) {
        try {
            Object obj = mListDatas.get(0);
            if (obj instanceof ProAudio) {
                openPlayerActivity(mediaUrl, mListDatas);
            }
        } catch (Exception e) {
            Log.i(TAG, "playSelectMedia> " + e.getMessage());
        }
    }

    private class LetterSideBarCallback implements LetterSideBar.LetterSideBarListener {
        @Override
        public void callback(int pos, String letter) {
            Logs.i(TAG, "LetterSideBarCallback -> callback(" + pos + "," + letter + ")");
            lvDatas.setSelection(pos);
        }
    }

    /**
     * ListView Item Click Event
     */
    private class LvItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!mIsLvItemClicking) {
                mIsLvItemClicking = true;
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(mmDelayResetClickingFlagRunnable, 1000);

                // Play Select MediaUrl
                Logs.i(TAG, "LvItemClick -> onItemClick(AdapterView," + position + ",id)");
                final Object objItem = parent.getItemAtPosition(position);
                if (objItem == null) {
                    return;
                }
                if (objItem instanceof AudioFilter) {
                    AudioFilter item = (AudioFilter) objItem;
                    mListDatas = item.listMedias;
                    mDataAdapter.refreshDatas(mListDatas);
                } else if (objItem instanceof ProAudio) {
                    ProAudio program = (ProAudio) objItem;
                    openPlayerActivity(program.mediaUrl, mListDatas);
                }
            }
        }

        private Runnable mmDelayResetClickingFlagRunnable = new Runnable() {

            @Override
            public void run() {
                mIsLvItemClicking = false;
            }
        };
    }

    protected void openPlayerActivity(String mediaUrl, List<?> listPrograms) {
        try {
            Intent playerIntent = new Intent(mAttachedActivity, SclLc2010VdcAudioPlayerActivity.class);
            playerIntent.putExtra("SELECT_MEDIA_URL", mediaUrl);
            playerIntent.putExtra("MEDIA_LIST", (Serializable) listPrograms);
            startActivityForResult(playerIntent, M_REQ_PLAYING_MEDIA_URL);
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "openPlayerActivity()", e);
        }
    }

    private class CollectBtnCallback implements SclLc2010VdcAudioNamesAdapter.CollectListener {
        @Override
        public void onClickCollectBtn(ImageView ivCollect, int pos) {
            Object item = mDataAdapter.getItem(pos);
            if (item != null && item instanceof ProAudio) {
                ProAudio media = (ProAudio) item;
                switch (media.isCollected) {
                    case 0:
                        media.isCollected = 1;
                        AudioDBManager.instance().updateMediaCollect(media);
                        ivCollect.setImageResource(R.drawable.favor_c);
                        break;
                    case 1:
                        media.isCollected = 0;
                        AudioDBManager.instance().updateMediaCollect(media);
                        ivCollect.setImageResource(R.drawable.favor_c_n);
                        break;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAttachedActivity.onActivityResult(requestCode, resultCode, data);
        mDataAdapter.refreshDatas(mAttachedActivity.getLastMediaPath());
    }
}