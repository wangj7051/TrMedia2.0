package js.lib.android.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQlite Database Manager
 * <p>
 * 使用该类，必须初始化 {@link DbUtil#init(Context)}
 * </p>
 * 
 * @author Jun.Wang
 */
public class DbUtil {
	/**
	 * 上下文
	 */
	protected static Context mContext;

	/**
	 * SQLiteDatabase Object
	 */
	protected static SQLiteDatabase mDB;

	/**
	 * 初始化
	 */
	public static void init(Context context) {
		mContext = context;
	}

	/**
	 * 打开数据库连接
	 */
	protected static boolean openDB(SQLiteOpenHelper helper) {
		if (mDB == null || !mDB.isOpen()) {
			try {
				mDB = helper.getWritableDatabase();
			} catch (Exception e) {
				e.printStackTrace();
				closeDB();
			}
		}
		return (mDB == null) ? false : mDB.isOpen();
	}

	/**
	 * 关闭数据库连接
	 */
	public static void closeDB() {
		if (mDB != null) {
			try {
				mDB.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mDB = null;
			}
		}
	}
}
