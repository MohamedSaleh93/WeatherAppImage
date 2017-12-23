package com.weather.photo.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by MohamedSaleh on 12/23/2017.
 */

public class MainProgram extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
