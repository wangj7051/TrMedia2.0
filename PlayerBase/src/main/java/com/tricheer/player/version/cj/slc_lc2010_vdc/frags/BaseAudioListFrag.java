package com.tricheer.player.version.cj.slc_lc2010_vdc.frags;

import com.tricheer.player.bean.ProMusic;

import java.util.List;

import js.lib.android.fragment.BaseAppV4Fragment;

public abstract class BaseAudioListFrag extends BaseAppV4Fragment {
    public final int M_REQ_PLAYING_MEDIA_URL = 1;

    public abstract void refreshDatas(String targetMediaUrl);

    public abstract void refreshDatas(List<ProMusic> listMedias, String targetMediaUrl);

    public abstract void next();

    public abstract void prev();
}
