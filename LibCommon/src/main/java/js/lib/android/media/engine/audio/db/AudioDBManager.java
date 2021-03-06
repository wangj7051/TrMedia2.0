package js.lib.android.media.engine.audio.db;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.engine.audio.db.AudioTables.AudioCacheInfo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

/**
 * 该类用来处理数据库操作
 *
 * @author Jun.Wang
 */
public class AudioDBManager {
    // TAG
    private static final String TAG = "AudioDBManager";

    /**
     * 上下文
     */
    private Context mContext;
    /**
     * It should be similar to "/sdcard/Music/TrAudio.sqlite"
     */
    private String mDbName = "";

    /**
     * SQLiteDatabase Object
     */
    private SQLiteDatabase mDB;

    private AudioDBManager() {
    }

    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private static final AudioDBManager INSTANCE = new AudioDBManager();
    }

    public static AudioDBManager instance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Initialize
     *
     * @param context {@link Context}
     * @param dbName  It should be similar to "/sdcard/Music/TrAudio.sqlite"
     */
    public void init(Context context, String dbName) {
        try {
            Log.i(TAG, "init(" + context + "," + dbName + ")");
            mContext = context.getApplicationContext();
            mDbName = dbName;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开数据库连接
     */
    private boolean openDB() {
        if (mDB == null || !mDB.isOpen()) {
            try {
                SQLiteOpenHelper helper = new AudioDBHelper(mContext, mDbName);
                mDB = helper.getWritableDatabase();
            } catch (Exception e) {
                Log.i(TAG, "openDB() :: Exception-" + e.getMessage());
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
     * Construct ContentValues By ProAudio Object
     */
    private ContentValues getContentValues(ProAudio media, long currTime) {
        ContentValues cvs = new ContentValues();
        if (media.source == 1) {
            cvs.put(AudioCacheInfo.ID, media.id);
        }
        cvs.put(AudioCacheInfo.SYS_MEDIA_ID, media.sysMediaID);
        cvs.put(AudioCacheInfo.TITLE, media.title);//
        cvs.put(AudioCacheInfo.TITLE_PINYIN, media.titlePinYin);//

        cvs.put(AudioCacheInfo.ALBUM_ID, media.albumID);
        cvs.put(AudioCacheInfo.ALBUM, media.album);//
        cvs.put(AudioCacheInfo.ALBUM_PINYIN, media.albumPinYin);//

        cvs.put(AudioCacheInfo.ARTIST, media.artist);//
        cvs.put(AudioCacheInfo.ARTIST_PINYIN, media.artistPinYin);//

        cvs.put(AudioCacheInfo.MEDIA_URL, media.mediaUrl);//
        cvs.put(AudioCacheInfo.MEDIA_DIRECTORY, media.mediaDirectory);//
        cvs.put(AudioCacheInfo.MEDIA_DIRECTORY_PINYIN, media.mediaDirectoryPinYin);//

        cvs.put(AudioCacheInfo.DURATION, media.duration);//
        cvs.put(AudioCacheInfo.IS_COLLECT, media.isCollected);//
        cvs.put(AudioCacheInfo.COVER_URL, media.coverUrl);//
        cvs.put(AudioCacheInfo.LYRIC, media.lyric);//
        cvs.put(AudioCacheInfo.SOURCE, media.source);//

        if (currTime >= 0) {
            cvs.put(AudioCacheInfo.CREATE_TIME, currTime);//
            cvs.put(AudioCacheInfo.UPDATE_TIME, 0);//
        } else {
            cvs.put(AudioCacheInfo.CREATE_TIME, media.createTime);//
            cvs.put(AudioCacheInfo.UPDATE_TIME, media.updateTime);//
        }
        return cvs;
    }

    /**
     * Insert List<ProAudio>
     */
    public int insertListMusics(final List<ProAudio> listPrograms) {
        int count = 0;
        if (!EmptyUtil.isEmpty(listPrograms) && openDB()) {
            Throwable throwable = null;
            try {
                mDB.beginTransaction();
                long currTime = System.currentTimeMillis();
                for (ProAudio media : listPrograms) {
                    long rowId = mDB.insert(AudioCacheInfo.T_NAME, null, getContentValues(media, currTime));
                    if (rowId > 0) {
                        count++;
                    }
                }
                mDB.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
                throwable = e;
                Logs.printStackTrace(TAG + "insertNewMusics()", e);
            } finally {
                closeDBInfo(null, true, throwable != null);
            }
        }
        return count;
    }

    /**
     * Delete All record by when mediaUrl like '%strLike%'
     */
    public void deleteMusics(String strLike) {
        if (openDB()) {
            Throwable throwable = null;
            try {
                String whereClause = null;
                if (!EmptyUtil.isEmpty(strLike)) {
                    whereClause = AudioCacheInfo.MEDIA_URL + " like '%" + strLike + "%'";
                }
                mDB.delete(AudioCacheInfo.T_NAME, EmptyUtil.isEmpty(whereClause) ? null : whereClause, null);
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "clearMusics()", e);
            } finally {
                closeDBInfo(null, false, throwable != null);
            }
        }
    }

    /**
     * Delete Music
     */
    public int deleteMusic(ProAudio music) {
        int count = 0;
        if (openDB()) {
            Throwable throwable = null;
            try {
                String whereClause = AudioCacheInfo.MEDIA_URL + "=?";
                String[] whereArgs = new String[]{music.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(AudioCacheInfo.UPDATE_TIME, System.currentTimeMillis());
                cvs.put(AudioCacheInfo.DURATION, music.duration);
                count = mDB.delete(AudioCacheInfo.T_NAME, whereClause, whereArgs);
            } catch (Exception e) {
                count = -1;
                throwable = e;
                Logs.printStackTrace(TAG + "deleteMusic()", e);
            } finally {
                closeDBInfo(null, false, throwable != null);
            }
        }
        return count;
    }

    /**
     * Update List<ProAudio>
     */
    public void updateListMusics(final List<ProAudio> listMusics) {
        if (openDB()) {
            Throwable throwable = null;
            try {
                mDB.beginTransaction();
                // Update ProAudio
                String table = AudioCacheInfo.T_NAME;
                String selection = AudioCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = null;
                long currTime = System.currentTimeMillis();
                for (ProAudio media : listMusics) {
                    selectionArgs = new String[]{media.mediaUrl};
                    int rowsNum = mDB.update(table, getContentValues(media, currTime), selection, selectionArgs);
                    Log.i(TAG, "updateListMusics - rowsNum: " + rowsNum);
                }
                mDB.setTransactionSuccessful();
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "updateListMusics()", e);
            } finally {
                closeDBInfo(null, true, throwable != null);
            }
        }
    }

    /**
     * Update Music Information to Database
     */
    public void updateMusicInfo(ProAudio music) {
        if (openDB()) {
            Throwable throwable = null;
            try {
                String selection = AudioCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{music.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(AudioCacheInfo.UPDATE_TIME, System.currentTimeMillis());
                cvs.put(AudioCacheInfo.DURATION, music.duration);

                int rowsNum = mDB.update(AudioCacheInfo.T_NAME, cvs, selection, selectionArgs);
                Log.i(TAG, "updateMusicInfo - rowsNum: " + rowsNum);
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "updateMusicInfo()", e);
            } finally {
                closeDBInfo(null, false, throwable != null);
            }
        }
    }

    /**
     * Update Media Cover URL
     */
    public void updateMediaCoverUrl(ProAudio media) {
        if (media != null && openDB()) {
            Throwable throwable = null;
            try {
                String selection = AudioCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{media.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(AudioCacheInfo.COVER_URL, media.coverUrl);
                int rowsNum = mDB.update(AudioCacheInfo.T_NAME, cvs, selection, selectionArgs);
                Log.i(TAG, "updateMediaCoverUrl - rowsNum: " + rowsNum);
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "updateMediaCoverUrl()", e);
            } finally {
                closeDBInfo(null, false, throwable != null);
            }
        }
    }

    /**
     * Update Media Collected Status
     */
    public void updateMediaCollect(ProAudio media) {
        if (media != null && openDB()) {
            Throwable throwable = null;
            try {
                String selection = AudioCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{media.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(AudioCacheInfo.IS_COLLECT, media.isCollected);
                cvs.put(AudioCacheInfo.UPDATE_TIME, media.updateTime);
                int rowsNum = mDB.update(AudioCacheInfo.T_NAME, cvs, selection, selectionArgs);
                Log.i(TAG, "updateMediaCollect - rowsNum: " + rowsNum);
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "updateMediaCollect()", e);
            } finally {
                closeDBInfo(null, false, throwable != null);
            }
        }
    }

    /**
     * Get Music List
     */
    public ProAudio getMedia(String path) {
        Log.i(TAG, "getMedia(" + path + ")");
        ProAudio media = null;
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = AudioCacheInfo.T_NAME;
                String[] columns = new String[]{"*"};
                String selection = AudioCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{path};

                //
                cur = mDB.query(table, columns, selection, selectionArgs, null, null, null, null);
                if (cur != null && cur.moveToFirst()) {
                    media = getMusicByCursor(cur);
                }
            } catch (Exception e) {
                throwable = e;
                Log.i(TAG, "e:" + e.getMessage());
                e.printStackTrace();
            } finally {
                closeDBInfo(cur, false, throwable != null);
            }
        }
        return media;
    }

    /**
     * Get Music List
     */
    public List<ProAudio> getListMusics() {
        List<ProAudio> listMusics = new ArrayList<>();
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = AudioCacheInfo.T_NAME;
                String[] columns = new String[]{"*"};
                String orderBy = "upper(" + AudioCacheInfo.TITLE_PINYIN + ")";
                cur = mDB.query(table, columns, null, null, null, null, orderBy, null);
                if (cur != null) {
                    Set<String> setAddedMedias = new HashSet<String>();
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        ProAudio music = getMusicByCursor(cur);
                        if (music != null) {
                            if (setAddedMedias.contains(music.mediaUrl)) {
                                continue;
                            }
                            setAddedMedias.add(music.mediaUrl);
                            listMusics.add(music);
                        }
                    }
                }
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "getListMusics()", e);
            } finally {
                closeDBInfo(cur, false, throwable != null);
            }
        }
        return listMusics;
    }

    /**
     * Get Music List
     */
    public List<ProAudio> getListMusics(String title, String artist) {
        Log.i(TAG, "getListMusics(" + title + "," + artist + ")");
        List<ProAudio> listMusics = new ArrayList<>();
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = AudioCacheInfo.T_NAME;
                String[] columns = new String[]{"*"};

                String selection = null;
                String[] selectionArgs = null;
                if (!EmptyUtil.isEmpty(title) && EmptyUtil.isEmpty(artist)) {
                    selection = AudioCacheInfo.TITLE + " like ?";
                    selectionArgs = new String[]{"%" + title + "%"};
                } else if (!EmptyUtil.isEmpty(title) && !EmptyUtil.isEmpty(artist)) {
                    selection = AudioCacheInfo.TITLE + " like ? and " + AudioCacheInfo.ARTIST + " like ?";
                    selectionArgs = new String[]{"%" + title + "%", "%" + artist + "%"};
                } else if (EmptyUtil.isEmpty(title) && !EmptyUtil.isEmpty(artist)) {
                    selection = AudioCacheInfo.ARTIST + " like ?";
                    selectionArgs = new String[]{"%" + artist + "%"};
                }

                //
                if (selection != null) {
                    cur = mDB.query(table, columns, selection, selectionArgs, null, null, null, null);
                    if (cur != null) {
                        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                            ProAudio music = getMusicByCursor(cur);
                            if (music != null) {
                                listMusics.add(music);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "getListMusics(title,artist)", e);
            } finally {
                closeDBInfo(cur, false, throwable != null);
            }
        }
        return listMusics;
    }

    /**
     * Get Music List
     */
    public Map<String, ProAudio> getMapMusics() {
        Map<String, ProAudio> mapMusics = new HashMap<>();
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = AudioCacheInfo.T_NAME;
                String[] columns = new String[]{"*"};
                String orderBy = AudioCacheInfo.UPDATE_TIME + " desc";

                cur = mDB.query(table, columns, null, null, null, null, orderBy, null);
                if (cur != null) {
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        ProAudio music = getMusicByCursor(cur);
                        if (music != null) {
                            mapMusics.put(music.mediaUrl, music);
                        }
                    }
                }

            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "getMapMusics()", e);
            } finally {
                closeDBInfo(cur, false, throwable != null);
            }
        }
        return mapMusics;
    }

    /**
     * Get Music From Cursor
     */
    private ProAudio getMusicByCursor(Cursor cur) {
        ProAudio music = null;
        try {
            String mediaUrl = cur.getString(cur.getColumnIndex(AudioCacheInfo.MEDIA_URL));
            File mediaFile = new File(mediaUrl);
            if (mediaFile.exists()) {
                music = new ProAudio();
                music.id = cur.getInt(cur.getColumnIndex(AudioCacheInfo.ID));
                music.sysMediaID = cur.getLong(cur.getColumnIndex(AudioCacheInfo.SYS_MEDIA_ID));
                music.title = cur.getString(cur.getColumnIndex(AudioCacheInfo.TITLE));
                music.titlePinYin = cur.getString(cur.getColumnIndex(AudioCacheInfo.TITLE_PINYIN));
                music.albumID = cur.getLong(cur.getColumnIndex(AudioCacheInfo.ALBUM_ID));
                music.album = cur.getString(cur.getColumnIndex(AudioCacheInfo.ALBUM));
                music.albumPinYin = cur.getString(cur.getColumnIndex(AudioCacheInfo.ALBUM_PINYIN));
                music.artist = cur.getString(cur.getColumnIndex(AudioCacheInfo.ARTIST));
                music.artistPinYin = cur.getString(cur.getColumnIndex(AudioCacheInfo.ARTIST_PINYIN));
                music.mediaUrl = mediaUrl;
                music.mediaDirectory = cur.getString(cur.getColumnIndex(AudioCacheInfo.MEDIA_DIRECTORY));
                music.mediaDirectoryPinYin = cur.getString(cur.getColumnIndex(AudioCacheInfo.MEDIA_DIRECTORY_PINYIN));
                music.duration = cur.getInt(cur.getColumnIndex(AudioCacheInfo.DURATION));
                music.isCollected = cur.getInt(cur.getColumnIndex(AudioCacheInfo.IS_COLLECT));
                music.coverUrl = cur.getString(cur.getColumnIndex(AudioCacheInfo.COVER_URL));
                music.lyric = cur.getString(cur.getColumnIndex(AudioCacheInfo.LYRIC));
                music.source = cur.getInt(cur.getColumnIndex(AudioCacheInfo.SOURCE));
                music.createTime = cur.getLong(cur.getColumnIndex(AudioCacheInfo.CREATE_TIME));
                music.updateTime = cur.getLong(cur.getColumnIndex(AudioCacheInfo.UPDATE_TIME));
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getMusicByCursor()", e);
        }
        return music;
    }
}
