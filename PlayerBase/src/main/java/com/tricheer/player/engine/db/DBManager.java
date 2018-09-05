package com.tricheer.player.engine.db;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import js.lib.android.utils.DbUtil;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.JsFileUtils;
import js.lib.android.utils.Logs;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.tricheer.player.bean.OnlineMusicInfo;
import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.engine.db.Tables.MusicCacheInfo;
import com.tricheer.player.engine.db.Tables.MusicOnlineSearchInfo;
import com.tricheer.player.engine.db.Tables.VideoCacheInfo;
import com.tricheer.player.utils.PlayerFileUtils;
import com.tricheer.player.utils.PlayerLogicUtils;

/**
 * 该类用来处理数据库操作
 *
 * @author Jun.Wang
 */
public class DBManager extends DbUtil {
    // TAG
    private static final String TAG = "DBManager";

    /**
     * 打开数据库连接
     */
    private static boolean openDB() {
        if (mDB == null) {
            openDB(new DBHelper(mContext));
        }
        return (mDB == null) ? false : mDB.isOpen();
    }

    /**
     * 关闭数据库信息
     *
     * @param cur              : {@link Cursor}
     * @param isEndTransaction : is Close DBTransaction
     * @param isCloseDB        : isClose DB
     */
    private static void closeDBInfo(Cursor cur, boolean isEndTransaction, boolean isCloseDB) {
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

    // ================MusicOnline Search Methods Start================
    public static long saveMusicSearchInfo(OnlineMusicInfo searchInfo) {
        long count = 0;
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = MusicOnlineSearchInfo.T_NAME;
                String[] columns = new String[]{"*"};
                String selection = MusicOnlineSearchInfo.KEY + "=?";
                String[] selectionArgs = new String[]{searchInfo.key};

                cur = mDB.query(table, columns, selection, selectionArgs, null, null, null);
                if (cur.getCount() > 0) {
                    // Insert
                } else {
                    count = mDB.insert(table, null, getContentValues(searchInfo));
                }
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "saveMusicSearchInfo()", e);
            } finally {
                closeDBInfo(cur, false, throwable != null);
            }
        }
        return count;
    }

    /**
     * Construct ContentValues By ProMusic Object
     */
    private static ContentValues getContentValues(OnlineMusicInfo searchInfo) {
        ContentValues cvs = new ContentValues();
        cvs.put(MusicOnlineSearchInfo.KEY, searchInfo.key);
        cvs.put(MusicOnlineSearchInfo.KEY_PINYIN, searchInfo.keyPinyin);
        cvs.put(MusicOnlineSearchInfo.VALUE, searchInfo.value);
        cvs.put(MusicOnlineSearchInfo.CREATE_TIME, searchInfo.createTime);
        cvs.put(MusicOnlineSearchInfo.UPDATE_TIME, searchInfo.updateTime);
        return cvs;
    }

    /**
     * Get MusicSearchInfo By "Search Key"
     */
    public static OnlineMusicInfo getSearchInfo(String searchKey) {
        OnlineMusicInfo searchInfo = null;
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = MusicOnlineSearchInfo.T_NAME;
                String[] columns = new String[]{"*"};
                String where = MusicOnlineSearchInfo.KEY + "=?";
                String[] whereArgs = new String[]{searchKey};
                String orderBy = MusicOnlineSearchInfo.KEY_PINYIN + " desc";
                // Query
                cur = mDB.query(table, columns, where, whereArgs, null, null, orderBy);
                if (cur != null && cur.getCount() > 0 && cur.moveToFirst()) {
                    searchInfo = getMusicSearchInfo(cur);
                }

            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "getSearchInfo()", e);
            } finally {
                closeDBInfo(cur, false, throwable != null);
            }
        }
        return searchInfo;
    }

    public static List<OnlineMusicInfo> getListSearchInfos() {
        List<OnlineMusicInfo> listInfos = new ArrayList<OnlineMusicInfo>();
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = MusicOnlineSearchInfo.T_NAME;
                String[] columns = new String[]{"*"};
                String orderBy = MusicOnlineSearchInfo.KEY_PINYIN + " desc";

                cur = mDB.query(table, columns, null, null, null, null, orderBy);
                if (cur != null) {
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        OnlineMusicInfo searchInfo = getMusicSearchInfo(cur);
                        if (searchInfo != null) {
                            listInfos.add(searchInfo);
                        }
                    }
                }
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "getListSearchInfos()", e);
            } finally {
                closeDBInfo(cur, false, throwable != null);
            }
        }
        return listInfos;
    }

    /**
     * Get Music Search Information From Cursor
     */
    private static OnlineMusicInfo getMusicSearchInfo(Cursor cur) {
        OnlineMusicInfo searchInfo = new OnlineMusicInfo();
        try {
            searchInfo.key = cur.getString(cur.getColumnIndex(MusicOnlineSearchInfo.KEY));
            searchInfo.keyPinyin = cur.getString(cur.getColumnIndex(MusicOnlineSearchInfo.KEY_PINYIN));
            searchInfo.value = cur.getString(cur.getColumnIndex(MusicOnlineSearchInfo.VALUE));
            searchInfo.createTime = cur.getLong(cur.getColumnIndex(MusicOnlineSearchInfo.CREATE_TIME));
            searchInfo.updateTime = cur.getLong(cur.getColumnIndex(MusicOnlineSearchInfo.UPDATE_TIME));
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getMusicSearchInfo(Cursor)", e);
            searchInfo = null;
        }
        return searchInfo;
    }

    // =======================Music Methods Start=======================

    /**
     * Construct ContentValues By ProMusic Object
     */
    private static ContentValues getContentValues(ProMusic program, long currTime) {
        ContentValues cvs = new ContentValues();
        if (program.isFromNet == 1) {
            cvs.put(MusicCacheInfo.ID, program.id);
        }
        cvs.put(MusicCacheInfo.SYS_MEDIA_ID, program.sysMediaID);
        cvs.put(MusicCacheInfo.ALBUM_ID, program.albumID);
        cvs.put(MusicCacheInfo.ALBUM_NAME, program.albumName);//
        cvs.put(MusicCacheInfo.TITLE, program.title);//
        cvs.put(MusicCacheInfo.TITLE_PINYIN, program.titlePinYin);//
        cvs.put(MusicCacheInfo.ARTIST, program.artist);//
        cvs.put(MusicCacheInfo.LYRIC, program.lyric);//
        cvs.put(MusicCacheInfo.MEDIA_URL, program.mediaUrl);//
        cvs.put(MusicCacheInfo.COVER_URL, program.coverUrl);//
        cvs.put(MusicCacheInfo.DURATION, program.duration);//
        cvs.put(MusicCacheInfo.IS_FROM_NET, program.isFromNet);//
        cvs.put(MusicCacheInfo.IS_COLLECT, program.isCollected);//
        cvs.put(MusicCacheInfo.IS_CAUSE_ERROR, program.isCauseError);//
        if (currTime >= 0) {
            cvs.put(MusicCacheInfo.CREATE_TIME, currTime);//
            cvs.put(MusicCacheInfo.UPDATE_TIME, 0);//
        } else {
            cvs.put(MusicCacheInfo.CREATE_TIME, program.createTime);//
            cvs.put(MusicCacheInfo.UPDATE_TIME, program.updateTime);//
        }
        return cvs;
    }

    /**
     * Insert List<ProMusic>
     */
    public static int insertListMusics(final List<ProMusic> listPrograms) {
        int count = 0;
        if (!EmptyUtil.isEmpty(listPrograms) && openDB()) {
            Throwable throwable = null;
            try {
                mDB.beginTransaction();
                long currTime = System.currentTimeMillis();
                for (ProMusic program : listPrograms) {
                    if (mDB.insert(MusicCacheInfo.T_NAME, null, getContentValues(program, currTime)) > 0) {
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
     * Save Search
     */
    public static long saveOnlineMusic(ProMusic program) {
        long count = 0;
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = MusicCacheInfo.T_NAME;
                String[] columns = new String[]{"*"};
                String selection = MusicCacheInfo.ID + "=? or " + MusicCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{String.valueOf(program.id), String.valueOf(program.mediaUrl)};
                cur = mDB.query(table, columns, selection, selectionArgs, null, null, null);
                if (cur.getCount() > 0) {
                    count = mDB.update(table, getContentValues(program, System.currentTimeMillis()), selection, selectionArgs);
                    // Insert
                } else {
                    count = mDB.insert(table, null, getContentValues(program, System.currentTimeMillis()));
                }

            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "saveOnlineMusic()", e);
            } finally {
                closeDBInfo(cur, false, throwable != null);
            }
        }
        return count;
    }

    /**
     * Delete All record by when mediaUrl like '%strLike%'
     */
    public static void deleteMusics(String strLike) {
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
    public static int deleteMusic(ProMusic music) {
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
     * Update List<ProMusic>
     */
    public static void updateListMusics(final List<ProMusic> listMusics) {
        if (openDB()) {
            Throwable throwable = null;
            try {
                mDB.beginTransaction();
                // Update ProMusic
                String table = MusicCacheInfo.T_NAME;
                String selection = MusicCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = null;
                long currTime = System.currentTimeMillis();
                for (ProMusic media : listMusics) {
                    selectionArgs = new String[]{media.mediaUrl};
                    mDB.update(table, getContentValues(media, currTime), selection, selectionArgs);
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
    public static void updateMusicInfo(ProMusic music) {
        if (openDB()) {
            Throwable throwable = null;
            try {
                String selection = MusicCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{music.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(MusicCacheInfo.UPDATE_TIME, System.currentTimeMillis());
                cvs.put(MusicCacheInfo.DURATION, music.duration);

                mDB.update(MusicCacheInfo.T_NAME, cvs, selection, selectionArgs);
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
    public static void updateMediaCoverUrl(ProMusic media) {
        if (media != null && openDB()) {
            Throwable throwable = null;
            try {
                String selection = MusicCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{media.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(MusicCacheInfo.COVER_URL, media.coverUrl);
                cvs.put(MusicCacheInfo.PARSE_INFO_FLAG, media.parseInfoFlag);
                mDB.update(MusicCacheInfo.T_NAME, cvs, selection, selectionArgs);
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
    public static void updateMediaCollect(ProMusic media) {
        if (media != null && openDB()) {
            Throwable throwable = null;
            try {
                String selection = MusicCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{media.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(MusicCacheInfo.IS_COLLECT, media.isCollected);
                mDB.update(MusicCacheInfo.T_NAME, cvs, selection, selectionArgs);
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
    public static ProMusic getOnlineMusicByID(int musicID) {
        ProMusic music = null;
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = MusicCacheInfo.T_NAME;
                String[] columns = new String[]{"*"};
                String selection = MusicCacheInfo.IS_FROM_NET + "=? and " + MusicCacheInfo.ID + "=?";
                String[] selectionArgs = new String[]{"1", String.valueOf(musicID)};
                cur = mDB.query(table, columns, selection, selectionArgs, null, null, null, null);
                if (cur != null) {
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        music = getMusicByCursor(cur);
                        if (music != null) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "getOnlineMusicByID()", e);
            } finally {
                closeDBInfo(cur, false, throwable != null);
            }
        }
        return music;
    }

    /**
     * Get Music List
     */
    public static List<ProMusic> getListMusics() {
        List<ProMusic> listMusics = new ArrayList<ProMusic>();
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
                        ProMusic music = getMusicByCursor(cur);
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
    public static List<ProMusic> getListMusics(String title, String artist) {
        List<ProMusic> listMusics = new ArrayList<ProMusic>();
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
                            ProMusic music = getMusicByCursor(cur);
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
    public static Map<String, ProMusic> getMapMusics() {
        Map<String, ProMusic> mapMusics = new HashMap<String, ProMusic>();
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
                        ProMusic music = getMusicByCursor(cur);
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
     * Get Online Musics
     *
     * @return Map<program.name                                                                                                                                                                                                                                                               +                                                                                                                                                                                                                                                               "                                                                                                                               _                                                                                                                               "                                                                                                                                                                                                                                                               +                                                                                                                                                                                                                                                               program.singer                                                                                                                               ,                                                                                                                               ProMusic>
     */
    public static Map<String, ProMusic> getMapOnlineMusics() {
        Map<String, ProMusic> mapPrograms = new HashMap<String, ProMusic>();
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                String table = MusicCacheInfo.T_NAME;
                String[] columns = new String[]{"*"};
                String selection = MusicCacheInfo.IS_FROM_NET + "=? and " + MusicCacheInfo.IS_CAUSE_ERROR + "=?";
                String[] selectionArgs = new String[]{"1", "0"};

                cur = mDB.query(table, columns, selection, selectionArgs, null, null, null);
                if (cur != null) {
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        ProMusic program = getMusicByCursor(cur);
                        if (program != null) {
                            mapPrograms.put(program.title + "_" + program.artist, program);
                        }
                    }
                }
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "getMapOnlineMusics()", e);
            } finally {
                closeDBInfo(cur, false, throwable != null);
            }
        }
        return mapPrograms;
    }

    /**
     * Get Music From Cursor
     */
    private static ProMusic getMusicByCursor(Cursor cur) {
        ProMusic music = null;
        try {
            // Check Exist
            String mediaUrl = cur.getString(cur.getColumnIndex(MusicCacheInfo.MEDIA_URL));
            // 本地音乐不存在, null
            if (!PlayerLogicUtils.isHttpUrl(mediaUrl) && !JsFileUtils.isFileExist(mediaUrl)) {
                Logs.i(TAG, "getMusicByCursor() -> [exists:" + false + " ; path:" + mediaUrl);
                return null;
            }

            // Get
            music = new ProMusic();
            music.id = cur.getInt(cur.getColumnIndex(MusicCacheInfo.ID));
            music.sysMediaID = cur.getLong(cur.getColumnIndex(MusicCacheInfo.SYS_MEDIA_ID));
            music.albumID = cur.getLong(cur.getColumnIndex(MusicCacheInfo.ALBUM_ID));
            music.albumName = cur.getString(cur.getColumnIndex(MusicCacheInfo.ALBUM_NAME));
            music.title = cur.getString(cur.getColumnIndex(MusicCacheInfo.TITLE));
            music.artist = cur.getString(cur.getColumnIndex(MusicCacheInfo.ARTIST));
            music.lyric = cur.getString(cur.getColumnIndex(MusicCacheInfo.LYRIC));
            music.mediaUrl = mediaUrl;
            music.coverUrl = cur.getString(cur.getColumnIndex(MusicCacheInfo.COVER_URL));
            music.duration = cur.getInt(cur.getColumnIndex(MusicCacheInfo.DURATION));
            music.parseInfoFlag = cur.getInt(cur.getColumnIndex(MusicCacheInfo.PARSE_INFO_FLAG));
            music.isFromNet = cur.getInt(cur.getColumnIndex(MusicCacheInfo.IS_FROM_NET));
            music.isCollected = cur.getInt(cur.getColumnIndex(MusicCacheInfo.IS_COLLECT));
            music.isCauseError = cur.getInt(cur.getColumnIndex(MusicCacheInfo.IS_CAUSE_ERROR));
            music.createTime = cur.getLong(cur.getColumnIndex(MusicCacheInfo.CREATE_TIME));
            music.updateTime = cur.getLong(cur.getColumnIndex(MusicCacheInfo.UPDATE_TIME));
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getMusicByCursor()", e);
            music = null;
        }
        return music;
    }

    // =======================Video Methods Start=======================

    /**
     * Construct ContentValues By ProVideo Object
     */
    private static ContentValues getContentValues(ProVideo program, long currTime) {
        ContentValues cvs = new ContentValues();
        cvs.put(VideoCacheInfo.TITLE, program.title);
        cvs.put(VideoCacheInfo.MEDIA_URL, program.mediaUrl);
        cvs.put(VideoCacheInfo.DURATION, program.duration);
        cvs.put(VideoCacheInfo.COVER_URL, program.coverUrl);
        cvs.put(VideoCacheInfo.IS_COLLECT, program.isCollected);
        cvs.put(VideoCacheInfo.IS_CAUSE_ERROR, program.isCauseError);
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
    public static int insertListVideos(List<ProVideo> listPrograms) {
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
    public static void deleteVideos(String clearPath) {
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
    public static void updateListVideos(List<ProVideo> listVideos) {
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
                    mDB.update(table, getContentValues(program, -1), selection, selectionArgs);
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
    public static void updateProgramInfo(ProVideo program) {
        if (program != null && openDB()) {
            Throwable throwable = null;
            try {
                String selection = VideoCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{program.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(VideoCacheInfo.UPDATE_TIME, System.currentTimeMillis());
                cvs.put(VideoCacheInfo.IS_CAUSE_ERROR, program.isCauseError);
                cvs.put(VideoCacheInfo.DURATION, program.duration);

                mDB.update(VideoCacheInfo.T_NAME, cvs, selection, selectionArgs);
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
    public static void updateMediaDuration(ProVideo media) {
        if (media != null && openDB()) {
            Throwable throwable = null;
            try {
                String selection = VideoCacheInfo.MEDIA_URL + "=?";
                String[] selectionArgs = new String[]{media.mediaUrl};

                ContentValues cvs = new ContentValues();
                cvs.put(VideoCacheInfo.DURATION, media.duration);
                mDB.update(VideoCacheInfo.T_NAME, cvs, selection, selectionArgs);
            } catch (Exception e) {
                throwable = e;
                Logs.printStackTrace(TAG + "updateMediaDuration()", e);
            } finally {
                closeDBInfo(null, false, throwable != null);
            }
        }
    }

    public static List<ProVideo> getListVideos(boolean isContainError, boolean isContainDrivingRecord) {
        List<ProVideo> listVideos = new ArrayList<ProVideo>();
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                cur = mDB.query(VideoCacheInfo.T_NAME, new String[]{"*"}, null, null, null, null, VideoCacheInfo.TITLE);
                if (cur != null) {
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        // Filter Program that has caused error when play
                        int isCauseError = cur.getInt(cur.getColumnIndex(VideoCacheInfo.IS_CAUSE_ERROR));
                        if (!isContainError && isCauseError == 1) {
                            continue;
                        }

                        // Filter Program that not exist
                        String mediaUrl = cur.getString(cur.getColumnIndex(VideoCacheInfo.MEDIA_URL));
                        File musicFile = new File(mediaUrl);
                        if (!musicFile.exists()) {
                            continue;
                        }

                        // Filter Program that product by Driving Camera
                        if (PlayerFileUtils.isInBlacklist(mediaUrl)) {
                            if (!isContainDrivingRecord) {
                                continue;
                            }
                        }

                        ProVideo program = new ProVideo();
                        program.mediaUrl = mediaUrl;
                        program.title = cur.getString(cur.getColumnIndex(VideoCacheInfo.TITLE));
                        program.duration = cur.getInt(cur.getColumnIndex(VideoCacheInfo.DURATION));
                        program.isCauseError = isCauseError;
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
    public static Map<String, ProVideo> getMapVideos(boolean isContainError, boolean isContainDrivingRecord) {
        Map<String, ProVideo> mapVideos = new HashMap<String, ProVideo>();
        if (openDB()) {
            Throwable throwable = null;
            Cursor cur = null;
            try {
                cur = mDB.query(VideoCacheInfo.T_NAME, new String[]{"*"}, null, null, null, null, null);
                if (cur != null) {
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        // Filter Program that has caused error when play
                        int isCauseError = cur.getInt(cur.getColumnIndex(VideoCacheInfo.IS_CAUSE_ERROR));
                        if (!isContainError && isCauseError == 1) {
                            continue;
                        }

                        // Filter Program that not exist
                        String mediaUrl = cur.getString(cur.getColumnIndex(VideoCacheInfo.MEDIA_URL));
                        File musicFile = new File(mediaUrl);
                        if (!musicFile.exists()) {
                            continue;
                        }

                        // Filter Program that product by Driving Camera
                        Log.i("test_media_url", mediaUrl);
                        if (PlayerFileUtils.isInBlacklist(mediaUrl)) {
                            if (!isContainDrivingRecord) {
                                continue;
                            }
                        }

                        ProVideo program = new ProVideo();
                        program.mediaUrl = mediaUrl;
                        program.title = cur.getString(cur.getColumnIndex(VideoCacheInfo.TITLE));
                        program.duration = cur.getInt(cur.getColumnIndex(VideoCacheInfo.DURATION));
                        program.isCauseError = isCauseError;
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
