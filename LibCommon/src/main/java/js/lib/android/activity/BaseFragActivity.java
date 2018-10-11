package js.lib.android.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Base {@link FragmentActivity}
 *
 * @author Jun.Wang
 */
public abstract class BaseFragActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
