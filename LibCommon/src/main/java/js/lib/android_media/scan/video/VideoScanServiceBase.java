package js.lib.android_media.scan.video;

import java.util.List;

import js.lib.android.media.bean.ProVideo;
import js.lib.android_media.scan.BaseScanService;
import js.lib.android_media.scan.IMediaScanDelegate;

/**
 * Scan service base.
 * <p>Supply callback.</p>
 *
 * @author Jun.Wang
 */
public abstract class VideoScanServiceBase extends BaseScanService {

    /**
     * Video scan delegate.
     */
    public interface VideoScanDelegate extends IMediaScanDelegate {
        void onMediaScanningRefresh(List<ProVideo> listMedias, boolean isOnUiThread);
    }

    /**
     * Notify scanning start.
     */
    protected void notifyScanningStart() {
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
    protected void notifyScanningEnd(boolean isHasMedias) {
        try {
            for (IMediaScanDelegate delegate : mSetScanDelegates) {
                delegate.onMediaScanningEnd(isHasMedias);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify scanning cancel.
     */
    protected void notifyScanningCancel() {
        try {
            for (IMediaScanDelegate delegate : mSetScanDelegates) {
                delegate.onMediaScanningCancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Video - Notify scanning refresh.
     *
     * @param listMedias   Delta video list.
     * @param isOnUiThread If callback is in UI thread.
     */
    protected void notifyVideoScanningRefresh(final List<ProVideo> listMedias, boolean isOnUiThread) {
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
