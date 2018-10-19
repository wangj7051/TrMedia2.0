package com.tri.lib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import js.lib.android.utils.Logs;
import js.lib.android.utils.SysBrightnessUtils;
import js.lib.android.utils.SysVolUtils;

public class PanelTouchImpl implements View.OnTouchListener {
    //TAG
    private static final String TAG = "PanelTouchImpl";

    /**
     * {@link Context}
     */
    private Context mContext;

    //Adjust brightness variables
    private boolean mIsAdjustScreenBrightness;

    //Adjust volume variables
    private boolean mIsAdjustSysVol;
    private int mAdjustBrightness;

    //Adjust progress variables
    private boolean mIsAdjustProgress;
    private int mAdjustProgressDelta;
    /**
     * 0-Scroll to right; 1-Scroll to left.
     */
    private int mAdjustProgressDirection;


    /**
     * {@link GestureDetectorCompat}
     */
    private GestureDetectorCompat mGestureDetectorCompat;
    private int mLimitLeftX = 300, mLimitRightX = 980;

    /**
     * {@link PanelTouchCallback}
     */
    private PanelTouchCallback mCallback;

    public interface PanelTouchCallback {
        void onActionDown();

        void onActionUp();

        /**
         * You can switch light mode here.
         */
        void onSingleTapUp();

        void onPrepareAdjustBrightness();

        /**
         * @param rate (1~256)/256
         */
        void onAdjustBrightness(double rate);

        void onPrepareAdjustVol();

        /**
         * @param vol Volume value between system volume area.
         */
        void onAdjustVol(int vol, int maxVol);

        void onPrepareAdjustProgress();

        /**
         * @param direction     0-Scroll to right; 1-Scroll to left.
         * @param progressDelta Progress delta.
         */
        void onAdjustProgress(int direction, int progressDelta);

        /**
         * You can seek void progress here.
         *
         * @param direction     0-Scroll to right; 1-Scroll to left.
         * @param progressDelta Progress delta.
         */
        void seekProgress(int direction, int progressDelta);
    }

    public void init(Context context) {
        mContext = context;
        mGestureDetectorCompat = new GestureDetectorCompat(mContext, new GdOnGesture());
    }

    public void addCallback(PanelTouchCallback callback) {
        mCallback = callback;
    }

    /**
     * 设置触摸移动区间
     *
     * @param leftX  左侧范围
     * @param rightX 右侧范围
     */
    public void setScrollLimit(int leftX, int rightX) {
        mLimitLeftX = leftX;
        mLimitRightX = rightX;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "(" + event.getX() + "," + event.getY() + ")");
                //Callback
                if (mCallback != null) {
                    mCallback.onActionDown();
                }
                break;
            case MotionEvent.ACTION_UP:
                //Callback
                if (mCallback != null) {
                    mCallback.onActionUp();
                }

