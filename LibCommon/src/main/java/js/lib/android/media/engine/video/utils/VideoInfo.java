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

    //媒体格式后缀
    private static final String AUDIO_QUALCOMM_MP4 = ".mp4";
    private static final String AUDIO_QUALCOMM_AVI = ".avi";
    private static final String AUDIO_QUALCOMM_MKV = ".mkv";
    private static final String AUDIO_QUALCOMM_3GP = ".3gp";
    //Others
    private static final String AUDIO_FLV = ".flv";
    private static final String AUDIO_MOV = ".mov";
    private static final String AUDIO_VOB = ".vob";
    private static final String AUDIO_TS = ".ts";

    static {
        mSetSuffixes.add(AUDIO_QUALCOMM_MP4);
        mSetSuffixes.add(AUDIO_QUALCOMM_AVI);
        mSetSuffixes.add(AUDIO_QUALCOMM_MKV);
        mSetSuffixes.add(AUDIO_QUALCOMM_3GP);
        mSetSuffixes.add(AUDIO_FLV);
        mSetSuffixes.add(AUDIO_MOV);
        mSetSuffixes.add(AUDIO_VOB);
        mSetSuffixes.add(AUDIO_TS);
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
}
