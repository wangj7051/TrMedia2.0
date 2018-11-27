package js.lib.android_media_scan.parse_controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.engine.video.db.VideoDBManager;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

public class ParseVideoController extends ParseMediaController {
    //TAG
    private static final String TAG = "ParseVideoController";

    /**
     * {@link Context}
     */
    private Context mAppContext;

    /**
     * {@link ParseTask} object.
     */
    @SuppressLint("StaticFieldLeak")
    private static ParseTask mParseMediaTask;

    public ParseVideoController(Context context) {
        mAppContext = context.getApplicationContext();
    }

    /**
     * Execute task
     */
    public void startParseMediaTask(List<ProVideo> listAudios, ParseMediaDelegate delegate) {
        setParseMediaDelegate(delegate);
        Logs.i(TAG, "----startParseMediaTask()----");
        if (mParseMediaTask == null) {
            mParseMediaTask = new ParseTask(mAppContext, listAudios);
            mParseMediaTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * Parse media information task
     */
    @SuppressLint("StaticFieldLeak")
    private class ParseTask extends AsyncTask<Void, Void, Void> {
        //
        private Context mmContext;
        private List<ProVideo> mmListMedias;
        private List<ProVideo> mmListParsedMedias;

        ParseTask(Context context, List<ProVideo> listAudios) {
            mmContext = context;
            mmListMedias = listAudios;
            mmListParsedMedias = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (EmptyUtil.isEmpty(mmListMedias)) {
                return null;
            }

            int loop = mmListMedias.size();
            for (int idx = 0; idx < loop; idx++) {
                ProVideo media = mmListMedias.get(idx);
                //Parse
                ProVideo.parseMedia(mmContext, media);
                mmListParsedMedias.add(media);
                if (idx > 0 && (idx + 1) % 10 == 0) {
                    saveParsedMedias();
                }

                //Check media cover picture.
                String coverPicFilePath = getCoverBitmapPath(media, 1);
                Log.i("coverScanReceiver", "coverPicFilePath: " + coverPicFilePath);
                File coverPicFile = new File(coverPicFilePath);
                if (coverPicFile.exists()) {
                    Log.i("coverScanReceiver", " [ EXIST ]");
                    media.coverUrl = coverPicFilePath;
                    //If cover picture is not exist, try to get it.
                } else {
                    Log.i("coverScanReceiver", " [ CREATE NEW ]");
                    Bitmap coverBitmap = media.getThumbNail(media.mediaUrl, 200, 200, MediaStore.Images.Thumbnails.MINI_KIND);
                    if (coverBitmap == null) {
                        Log.i("coverScanReceiver", " [ CREATE NEW ] - FAIL - ");
                    } else {
                        Log.i("coverScanReceiver", " [ CREATE NEW ] - SUCCESSFULLY - ");
                        storeBitmap(coverPicFilePath, coverBitmap);
                        media.coverUrl = coverPicFilePath;
                    }
                }

                //
                if (idx > 0 && (idx + 1) % 10 == 0) {
                    saveParsedMedias();
                }
            }
            saveParsedMedias();
            return null;
        }

        private void saveParsedMedias() {
            int count = VideoDBManager.instance().insertListVideos(mmListParsedMedias);
            Log.i(TAG, "count:" + count);
            mmListParsedMedias.clear();
        }

        void destroy() {
            mmContext = null;
            mmListMedias.clear();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            destroy();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyParseEnd();
            destroy();
        }
    }

    public void destroy() {
        mAppContext = null;
        cancelAllTasks();
    }

    /**
     * Cancel list media task.
     */
    private void cancelAllTasks() {
        if (mParseMediaTask != null) {
            mParseMediaTask.cancel(true);
            mParseMediaTask.destroy();
            mParseMediaTask = null;
        }
    }
}
