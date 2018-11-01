package js.lib.android.media.engine.video;

import android.os.Handler;

/**
 * Video light mode controller
 *
 * @author Jun.Wang
 */
public class MediaLightModeController {
    /**
     * Thread handler
     */
    Handler mHandler = new Handler();

    // Means Light Mode Enable and Show Panel
    private final int ON = 1;
    // Means Light Mode Enable and Hide Panel
    private final int OFF = 2;

    /**
     * Light Mode Flag
     * <p>{@link #ON} or {@link #OFF}</p>
     */
    private int mLightMode = ON;

    /**
     * {@link MediaLightModeListener} object
     */
    private MediaLightModeListener mMediaLightModeListener;

    public interface MediaLightModeListener {
        void onLightOn();

        void onLightOff();
    }

    /**
     * Constructor
     */
    public MediaLightModeController() {
    }

    /**
     * Is Light Mode On
     */
    protected boolean isLightOn() {
        return mLightMode == ON;
    }

    /**
     * Set Light Mode
     *
     * @param lightMode {@link #ON} or {@link #OFF}
     */
    private void setLightMode(int lightMode) {
        this.mLightMode = lightMode;
        if (mMediaLightModeListener != null) {
            switch (mLightMode) {
                case ON:
                    mMediaLightModeListener.onLightOn();
                    break;
                case OFF:
                    mMediaLightModeListener.onLightOff();
                    break;
            }
        }
    }

    public void makeLightOff() {
        setLightMode(OFF);
    }

    public void makeLightOn() {
        setLightMode(ON);
    }

    /**
     * Keep current mode is ON.
     */
    public void keepLightOn() {
        makeLightOn();
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Switch Light Mode
     */
    public void switchLightMode() {
        switch (mLightMode) {
            case ON:
                makeLightOff();
                break;
            case OFF:
                resetLightMode();
                break;
        }
    }

    /**
     * Reset Light Mode
     */
    public void resetLightMode() {
        makeLightOn();
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                setLightMode(OFF);
            }
        }, 3 * 1000);
    }

    /**
     * Settings on activity or other view destroy
     */
    public void destroy() {
        mHandler.removeCallbacksAndMessages(null);
    }

    public void addModeListener(MediaLightModeListener l) {
        this.mMediaLightModeListener = l;
    }
}
