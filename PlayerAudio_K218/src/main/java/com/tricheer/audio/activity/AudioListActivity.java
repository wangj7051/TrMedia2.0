package com.tricheer.audio.activity;

import android.app.Activity;
import android.os.Bundle;

import com.tricheer.audio.R;

public class AudioListActivity extends Activity {
    //TAG
    private static final String TAG = "AudioListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);
    }
}
