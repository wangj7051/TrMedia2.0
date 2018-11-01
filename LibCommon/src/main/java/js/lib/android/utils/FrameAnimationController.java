package js.lib.android.utils;

import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

public class FrameAnimationController {
    private Handler mmHandler = new Handler();

    //
    private ImageView iv;

    //Resources
    private int[] mmFrameImgResArrs;
    private int mmIdx = 0;

    public FrameAnimationController() {
    }

    public void setIv(ImageView iv) {
        this.iv = iv;
    }

    public void setFrameImgResArr(int[] mmFrameImgResArrs) {
        this.mmFrameImgResArrs = mmFrameImgResArrs;
    }

    public void start() {
        if (iv == null
                || mmFrameImgResArrs == null
                || mmFrameImgResArrs.length == 0) {
            return;
        }
        mmIdx = 0;
        start(mmFrameImgResArrs[mmIdx]);
    }

    private void start(@DrawableRes final int frameImgRes) {
        iv.setImageResource(frameImgRes);
        mmHandler.removeCallbacksAndMessages(null);
        mmHandler.postDelayed(mRefreshFrameRunnable, 50);
    }

    private Runnable mRefreshFrameRunnable = new Runnable() {
        @Override
        public void run() {
            mmIdx++;
            if (mmIdx >= mmFrameImgResArrs.length) {
                mmIdx = 0;
            }
            start(mmFrameImgResArrs[mmIdx]);
        }
    };

    public void stop() {
        mmHandler.removeCallbacksAndMessages(null);
    }

    public void destroy() {
        stop();
        if (iv != null) {
            iv.setImageResource(0);
            iv = null;
        }
    }
}
