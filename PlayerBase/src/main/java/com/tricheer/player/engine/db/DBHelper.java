package com.tricheer.player.engine.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tricheer.player.engine.db.Tables.MusicCacheInfo;
import com.tricheer.player.engine.db.Tables.MusicOnlineSearchInfo;
import com.tricheer.player.engine.db.Tables.VideoCacheInfo;
import com.tricheer.player.utils.PlayerFileUtils;

/**
 * DataBase Create
 * 
 * @author Jun.Wang
 */
public class DBHelper extends SQLiteOpenHelper {
	/**
	 * Database Copy Number
	 * <p/>
	 * History Copy Number : 1
	 * <p/>
	 * Now : 1
	 */
	public static final int DB_VERSION = 1;

	/**
	 * 数据库名称
	 */
	private static final String DB_NAME = PlayerFileUtils.getDBPath() + "/TricheerMusicPlayer.sqlite";

	/**
	 * @param context
	 *            ：上下文环境
	 */
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MusicCacheInfo.SQL_CREATE);
		db.execSQL(MusicOnlineSearchInfo.SQL_CREATE);
		db.execSQL(VideoCacheInfo.SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
