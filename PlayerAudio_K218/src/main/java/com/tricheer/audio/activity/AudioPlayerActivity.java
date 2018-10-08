package com.tricheer.audio.activity;

import android.app.Activity;
import android.os.Bundle;

import com.tricheer.audio.R;

public class AudioPlayerActivity extends Activity {
    //TAG
    private static final String TAG = "AudioPlayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
    }
}
