package js.lib.android.media.video.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import js.lib.android.media.video.bean.ProVideo;
import js.lib.android.media.video.db.VideoTables.VideoCacheInfo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * 该类用来处理数据库操作
 *
 * @author Jun.Wang
 */
public class VideoDBManager {
    // TAG
    private static final String TAG = "VideoDBManager";

    /**
     * 上下文
     */
    private Context mContext;
    /**
     * It should be similar to "/sdcard/Music/TrVideo.sqlite"
     */
    private String mDbName = "";

    /**
     * SQLiteDatabase Object
     */
    private SQLiteDatabase mDB;

    private VideoDBManager() {
    }

    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private static final VideoDBManager INSTANCE = new VideoDBManager();
    }

    public static VideoDBManager instance() {
        return VideoDBManager.SingletonHolder.INSTANCE;
    }

    /**
     * Initialize
     *
     * @param context {@link Context}
     * @param dbName  It should be similar to "/sdcard/Music/TrAudio.sqlite"
     */
    public void init(Context context, String dbName) {
        mContext = context;
        mDbName = dbName;
    }

    /**
     * 打开数据库连接
     */
    private boolean openDB() {
        if (mDB == null || !mDB.isOpen()) {
            try {
                SQLiteOpenHelper helper = new VideoDBHelper(mContext, mDbName);
                mDB = helper.getWritableDatabase();
            } catch (Exception e) {
                e.printStackTrace();
                closeDB();
            }
        }
        return mDB != null && mDB.isOpen();
    }

    /**
     * 关闭数据库连接
     */
    private void closeDB() {
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

    /**
     * 关闭数据库信息
     *
     * @param cur              : {@link Cursor}
     * @param isEndTransaction : is Close DBTransaction
     * @param isCloseDB        : isClose DB
     */
    private void closeDBInfo(Cursor cur, boolean isEndTransaction, boolean isCloseDB) {
        try {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
            if (isEndTransaction) {
                mDB.endTransaction();
            }
            if (isCloseDB) {
                closeDB();
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "closeDBInfo()", e);
        }
    }

    /**
     * Construct ContentValues By ProVideo Object
     */
    private static ContentValues getContentValues(ProVideo program, long currTime) {
        ContentValues cvs = new ContentValues();
        cvs.put(VideoCacheInfo.TITLE, program.title);
        cvs.put(VideoCacheInfo.TITLE_PINYIN, program.titlePinYin);
        cvs.put(VideoCacheInfo.MEDIA_URL, program.mediaUrl);
        cvs.put(VideoCacheInfo.MEDIA_DIRECTORY, program.mediaDirectory);
        cvs.put(VideoCacheInfo.MEDIA_DIRECTORY_PINYIN, program.mediaDirectoryPinYin);
        cvs.put(VideoCacheInfo.DURATION, program.duration);
        cvs.put(VideoCacheInfo.IS_COLLECT, program.isCollected);
        cvs.put(VideoCacheInfo.COVER_URL, program.coverUrl);
        if (currTime >= 0) {
            cvs.put(VideoCacheInfo.CREATE_TIME, currTime);
            cvs.put(VideoCacheInfo.UPDATE_TIME, 0);
        } else {
            cvs.put(VideoCacheInfo.CREATE_TIME, program.createTime);
            cvs.put(VideoCacheInfo.UPDATE_TIME, program.updateTime);
        }
        return cvs;
    }

    /**
     * Insert new Videos
     */
    public int insertListVideos(List<ProVideo> listPrograms) {
        int count = 0;
        if (openDB()) {
            Throwable throwable = null;
            try {
                mDB.beginTransaction();
                long currTime = System.currentTimeMillis();
                for (ProVideo program : listPrograms) {
                    if (mDB.insert(VideoCacheInfo.T_NAME, null, getContentValues(program, currTime)) > 0) {
                        count++;
                    }
                }
                mDB.setTransactionSuccessful();
            } catch (Exception e) {
                count = -1;
                throwable = e;
                Logs.printStackTrace(TAG + "insertNewVideos()", e);
            } finally {
                closeDBInfo(null, true, throwable != null);
            }
        }
        return count;
    }

    /**
     * Clear Video Cache Information
     */
    public void deleteVideos(String clearPath) {
        if (openDB()) {
            Throwable throwable = null;
            try {
                String whereClause = null;
                if (!EmptyUtil.isEmpty(clearPath)) {
                    whereClause = VideoCacheInfo.MEDIA_URL + " like '%" + clearPath + "%'";
                }
                mDB.delete(VideoCacheInfo.T_NAME, whereClause, null);
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "clearVideos()", e);
            } finally {
                closeDBInfo(null, false, throwable != null);
            }
        }
    }

    /**
     * Update List<Video>
     */
    public void updateListVideos(List<ProVideo> listVideos) {
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                mDB.beginTransaction();
                // Update Videos
                String table = VideoCacheInfo.T_NAME;
                String selection = VideoCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = null;
                for (ProVideo program : listVideos) {
                    selectionArgs = new String[]{program.mediaUrl};
                    int rowsNum = mDB.update(table, getContentValues(program, -1), selection, selectionArgs);
                    Log.i(TAG, "updateListVideos - rowsNum: " + rowsNum);
                }
                mDB.setTransactionSuccessful();
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "updateListVideos()", e);
            } finally {
                closeDBInfo(cur, true, throwable != null);
            }
        }
    }

    /**
     * Update Program Information to Database
     */
    public void updateProgramInfo(ProVideo program) {
        if (program != null && openDB()) {
            Throwable throwable = null;
            try {
                String selection = VideoCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{program.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(VideoCacheInfo.UPDATE_TIME, System.currentTimeMillis());
                cvs.put(VideoCacheInfo.DURATION, program.duration);

                int rowsNum = mDB.update(VideoCacheInfo.T_NAME, cvs, selection, selectionArgs);
                Log.i(TAG, "updateProgramInfo - rowsNum: " + rowsNum);
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "updateProgramInfo()", e);
            } finally {
                closeDBInfo(null, false, throwable != null);
            }
        }
    }

    /**
     * Update Media Duration
     */
    public void updateMediaDuration(ProVideo media) {
        if (media != null && openDB()) {
            Throwable throwable = null;
            try {
                String selection = VideoCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{media.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(VideoCacheInfo.DURATION, media.duration);
                int rowsNum = mDB.update(VideoCacheInfo.T_NAME, cvs, selection, selectionArgs);
                Log.i(TAG, "updateMediaDuration - rowsNum: " + rowsNum);
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "updateMediaDuration()", e);
            } finally {
                closeDBInfo(null, false, throwable != null);
            }
        }
    }

    public List<ProVideo> getListVideos(boolean isContainError, boolean isContainDrivingRecord) {
        List<ProVideo> listVideos = new ArrayList<ProVideo>();
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                cur = mDB.query(VideoCacheInfo.T_NAME, new String[]{"*"}, null, null, null, null, VideoCacheInfo.TITLE);
                if (cur != null) {
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        // Filter Program that not exist
                        String mediaUrl = cur.getString(cur.getColumnIndex(VideoCacheInfo.MEDIA_URL));
                        File musicFile = new File(mediaUrl);
                        if (!musicFile.exists()) {
                            continue;
                        }

                        ProVideo program = new ProVideo();
                        program.mediaUrl = mediaUrl;
                        program.title = cur.getString(cur.getColumnIndex(VideoCacheInfo.TITLE));
                        program.duration = cur.getInt(cur.getColumnIndex(VideoCacheInfo.DURATION));
                        listVideos.add(program);
                    }
                }
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "getListVideos()", e);
            } finally {
                closeDBInfo(cur, false, throwable != null);
            }
        }
        return listVideos;
    }

    /**
     * Get Map Videos
     */
    public Map<String, ProVideo> getMapVideos(boolean isContainError, boolean isContainDrivingRecord) {
        Map<String, ProVideo> mapVideos = new HashMap<String, ProVideo>();
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                cur = mDB.query(VideoCacheInfo.T_NAME, new String[]{"*"}, null, null, null, null, null);
                if (cur != null) {
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        // Filter Program that not exist
                        String mediaUrl = cur.getString(cur.getColumnIndex(VideoCacheInfo.MEDIA_URL));
                        File musicFile = new File(mediaUrl);
                        if (!musicFile.exists()) {
                            continue;
                        }

                        //
                        ProVideo program = new ProVideo();
                        program.mediaUrl = mediaUrl;
                        program.title = cur.getString(cur.getColumnIndex(VideoCacheInfo.TITLE));
                        program.duration = cur.getInt(cur.getColumnIndex(VideoCacheInfo.DURATION));
                        mapVideos.put(program.mediaUrl, program);
                    }
                }
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "getMapVideos()", e);
            } finally {
                closeDBInfo(cur, false, throwable != null);
            }
        }
        return mapVideos;
    }
}
