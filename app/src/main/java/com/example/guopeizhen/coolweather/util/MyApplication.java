package com.example.guopeizhen.coolweather.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/8/24.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
