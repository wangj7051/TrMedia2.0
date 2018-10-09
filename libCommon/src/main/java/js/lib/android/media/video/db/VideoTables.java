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
        String MEDIA_URL = "mediaUrl";
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
                + TITLE_PINYIN + " TEXT,"

                + MEDIA_URL + " TEXT,"
                + MEDIA_DIRECTORY + " TEXT,"
                + MEDIA_DIRECTORY_PINYIN + " TEXT,"

                + DURATION + " TEXT,"
                + IS_COLLECT + " INTEGER DEFAULT 0,"

                + COVER_URL + " LONG,"
                + CREATE_TIME + " LONG,"
                + UPDATE_TIME + " LONG"
                + ")";
    }
}