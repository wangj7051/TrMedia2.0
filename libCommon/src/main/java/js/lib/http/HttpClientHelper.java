package js.lib.http;

import java.security.KeyStore;

import js.lib.android.utils.Logs;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

/**
 * HttpClient 构建
 * 
 * @author Jun.Wang
 */
@SuppressWarnings("deprecation")
public class HttpClientHelper {
	// TAG
	private static final String TAG = "HttpClientHelper -> ";

	/**
	 * Request Timeout
	 */
	private static final int REQUEST_TIMEOUT = 1000 * 15;

	private HttpClientHelper() {

	}

	public static synchronized HttpClient getHttpClient() {

		HttpClient httpClient = null;

		// 设置超时
		HttpParams httpParams = new BasicHttpParams();
		ConnManagerParams.setTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, REQUEST_TIMEOUT);

		//
		// httpClient = getHttpClientSecurity(httpParams);
		// httpClient = getHttpClientNoSecurity(httpParams);
		httpClient = new DefaultHttpClient(httpParams);

		return httpClient;
	}

	/**
	 * 严格认证 <必须证书匹配>
	 * 
	 * @param httpParams
	 * @return
	 */
	private static synchronized HttpClient getHttpClientSecurity(HttpParams httpParams) {

		HttpClient httpClient = null;
		Exception exception = null;

		// try {
		//
		// //
		// ShowcaseApp.getLogicCer();
		//
		// //
		// CertificateFactory cerFactory =
		// CertificateFactory.getInstance("X.509");
		// Certificate cer =
		// cerFactory.generateCertificate(ShowcaseApp.logicCer);
		//
		// KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
		// keyStore.load(null, null);
		// keyStore.setCertificateEntry("trust", cer);
		//
		// SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
		// Scheme sch = new Scheme("https", socketFactory, 443);
		//
		// httpClient = new DefaultHttpClient(httpParams);
		// httpClient.getConnectionManager().getSchemeRegistry().register(sch);
		//
		// } catch (CertificateException e) {
		// exception = e;
		// e.printStackTrace();
		// } catch (KeyStoreException e) {
		// exception = e;
		// e.printStackTrace();
		// } catch (NoSuchProviderException e) {
		// exception = e;
		// e.printStackTrace();
		// } catch (NoSuchAlgorithmException e) {
		// exception = e;
		// e.printStackTrace();
		// } catch (IOException e) {
		// exception = e;
		// e.printStackTrace();
		// } catch (KeyManagementException e) {
		// exception = e;
		// e.printStackTrace();
		// } catch (UnrecoverableKeyException e) {
		// exception = e;
		// e.printStackTrace();
		// } finally {
		// if (exception != null) {
		// return new DefaultHttpClient(httpParams);
		// }
		// }

		return httpClient;
	}

	/**
	 * 非严格认证 <该方法会默认所有证书通过验证>
	 * 
	 * @param params
	 * @return
	 */
	private static synchronized HttpClient getHttpClientNoSecurity(HttpParams params) {

		HttpClient httpClient = null;

		try {
			//
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			//
			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); // 允许所有主机的验证

			//
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);

			// 设置HTTP HTTPS支持
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", sf, 443));

			//
			ClientConnectionManager conManager = new ThreadSafeClientConnManager(params, schReg);
			httpClient = new DefaultHttpClient(conManager, params);

		} catch (Exception e) {
			Logs.printStackTrace(TAG + "getHttpClientNoSecurity()", e);
			return new DefaultHttpClient(params);
		}

		return httpClient;
	}
}
