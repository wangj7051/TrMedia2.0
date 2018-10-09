package js.lib.android.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Kill Touch Down
 * 
 * @author Jun.Wang
 */
public class KillTouch implements OnTouchListener {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return true;
	}
}