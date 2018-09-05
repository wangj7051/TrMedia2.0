package js.lib.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 该类是为了让ListView在被嵌套在其他滚动控件中，能够多行列显示
 * 
 * @author Jun.Wang
 */
public class IListView extends ListView {
	public IListView(Context context) {
		super(context);
	}

	public IListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
