package js.lib.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 该类是为了让GridView在被嵌套在其他滚动控件中，能够多行列显示
 * 
 * @author Jun.Wang
 */
public class IGridView extends GridView {
	public IGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IGridView(Context context) {
		super(context);
	}

	public IGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
