package js.lib.android_media.scan.video;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import js.lib.android.media.bean.ProVideo;
import js.lib.android_media.scan.IMediaScanDelegate;
import js.lib.android_media.scan.IMediaScanService;
import js.lib.android_media.scan.video.controller.VideoScanController;

/**
 * Media scan service.
 * <p>This service should start when the system boot completed.</p>
 *
 * @author Jun.Wang
 */
public class VideoScanService extends VideoScanServiceBase {
    //TAG
    private static final String TAG = "VideoScanService";

    /**
     * {@link Context} object
     */
    private Context mContext;

    /**
     * {@link VideoScanController} Object
     */
    private VideoScanController mMediaScanController;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        Log.i(TAG, "init()");
        mContext = this;
        mMediaScanController = new VideoScanController();
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
            mMediaScanController.setMediaScanListener(new VideoScanResp());
            mMediaScanController.startListMediaTask(this);
        }
    }

    private class VideoScanResp implements VideoScanController.MediaScanListener {
        @Override
        public void onMediaScanningStart() {
            Log.i(TAG, "VideoScanResp->onMediaScanningStart()");
            notifyScanningStart();
        }

        @Override
        public void onMediaScanningCancel() {
            Log.i(TAG, "VideoScanResp->onMediaScanningCancel()");
            notifyScanningCancel();
        }

        @Override
        public void onMediaScanningEnd(boolean isHasMedias) {
            Log.i(TAG, "VideoScanResp->onMediaScanningAudioEnd(" + isHasMedias + ")");
            notifyScanningEnd(isHasMedias);
        }

        @Override
        public void onMediaScanningResp(List<ProVideo> listMedias, boolean isOnUiThread) {
            Log.i(TAG, "VideoScanResp->onMediaScanningRespVideo()");
            notifyVideoScanningRefresh(listMedias, isOnUiThread);
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
