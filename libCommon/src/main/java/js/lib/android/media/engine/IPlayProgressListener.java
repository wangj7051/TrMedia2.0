package js.lib.android.media.engine;

/**
 * Play progress Listener
 *
 * @author Jun.Wang
 */
public interface IPlayProgressListener {
    /**
     * Progress change callback
     */
    void onProgressChanged(String mediaPath, int progress, int duration);
}
