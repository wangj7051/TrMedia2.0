package com.yj.scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import js.lib.utils.date.DateFormatUtil;
import js.lib.utils.date.DateTemplates;

public class MainActivity extends Activity {
    //TAG
    private static final String TAG = "ScanMain";

    /**
     * Widgets.
     */
    private TextView tvMedias, tvStart, tvEnd;

    /**
     * Media list.
     */
    private List<String> mListAudios, mListVideos;

    //Scanner
    private LocalMediaScanner mLocalMediaScanner;
    private MediaHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        //
        tvMedias = (TextView) findViewById(R.id.tv_medias);
        tvStart = (TextView) findViewById(R.id.tv_start_time);
        tvStart.setText("START : " + DateFormatUtil.format(System.currentTimeMillis(), DateTemplates.FORMAT_53));
        tvEnd = (TextView) findViewById(R.id.tv_end_time);

        //
        mListAudios = new ArrayList<>();
        mListVideos = new ArrayList<>();
        mHandler = new MediaHandler(this);
        mLocalMediaScanner = new LocalMediaScanner(this, mHandler, 10);
        mLocalMediaScanner.startListMedias();
    }

    @Override
    protected void onDestroy() {
        if (mLocalMediaScanner != null) {
            mLocalMediaScanner.destroy();
            mLocalMediaScanner = null;
        }
        if (mHandler != null) {
            mHandler.destroy();
            mHandler = null;
        }
        super.onDestroy();
    }

    /**
     * Media Handler
     */
    @SuppressLint("HandlerLeak")
    private class MediaHandler extends Handler {
        private MainActivity mCallBack;

        MediaHandler(MainActivity callback) {
            mCallBack = callback;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LocalMediaScanner.TYPE_AUDIO:
                    if (msg.obj != null && mCallBack != null) {
                        mCallBack.addAudios((String) msg.obj);
                    }
                    break;
                case LocalMediaScanner.TYPE_VIDEO:
                    if (msg.obj != null && mCallBack != null) {
                        mCallBack.addVideos((String) msg.obj);
                    }
                    break;
            }
        }

        void destroy() {
            mCallBack = null;
        }
    }

    @SuppressLint("SetTextI18n")
    private void addAudios(String path) {
        if (mListAudios != null) {
            mListAudios.add(path);
            if (mListAudios != null) {
                tvEnd.setText("END : " + DateFormatUtil.format(System.currentTimeMillis(), DateTemplates.FORMAT_53));
                tvMedias.setText("media-count: " + mListAudios.size());
            }
        }
    }

    private void addVideos(String path) {
        if (mListVideos != null) {
            mListVideos.add(path);
        }
    }
}
