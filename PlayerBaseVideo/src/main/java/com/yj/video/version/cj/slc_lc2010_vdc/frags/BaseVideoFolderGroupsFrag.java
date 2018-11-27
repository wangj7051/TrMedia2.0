package com.yj.video.version.cj.slc_lc2010_vdc.frags;

import android.text.TextUtils;

import com.yj.video.version.cj.slc_lc2010_vdc.bean.VideoFilter;

import java.util.List;

import js.lib.android.fragment.BaseAppV4Fragment;
import js.lib.android.media.bean.ProVideo;

public abstract class BaseVideoFolderGroupsFrag extends BaseAppV4Fragment {
    // TAG
    private static final String TAG = "BaseAudioGroupsFrag";

    protected abstract void setListData(List<?> listData);

    protected abstract void setListener(SclLc2010VdcVideoFoldersFrag fragParent);

    public abstract void refreshData();

    public abstract void refreshData(List<ProVideo> listMedias);

    /**
     * Refresh playing
     *
     * @param mediaUrl Media url that is playing.
     */
    public abstract void refreshPlaying(String mediaUrl);

    /**
     * Select next
     */
    public abstract void selectNext();

    /**
     * Select previous
     */
    public abstract void selectPrev();

    /**
     * Play selected
     */
    public abstract void playSelected();

    public abstract void onSidebarCallback(int pos, String letter);

    protected int getPlayingPosInMediaList(String playMediaUrl, List<?> listData) {
        int currPos = -1;
        if (listData != null) {
            int loop = listData.size();
            for (int idx = 0; idx < loop; idx++) {
                ProVideo media = (ProVideo) listData.get(idx);
                if (TextUtils.equals(playMediaUrl, media.mediaUrl)) {
                    currPos = idx;
                    break;
                }
            }
        }
        return currPos;
    }

    protected int getPlayingPosInGroupList(String playMediaUrl, List<?> listData) {
        int currPos = -1;
        if (listData != null) {
            int loop = listData.size();
            for (int idx = 0; idx < loop; idx++) {
                VideoFilter vf = (VideoFilter) listData.get(idx);
                if (vf.listMedias != null) {
                    int loopJ = vf.listMedias.size();
                    for (int jdx = 0; jdx < loopJ; jdx++) {
                        ProVideo media = vf.listMedias.get(jdx);
                        if (TextUtils.equals(playMediaUrl, media.mediaUrl)) {
                            currPos = idx;
                            break;
                        }
                    }
                }
                if (currPos != -1) {
                    break;
                }
            }
        }
        return currPos;
    }

    /**
     * 根据数据位置，计算并获取该数据所在页的第一个数据索引
     */
    protected int getGvPageFirstPos(int idx) {
        if (idx % 12 > 0) {
            return (idx / 12) * 12;
        }
        return idx;
    }

    /**
     * 根据数据位置，计算并获取该数据所在页的第一个数据索引
     */
    protected int getLvPageFirstPos(int idx) {
        if (idx % 5 > 0) {
            return (idx / 5) * 5;
        }
        return idx;
    }
}
