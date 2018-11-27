package com.js.sidebar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
    //TAG
    private final String TAG = "SideBarMain";

    private LetterSideBar lsb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        lsb = (LetterSideBar) findViewById(R.id.lsb);
        lsb.addCallback(new LetterSideBar.LetterSideBarListener() {
            @Override
            public void callback(int pos, String letter) {
                Log.i(TAG, "lsb.calback(" + pos + "," + letter + ")");
            }

            @Override
            public void onScroll(boolean isScrolling) {
            }
        });
    }
}
