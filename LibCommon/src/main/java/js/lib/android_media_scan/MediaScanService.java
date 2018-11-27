package js.lib.android_media_scan;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.ProVideo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android_media_scan.parse_controller.ParseAudioController;
import js.lib.android_media_scan.parse_controller.ParseMediaController;
import js.lib.android_media_scan.parse_controller.ParseVideoController;
import js.lib.android_media_scan.scan_controller.MediaScanController;

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
     * {@link Context} object
     */
    private Context mContext;

    /**
     * {@link MediaScanController} Object
     */
    private MediaScanController mMediaScanController;

    /**
     * {@link ParseAudioController} object
     */
    private ParseAudioController mParseAudioController;

    /**
     * {@link ParseVideoController} object
     */
    private ParseVideoController mParseVideoController;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        Log.i(TAG, "init()");
        mContext = this;
        mMediaScanController = new MediaScanController();
        mParseAudioController = new ParseAudioController(this);
        mParseVideoController = new ParseVideoController(this);
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
            mMediaScanController.setMediaScanListener(new MediaScanResp());
            mMediaScanController.startListMediaTask(this);
        }
    }

    private class MediaScanResp implements MediaScanController.MediaScanListener {
        @Override
        public void onMediaScanningStart() {
            Log.i(TAG, "MediaScanResp->onMediaScanningStart()");
            notifyScanningStart();
        }

        @Override
        public void onMediaScanningCancel() {
            Log.i(TAG, "MediaScanResp->onMediaScanningCancel()");
            notifyScanningCancel();
        }

        @Override
        public void onMediaScanningAudioEnd(boolean isHasMedias) {
            Log.i(TAG, "MediaScanResp->onMediaScanningAudioEnd(" + isHasMedias + ")");
            notifyScanningAudioEnd(isHasMedias);
            if (mParseAudioController != null) {
                final List<ProAudio> listNewMedias = mMediaScanController.getNewAudios();
                if (EmptyUtil.isEmpty(listNewMedias)) {
                    Log.i(TAG, "startParseMedias()-Audio-1--NUM:0--");
                    notifyParseEnd(0);
                } else {
                    Log.i(TAG, "startParseMedias()-Audio-2--NUM:" + listNewMedias.size() + "--");
                    mParseAudioController.startParseMediaTask(listNewMedias, new ParseMediaController.ParseMediaDelegate() {
                        @Override
                        public void onParseEnd() {
                            Log.i(TAG, "startParseMedias()-Audio-3--onParseEnd()--");
                            notifyParseEnd(0);
                        }
                    });
                }
            }
        }

        @Override
        public void onMediaScanningVideoEnd(boolean isHasMedias) {
            Log.i(TAG, "MediaScanResp->onMediaScanningAudioEnd(" + isHasMedias + ")");
            notifyScanningVideoEnd(isHasMedias);
            if (mParseVideoController != null) {
                final List<ProVideo> listNewMedias = mMediaScanController.getNewVideos();
                if (EmptyUtil.isEmpty(listNewMedias)) {
                    Log.i(TAG, "startParseMedias()-Video-1--NUM:0--");
                    notifyParseEnd(1);
                } else {
                    Log.i(TAG, "startParseMedias()-Video-2--NUM:" + listNewMedias.size() + "--");
                    mParseVideoController.startParseMediaTask(listNewMedias, new ParseMediaController.ParseMediaDelegate() {
                        @Override
                        public void onParseEnd() {
                            Log.i(TAG, "startParseMedias()-Video-3--onParseEnd()--");
                            notifyParseEnd(1);
                        }
                    });
                }
            }
        }

        @Override
        public void onMediaScanningRespAudio(List<ProAudio> listMedias, boolean isOnUiThread) {
            Log.i(TAG, "MediaScanResp->onMediaScanningRespAudio()");
            notifyAudioScanningRefresh(listMedias, isOnUiThread);
        }

        @Override
        public void onMediaScanningRespVideo(List<ProVideo> listMedias, boolean isOnUiThread) {
            Log.i(TAG, "MediaScanResp->onMediaScanningRespVideo()");
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
            mMediaScanController.refreshMountStatus(mContext);
            mMediaScanController.destroy();
        }
        if (mParseAudioController != null) {
            mParseAudioController.destroy();
        }
        if (mParseVideoController != null) {
            mParseVideoController.destroy();
        }
    }
}
