package js.lib.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

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
     * Resource ID
     */
    protected int mResID;

    /**
     * Layout Inflater
     */
    protected LayoutInflater mInflater;

    public BaseArrayAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }
}
