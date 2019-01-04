package com.yj.scanner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import js.lib.android.media.engine.MediaUtils;
import js.lib.android.media.engine.audio.utils.AudioInfo;
import js.lib.android.media.engine.video.utils.VideoInfo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.sdcard.SDCardInfo;
import js.lib.android.utils.sdcard.SDCardUtils;

public class LocalMediaScanner {

    public static final int TYPE_AUDIO = 0;
    public static final int TYPE_VIDEO = 1;


    private Context mContext;
    private Handler mHandler;
    private ThreadPoolExecutor mExecutor;

    public LocalMediaScanner(Context context, Handler handler, int nThreads) {
        mContext = context;
        mHandler = handler;
        mExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads <= 0 ? 1 : nThreads);
    }

    public final void startListMedias() {
        HashMap<String, SDCardInfo> mapSdInfos = SDCardUtils.getSDCardInfos(mContext, false);
        if (mExecutor != null && !EmptyUtil.isEmpty(mapSdInfos)) {
            mExecutor.execute(new FolderRunnable(mapSdInfos));
        }
    }

    private class FolderRunnable implements Runnable {
        private HashMap<String, SDCardInfo> mMapSdInfos;
        private String mFolderPath;

        FolderRunnable(HashMap<String, SDCardInfo> mapSdInfos) {
            mMapSdInfos = mapSdInfos;
        }

        FolderRunnable(String folderPath) {
            mFolderPath = folderPath;
        }

        @Override
        public void run() {
            if (!EmptyUtil.isEmpty(mMapSdInfos)) {
                listRootPaths();
            } else if (mFolderPath != null) {
                listAll(new File(mFolderPath));
            }
        }

        private void listRootPaths() {
            for (SDCardInfo sdInfo : mMapSdInfos.values()) {
                listAll(new File(sdInfo.root));
            }
        }

        private void listAll(File folder) {
            if (folder == null || folder.isFile() || folder.listFiles() == null) {
                return;
            }

            File[] childFiles = folder.listFiles();
            for (File tmpF : childFiles) {
                if (tmpF == null) {
                    continue;
                }

                String path = tmpF.getPath();
                if (tmpF.isFile()) {
                    String suffix = MediaUtils.getSuffix(path);
                    if (AudioInfo.isSupport(suffix)) {
                        postMedia(TYPE_AUDIO, path);
                    } else if (VideoInfo.isSupport(suffix)) {
                        postMedia(TYPE_VIDEO, path);
                    }
                } else if (tmpF.isDirectory() && !tmpF.isHidden()) {
                    mExecutor.execute(new FolderRunnable(path));
                }
            }
        }

        void postMedia(int type, String path) {
            if (mHandler != null) {
                Message msgMedia = new Message();
                msgMedia.what = type;
                msgMedia.obj = path;
                mHandler.sendMessage(msgMedia);
            }
        }
    }

    public void destroy() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
        }
    }
}
