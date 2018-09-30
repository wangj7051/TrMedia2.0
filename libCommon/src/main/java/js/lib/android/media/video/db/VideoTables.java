package js.lib.android.media.video.db;

/**
 * 表信息
 *
 * @author Jun.Wang
 */
public interface VideoTables {
    /**
     * Video Cache Info
     */
    public interface VideoCacheInfo {
        // Table Name
        String T_NAME = "VideoCacheInfo";

        // ID
        String ID = "id";

        // Media Title
        String TITLE = "TITLE";
        String TITLE_PINYIN = "titlePinYin";

        // Media URL
        String MEDIA_URL = "videoMeidaUrl";
        String MEDIA_DIRECTORY = "mediaDirectory";
        String MEDIA_DIRECTORY_PINYIN = "mediaDirectoryPinYin";

        // Media Duration
        String DURATION = "duration";

        // Is Collected
        String IS_COLLECT = "isCollected";

        // Media CoverImage URL
        String COVER_URL = "coverUrl";

        // Record Create Time
        String CREATE_TIME = "createTime";
        // Record Update Time
        String UPDATE_TIME = "updateTime";

        // Table Create SQL
        String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + T_NAME
                + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TITLE + " TEXT,"
                + MEDIA_URL + " TEXT,"
                + DURATION + " TEXT,"
                + COVER_URL + " LONG,"
                + IS_COLLECT + " INTEGER DEFAULT 0,"
                + CREATE_TIME + " LONG,"
                + UPDATE_TIME + " LONG"
                + ")";
    }
}