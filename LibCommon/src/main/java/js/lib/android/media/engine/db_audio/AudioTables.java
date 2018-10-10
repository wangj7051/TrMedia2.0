package js.lib.android.media.engine.db_audio;

/**
 * 表信息
 *
 * @author Jun.Wang
 */
public interface AudioTables {

    /**
     * Music Search Info
     */
    public interface MusicOnlineSearchInfo {
        // Table Name
        String T_NAME = "MusicOnlineSearchInfo";

        // ID
        String ID = "id";

        // Key
        String KEY = "key";
        String KEY_PINYIN = "keyPinYin";
        // Value
        String VALUE = "value";

        // Record Create Time
        String CREATE_TIME = "createTime";
        // Record Update Time
        String UPDATE_TIME = "updateTime";
        // Table Create SQL
        String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + T_NAME
                + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY + " TEXT,"
                + KEY_PINYIN + " TEXT,"
                + VALUE + " TEXT,"
                + CREATE_TIME + " LONG,"
                + UPDATE_TIME + " LONG"
                + ")";
    }

    /**
     * Music Cache Info
     */
    public interface MusicCacheInfo {
        // Table Name
        String T_NAME = "MusicCacheInfo";

        /**
         * ID
         * <p>If media is from online, id will be got from online.</p>
         */
        String ID = "id";

        /**
         * System Database Media ID
         * <p>From {@link android.content.ContentProvider}</p>
         */
        String SYS_MEDIA_ID = "sysMediaID";

        // Media File Display Name,like "../11.mp3", "11" is display name.
        //  String DISPLAY_NAME = "displayName";
        // Media Title, like "../11.mp3", see the file attribute, if the
        // attribute title like "Die Another Day", then the "Die Another Day" is
        // title.
        String TITLE = "title";
        String TITLE_PINYIN = "titlePinYin";

        // Album ID
        String ALBUM_ID = "albumID";
        // Album Name
        String ALBUM = "album";
        String ALBUM_PINYIN = "albumPinYin";

        // Media Artist
        String ARTIST = "artist";
        String ARTIST_PINYIN = "artistPinYin";

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

        // Media Lyric
        String LYRIC = "lyric";

        // Media source
        // 1~online ; 0~local
        String SOURCE = "source";

        // Record Create Time
        String CREATE_TIME = "createTime";
        // Record Update Time
        String UPDATE_TIME = "updateTime";

        // Table Create SQL
        String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + T_NAME
                + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SYS_MEDIA_ID + " LONG,"

                + TITLE + " TEXT,"
                + TITLE_PINYIN + " TEXT,"

                + ALBUM_ID + " LONG,"
                + ALBUM + " TEXT,"
                + ALBUM_PINYIN + " TEXT,"

                + ARTIST + " TEXT,"
                + ARTIST_PINYIN + " TEXT,"

                + MEDIA_URL + " TEXT,"
                + MEDIA_DIRECTORY + " TEXT,"
                + MEDIA_DIRECTORY_PINYIN + " TEXT,"

                + DURATION + " INTEGER,"
                + IS_COLLECT + " INTEGER DEFAULT 0,"
                + COVER_URL + " TEXT,"
                + LYRIC + " TEXT,"
                + SOURCE + " INTEGER,"

                + CREATE_TIME + " LONG,"
                + UPDATE_TIME + " LONG"
                + ")";
    }
}