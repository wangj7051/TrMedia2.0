package js.lib.android.media.video.utils;

import android.net.Uri;
import android.provider.MediaStore;

import java.util.HashSet;
import java.util.Set;

/**
 * Video Information
 *
 * @author Jun.Wang
 */
public class VideoInfo {
    /**
     * Support Suffixes
     * <p>
     * like Set<".mp4"> ...
     */
    private static Set<String> mSetSuffixs = new HashSet<>();

    static {
        mSetSuffixs.add(".3gp");
        mSetSuffixs.add(".avi");
        mSetSuffixs.add(".mp4");
        mSetSuffixs.add(".mkv");
    }

    /**
     * MIME
     */
    public static final String[] MIME = {"video/*"};

    /**
     * Query System Media URI
     */
    public static final Uri QUERY_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    /**
     * MIME_TYPE
     */
    public String mimeType = "";
    public static final String _MIME_TYPE = MediaStore.Video.Media.MIME_TYPE;

    /**
     * Display Name
     */
    public String title = "", titlePinYin = "";
    public static final String _DISPLAY_NAME = MediaStore.Video.Media.DISPLAY_NAME;

    /**
     * Path
     */
    public String path = "", directory = "", directoryPinYin = "";
    public static final String _PATH = MediaStore.Video.Media.DATA;

    /**
     * Duration
     */
    public int duration = 0;
    public static final String _DURATION = MediaStore.Video.Media.DURATION;

    /**
     * Is Support Files
     */
    public static boolean isSupport(String suffix) {
        return mSetSuffixs.contains(suffix);
    }

    /**
     * Set Support Media Types
     */
    public static void setSupportMedias(boolean isSupportAll) {
        if (isSupportAll) {
            // mSetSuffixs.add(".3gp");
            // mSetSuffixs.add(".avi");
            mSetSuffixs.add(".flv");
            // mSetSuffixs.add(".mp4");
            // mSetSuffixs.add(".mkv");
            mSetSuffixs.add(".ts");
            mSetSuffixs.add(".mov");
            //mSetSuffixs.add(".mpg");
        }
    }
}
