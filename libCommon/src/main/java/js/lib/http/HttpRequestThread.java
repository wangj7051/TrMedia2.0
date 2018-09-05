package js.lib.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.http.IRequest.ReqMode;
import js.lib.http.IResponse.ResInfo;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;

/**
 * HTTP 请求
 * 
 * @author Jun.Wang
 */
@SuppressWarnings("deprecation")
public class HttpRequestThread extends Thread {
	// TAG
	private static final String TAG = "HttpRequestThread -> ";

	/**
	 * Exception
	 */
	private Throwable exception;

	/**
	 * Send Response Flag
	 */
	private final int MSG_HANDLER = 0x001;

	/**
	 * Response 队列
	 */
	public List<IResponse> listResponse;

	public HttpRequestThread() {
		listResponse = new ArrayList<IResponse>();
	}

	/**
	 * Handler Object , Used to Send Response
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_HANDLER) {

				//
				if (listResponse == null || listResponse.size() == 0) {
					return;
				}

				//
				IResponse response = listResponse.get(0);
				listResponse.remove(0);

				//
				if (response == null || response.delegate == null) {
					return;
				} else {
					response.delegate.callback(response);
				}
			}
		};
	};

	@Override
	public void run() {
		while (HttpUtils.getInstance().hasRequest()) {
			//
			exception = null;

			// 获取下一个Request请求
			IRequest request = HttpUtils.getInstance().nextRequest();
			if (request == null) {
				continue;
			}

			// 设置相应队列
			IResponse response = new IResponse();
			response.delegate = request.delegate;
			response.usage = request.usage;
			listResponse.add(response);

			// 获取HttpClient
			HttpClient httpClient = HttpClientHelper.getHttpClient();
			HttpResponse httpResponse = null;

			// Get 方式交互信息
			if (request.mode == ReqMode.GET) {
				try {
					// 去除空格
					request.uri = request.uri.replaceAll(" ", "%20");

					//
					HttpGet httpGet = new HttpGet(request.uri);
					//
					httpResponse = httpClient.execute(httpGet);

					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						response.resCode = HttpStatus.SC_OK;
						response.resStr = EntityUtils.toString(httpResponse.getEntity(), "GBK");
						// response.resStr =
						// EntityUtils.toString(httpResponse.getEntity(),
						// "UTF-8");
					}

				} catch (ClientProtocolException e) {
					exception = e;
				} catch (IOException e) {
					exception = e;
				} catch (Exception e) {
					exception = e;
				} catch (Error e) {
					exception = e;
				} finally {
					if (exception != null) {
						Logs.printStackTrace(TAG + "getHttpClientNoSecurity()", exception);
						response.resCode = ResInfo.RES_FAIL_CODE;
						response.resStr = ResInfo.RES_FAIL_STR;
					}

					// 通知发送响应
					sendMsg(MSG_HANDLER);
				}

				// 以Post方式交互数据
			} else if (request.mode == ReqMode.POST) {
				try {
					HttpPost httpPost = new HttpPost(URI.create(request.uri));

					if (!EmptyUtil.isEmpty(request.strJsonData)) {
						StringEntity entity = new StringEntity(request.strJsonData, HTTP.UTF_8);
						entity.setContentType("application/json");
						httpPost.setEntity(entity);
					}

					if (!(request.listData == null || request.listData.size() == 0)) {
						httpPost.setEntity(new UrlEncodedFormEntity(request.listData, HTTP.UTF_8));
					}

					if (request.httpEntity != null) {
						httpPost.setEntity(request.httpEntity);
					}

					httpResponse = httpClient.execute(httpPost);
					response.resCode = httpResponse.getStatusLine().getStatusCode();

					if (response.resCode == HttpStatus.SC_OK) {
						response.resStr = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
					} else {
						response.resStr = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
					}

				} catch (UnsupportedEncodingException e) {
					exception = e;
				} catch (ClientProtocolException e) {
					exception = e;
				} catch (IOException e) {
					exception = e;
				} catch (Exception e) {
					exception = e;
				} catch (Error e) {
					exception = e;
				} finally {
					if (exception != null) {
						Logs.printStackTrace(TAG + "run()", exception);
						response.resCode = ResInfo.RES_FAIL_CODE;
						response.resStr = ResInfo.RES_FAIL_STR;
					}

					// 通知发送响应
					sendMsg(MSG_HANDLER);
				}
			}
		}
	};

	/**
	 * 发送消息
	 */
	private void sendMsg(int msgWhat) {
		Message message = new Message();
		message.what = msgWhat;
		mHandler.sendMessage(message);
	}
}
