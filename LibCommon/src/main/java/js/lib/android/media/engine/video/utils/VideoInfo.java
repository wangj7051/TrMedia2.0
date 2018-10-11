package js.lib.android.media.engine.video.utils;

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
    private static Set<String> mSetSuffixes = new HashSet<>();

    static {
        mSetSuffixes.add(".3gp");
        mSetSuffixes.add(".avi");
        mSetSuffixes.add(".mp4");
        mSetSuffixes.add(".mkv");
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
        return mSetSuffixes.contains(suffix);
    }

    /**
     * Set Support Media Types
     */
    public static void setSupportMedias(boolean isSupportAll) {
        if (isSupportAll) {
            // mSetSuffixes.add(".3gp");
            // mSetSuffixes.add(".avi");
            mSetSuffixes.add(".flv");
            // mSetSuffixes.add(".mp4");
            // mSetSuffixes.add(".mkv");
            mSetSuffixes.add(".ts");
            mSetSuffixes.add(".mov");
            //mSetSuffixes.add(".mpg");
        }
    }
}
