// IMediaScanDelegate.aidl
package js.lib.android.media.engine.scan;

// Declare any non-default types here with import statements

interface IMediaScanDelegate {
        void onMediaScanningStart();
        void onMediaScanningEnd();
        void onMediaScanningCancel();
}
