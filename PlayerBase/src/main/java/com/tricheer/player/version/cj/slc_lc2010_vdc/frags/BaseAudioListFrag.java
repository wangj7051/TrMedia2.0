package com.tricheer.player.version.cj.slc_lc2010_vdc.frags;

import java.util.List;

import js.lib.android.fragment.BaseAppV4Fragment;
import js.lib.android.media.bean.ProAudio;

public abstract class BaseAudioListFrag extends BaseAppV4Fragment {
    public final int M_REQ_PLAYING_MEDIA_URL = 1;

    /**
     * @return <p>0-Frag is in base page.</p>
     * <p>1-Frag back from 1`st page to base page.</p>
     */
    public abstract int onBackPressed();

    public abstract void refreshDatas(List<ProAudio> listMedias, String targetMediaUrl);

    public abstract void refreshDatas(String targetMediaUrl);

    public abstract void next();

    public abstract void prev();

    public abstract void playSelectMedia(String mediaUrl);
}
