package com.ray.gg;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.ray.gg.init.FrescoConfig;

import log.Log;

public class App extends Application {
    private static Application sApp;
    public App() {
        super();
    }

    public static Application get() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.setIsDebug(BuildConfig.DEBUG);
        Fresco.initialize(this, FrescoConfig.getImagePipelineConfig(getApplicationContext()));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sApp = this;
    }
}
