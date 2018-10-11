package com.tricheer.radio;

import android.app.Application;

import com.tricheer.radio.utils.TrRadioPreferUtils;


/**
 * Application
 *
 * @author Jun.Wang
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        TrRadioPreferUtils.init(this);
    }
}
