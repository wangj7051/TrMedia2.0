package js.lib.android.media.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaPlayer;

import java.util.List;

import js.lib.android.media.engine.video.utils.VideoInfo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

public class MediaUtils {
    // TAG
    private static final String TAG = "MediaPlayerUtils -> ";

    /**
     * Context
     */
    protected static ContentResolver mContentResolver;

    /**
     * You can only get medias at these paths
     */
    protected static List<String> mListSupportPaths;
    /**
     * You cannot get medias at these paths
     */
    protected static List<String> mListFilterPaths;

    public static void init(Context cxt, List<String> listSupportPaths, List<String> listFilterPaths) {
        mContentResolver = cxt.getContentResolver();
        mListSupportPaths = listSupportPaths;
        mListFilterPaths = listFilterPaths;
    }

    /**
     * Get Query Selection
     */
    protected static String getSelection() {
        String selection = null;

        //
        String limitLike = null;
        if (!EmptyUtil.isEmpty(mListSupportPaths)) {
            limitLike = "";
            int loop = mListSupportPaths.size();
            for (int idx = 0; idx < loop; idx++) {
                String path = mListSupportPaths.get(idx);
                if (idx > 0) {
                    limitLike += " or ";
                }
                limitLike += VideoInfo._PATH + " like '%" + path + "%' ";
            }
        }

        //
        String filterNotLike = null;
        if (!EmptyUtil.isEmpty(mListFilterPaths)) {
            filterNotLike = "";
            int loop = mListFilterPaths.size();
            for (int idx = 0; idx < loop; idx++) {
                String path = mListFilterPaths.get(idx);
                if (idx > 0) {
                    filterNotLike += " and ";
                }
                filterNotLike += VideoInfo._PATH + " not like '%" + path + "%' ";
            }
        }

        //
        selection = limitLike;
        if (EmptyUtil.isEmpty(selection)) {
            selection = filterNotLike;
        } else if (!EmptyUtil.isEmpty(filterNotLike)) {
            selection = "(" + limitLike + ")" + " and " + filterNotLike;
        }
        return selection;
    }

    /**
     * Get Query Selection Arguments
     */
    protected static String getSelectionArgs(List<String> listSelectedPaths) {
        StringBuffer sbArgs = new StringBuffer();
        int loop = listSelectedPaths.size();
        for (int idx = 0; idx < loop; idx++) {
            String str = listSelectedPaths.get(idx);
            sbArgs.append("'" + str + "'");
            if (idx != (loop - 1)) {
                sbArgs.append(",");
            }
        }
        return "(" + sbArgs.toString() + ")";
    }

    /**
     * Get Media Suffix
     */
    public static String getSuffix(String fPathOrDisPlayName) {
        try {
            return fPathOrDisPlayName.substring(fPathOrDisPlayName.lastIndexOf("."));
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getSuffix()", e);
        }
        return "";
    }

    /**
     * Print MeidaPlayer Error
     */
    public static void printError(MediaPlayer mp, int what, int extra) {
        Logs.i(TAG, "printError(mp,what,extra) -> [what:" + what + " ; extra:" + extra + "]");
        // what
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Logs.i(TAG, "发生未知错误");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Logs.i(TAG, "媒体服务器死机");
                break;
            default:
                Logs.i(TAG, "Default what Error");
                break;
        }

        // extra
        switch (extra) {
            // I/O 读写错误
            case MediaPlayer.MEDIA_ERROR_IO:
                Logs.i(TAG, "文件或网络相关的IO操作错误");
                break;

            // 文件格式不支持
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Logs.i(TAG, "比特流编码标准或文件不符合相关规范");
                break;

            // 一些操作需要太长时间来完成,通常超过3 - 5秒。
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Logs.i(TAG, "操作超时");
                break;

            // 比特流编码标准或文件符合相关规范,但媒体框架不支持该功能
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Logs.i(TAG, "比特流编码标准或文件符合相关规范,但媒体框架不支持该功能");
                break;
            default:
                Logs.i(TAG, "Default extra Error");
                break;
        }
    }
}
