package js.lib.android.media.player.audio;

import js.lib.android.media.player.PlayDelegate;

/**
 * Define Player actions
 *
 * @author Jun.Wang
 */
public interface IAudioPlayer {

    void playMedia(String mediaUrl);

    void playMedia();

    void pauseMedia();

    void resumeMedia();

    void resetMedia();

    void stopMedia();

    void releaseMedia();

    /**
     * Is media playing now.
     *
     * @return true-Playing.
     */
    boolean isMediaPlaying();

    /**
     * Current time position in the duration.
     *
     * @return int~Microsecond
     */
    int getMediaTime();

    /**
     * Media duration, total time of this media
     *
     * @return @return int~Microsecond
     */
    int getMediaDuration();

    /**
     * Seek media to select position in the duration.
     *
     * @param microsecond int~Microsecond
     */
    void seekMediaTo(int microsecond);

    /**
     * Set Target MediaUrl
     */
    void setMediaPath(String path);

    /**
     * Get current Media URL
     */
    String getMediaPath();

    /**
     * Set Player delegate
     */
    void setPlayerDelegate(PlayDelegate l);

    /**
     * 设置左右声道声音比率
     *
     * @param leftVolume  [0f~1f]
     * @param rightVolume [0f~1f]
     */
    void setVolume(float leftVolume, float rightVolume);
}
