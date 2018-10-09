package js.lib.android.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * {@link ViewPager} Implement
 */
public class ViewPagerImpl extends ViewPager {

    private boolean mIsScrollEnable = true;

    public ViewPagerImpl(Context context) {
        super(context);
    }

    public ViewPagerImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollEnable(boolean isScrollEnable) {
        mIsScrollEnable = isScrollEnable;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev) && mIsScrollEnable;
    }
}
