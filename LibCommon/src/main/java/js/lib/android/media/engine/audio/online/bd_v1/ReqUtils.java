package js.lib.android.media.engine.audio.online.bd_v1;

import js.lib.android.media.engine.audio.online.ReqUsage;

import js.lib.android.media.engine.audio.online.PlayerDelegate;
import js.lib.http.HttpUtils;
import js.lib.http.IRequest;
import js.lib.http.IRequest.ReqLevel;
import js.lib.http.IRequest.ReqMode;

/**
 * Request Methods
 *
 * @author Jun.Wang
 */
public class ReqUtils {
    /**
     * Search Music List
     *
     * @param params : [0] Search Key Word
     */
    public static void doSearchMusicList(PlayerDelegate delegate, String[] params) {
        IRequest request = new IRequest();
        request.mode = ReqMode.POST;
        request.level = ReqLevel.HIGHT;
        request.delegate = delegate;
        request.uri = BaiduMusicAPI.SEARCH_LIST_URL;
        request.usage = ReqUsage.GET_BAIDU_MUSIC_LIST;
        request.listData = BaiduMusicAPI.getSearchListValues(params[0]);
        HttpUtils.getInstance().doRequest(request);
    }

    /**
     * Search Music Music By Title & Artist
     *
     * @param params : [0] title; [1] artist
     */
    public static void doSearchMusic(PlayerDelegate delegate, String[] params) {
        IRequest request = new IRequest();
        request.mode = ReqMode.GET;
        request.level = ReqLevel.HIGHT;
        request.delegate = delegate;
        request.uri = BaiduMusicAPI.getSearchMusicUrl(params[0], params[1]);
        request.usage = ReqUsage.GET_BAIDU_MUSIC;
        HttpUtils.getInstance().doRequest(request);
    }

    /**
     * Search Music Music By Title & Artist
     */
    public static void doSearchMusicByTitle(PlayerDelegate delegate, String title) {
        IRequest request = new IRequest();
        request.mode = ReqMode.GET;
        request.level = ReqLevel.HIGHT;
        request.delegate = delegate;
        request.uri = BaiduMusicAPI.getSearchMusicUrl(title, "");
        request.usage = ReqUsage.GET_BAIDU_MUSIC_BY_TITLE;
        HttpUtils.getInstance().doRequest(request);
    }
}
