package com.tricheer.player.engine.db;

/**
 * 表信息
 *
 * @author Jun.Wang
 */
public interface Tables {

    /**
     * Music Search Info
     */
    public interface MusicOnlineSearchInfo {
        // Table Name
        public final String T_NAME = "MusicOnlineSearchInfo";

        // ID
        public final String ID = "id";

        // Key
        public final String KEY = "key";
        public final String KEY_PINYIN = "keyPinYin";
        // Value
        public final String VALUE = "value";

        // Record Create Time
        public final String CREATE_TIME = "createTime";
        // Record Update Time
        public final String UPDATE_TIME = "updateTime";
        // Table Create SQL
        public final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + T_NAME + "(" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY + " TEXT," + KEY_PINYIN + " TEXT," + VALUE + " TEXT,"
                + CREATE_TIME + " LONG," + UPDATE_TIME + " LONG)";
    }

    /**
     * Music Cache Info
     */
    public interface MusicCacheInfo {
        // Table Name
        public final String T_NAME = "MusicCacheInfo";

        /**
         * ID
         */
        public final String ID = "id";

        /**
         * System Database Media ID
         * <p>From {@link android.content.ContentProvider}</p>
         */
        public final String SYS_MEDIA_ID = "sysMediaID";

        // Album ID
        public final String ALBUM_ID = "albumID";
        // Album Name
        public final String ALBUM_NAME = "albumName";
        // Media File Display Name,like "../11.mp3", "11" is display name.
        // public final String DISPLAY_NAME = "displayName";
        // Media Title, like "../11.mp3", see the file attribute, if the
        // attribute title like "Die Another Day", then the "Die Another Day" is
        // title.
        public final String TITLE = "title";
        public final String TITLE_PINYIN = "titlePinYin";
        // Media Artist
        public final String ARTIST = "artist";
        // Media Lyric
        public final String LYRIC = "lyric";
        // Media URL
        public final String MEDIA_URL = "mediaUrl";
        // Media CoverImage URL
        public final String COVER_URL = "coverUrl";
        // Media Duration
        public final String DURATION = "duration";
        // Parse MP3 Information By Mp3File
        public final String PARSE_INFO_FLAG = "parseInfoFlag";

        // IS Media From Net
        public final String IS_FROM_NET = "isFromNet";
        // Is Collected
        public final String IS_COLLECT = "isCollected";
        // Is Caused Error
        public final String IS_CAUSE_ERROR = "isCauseError";

        // Record Create Time
        public final String CREATE_TIME = "createTime";
        // Record Update Time
        public final String UPDATE_TIME = "updateTime";

        // Table Create SQL
        public final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + T_NAME + "(" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + SYS_MEDIA_ID + " LONG," + ALBUM_ID + " LONG," + ALBUM_NAME + " TEXT,"
                + TITLE + " TEXT," + TITLE_PINYIN + " TEXT," + ARTIST + " TEXT," + LYRIC + " TEXT," + MEDIA_URL + " TEXT,"
                + COVER_URL + " TEXT," + DURATION + " INTEGER," + PARSE_INFO_FLAG + " INTEGER,"

                + IS_FROM_NET + " INTEGER DEFAULT 0," + IS_COLLECT + " INTEGER DEFAULT 0," + IS_CAUSE_ERROR
                + " INTEGER DEFAULT 0,"

                + CREATE_TIME + " LONG," + UPDATE_TIME + " LONG)";
    }

    /**
     * Video Cache Info
     */
    public interface VideoCacheInfo {
        // Table Name
        public final String T_NAME = "VideoCacheInfo";

        // ID
        public final String ID = "id";

        // Media Title
        public final String TITLE = "TITLE";
        // Media URL
        public final String MEDIA_URL = "videoMeidaUrl";
        // Media Duration
        public final String DURATION = "duration";
        // Media CoverImage URL
        public final String COVER_URL = "coverUrl";

        // Is Collected
        public final String IS_COLLECT = "isCollected";
        // Is Caused Error
        public final String IS_CAUSE_ERROR = "isCauseError";

        // Record Create Time
        public final String CREATE_TIME = "createTime";
        // Record Update Time
        public final String UPDATE_TIME = "updateTime";

        // Table Create SQL
        public final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + T_NAME + "(" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + TITLE + " TEXT," + MEDIA_URL + " TEXT," + DURATION + " TEXT,"
                + COVER_URL + " LONG,"

                + IS_COLLECT + " INTEGER DEFAULT 0," + IS_CAUSE_ERROR + " INTEGER DEFAULT 0,"

                + CREATE_TIME + " LONG," + UPDATE_TIME + " LONG)";
    }
}