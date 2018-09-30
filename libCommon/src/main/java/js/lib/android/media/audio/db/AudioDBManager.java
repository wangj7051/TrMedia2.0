package js.lib.android.media.audio.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import js.lib.android.media.audio.bean.ProAudio;
import js.lib.android.media.audio.db.AudioTables.MusicCacheInfo;
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
        mContext = context;
        mDbName = dbName;
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
            cvs.put(MusicCacheInfo.ID, media.id);
        }
        cvs.put(MusicCacheInfo.SYS_MEDIA_ID, media.sysMediaID);
        cvs.put(MusicCacheInfo.TITLE, media.title);//
        cvs.put(MusicCacheInfo.TITLE_PINYIN, media.titlePinYin);//

        cvs.put(MusicCacheInfo.ALBUM_ID, media.albumID);
        cvs.put(MusicCacheInfo.ALBUM, media.album);//
        cvs.put(MusicCacheInfo.ALBUM_PINYIN, media.albumPinYin);//

        cvs.put(MusicCacheInfo.ARTIST, media.artist);//
        cvs.put(MusicCacheInfo.ARTIST_PINYIN, media.artistPinYin);//

        cvs.put(MusicCacheInfo.MEDIA_URL, media.mediaUrl);//
        cvs.put(MusicCacheInfo.MEDIA_DIRECTORY, media.mediaDirectory);//
        cvs.put(MusicCacheInfo.MEDIA_DIRECTORY_PINYIN, media.mediaDirectoryPinYin);//

        cvs.put(MusicCacheInfo.DURATION, media.duration);//
        cvs.put(MusicCacheInfo.IS_COLLECT, media.isCollected);//
        cvs.put(MusicCacheInfo.COVER_URL, media.coverUrl);//
        cvs.put(MusicCacheInfo.LYRIC, media.lyric);//
        cvs.put(MusicCacheInfo.SOURCE, media.source);//

        if (currTime >= 0) {
            cvs.put(MusicCacheInfo.CREATE_TIME, currTime);//
            cvs.put(MusicCacheInfo.UPDATE_TIME, 0);//
        } else {
            cvs.put(MusicCacheInfo.CREATE_TIME, media.createTime);//
            cvs.put(MusicCacheInfo.UPDATE_TIME, media.updateTime);//
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
                    if (mDB.insert(MusicCacheInfo.T_NAME, null, getContentValues(media, currTime)) > 0) {
                        count++;
                    }
                }
                mDB.setTransactionSuccessful();
            } catch (Exception e) {
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
                    whereClause = MusicCacheInfo.MEDIA_URL + " like '%" + strLike + "%'";
                }
                mDB.delete(MusicCacheInfo.T_NAME, EmptyUtil.isEmpty(whereClause) ? null : whereClause, null);
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
                String whereClause = MusicCacheInfo.MEDIA_URL + "=?";
                String[] whereArgs = new String[]{music.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(MusicCacheInfo.UPDATE_TIME, System.currentTimeMillis());
                cvs.put(MusicCacheInfo.DURATION, music.duration);
                count = mDB.delete(MusicCacheInfo.T_NAME, whereClause, whereArgs);
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
                String table = MusicCacheInfo.T_NAME;
                String selection = MusicCacheInfo.MEDIA_URL + "=?";
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
                String selection = MusicCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{music.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(MusicCacheInfo.UPDATE_TIME, System.currentTimeMillis());
                cvs.put(MusicCacheInfo.DURATION, music.duration);

                int rowsNum = mDB.update(MusicCacheInfo.T_NAME, cvs, selection, selectionArgs);
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
                String selection = MusicCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{media.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(MusicCacheInfo.COVER_URL, media.coverUrl);
                int rowsNum = mDB.update(MusicCacheInfo.T_NAME, cvs, selection, selectionArgs);
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
                String selection = MusicCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{media.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(MusicCacheInfo.IS_COLLECT, media.isCollected);
                int rowsNum = mDB.update(MusicCacheInfo.T_NAME, cvs, selection, selectionArgs);
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
    public List<ProAudio> getListMusics() {
        List<ProAudio> listMusics = new ArrayList<ProAudio>();
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = MusicCacheInfo.T_NAME;
                String[] columns = new String[]{"*"};
                String orderBy = "upper(" + MusicCacheInfo.TITLE_PINYIN + ")";
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
        List<ProAudio> listMusics = new ArrayList<ProAudio>();
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = MusicCacheInfo.T_NAME;
                String[] columns = new String[]{"*"};

                String selection = null;
                String[] selectionArgs = null;
                if (!EmptyUtil.isEmpty(title) && EmptyUtil.isEmpty(artist)) {
                    selection = MusicCacheInfo.TITLE + " like ?";
                    selectionArgs = new String[]{"%" + title + "%"};
                } else if (!EmptyUtil.isEmpty(title) && !EmptyUtil.isEmpty(artist)) {
                    selection = MusicCacheInfo.TITLE + " like ? and " + MusicCacheInfo.ARTIST + " like ?";
                    selectionArgs = new String[]{"%" + title + "%", "%" + artist + "%"};
                } else if (EmptyUtil.isEmpty(title) && !EmptyUtil.isEmpty(artist)) {
                    selection = MusicCacheInfo.ARTIST + " like ?";
                    selectionArgs = new String[]{"%" + artist + "%"};
                }

                //
                if (selection == null || selectionArgs == null) {
                } else {
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
                Logs.printStackTrace(TAG + "getListMusics(String,String)", e);
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
        Map<String, ProAudio> mapMusics = new HashMap<String, ProAudio>();
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = MusicCacheInfo.T_NAME;
                String[] columns = new String[]{"*"};
                String orderBy = MusicCacheInfo.UPDATE_TIME + " desc";

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
        ProAudio music;
        try {
            music = new ProAudio();
            music.id = cur.getInt(cur.getColumnIndex(MusicCacheInfo.ID));
            music.sysMediaID = cur.getLong(cur.getColumnIndex(MusicCacheInfo.SYS_MEDIA_ID));
            music.title = cur.getString(cur.getColumnIndex(MusicCacheInfo.TITLE));
            music.titlePinYin = cur.getString(cur.getColumnIndex(MusicCacheInfo.TITLE_PINYIN));
            music.albumID = cur.getLong(cur.getColumnIndex(MusicCacheInfo.ALBUM_ID));
            music.album = cur.getString(cur.getColumnIndex(MusicCacheInfo.ALBUM));
            music.albumPinYin = cur.getString(cur.getColumnIndex(MusicCacheInfo.ALBUM_PINYIN));
            music.artist = cur.getString(cur.getColumnIndex(MusicCacheInfo.ARTIST));
            music.artistPinYin = cur.getString(cur.getColumnIndex(MusicCacheInfo.ARTIST_PINYIN));
            music.mediaUrl = cur.getString(cur.getColumnIndex(MusicCacheInfo.MEDIA_URL));
            music.mediaDirectory = cur.getString(cur.getColumnIndex(MusicCacheInfo.MEDIA_DIRECTORY));
            music.mediaDirectoryPinYin = cur.getString(cur.getColumnIndex(MusicCacheInfo.MEDIA_DIRECTORY_PINYIN));
            music.duration = cur.getInt(cur.getColumnIndex(MusicCacheInfo.DURATION));
            music.isCollected = cur.getInt(cur.getColumnIndex(MusicCacheInfo.IS_COLLECT));
            music.coverUrl = cur.getString(cur.getColumnIndex(MusicCacheInfo.COVER_URL));
            music.lyric = cur.getString(cur.getColumnIndex(MusicCacheInfo.LYRIC));
            music.source = cur.getInt(cur.getColumnIndex(MusicCacheInfo.SOURCE));
            music.createTime = cur.getLong(cur.getColumnIndex(MusicCacheInfo.CREATE_TIME));
            music.updateTime = cur.getLong(cur.getColumnIndex(MusicCacheInfo.UPDATE_TIME));
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getMusicByCursor()", e);
            music = null;
        }
        return music;
    }
}
