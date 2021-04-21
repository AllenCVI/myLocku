package com.lockulockme.lockuchat.utils;

import android.content.Context;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

public class ResouseUtils {
    private Context context;

    private ResouseUtils(){
    }
    private static class InstanceHolder{
        private static ResouseUtils INSTANCE = new ResouseUtils();
    }
    public static ResouseUtils getInstance(){
        return InstanceHolder.INSTANCE;
    }
    public void init(Context context){
        this.context=context;
    }

    public Context getContext() {
        return context;
    }

    public static String getResouseString(@StringRes int resId) {
        Context context = getInstance().getContext();
        return context.getString(resId);
    }
    public static String[] getResouseStringArray(@ArrayRes int resId) {
        Context context = getInstance().getContext();
        return context.getResources().getStringArray(resId);
    }

    public static int getResouseColor(@ColorRes int colorId) {
        Context context = getInstance().getContext();
        return context.getResources().getColor(colorId);
    }
}
