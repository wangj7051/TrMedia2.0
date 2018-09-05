package com.lib.view.contacts;

import js.lib.android.utils.Logs;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.lib.view.ViewDispatchTouchListener;
import com.tricheer.player.R;

/**
 * Letter Side Bar
 * 
 * Jun.Wang
 */
public class SideBar extends View {
	// TAG
	private static final String TAG = "SideBar -> ";

	/**
	 * ==========Widget in this View==========
	 */
	/**
	 * Center Letter Square View
	 */
	private TextView tvCenterSquare;

	/**
	 * ==========Variable in this Activity==========
	 */
	/**
	 * Context
	 */
	private Context mContext;

	/**
	 * SideBarDispatchTouchListener
	 */
	private ViewDispatchTouchListener mDispatchTouchListener;

	/**
	 * Listener Touching Letter Change
	 */
	private OnTouchingLetterChangedListener mTouchingLetterChangedListener;

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

	/**
	 * Letter Text Size
	 */
	private int mLetterFontSize = 20;

	/**
	 * 选中位置
	 */
	private int mChoosePos = -1;

	/**
	 * Transparent Color
	 */
	private int mColorTransparent;

	/**
	 * 26 Letters
	 */
	private String[] mLetters = new String[] {};

	/**
	 * Painter
	 */
	private Paint mPaint = new Paint();

	/**
	 * Letter Color
	 */
	private int mLetterColor = Color.rgb(33, 65, 98), mLetterColorHL = Color.parseColor("#3399ff");

	/**
	 * Background on Touch
	 */
	private int mBgResID = R.drawable.lib_sidebar_bg;

	public SideBar(Context context) {
		super(context);
		init(context);
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context cxt) {
		this.mContext = cxt;
		this.mColorTransparent = mContext.getResources().getColor(android.R.color.transparent);
	}

	/**
	 * 重写这个方法
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		try {
			if (mLetters == null || mLetters.length == 0) {
				return;
			}

			// 获取焦点改变背景颜色.
			// 获取对应高度
			int height = getHeight();
			// 获取对应宽度
			int width = getWidth();
			// 获取每一个字母所占据的高度
			int deltaY = height / mLetters.length;

			// 绘制每个字母
			for (int i = 0; i < mLetters.length; i++) {
				mPaint.setColor(mLetterColor);
				mPaint.setTypeface(Typeface.DEFAULT_BOLD);
				mPaint.setAntiAlias(true);
				mPaint.setTextSize(20);

				// 选中的状态
				if (i == mChoosePos) {
					mPaint.setColor(mLetterColorHL);
					mPaint.setFakeBoldText(true);
				}

				// x坐标等于中间-字符串宽度的一半.
				float xPos = width / 2 - mPaint.measureText(mLetters[i]) / 2;
				// Y标
				float yPos = deltaY * i + mLetterFontSize;
				// 绘制
				canvas.drawText(mLetters[i], xPos, yPos, mPaint);

				// 重置画笔
				mPaint.reset();
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "onDraw()", e);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();// 点击y坐标
		final int oldChoose = mChoosePos;
		final int c = (int) (y / getHeight() * mLetters.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

		switch (action) {
		case MotionEvent.ACTION_UP:
			setBackgroundColor(mColorTransparent);
			mChoosePos = -1;//
			invalidate();
			if (tvCenterSquare != null) {
				tvCenterSquare.setVisibility(View.INVISIBLE);
			}
			break;

		default:
			setBackgroundResource(mBgResID);
			if (oldChoose != c) {
				if (c >= 0 && c < mLetters.length) {
					if (mTouchingLetterChangedListener != null) {
						mTouchingLetterChangedListener.onTouchingLetterChanged(mLetters[c]);
					}
					if (tvCenterSquare != null) {
						tvCenterSquare.setText(mLetters[c]);
						tvCenterSquare.setVisibility(View.VISIBLE);
					}

					mChoosePos = c;
					invalidate();
				}
			}

			break;
		}

		//
		if (mDispatchTouchListener != null) {
			mDispatchTouchListener.onEventChange(event);
		}
		return true;
	}

	/**
	 * Set touching background
	 */
	public void setTouchBg(int bgResID) {
		this.mBgResID = bgResID;
	}

	/**
	 * Set Letter Paint Color
	 */
	public void setLetterColor(int color, int colorHL) {
		this.mLetterColor = color;
		this.mLetterColorHL = colorHL;
	}

	/**
	 * Set Letter Paint Color
	 */
	public void setLetterFontSize(int fontSize) {
		this.mLetterFontSize = fontSize;
	}

	/**
	 * Refresh Letters Source
	 */
	public void refreshLetters(Object[] objLetters) {
		try {
			if (objLetters == null || objLetters.length == 0) {
				this.mLetters = new String[] {};
			}

			int loopNum = objLetters.length;
			this.mLetters = new String[loopNum];
			for (int idx = 0; idx < loopNum; idx++) {
				this.mLetters[idx] = (String) objLetters[idx];
			}

			invalidate();
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "refreshLetters()", e);
		}
	}

	/**
	 * Set Center Square
	 * 
	 * @param bg
	 *            : center square background
	 * @param txtColor
	 *            : center square text color
	 */
	public void setCenterSquare(TextView tvSquare, int bgResID, int txtColor) {
		this.tvCenterSquare = tvSquare;
		if (bgResID != -1) {
			this.tvCenterSquare.setBackgroundResource(bgResID);
		}
		if (txtColor != -1) {
			this.tvCenterSquare.setTextColor(txtColor);
		}
	}

	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.mTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public void setSideBarDispatchTouchListener(ViewDispatchTouchListener l) {
		this.mDispatchTouchListener = l;
	}
}