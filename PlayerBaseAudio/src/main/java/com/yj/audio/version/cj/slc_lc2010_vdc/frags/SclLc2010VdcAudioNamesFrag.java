package com.yj.audio.version.cj.slc_lc2010_vdc.frags;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * SCL_LC2010_VDC - Music names fragment
 *
 * @author Jun.Wang
 */
public class SclLc2010VdcAudioNamesFrag extends BaseAudioNamesFrag {
    // TAG
    private static final String TAG = "AudioNamesFrag";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated()");
    }
}