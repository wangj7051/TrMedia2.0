package js.lib.android.media.player;

/**
 * Play State
 *
 * @author Jun.Wang
 */
public enum PlayState {
    NONE(0, ""),
    RESET(1, ""),
    PLAY(2, ""),
    PREPARED(3, ""),
    PAUSE(4, ""),
    COMPLETE(5, ""),
    STOP(6, ""),
    ERROR(7, ""),
    SEEK_COMPLETED(8, ""),
    RELEASE(9, ""),

    /**
     * Cause Error, Player Initialization failed.
     */
    ERROR_PLAYER_INIT(100, ""),
    /**
     * ERROR : File is not exist.
     */
    ERROR_FILE_NOT_EXIST(101, ""),
    /**
     * Notify Refresh UI, EXEC before Prepare() or PrepareSync();
     */
    REFRESH_UI(200, ""),
    /**
     * Notify Refresh UI, EXEC before Prepare() or PrepareSync();
     */
    REFRESH_ON_ERROR(201, "");

    private int mValue;
    private String mKey;

    PlayState(int val, String key) {
        mValue = val;
        mKey = key;
    }

    public int getValue() {
        return mValue;
    }

    public String getKey() {
        return mKey;
    }
}
