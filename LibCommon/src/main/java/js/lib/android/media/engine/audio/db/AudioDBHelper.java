package js.lib.android.media.engine.audio.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DataBase Create
 *
 * @author Jun.Wang
 */
public class AudioDBHelper extends SQLiteOpenHelper {
    /**
     * Database Copy Number
     * <p/>
     * History Copy Number : 1
     * <p/>
     * Now : 1
     */
    static final int DB_VERSION = 1;

    /**
     * @param context ：上下文环境
     */
    public AudioDBHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AudioTables.AudioCacheInfo.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
