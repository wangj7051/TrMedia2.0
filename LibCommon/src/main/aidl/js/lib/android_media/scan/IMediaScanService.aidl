// IMediaScanService.aidl
package js.lib.android_media.scan;

import js.lib.android_media.scan.IMediaScanDelegate;

// Declare any non-default types here with import statements

interface IMediaScanService {
    void startScan();
    void destroy();
    boolean isMediaScanning();
    void registerDelegate(IMediaScanDelegate delegate);
    void unregisterDelegate(IMediaScanDelegate delegate);
}