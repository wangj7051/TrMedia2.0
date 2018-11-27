// IMediaScanDelegate.aidl
package js.lib.android_media_scan;

// Declare any non-default types here with import statements

interface IMediaScanDelegate {
        void onMediaScanningStart(); // START
        void onMediaScanningNew(); // Found new medias.
        void onMediaScanningEnd(); // END
        void onMediaScanningCancel(); //CANCEL
        void onMediaParseEnd(int type); //PARSE END 0-Audio ,1-Video
}
