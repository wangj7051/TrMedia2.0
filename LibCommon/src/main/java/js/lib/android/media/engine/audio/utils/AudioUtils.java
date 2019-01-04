package js.lib.android.media.engine.audio.utils;

import js.lib.android.media.engine.MediaUtils;

/**
 * Audio Common Methods
 *
 * @author Jun.Wang
 */
public class AudioUtils extends MediaUtils {
    // TAG
    private static final String TAG = "AudioUtils -> ";

//    /**
//     * Query Audio Path
//     */
//    public static AudioInfo queryAudioInfo(String selectedPath) {
//        //
//        AudioInfo audio = null;
//
//        //
//        Cursor cur = null;
//        try {
//            String[] projection = AudioInfo.QUERY_PROJECTION;
//            Uri audioUri = AudioInfo.QUERY_URI;
//            String sortOrder = AudioInfo._TITLE;
//            String selection = null;
//            if (!EmptyUtil.isEmpty(selectedPath)) {
//                selection = AudioInfo._PATH + "='" + selectedPath + "'";
//            } else if (!EmptyUtil.isEmpty(mListSupportPaths) || !EmptyUtil.isEmpty(mListFilterPaths)) {
//                selection = getSelection();
//            }
//
//            cur = mContentResolver.query(audioUri, projection, selection, null, sortOrder);
//            if (cur != null) {
//                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
//                    audio = getAudioByCursor(cur);
//                }
//            }
//        } catch (Exception e) {
//            Logs.printStackTrace(TAG + "queryAudioInfo()", e);
//        } finally {
//            if (cur != null && !cur.isClosed()) {
//                cur.close();
//            }
//        }
//        return audio;
//    }
//
//    /**
//     * Query Audio Paths
//     * //Audio in path "/data/data/com.android.providers.media/databases/"
//     */
//    public static Map<String, AudioInfo> queryMapAudioInfos(List<String> listSelectedPaths) {
//        //
//        Map<String, AudioInfo> mapAudioInfos = new HashMap<String, AudioInfo>();
//
//        //
//        Cursor cur = null;
//        try {
//            String[] projection = AudioInfo.QUERY_PROJECTION;
//            Uri audioUri = AudioInfo.QUERY_URI;
//            String sortOrder = AudioInfo._TITLE;
//            String selection = null;
//            if (!EmptyUtil.isEmpty(listSelectedPaths)) {
//                selection = AudioInfo._PATH + " in " + getSelectionArgs(listSelectedPaths);
//            } else if (!EmptyUtil.isEmpty(mListSupportPaths) || !EmptyUtil.isEmpty(mListFilterPaths)) {
//                selection = getSelection();
//            }
//
//            cur = mContentResolver.query(audioUri, projection, selection, null, sortOrder);
//            if (cur != null) {
//                Logs.i(TAG, "queryMapAudioInfos(listSelectedPaths) -> cur.getCount():" + cur.getCount());
//                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
//                    AudioInfo audio = getAudioByCursor(cur);
//                    if (audio != null) {
//                        mapAudioInfos.put(audio.path, audio);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Logs.printStackTrace(TAG + "queryMapAudioInfos()", e);
//        } finally {
//            if (cur != null && !cur.isClosed()) {
//                cur.close();
//            }
//        }
//
//        return mapAudioInfos;
//    }
//
//    /**
//     * Query Audio Paths
//     */
//    public static ArrayList<AudioInfo> queryListAudioInfos(List<String> listSelectedPaths) {
//        //
//        ArrayList<AudioInfo> listAudioInfos = new ArrayList<AudioInfo>();
//
//        //
//        Cursor cur = null;
//        try {
//            String[] projection = AudioInfo.QUERY_PROJECTION;
//            Uri audioUri = AudioInfo.QUERY_URI;
//            String sortOrder = AudioInfo._TITLE;
//            String selection = null;
//            if (!EmptyUtil.isEmpty(listSelectedPaths)) {
//                selection = AudioInfo._PATH + " in " + getSelectionArgs(listSelectedPaths);
//            } else if (!EmptyUtil.isEmpty(mListSupportPaths) || !EmptyUtil.isEmpty(mListFilterPaths)) {
//                selection = getSelection();
//            }
//
//            cur = mContentResolver.query(audioUri, projection, selection, null, sortOrder);
//            if (cur != null) {
//                Logs.i(TAG, "queryListAudioInfos(listSelectedPaths) -> cur.getCount():" + cur.getCount());
//                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
//                    AudioInfo audio = getAudioByCursor(cur);
//                    if (audio != null) {
//                        listAudioInfos.add(audio);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Logs.printStackTrace(TAG + "queryListAudioInfos()", e);
//        } finally {
//            if (cur != null && !cur.isClosed()) {
//                cur.close();
//            }
//        }
//
//        return listAudioInfos;
//    }
//
//    private static AudioInfo getAudioByCursor(Cursor cur) {
//        AudioInfo audio = null;
//        try {
//            String displayName = cur.getString(cur.getColumnIndex(AudioInfo._DISPLAY_NAME));
//            String path = cur.getString(cur.getColumnIndex(AudioInfo._PATH));
//            String suffix = getSuffix(displayName);
//            if (AudioInfo.isSupport(suffix) && isExist(path)) {
//                audio = new AudioInfo();
//                audio.sysMediaID = cur.getInt(cur.getColumnIndex(AudioInfo._SYS_MEDIA_ID));
//                audio.mimeType = cur.getString(cur.getColumnIndex(AudioInfo._MIME_TYPE));
//                audio.displayName = displayName;
//                audio.title = cur.getString(cur.getColumnIndex(AudioInfo._TITLE));
//                audio.titlePinYin = CharacterParser.getPingYin(audio.title);
//                audio.artist = cur.getString(cur.getColumnIndex(AudioInfo._ARTIST));
//                audio.artistPinYin = CharacterParser.getPingYin(audio.artist);
//                audio.albumID = cur.getInt(cur.getColumnIndex(AudioInfo._ALBUM_ID));
//                audio.album = cur.getString(cur.getColumnIndex(AudioInfo._ALBUM));
//                audio.albumPinYin = CharacterParser.getPingYin(audio.album);
//
//                //
//                audio.path = path;
//                File file = new File(audio.path);
//                File parentFile = file.getParentFile();
//                if (parentFile != null) {
//                    audio.directory = parentFile.getName();
//                    audio.directoryPinYin = CharacterParser.getPingYin(audio.directory);
//                }
//
//                //
//                audio.duration = DateFormatUtil.getIntSecondMsec(cur.getInt(cur.getColumnIndex(AudioInfo._DURATION)));
//                // Messy Code
//                if (ChineseUtils.isMessyCode(audio.title)) {
//                    audio.title = audio.displayName.replace(suffix, "");
//                    audio.artist = "";
//                }
//            }
//        } catch (Exception e) {
//            Logs.printStackTrace(TAG + "getAudioByCursor()", e);
//            audio = null;
//        }
//        return audio;
//    }
//
//    public static boolean isExist(String path) {
//        boolean isExist = false;
//        try {
//            isExist = (new File(path)).exists();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isExist;
//    }
}
