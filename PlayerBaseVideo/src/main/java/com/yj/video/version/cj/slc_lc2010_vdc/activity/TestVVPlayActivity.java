package com.yj.video.version.cj.slc_lc2010_vdc.activity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.VideoView;

import com.yj.video.R;

public class TestVVPlayActivity extends Activity {

    private Context mContext;
    private VideoView vvPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_vv);
        play();
    }

    private void play() {
        mContext = this;
        vvPlayer = (VideoView) findViewById(R.id.vv_player);
        setupVideo();
    }

    private void setupVideo() {
        vvPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(android.media.MediaPlayer mp) {
                vvPlayer.start();
            }
        });
        vvPlayer.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(android.media.MediaPlayer mp) {
                stopPlaybackVideo();
            }
        });
        vvPlayer.setOnErrorListener(new android.media.MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
                stopPlaybackVideo();
                return true;
            }
        });

        try {
            vvPlayer.setVideoURI(Uri.parse("/storage/emulated/0/Music/qianfu02.mp4"));
//            vvPlayer.setVideoURI(Uri.parse("/storage/emulated/0/Music/jilejingtu.mp4"));
//            vvPlayer.setVideoURI(Uri.parse("/storage/emulated/0/Music/vob001.vob"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!vvPlayer.isPlaying()) {
            vvPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vvPlayer.canPause()) {
            vvPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlaybackVideo();
    }

    private void stopPlaybackVideo() {
        try {
            vvPlayer.stopPlayback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
