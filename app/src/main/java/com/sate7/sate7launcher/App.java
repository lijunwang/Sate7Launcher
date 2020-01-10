package com.sate7.sate7launcher;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    public static App sApplication;
    public static int screenWidth;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

    }

    public static App getApp() {
        return sApplication;
    }

    public static Context getAppContext() {
        return sApplication.getApplicationContext();
    }
}