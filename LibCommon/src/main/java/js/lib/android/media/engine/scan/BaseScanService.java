package js.lib.android.media.engine.scan;

import android.app.Service;
import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.ProVideo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.android.utils.sdcard.SDCardInfo;
import js.lib.android.utils.sdcard.SDCardUtils;


/**
 * Scan service base.
 * <p>Supply callback.</p>
 *
 * @author Jun.Wang
 */
public abstract class BaseScanService extends Service {
    //TAG
    private static final String TAG = "BaseScanService";

    /**
     * Scanning listener
     */
    private Set<MediaScanDelegate> mSetScanDelegates = new LinkedHashSet<>();

    interface MediaScanDelegate {
        void onMediaScanningStart();

        void onMediaScanningEnd();

        void onMediaScanningCancel();
    }

    /**
     * Audio scan delegate.
     */
    public interface AudioScanDelegate extends MediaScanDelegate {
        void onMediaScanningRefresh(List<ProAudio> listMedias, boolean isOnUiThread);
    }

    /**
     * Video scan delegate.
     */
    public interface VideoScanDelegate extends MediaScanDelegate {
        void onMediaScanningRefresh(List<ProVideo> listMedias, boolean isOnUiThread);
    }

    public void register(MediaScanDelegate delegate) {
        if (delegate != null) {
            mSetScanDelegates.add(delegate);
        }
    }

    public void unregister(MediaScanDelegate delegate) {
        if (delegate != null) {
            mSetScanDelegates.remove(delegate);
        }
    }

    /**
     * Notify scanning start.
     */
    protected void notifyScanningStart() {
        for (MediaScanDelegate delegate : mSetScanDelegates) {
            delegate.onMediaScanningStart();
        }
    }

    /**
     * Notify scanning end.
     */
    protected void notifyScanningEnd() {
        for (MediaScanDelegate delegate : mSetScanDelegates) {
            delegate.onMediaScanningEnd();
        }
    }

    /**
     * Notify scanning cancel.
     */
    protected void notifyScanningCancel() {
        for (MediaScanDelegate delegate : mSetScanDelegates) {
            delegate.onMediaScanningCancel();
        }
    }

    /**
     * Audio - Notify scanning refresh.
     *
     * @param listMedias   Delta audio list.
     * @param isOnUiThread If callback is in UI thread.
     */
    protected void notifyAudioScanningRefresh(final List<ProAudio> listMedias, boolean isOnUiThread) {
        for (MediaScanDelegate delegate : mSetScanDelegates) {
            if (delegate instanceof AudioScanDelegate) {
                ((AudioScanDelegate) delegate).onMediaScanningRefresh(listMedias, isOnUiThread);
            }
        }
    }

    /**
     * Video - Notify scanning refresh.
     *
     * @param listMedias   Delta video list.
     * @param isOnUiThread If callback is in UI thread.
     */
    protected void notifyVideoScanningRefresh(final List<ProVideo> listMedias, boolean isOnUiThread) {
        for (MediaScanDelegate delegate : mSetScanDelegates) {
            if (delegate instanceof VideoScanDelegate) {
                ((VideoScanDelegate) delegate).onMediaScanningRefresh(listMedias, isOnUiThread);
            }
        }
    }

    /**
     * Refresh mount status of SdCard or UDisk.
     */
    public static void refreshMountStatus(Context context) {
        Log.i(TAG, "refreshMountStatus(Context)");
        if (context == null) {
            return;
        }

        // 挂载/未挂载 SDCard
        mSetPathsMounted.clear();
        mSetPathsUnMounted.clear();

        // 获取支持的挂载点
        List<String> listSupportPaths = PlayerFileUtils.getListSuppportPaths();
        // 如果SD支持路径列表为空，那么认为该设备支持所有盘符
        if (EmptyUtil.isEmpty(listSupportPaths)) {
            Log.i(TAG, "-- SUPPORT ALL --");
            HashMap<String, SDCardInfo> mapSDCardInfos = SDCardUtils.getSDCardInfos(context.getApplicationContext(), false);
            for (SDCardInfo temp : mapSDCardInfos.values()) {
                Logs.i(TAG, "refreshMountStatus() -1-> [temp:" + temp.label + "-" + temp.isMounted + "-" + temp.root);
                if (temp.isMounted) {
                    mSetPathsMounted.add(temp.root);
                } else {
                    mSetPathsUnMounted.add(temp.root);
                }
            }
            // 如果SD支持路径列表不为空，那么认为该设备只支持列表所含盘符
        } else {
            Log.i(TAG, "-- SUPPORT FIXED --");
            HashMap<String, SDCardInfo> mapAllSdCards = SDCardUtils.getSDCardInfos(context.getApplicationContext(), false);
            for (SDCardInfo temp : mapAllSdCards.values()) {
                Logs.i(TAG, "refreshMountStatus() -2-> [temp:" + temp.label + "-" + temp.isMounted + "-" + temp.root);
                if (listSupportPaths.contains(temp.root)) {
                    if (temp.isMounted) {
                        mSetPathsMounted.add(temp.root);
                    } else {
                        mSetPathsUnMounted.add(temp.root);
                    }
                }
            }
        }

        Logs.i(TAG, " *** Start ***");
        Logs.i(TAG, "mSetPathsMounted:" + mSetPathsMounted.toString());
        Logs.i(TAG, "mSetPathsUnMounted:" + mSetPathsUnMounted.toString());
        Logs.i(TAG, " ***  End  ***");
    }
}
