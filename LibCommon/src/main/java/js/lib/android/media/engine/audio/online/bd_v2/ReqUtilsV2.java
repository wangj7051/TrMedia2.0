package js.lib.android.media.engine.audio.online.bd_v2;

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
public class ReqUtilsV2 {
    /**
     * Search Music List
     *
     * @param params : [0] Search Key Word
     */
    public static void doSearchMusicList(PlayerDelegate delegate, String[] params) {
        IRequest request = new IRequest();
        request.mode = ReqMode.GET;
        request.level = ReqLevel.HIGHT;
        request.delegate = delegate;
        request.uri = BaiduMusicAPI_V2.getSearchListUrl(params[0]);
        request.usage = ReqUsage.GET_BAIDU_MUSIC_LIST;
        HttpUtils.getInstance().doRequest(request);
    }

    /**
     * Search Music Song ID
     */
    public static void doSearchMusic(PlayerDelegate delegate, int songID) {
        IRequest request = new IRequest();
        request.mode = ReqMode.GET;
        request.level = ReqLevel.HIGHT;
        request.delegate = delegate;
        request.uri = BaiduMusicAPI_V2.getSearchMusicUrl(songID);
        request.usage = ReqUsage.GET_BAIDU_MUSIC_BY_ID;
        HttpUtils.getInstance().doRequest(request);
    }
}
