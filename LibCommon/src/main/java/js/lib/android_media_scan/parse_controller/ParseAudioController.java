package js.lib.android_media_scan.parse_controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

public class ParseAudioController extends ParseMediaController {
    //TAG
    private static final String TAG = "ParseAudioController";

    /**
     * {@link Context}
     */
    private Context mAppContext;

    /**
     * {@link ParseTask} object.
     */
    @SuppressLint("StaticFieldLeak")
    private static ParseTask mParseMediaTask;

    public ParseAudioController(Context context) {
        mAppContext = context.getApplicationContext();
    }

    /**
     * Execute task
     */
    public void startParseMediaTask(List<ProAudio> listAudios, ParseMediaDelegate delegate) {
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
        private List<ProAudio> mmListMedias;
        private List<ProAudio> mmListParsedMedias;

        ParseTask(Context context, List<ProAudio> listAudios) {
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
                Log.i(TAG, idx + "/" + loop);
                ProAudio media = mmListMedias.get(idx);
                //Parse audio information
                ProAudio.parseMedia(mmContext, media);
                mmListParsedMedias.add(media);

                //Check media cover picture.
                String coverPicFilePath = getCoverBitmapPath(media, 0);
                File coverPicFile = new File(coverPicFilePath);
                if (coverPicFile.exists()) {
                    media.coverUrl = coverPicFilePath;
                    //If cover picture is not exist, try to get it.
                } else {
                    Bitmap coverBitmap = media.getThumbNail(mmContext, media.mediaUrl);
                    if (coverBitmap != null) {
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

        private void saveParsedMedias() {
            int count = AudioDBManager.instance().insertListMusics(mmListParsedMedias);
            Log.i(TAG, "count:" + count);
            mmListParsedMedias.clear();
        }

        void destroy() {
            mmContext = null;
            mmListMedias.clear();
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
