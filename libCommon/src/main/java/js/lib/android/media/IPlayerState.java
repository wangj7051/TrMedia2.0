package js.lib.android.media;

/**
 * Player State
 * 
 * @author Jun.Wang
 */
public class IPlayerState {
	// >>---- Common ----<<
	public static final int RESET = 1;
	public static final int PLAY = 2;
	public static final int PREPARED = 3;
	public static final int PAUSE = 4;
	public static final int COMPLETE = 5;
	public static final int STOP = 6;
	public static final int ERROR = 7;
	public static final int SEEK_COMPLETED = 8;
	public static final int RELEASE = 9;

	/**
	 * Cause Error, Player Initialization failed.
	 */
	public static final int ERROR_PLAYER_INIT = 100;
	/**
	 * ERROR : File is not exist.
	 */
	public static final int ERROR_FILE_NOT_EXIST = 101;

	/**
	 * Notify Refresh UI, EXEC before Prepare() or PrepareSync();
	 */
	public static final int REFRESH_UI = 200;
	/**
	 * Notify Refresh UI, EXEC before Prepare() or PrepareSync();
	 */
	public static final int REFRESH_ON_ERROR = 201;

	public static String getStateDesc(int state) {
		switch (state) {
		// ----Common----
		case RESET:
			return "RESET";
		case PLAY:
			return "PLAY";
		case PREPARED:
			return "PREPARED";
		case PAUSE:
			return "PAUSE";
		case COMPLETE:
			return "COMPLETE";
		case STOP:
			return "STOP";
		case ERROR:
			return "ERROR";
		case SEEK_COMPLETED:
			return "SEEK_COMPLETED";
		case RELEASE:
			return "RELEASE";

		case ERROR_PLAYER_INIT:
			return "ERROR_PLAYER_INIT";
		case ERROR_FILE_NOT_EXIST:
			return "ERROR_FILE_NOT_EXIST";

		case REFRESH_UI:
			return "REFRESH_UI";
		case REFRESH_ON_ERROR:
			return "REFRESH_ON_ERROR";
		}
		return "";
	}
}
