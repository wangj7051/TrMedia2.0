package js.lib.android.media.engine.scan;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.ProVideo;

/**
 * Media scan service.
 * <p>This service should start when the system boot completed.</p>
 *
 * @author Jun.Wang
 */
public class MediaScanService extends BaseScanService {
    //TAG
    private static final String TAG = "MediaScanService";

    /**
     * Start list all medias action
     */
    public static final String ACTION_START_LIST = "js.lib.media.SCAN_ALL_MEDIAS";

    /**
     * 挂载的路径 / 未挂载的路径
     */
    private Set<String> mSetPathsMounted = new HashSet<>(), mSetPathsUnMounted = new HashSet<>();

    //
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        Log.i(TAG, "init()");

        //
        mContext = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * List Medias Task
     */
    private class ListMediaTask extends AsyncTask<Void, Void, Void> {
        //
        private boolean mmIsScanning = false;

        //
        private Map<String, ProAudio> mmMapDbAudios;
        private Map<String, ProVideo> mmMapDbVideos;

        //
        private ArrayList<ProAudio> mmListNewAudios = new ArrayList<>();
        private ArrayList<ProVideo> mmListNewVideos = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
