package js.lib.android.media.video.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DataBase Create
 *
 * @author Jun.Wang
 */
public class VideoDBHelper extends SQLiteOpenHelper {
    /**
     * Database Copy Number
     * <p/>
     * History Copy Number : 1
     * <p/>
     * Now : 1
     */
    private static final int DB_VERSION = 1;

    /**
     * @param context ：上下文环境
     */
    public VideoDBHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VideoTables.VideoCacheInfo.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
