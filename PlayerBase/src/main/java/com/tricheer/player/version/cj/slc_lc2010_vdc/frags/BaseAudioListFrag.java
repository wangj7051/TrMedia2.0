package com.tricheer.player.version.cj.slc_lc2010_vdc.frags;

import js.lib.android.fragment.BaseAppV4Fragment;

/**
 * Base audio list fragment.
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioListFrag extends BaseAppV4Fragment {
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
