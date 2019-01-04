package com.yj.video.version.cj.slc_lc2010_vdc.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.SurfaceView;

import com.yj.video.R;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

public class TestVobPlayActivity extends Activity {

    private Context mContext;
    private LibVLC mLibVLC = null;
    private SurfaceView surfaceView = null;
    private MediaPlayer mMediaPlayer = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_vob);
        play();
    }

    private void play() {
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mContext = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final ArrayList<String> args = new ArrayList<>();
                args.add("-vvv");
                mLibVLC = new LibVLC(mContext, args);
                mMediaPlayer = new MediaPlayer(new Media(mLibVLC, "/storage/emulated/0/Music/vob001.vob"));
                IVLCVout vlcVout = mMediaPlayer.getVLCVout();
                vlcVout.addCallback(new IVLCVout.Callback() {
                    @Override
                    public void onSurfacesCreated(IVLCVout ivlcVout) {
                    }

                    @Override
                    public void onSurfacesDestroyed(IVLCVout ivlcVout) {
                    }
                });
                vlcVout.setVideoView(surfaceView);
                vlcVout.attachViews();
                mMediaPlayer.play();
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            //Release Media
            final Media media = mMediaPlayer.getMedia();
            if (media != null) {
                media.setEventListener(null);
            }

            //Release MediaPlayer
            mMediaPlayer.setEventListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.setMedia(null);
            mMediaPlayer.release();
            mMediaPlayer = null;

            //Release LibVLC
            mLibVLC.release();
            mLibVLC = null;
        }
        super.onDestroy();
    }
}
