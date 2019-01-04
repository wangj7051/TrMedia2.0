package js.lib.android_media.scan.audio;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android_media.scan.IMediaScanDelegate;
import js.lib.android_media.scan.IMediaScanService;
import js.lib.android_media.scan.audio.controller.AudioScanController;

/**
 * Media scan service.
 * <p>This service should start when the system boot completed.</p>
 *
 * @author Jun.Wang
 */
public class AudioScanService extends AudioScanServiceBase {
    //TAG
    private static final String TAG = "AudioScanService";

    /**
     * {@link Context} object
     */
    private Context mContext;

    /**
     * {@link AudioScanController} Object
     */
    private AudioScanController mMediaScanController;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        Log.i(TAG, "init()");
        mContext = this;
        mMediaScanController = new AudioScanController();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent != null) {
                String param = intent.getStringExtra(PARAM_SCAN);
                Log.i(TAG, "param : " + param);
                if (PARAM_SCAN_VAL_START.equals(param)) {
                    startList();
                } else if (PARAM_SCAN_VAL_CANCEL.equals(param)) {
                    destroyControllers();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends IMediaScanService.Stub {

        @Override
        public void startScan() {
            Log.i(TAG, "MyBinder->startScan()");
            startList();
        }

        @Override
        public void destroy() {
            Log.i(TAG, "MyBinder->destroy()");
            destroyControllers();
            stopSelf();
        }

        @Override
        public boolean isMediaScanning() {
            boolean isScanning = mMediaScanController != null && mMediaScanController.isMediaScanning();
            Log.i(TAG, "isScanning : " + isScanning);
            return isScanning;
        }

        @Override
        public void registerDelegate(IMediaScanDelegate delegate) {
            if (delegate != null) {
                Log.i(TAG, "MyBinder->registerDelegate(" + delegate.toString() + ")");
                register(delegate);
            }
        }

        @Override
        public void unregisterDelegate(IMediaScanDelegate delegate) {
            Log.i(TAG, "MyBinder->unregister(" + delegate.toString() + ")");
            unregister(delegate);
        }
    }

    private void startList() {
        Log.i(TAG, "startList()");
        if (mMediaScanController != null) {
            mMediaScanController.setMediaScanListener(new AudioScanResp());
            mMediaScanController.startListMediaTask(this);
        }
    }

    private class AudioScanResp implements AudioScanController.MediaScanListener {
        @Override
        public void onMediaScanningStart() {
            Log.i(TAG, "AudioScanResp->onMediaScanningStart()");
            notifyScanningStart();
        }

        @Override
        public void onMediaScanningCancel() {
            Log.i(TAG, "AudioScanResp->onMediaScanningCancel()");
            notifyScanningCancel();
        }

        @Override
        public void onMediaScanningEnd(boolean isHasMedias) {
            Log.i(TAG, "AudioScanResp->onMediaScanningEnd(" + isHasMedias + ")");
            notifyScanningEnd(isHasMedias);
        }

        @Override
        public void onMediaScanningResp(List<ProAudio> listMedias, boolean isOnUiThread) {
            Log.i(TAG, "AudioScanResp->onMediaScanningResp()");
            notifyAudioScanningRefresh(listMedias, isOnUiThread);
        }
    }

    @Override
    public void onDestroy() {
        //Destroy #mMediaScanController
        destroyControllers();
        super.onDestroy();
    }

    private void destroyControllers() {
        if (mMediaScanController != null) {
            mMediaScanController.destroy();
        }
    }
}
