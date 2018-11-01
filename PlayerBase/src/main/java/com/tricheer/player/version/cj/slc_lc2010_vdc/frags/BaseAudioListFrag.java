package com.tricheer.player.version.cj.slc_lc2010_vdc.frags;

import com.tricheer.player.R;

import js.lib.android.fragment.BaseAppV4Fragment;

/**
 * Base audio list fragment.
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioListFrag extends BaseAppV4Fragment {

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
     * Notify scanning status - START
     */
    public abstract void onMediaScanningStart();

    /**
     * Notify scanning status - END
     */
    public abstract void onMediaScanningEnd();

    /**
     * Notify scanning status - CANCEL
     */
    public abstract void onMediaScanningCancel();

    /**
     * @return <p>0-Frag is in base page.</p>
     * <p>1-Frag back from 1`st page to base page.</p>
     */
    public abstract int onBackPressed();

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
}
