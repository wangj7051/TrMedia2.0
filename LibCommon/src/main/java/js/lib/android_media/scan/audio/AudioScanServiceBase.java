package js.lib.android_media.scan.audio;

import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android_media.scan.BaseScanService;
import js.lib.android_media.scan.IMediaScanDelegate;

/**
 * Scan service base.
 * <p>Supply callback.</p>
 *
 * @author Jun.Wang
 */
public abstract class AudioScanServiceBase extends BaseScanService {

    /**
     * Audio scan delegate.
     */
    public interface AudioScanDelegate extends IMediaScanDelegate {
        void onMediaScanningRefresh(List<ProAudio> listMedias, boolean isOnUiThread);
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

    @Override
    public void onDestroy() {
        mSetScanDelegates.clear();
        super.onDestroy();
    }
}
