package js.lib.android_media_scan;

import android.app.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.ProVideo;

/**
 * Scan service base.
 * <p>Supply callback.</p>
 *
 * @author Jun.Wang
 */
public abstract class BaseScanService extends Service {
    public static final String PARAM_SCAN = "PARAM_SCAN";
    public static final String PARAM_SCAN_VAL_START = "START_LIST_ALL_MEDIAS";
    public static final String PARAM_SCAN_VAL_CANCEL = "CANCEL_LIST_ALL_MEDIAS";

    /**
     * Scanning listener
     */
    private Set<IMediaScanDelegate> mSetScanDelegates = new LinkedHashSet<>();

    /**
     * Audio scan delegate.
     */
    public interface AudioScanDelegate extends IMediaScanDelegate {
        void onMediaScanningRefresh(List<ProAudio> listMedias, boolean isOnUiThread);

        void onMediaScanningEnd(boolean isHasMedias);
    }

    /**
     * Video scan delegate.
     */
    public interface VideoScanDelegate extends IMediaScanDelegate {
        void onMediaScanningRefresh(List<ProVideo> listMedias, boolean isOnUiThread);

        void onMediaScanningEnd(boolean isHasMedias);
    }

    public void register(IMediaScanDelegate delegate) {
        if (delegate != null) {
            mSetScanDelegates.add(delegate);
        }
    }

    public void unregister(IMediaScanDelegate delegate) {
        if (delegate != null) {
            mSetScanDelegates.remove(delegate);
        }
    }

    /**
     * Notify scanning start.
     */
    void notifyScanningStart() {
        try {
            for (IMediaScanDelegate delegate : mSetScanDelegates) {
                delegate.onMediaScanningStart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify scanning end.
     */
    void notifyScanningAudioEnd(boolean isHasMedias) {
        try {
            for (IMediaScanDelegate delegate : mSetScanDelegates) {
                if (delegate instanceof AudioScanDelegate) {
                    ((AudioScanDelegate) delegate).onMediaScanningEnd(isHasMedias);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify scanning end.
     */
    void notifyScanningVideoEnd(boolean isHasMedias) {
        try {
            for (IMediaScanDelegate delegate : mSetScanDelegates) {
                if (delegate instanceof VideoScanDelegate) {
                    ((VideoScanDelegate) delegate).onMediaScanningEnd(isHasMedias);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify parse end.
     *
     * @param type 0-Audio ; 1-Video
     */
    public void notifyParseEnd(int type) {
        try {
            for (IMediaScanDelegate delegate : mSetScanDelegates) {
                delegate.onMediaParseEnd(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify scanning cancel.
     */
    void notifyScanningCancel() {
        try {
            for (IMediaScanDelegate delegate : mSetScanDelegates) {
                delegate.onMediaScanningCancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Audio - Notify scanning refresh.
     *
     * @param listMedias   Delta audio list.
     * @param isOnUiThread If callback is in UI thread.
     */
    void notifyAudioScanningRefresh(final List<ProAudio> listMedias, boolean isOnUiThread) {
        for (IMediaScanDelegate delegate : mSetScanDelegates) {
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
    void notifyVideoScanningRefresh(final List<ProVideo> listMedias, boolean isOnUiThread) {
        for (IMediaScanDelegate delegate : mSetScanDelegates) {
            if (delegate instanceof VideoScanDelegate) {
                ((VideoScanDelegate) delegate).onMediaScanningRefresh(listMedias, isOnUiThread);
            }
        }
    }

    @Override
    public void onDestroy() {
        mSetScanDelegates.clear();
        super.onDestroy();
    }
}
