// IMediaScanDelegate.aidl
package js.lib.android_media.scan;

// Declare any non-default types here with import statements

interface IMediaScanDelegate {
        void onMediaScanningStart(); // START
        void onMediaScanningEnd(boolean isHasMedias); //END
        void onMediaScanningCancel(); //CANCEL
}