package js.lib.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class SeekBarImpl extends SeekBar {

    private boolean mIsCanSeek = true;

    public SeekBarImpl(Context context) {
        super(context);
    }

    public SeekBarImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCanSeek(boolean isCanSeek) {
        mIsCanSeek = isCanSeek;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !mIsCanSeek || super.onTouchEvent(event);
    }
}
