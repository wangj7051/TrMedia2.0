package com.tricheer.player.version.cj.slc_lc2010_vdc.frags;

import java.util.List;

import js.lib.android.fragment.BaseAppV4Fragment;
import js.lib.android.media.bean.ProVideo;

public abstract class BaseVideoListFrag extends BaseAppV4Fragment {
    public abstract void refreshDatas();

    public abstract void refreshDatas(List<ProVideo> listMedias);

    public abstract void refreshDatas(List<ProVideo> listMedias, String targetMediaUrl);

    public abstract void next();

    public abstract void prev();

    public abstract void play();
}
