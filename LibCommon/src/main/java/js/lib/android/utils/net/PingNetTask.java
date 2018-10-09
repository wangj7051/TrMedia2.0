package js.lib.android.utils.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import android.os.AsyncTask;

/**
 * Ping And Check Net Work
 * 
 * @author Jun.Wang
 */
public class PingNetTask extends AsyncTask<Void, Void, Boolean> {
	// TAG
	private final String TAG = "PingNetTask -> ";

	/**
	 * Target Ping URL
	 */
	private String mTargetUrl = "";

	/**
	 * Ping Net Listener
	 */
	private PingNetListener mPingNetListener;

	public interface PingNetListener {
		public void onPinged(boolean isNetActive);
	}

	public PingNetTask() {
	}

	public PingNetTask(String targetUrl, PingNetListener l) {
		mTargetUrl = targetUrl;
		mPingNetListener = l;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		return pingUrl();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		postResults((result == null) ? false : result);
	}

	private boolean pingUrl() {
		if (!EmptyUtil.isEmpty(mTargetUrl)) {
			Process p;
			try {
				// ping -c 3 -w 100 中 ，-c 是指ping的次数 3是指ping 3次 ，-w 100
				// 以秒为单位指定超时间隔，是指超时时间为100秒
				p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + mTargetUrl);
				int status = p.waitFor();

				InputStream input = p.getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(input));
				StringBuffer buffer = new StringBuffer();
				String line = "";
				while ((line = in.readLine()) != null) {
					buffer.append("\n" + line);
				}
				Logs.i(TAG, "pingUrl() -> RetureStr:" + buffer.toString());

				// status == 0 , means success
				// status == 0 , means fail
				return (status == 0);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void postResults(final boolean isNetActive) {
		if (mPingNetListener != null) {
			mPingNetListener.onPinged(isNetActive);
		}
	}
}
