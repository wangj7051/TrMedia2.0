package com.yj.video.version.cj.slc_lc2010_vdc.frags;

import com.yj.video.R;
import com.yj.video.engine.ThemeController;

import js.lib.android.fragment.BaseAppV4Fragment;

public abstract class BaseVideoListFrag extends BaseAppV4Fragment implements ThemeController.ThemeChangeDelegate {

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
     * Show loading.
     */
    public abstract void showLoading(boolean isShow);

    /**
     * Notify scanning status - START
     */
    public abstract void onMediaScanningStart();

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
     * 该方法仅在以下情况执行
     * (1)页面没有数据
     * (2)首次加载数据
     */
    public abstract void loadLocalMedias();

    /**
     * 该方法仅在以下情况执行
     * (1)页面已有数据，更新数据
     */
    public abstract void refreshData();

    /**
     * Select next
     */
    public abstract void selectNext();

    /**
     * Select previous
     */
    public abstract void selectPrev();

    public abstract void playSelected();

    /**
     * 根据数据位置，计算并获取该数据所在页的第一个数据索引
     */
    protected int getPageFirstPos(int idx) {
        if (idx % 12 > 0) {
            return (idx / 12) * 12;
        }
        return idx;
    }
}
