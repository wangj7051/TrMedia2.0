package com.tricheer.player.version.cj.slc_lc2010_vdc.frags;

import com.tricheer.player.R;
import com.tricheer.player.engine.ThemeController;

import java.util.List;

import js.lib.android.fragment.BaseAppV4Fragment;
import js.lib.android.media.bean.ProVideo;

public abstract class BaseVideoListFrag extends BaseAppV4Fragment implements ThemeController.ThemeChangeDelegate{

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

    public abstract void refreshData();

    public abstract void refreshData(List<ProVideo> listMedias);

    public abstract void refreshData(List<ProVideo> listMedias, String targetMediaUrl);

    public abstract void next();

    public abstract void prev();

    public abstract void playSelected();

    public abstract void play();
}
