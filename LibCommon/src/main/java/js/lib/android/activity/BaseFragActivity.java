package js.lib.android.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Base {@link FragmentActivity}
 *
 * @author Jun.Wang
 */
public abstract class BaseFragActivity extends FragmentActivity {

    //==========Variable in this Activity==========
    // Flag :: is activity in foreground.
    private boolean mIsActForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsActForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsActForeground = false;
    }

    /**
     * Check if this activity is in foreground.
     */
    protected boolean isForeground() {
        return mIsActForeground;
    }
}
