package com.yj.audio.version.cj.slc_lc2010_vdc.frags;

import com.yj.audio.R;
import com.yj.audio.engine.ThemeController;

import js.lib.android.fragment.BaseAppV4Fragment;

/**
 * Base audio list fragment.
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioListFrag extends BaseAppV4Fragment implements ThemeController.ThemeChangeDelegate {

    protected final int[] LOADING_RES_ID_ARR = {R.drawable.loading_list0001,
            R.drawable.loading_list0002,
            R.drawable.loading_list0003,
            R.drawable.loading_list0004,
            R.drawable.loading_list0005,
            R.drawable.loading_list0006,
            R.drawable.loading_list0007,
            R.drawable.loading_list0008,
            R.drawable.loading_list0009,
            R.drawable.loading_list0010
    };

    /**
     * Set parameter
     */
    public abstract void setParam(String... param);

    /**
     * Show loading.
     */
    public abstract void showLoading(boolean isShow);

    /**
     * Notify scanning status - START
     */
    public abstract void onMediaScanningStart();

    /**
     * Notify scanning status - Found new.
     */
    public abstract void onMediaScanningNew();

    /**
     * Notify scanning status - END
     */
    public abstract void onMediaScanningEnd();

    /**
     * Notify scanning status - CANCEL
     */
    public abstract void onMediaScanningCancel();

    /**
     * Notify scanning status - Parse END
     */
    public abstract void onMediaParseEnd();

    /**
     * @return <p>0-Frag is in base page.</p>
     * <p>1-Frag back from 1`st page to base page.</p>
     */
    public abstract int onBackPressed();

    /**
     * Load local medias
     * <p>Data in database.</p>
     */
    public abstract void loadLocalMedias();

    /**
     * Load data list
     */
    public abstract void loadDataList();

    /**
     * Refresh
     */
    public abstract void refreshDataList();

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

    /**
     * Play selected media url
     */
    public abstract void playSelected(String mediaUrl);

    /**
     * 根据数据位置，计算并获取该数据所在页的第一个数据索引
     */
    protected int getPageFirstPos(int idx) {
        if (idx % 5 > 0) {
            return (idx / 5) * 5;
        }
        return idx;
    }
}
