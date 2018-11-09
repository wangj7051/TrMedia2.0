package js.lib.http;

/**
 * 响应对象
 *
 * @author Jun.Wang
 */
public class IResponse {
    /**
     * Like --- Success Response : 200
     */
    public int resCode;

    /**
     * Like --- Success Response : The Response Data String that from Server
     */
    public String resStr = "";

    /**
     * Flag which active to do
     */
    public int usage;

    /**
     * 监听对象
     */
    public BaseDelegate delegate;

    /**
     * Response when Failure Request
     */
    public interface ResInfo {

        // 非网络原因造成请求失败
        int RES_FAIL_CODE = 0x111;

        // 由于网络原因造成请求失败
        int RES_NET_FAIL_CODE = 0x222;

        // 请求失败提示信息
        String RES_FAIL_STR = "Request Failure!";
    }
}
