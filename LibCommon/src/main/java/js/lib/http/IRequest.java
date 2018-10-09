package js.lib.http;

import java.io.Serializable;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

/**
 * 请求对象
 * 
 * @author Jun.Wang
 */
@SuppressWarnings("deprecation")
public class IRequest implements Serializable {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Get or Post
	 */
	public int mode;

	public interface ReqMode {
		public final int GET = 1;
		public final int POST = 2;
	}

	/**
	 * Request URI
	 */
	public String uri = "";

	/**
	 * Save Data that you will submit
	 */
	/**
	 * 如果传输文件等，使用此对象
	 */
	public HttpEntity httpEntity;
	/**
	 * 以键值对方式提交数据
	 */
	public List<NameValuePair> listData;
	/**
	 * 以JSON方式提交数据
	 */
	public String strJsonData;

	/**
	 * 监听对象
	 */
	public BaseDelegate delegate;

	/**
	 * Flag which active to do
	 */
	public int usage;

	/**
	 * Request Level , There is High,Normal,Low
	 */
	public int level;

	public interface ReqLevel {
		public final int HIGHT = 1;
		public final int NORMAL = 2;
		public final int LOW = 3;
	}
}