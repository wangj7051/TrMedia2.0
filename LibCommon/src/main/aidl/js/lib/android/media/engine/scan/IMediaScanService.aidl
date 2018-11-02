// IMediaScanService.aidl
package js.lib.android.media.engine.scan;
import js.lib.android.media.engine.scan.IMediaScanDelegate;

// Declare any non-default types here with import statements

interface IMediaScanService {
    void startScan();
    boolean isMediaScanning();
    void registerDelegate(IMediaScanDelegate delegate);
    void unregisterDelegate(IMediaScanDelegate delegate);
}
