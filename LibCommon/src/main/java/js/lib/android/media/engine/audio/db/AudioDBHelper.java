package js.lib.android.media.engine.audio.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DataBase Create
 *
 * @author Jun.Wang
 */
public class AudioDBHelper extends SQLiteOpenHelper {
    //TAG
    private static final String TAG = "AudioDBHelper";

    /**
     * {@link Context}
     */
    private Context mContext;

    /**
     * Database name
     */
    private String mDbName;

    /**
     * Database Copy Number
     * <p/>
     * History Copy Number : 1,2
     * <p/>
     * Now : 3
     */
    private static final int DB_VERSION = 3;

    /**
     * @param context ：上下文环境
     */
    public AudioDBHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
        mContext = context;
        mDbName = dbName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate(SQLiteDatabase)");
        db.execSQL(AudioTables.AudioCacheInfo.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Delete
        if (mContext != null) {
            boolean delRes = mContext.deleteDatabase(mDbName);
            Log.i(TAG, "delRes : " + delRes);
        }

        //Create
        db.execSQL(AudioTables.AudioCacheInfo.SQL_CREATE);
    }
}
