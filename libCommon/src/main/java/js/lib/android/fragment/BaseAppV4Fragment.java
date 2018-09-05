package js.lib.android.fragment;

import js.lib.android.utils.ImageLoaderUtils;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.nostra13.universalimageloader.core.ImageLoader;

public class BaseAppV4Fragment extends Fragment {
	/**
	 * ImageLoader
	 */
	protected ImageLoader mImageLoader;
	protected Handler mHandler = new Handler();

	protected void init(String path) {
		this.mImageLoader = ImageLoaderUtils.getImageLoader(path);
	}
}
