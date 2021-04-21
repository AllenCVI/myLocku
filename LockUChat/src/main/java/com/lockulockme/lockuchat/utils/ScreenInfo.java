package com.lockulockme.lockuchat.utils;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;


public class ScreenInfo {
    public int screenWidth;
    public int screenHeight;
    public float density;

    private ScreenInfo(){
    }
    private static class InstanceHolder{
        private static ScreenInfo INSTANCE = new ScreenInfo();
    }
    public static ScreenInfo getInstance(){
        return InstanceHolder.INSTANCE;
    }

    public int dip2px(float dipValue) {
        return (int) (dipValue * density + 0.5f);
    }

    public void init(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        density = dm.density;
    }
}
