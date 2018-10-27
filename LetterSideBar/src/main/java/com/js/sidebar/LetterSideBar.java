package com.js.sidebar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LetterSideBar extends View {
    //TAG
    private static final String TAG = "LetterSideBar";

    //
    private int mFontColor, mHlFontColor;

    // 按压时选中的字母位置
    private int mLastTouchPos = -1;
    private int mTouchPos = -1;
    // 每个字母占有的区域高度
    private float mStepH;

    //Letters list
    private List<String> mListLetters = new ArrayList<>();

    /**
     * {@link LetterSideBarListener}
     */
    private LetterSideBarListener mListener;

    public interface LetterSideBarListener {
        /**
         * Callback when touch letters.
         *
         * @param pos    Letter position that u touched.
         * @param letter Letter that u touched.
         */
        void callback(int pos, String letter);
    }

    public LetterSideBar(Context context) {
        super(context);
        init(context);
    }

    public LetterSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LetterSideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mFontColor = getResources().getColor(android.R.color.white);
        mHlFontColor = Color.parseColor("#FF0000");
    }

    public void setColor(int fontColor, int hlFontColor) {
        if (fontColor != -1) {
            mFontColor = fontColor;
        }
        if (hlFontColor != -1) {
            mHlFontColor = hlFontColor;
        }
    }

    public void refreshLetters(List<String> listLetters) {
        if (listLetters == null || listLetters.size() == 0) {
            listLetters = new ArrayList<>();
            listLetters.add("A");
            listLetters.add("B");
            listLetters.add("C");
            listLetters.add("D");
            listLetters.add("E");
            listLetters.add("F");
            listLetters.add("G");
            listLetters.add("H");
            listLetters.add("I");
            listLetters.add("J");
            listLetters.add("K");
            listLetters.add("L");
            listLetters.add("M");
            listLetters.add("N");
            listLetters.add("O");
            listLetters.add("P");
            listLetters.add("Q");
            listLetters.add("R");
            listLetters.add("S");
            listLetters.add("T");
            listLetters.add("U");
            listLetters.add("V");
            listLetters.add("W");
            listLetters.add("X");
            listLetters.add("Y");
            listLetters.add("Z");
            listLetters.add("#");
        }

        //
        mListLetters.clear();
        mListLetters.addAll(listLetters);

        //
        invalidate();
    }

    public void addCallback(LetterSideBarListener l) {
        mListener = l;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw(Canvas)");
        //Check null
        if (mListLetters == null || mListLetters.size() == 0) {
            return;
        }

        // 字母书
        final int loop = mListLetters.size();
        // 每个字母占有的区域高度
        mStepH = ((float) getMeasuredHeight()) / loop;
        // 每个字母大小
        float fontSize = mStepH * 280 / 300;
        if (fontSize > 30f) {
            fontSize = 30f;
        }
        float fontSizeTouch = mStepH;
        if (fontSizeTouch > 40f) {
            fontSizeTouch = 40f;
        }
        // 每个字母绘制x坐标
        final float xCom = ((float) getMeasuredWidth()) * 9 / 10;

        //
        @SuppressLint("DrawAllocation")
        Paint paintCom = new Paint();
        paintCom.setColor(mFontColor);
        paintCom.setTextAlign(Paint.Align.RIGHT);
        paintCom.setTextSize(fontSize);
        paintCom.setTextScaleX(1.6f);
        paintCom.setStyle(Paint.Style.FILL_AND_STROKE);

        @SuppressLint("DrawAllocation")
        Paint paintTouch = new Paint();
        paintTouch.setColor(mHlFontColor);
        paintTouch.setTextAlign(Paint.Align.RIGHT);
        paintTouch.setTextSize(fontSizeTouch);
        paintTouch.setTextScaleX(1.6f);
        paintTouch.setStrokeWidth(3f);
        paintTouch.setStyle(Paint.Style.FILL_AND_STROKE);

        for (int idx = 0; idx < loop; idx++) {
            String letter = mListLetters.get(idx);
            if (TextUtils.isEmpty(letter) || TextUtils.isEmpty(letter.trim())) {
                continue;
            }

            float delta = (mStepH - fontSize) / 2;
            float y = mStepH * (idx + 1) - delta;
            float x = xCom;
            if (mTouchPos != -1) {
                switch (Math.abs(mTouchPos - idx)) {
//                    case 3:
//                        x *= 0.85;
//                        break;
                    case 2:
                        x *= 0.7;
                        break;
                    case 1:
                        x *= 0.55;
                        break;
                    case 0:
                        x *= 0.4;
                        break;
                }
            }
            if (mTouchPos == idx) {
                canvas.drawText(letter, x, y, paintTouch);
            } else {
                canvas.drawText(letter, x, y, paintCom);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "-- ACTION_DOWN --");
                refresh(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "-- ACTION_UP --");
                refresh(-1);
                break;
        }
//        return super.dispatchTouchEvent(event);
        return true;
    }

    private void refresh(float touchY) {
        Log.i(TAG, "refresh(" + touchY + ")");
        if (touchY < 0 || mStepH <= 0 || mListLetters == null || mListLetters.size() == 0) {
            mTouchPos = -1;
            mLastTouchPos = -1;
            invalidate();
            return;
        }

        //Get touch position
        float posFloat = touchY / mStepH;
        mTouchPos = (int) Math.floor(posFloat);
        //Filter multiply
        if (mLastTouchPos == mTouchPos) {
            return;
        }

        //Draw
        invalidate();

        //Callback
        if (mListener != null) {
            try {
                mListener.callback(mTouchPos, mListLetters.get(mTouchPos));
            } catch (Exception e) {
                Log.i(TAG, "refresh(touchY) > " + e.getMessage());
            }
        }

        //Record last position.
        mLastTouchPos = mTouchPos;
    }
}
