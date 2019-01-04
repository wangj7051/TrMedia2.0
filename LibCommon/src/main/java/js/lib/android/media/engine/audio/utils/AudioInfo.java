package js.lib.android.media.engine.audio.utils;

import android.net.Uri;
import android.provider.MediaStore;

import java.util.HashSet;
import java.util.Set;

import js.lib.android.media.engine.MediaUtils;
import js.lib.bean.BaseBean;

/**
 * SDCard Audio Information Bean
 * <p>
 * Audio Decoders :
 * PCM playback;AAC/AAC+/eAAC+;MP3;WMA (v9 and v10);WMALossless;WMAPro10;AMR-NB
 * AMR-WB;FLAC;ALAC;Vorbis;AIFF;APE;AC3 (from Dolby);eAC3 (from Dolby)
 * </p>
 * <p>
 * Audio Encoders:
 * PCM recording;AAC;AMR-NB;AMR-WB;EVRC;QCELP;AAC5.1
 * </p>
 *
 * @author Jun.Wang
 */
public class AudioInfo extends BaseBean {

    /**
     * Serialization
     */
    private static final long serialVersionUID = 1L;

    //媒体格式后缀
    private static final String AUDIO_QUALCOMM_MP3 = ".mp3";//高通支持
    private static final String AUDIO_QUALCOMM_AAC = ".aac";//高通支持
    private static final String AUDIO_QUALCOMM_FLAC = ".flac";//高通支持
    private static final String AUDIO_QUALCOMM_APE = ".ape";//高通支持
    private static final String AUDIO_QUALCOMM_WAV = ".wav";//高通支持
    private static final String AUDIO_QUALCOMM_M4A = ".m4a";//高通支持播放,但不支持拖动
    //Others
//    private static final String AUDIO_WMA = ".wma";//高通解码不支持,平台支持-需要软解码

    /**
     * Support Suffixes
     * <p>like Set<".mp3"> ...</p>
     */
    private static Set<String> mSetSuffixs = new HashSet<>();
    private static Set<String> mSetSuffixsOfQualcommSupport = new HashSet<>();

    static {
        //Add qualcomm support
        mSetSuffixsOfQualcommSupport.add(AUDIO_QUALCOMM_MP3);
        mSetSuffixsOfQualcommSupport.add(AUDIO_QUALCOMM_AAC);
        mSetSuffixsOfQualcommSupport.add(AUDIO_QUALCOMM_FLAC);
        mSetSuffixsOfQualcommSupport.add(AUDIO_QUALCOMM_APE);
        mSetSuffixsOfQualcommSupport.add(AUDIO_QUALCOMM_WAV);
        mSetSuffixsOfQualcommSupport.add(AUDIO_QUALCOMM_M4A);
        //Add all
        mSetSuffixs.addAll(mSetSuffixsOfQualcommSupport);
//        mSetSuffixs.add(AUDIO_WMA);
    }

    /**
     * MIME
     */
    public static final String[] MIME = {"audio/*"};

    /**
     * Query System Media URI
     */
    public static final Uri QUERY_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    /**
     * Query System Media QUERY_PROJECTION
     */
    public static final String[] QUERY_PROJECTION = new String[]{AudioInfo._SYS_MEDIA_ID, AudioInfo._MIME_TYPE,
            AudioInfo._DISPLAY_NAME, AudioInfo._TITLE, AudioInfo._ARTIST, AudioInfo._ALBUM, AudioInfo._ALBUM_ID, AudioInfo._PATH,
            AudioInfo._DURATION};

    /**
     * Media ID from System Database
     */
    public int sysMediaID = 0;
    public static final String _SYS_MEDIA_ID = MediaStore.Audio.Media._ID;

    /**
     * MIME_TYPE
     */
    public String mimeType = "";
    public static final String _MIME_TYPE = MediaStore.Audio.Media.MIME_TYPE;

    /**
     * Display Name
     */
    public String displayName = "";
    public static final String _DISPLAY_NAME = MediaStore.Audio.Media.DISPLAY_NAME;

    /**
     * Title
     */
    public String title = "", titlePinYin = "";
    public static final String _TITLE = MediaStore.Audio.Media.TITLE;

    /**
     * Title
     */
    public String artist = "", artistPinYin = "";
    public static final String _ARTIST = MediaStore.Audio.Media.ARTIST;

    /**
     * Album ID
     */
    public long albumID = 0;
    public static final String _ALBUM_ID = MediaStore.Audio.Media.ALBUM_ID;

    /**
     * Album
     */
    public String album = "", albumPinYin = "";
    public static final String _ALBUM = MediaStore.Audio.Media.ALBUM;

    /**
     * Path
     */
    public String path = "", directory = "", directoryPinYin = "";
    public static final String _PATH = MediaStore.Audio.Media.DATA;

    /**
     * Duration
     */
    public int duration = 0;
    public static final String _DURATION = MediaStore.Audio.Media.DURATION;

    /**
     * Is Support Files
     */
    public static boolean isSupport(String suffix) {
        return mSetSuffixs.contains(suffix);
    }

    /**
     * If media that qualcomm support.
     *
     * @param fPathOrDisplayName media name or path.
     */
    public static boolean isQualcommSupport(String fPathOrDisplayName) {
        String suffix = MediaUtils.getSuffix(fPathOrDisplayName);
        return mSetSuffixsOfQualcommSupport.contains(suffix);
    }
}
