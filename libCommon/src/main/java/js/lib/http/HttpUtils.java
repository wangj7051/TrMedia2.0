package js.lib.http;

import java.util.ArrayList;
import java.util.List;

import js.lib.http.IRequest.ReqLevel;

/**
 * HTTP请求工具类
 * 
 * @author Jun.Wang
 */
public class HttpUtils {
	/**
	 * 两个线程，用来执行Request操作
	 */
	private HttpRequestThread requestThreadF;
	private HttpRequestThread requestThreadS;

	/**
	 * 不同优先级的Request集合
	 */
	private List<IRequest> listRequestHighLevel;
	private List<IRequest> listRequestNormalLevel;
	private List<IRequest> listRequestLowLevel;

	/**
	 * 每一种优先级Request集合的状态
	 */
	private boolean isHasHighLevelRequest;
	private boolean isHasNormalLevelRequest;
	private boolean isHasLowLevelRequest;

	/**
	 * 构造方法
	 */
	private HttpUtils() {
		requestThreadF = new HttpRequestThread();
		requestThreadS = new HttpRequestThread();

		listRequestHighLevel = new ArrayList<IRequest>();
		listRequestNormalLevel = new ArrayList<IRequest>();
		listRequestLowLevel = new ArrayList<IRequest>();
	}

	/**
	 * 私有类，用来产生 HttpUtils 的单例；如果不调用，那么不会产生单例
	 */
	private static class InstanceHolder {
		public static HttpUtils instance = new HttpUtils();
	}

	/**
	 * 获取 HttpUtils 单例
	 * 
	 * @return HttpUtils 单例
	 */
	public synchronized static HttpUtils getInstance() {
		return InstanceHolder.instance;
	}

	/**
	 * 执行 Request
	 * 
	 * @param context
	 *            上下文
	 * @param request
	 *            请求内容
	 */
	public void doRequest(IRequest request) {

		if (request.level == ReqLevel.HIGHT) {
			listRequestHighLevel.add(request);
		} else if (request.level == ReqLevel.NORMAL) {
			listRequestNormalLevel.add(request);
		} else if (request.level == ReqLevel.LOW) {
			listRequestLowLevel.add(request);
		}

		if (requestThreadF == null || !requestThreadF.isAlive()) {
			requestThreadF = new HttpRequestThread();
			requestThreadF.start();
		}

		if (requestThreadS == null || !requestThreadS.isAlive()) {
			requestThreadS = new HttpRequestThread();
			requestThreadS.start();
		}
	}

	/**
	 * 判断是否仍然有请求队列
	 */
	public boolean hasRequest() {

		setRequestStaus();

		if (isHasHighLevelRequest || isHasNormalLevelRequest || isHasLowLevelRequest) {
			return true;
		}

		return false;
	}

	/**
	 * 获取下一个Request
	 * 
	 * @return Request Object
	 */
	public synchronized IRequest nextRequest() {

		setRequestStaus();

		IRequest request = null;

		if (isHasHighLevelRequest) {
			request = listRequestHighLevel.get(0);
			listRequestHighLevel.remove(0);
		} else if (isHasNormalLevelRequest) {
			request = listRequestNormalLevel.get(0);
			listRequestNormalLevel.remove(0);
		} else if (isHasLowLevelRequest) {
			request = listRequestLowLevel.get(0);
			listRequestLowLevel.remove(0);
		}

		return request;
	}

	/**
	 * 设置每一种优先级Request�?boolean 状态
	 */
	public void setRequestStaus() {
		isHasHighLevelRequest = (listRequestHighLevel != null && listRequestHighLevel.size() > 0);
		isHasNormalLevelRequest = (listRequestNormalLevel != null && listRequestNormalLevel.size() > 0);
		isHasLowLevelRequest = (listRequestLowLevel != null && listRequestLowLevel.size() > 0);
	}
}
