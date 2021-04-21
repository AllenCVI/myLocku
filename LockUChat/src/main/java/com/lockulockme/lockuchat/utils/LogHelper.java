package com.lockulockme.lockuchat.utils;

import android.util.Log;

public class LogHelper {
    public static boolean isLog=true;
    public static void e(String tag,String msg){
        if (isLog){
            Log.e(tag,msg);
        }
    }
}
