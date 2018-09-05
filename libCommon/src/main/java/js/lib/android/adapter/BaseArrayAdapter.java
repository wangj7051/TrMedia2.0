package js.lib.android.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Base Array Adapter
 * 
 * @author 409157
 */
public class BaseArrayAdapter<T> extends ArrayAdapter<T> {
	/**
	 * Context
	 */
	protected Context mContext;

	/**
	 * Thread Handler
	 */
	protected Handler mHandler;

	/**
	 * Resource ID
	 */
	protected int mResID;

	/**
	 * Layout Inflater
	 */
	protected LayoutInflater mInflater;

	/**
	 * Image loader
	 */
	protected ImageLoader mImageLoader;

	public BaseArrayAdapter(Context context, int resource) {
		super(context, resource);

		mContext = context;
		mResID = resource;
		mInflater = LayoutInflater.from(mContext);
		mHandler = new Handler();
	}

	/**
	 * Set Image loader
	 */
	public void setImageLoader(ImageLoader imgLoader) {
		this.mImageLoader = imgLoader;
	}
}
