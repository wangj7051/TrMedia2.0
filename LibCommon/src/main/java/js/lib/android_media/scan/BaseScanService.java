package js.lib.android_media.scan;

import android.app.Service;

import java.util.LinkedHashSet;
import java.util.Set;

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
    protected Set<IMediaScanDelegate> mSetScanDelegates = new LinkedHashSet<>();

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

    @Override
    public void onDestroy() {
        mSetScanDelegates.clear();
        super.onDestroy();
    }
}
