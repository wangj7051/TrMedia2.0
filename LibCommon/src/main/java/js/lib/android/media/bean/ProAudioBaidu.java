package js.lib.android.media.bean;

/**
 * BaiDu Music Bean
 *
 * @author Jun.Wang
 */
public class ProAudioBaidu extends ProAudio {

    /**
     * Serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * The music MV Link Number
     */
    public int hasMV;

    /**
     * Song Link / Show Link
     */
    public String songLink = "", showLink = "";

    /**
     * Song Pictures
     */
    public String songPicSmall = "", songPicBig = "", songPicRadio = "";
}