                //Adjust
                if (mIsAdjustScreenBrightness) {
                    mIsAdjustScreenBrightness = false;
                    adjustBrightnessOnActionUp();
                } else if (mIsAdjustSysVol) {
                    mIsAdjustSysVol = false;
                } else if (mIsAdjustProgress) {
                    mIsAdjustProgress = false;
                    adjustSeekOnActionUp();
                }
                break;
        }
        return mGestureDetectorCompat.onTouchEvent(event);
    }

    private void adjustBrightnessOnActionUp() {
        SysBrightnessUtils.setSystemBrightness(mContext, mAdjustBrightness);
    }

    private void adjustSeekOnActionUp() {
        if (mCallback != null) {
            mCallback.seekProgress(mAdjustProgressDirection, mAdjustProgressDelta);
        }
    }

    /**
     * VideoView Cover Gesture Implements
     */
    private class GdOnGesture extends GestureDetector.SimpleOnGestureListener {

        private float mmDistanceXVal, mmDistanceYVal;
        private int mmSysVol, mmSysMaxVol;
        private int mmSysBrightness, mmSysMaxBrightness;

        @Override
        public boolean onDown(MotionEvent e) {
            // System Volume
            mmSysVol = SysVolUtils.getMusicVolVal(mContext, false);
            mmSysMaxVol = SysVolUtils.getMusicVolVal(mContext, true);
            // System Screen Brightness
            mmSysBrightness = SysBrightnessUtils.getScreenBrightness(mContext);
            mmSysMaxBrightness = SysBrightnessUtils.getScreenMaxBrightness();

            //If u want use "onSingleTapUp" or "onScroll" or other methods,
            // u should return true to keep event continue.
            return true;
            //            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Logs.i("GestureDetectorCompat", "----onSingleTapUp----");
            if (mCallback != null) {
                mCallback.onSingleTapUp();
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Adjust screen brightness
            if (isAdjustScreenBrightness(e1.getX()) && isAdjustScreenBrightness(e2.getX())) {
                mmDistanceYVal = e2.getY() - e1.getY();
//                adjustScreenBrightness();

                // Adjust Volume
            } else if (isAdjustVolume(e1.getX()) && isAdjustVolume(e2.getX())) {
                mmDistanceYVal = e2.getY() - e1.getY();
//                adjustSysVol();

                // Adjust progress
            } else if (isAdjustProgress(e1.getX()) && isAdjustProgress(e2.getX())) {
                mmDistanceXVal = e2.getX() - e1.getX();
                adjustProgress();
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        private boolean isAdjustScreenBrightness(float x) {
            return (x <= mLimitLeftX);
        }

        private boolean isAdjustVolume(float x) {
            return (x >= mLimitRightX);
        }

        private boolean isAdjustProgress(float x) {
            return (x <= (mLimitRightX - 10) && x >= (mLimitLeftX + 10));
        }

        private void adjustScreenBrightness() {
            float absDistanceYVal = Math.abs(mmDistanceYVal);
            if (absDistanceYVal >= 50) {
                if (!mIsAdjustScreenBrightness) {
                    mIsAdjustScreenBrightness = true;
                    if (mCallback != null) {
                        mCallback.onPrepareAdjustBrightness();
                    }
                }

                // Set System Brightness
                Logs.i("GestureDetectorCompat", "adjustScreenBrightness() -> [mmDistanceYVal:" + mmDistanceYVal + "]");
                int distanceBrightness = ((int) (absDistanceYVal / 3));
                if (distanceBrightness == 0) {
                    distanceBrightness = 1;
                }
                if (mmDistanceYVal < 0) {
                    mAdjustBrightness = mmSysBrightness + distanceBrightness;
                } else if (mmDistanceYVal > 0) {
                    mAdjustBrightness = mmSysBrightness - distanceBrightness;
                }
                if (mAdjustBrightness > mmSysMaxBrightness) {
                    mAdjustBrightness = mmSysMaxBrightness;
                } else if (mAdjustBrightness < 10) {
                    mAdjustBrightness = 10;
                }
                double rate = ((double) (mAdjustBrightness - 10)) / (mmSysMaxBrightness - 10);
                if (mCallback != null) {
                    mCallback.onAdjustBrightness(rate);
                }
            }
        }

        private void adjustSysVol() {
            float absDistanceYVal = Math.abs(mmDistanceYVal);
            if (absDistanceYVal >= 50) {
                if (!mIsAdjustSysVol) {
                    mIsAdjustSysVol = true;
                    if (mCallback != null) {
                        mCallback.onPrepareAdjustVol();
                    }
                }

                // Set System Volume
                Logs.i("GestureDetectorCompat", "adjustSysVol() -> [mmDistanceYVal:" + mmDistanceYVal + "]");
                int distanceVol = ((int) (absDistanceYVal / 50));
                if (distanceVol == 0) {
                    distanceVol = 1;
                }

                int adjustVolDelta = 0;
                if (mmDistanceYVal < 0) {
                    adjustVolDelta = mmSysVol + distanceVol;
                } else if (mmDistanceYVal > 0) {
                    adjustVolDelta = mmSysVol - distanceVol;
                }
                if (adjustVolDelta > mmSysMaxVol) {
                    adjustVolDelta = mmSysMaxVol;
                } else if (adjustVolDelta < 0) {
                    adjustVolDelta = 0;
                }
                SysVolUtils.setMusicVolVal(mContext, adjustVolDelta, AudioManager.FLAG_SHOW_UI);
                if (mCallback != null) {
                    mCallback.onAdjustVol(adjustVolDelta, mmSysMaxVol);
                }
            }
        }

        private void adjustProgress() {
            float absDistanceXVal = Math.abs(mmDistanceXVal);
            if (absDistanceXVal >= 50) {
                if (!mIsAdjustProgress) {
                    mIsAdjustProgress = true;
                    if (mCallback != null) {
                        mCallback.onPrepareAdjustProgress();
                    }
                }

                // Set progress
                Logs.i("GestureDetectorCompat", "adjustProgress() -> [mmDistanceXVal:" + mmDistanceXVal + "]");
                mAdjustProgressDelta = ((int) (absDistanceXVal / 50)) * 1000;
                if (mmDistanceXVal > 0) {
                    mAdjustProgressDirection = 0;
                    Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>");
                } else if (mmDistanceXVal < 0) {
                    mAdjustProgressDirection = 1;
                    Log.i(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<");
                }
                if (mCallback != null) {
                    mCallback.onAdjustProgress(mAdjustProgressDirection, mAdjustProgressDelta);
                }
            }
        }
    }
}
