
package com.lockulockme.locku.base.utils;

import android.util.Log;

public class LogUtil {

    public static boolean flag = true;

    public static void LogE(String TAG, String msg) {
        if (flag) {
            Log.e(TAG, msg);
        }
    }

    public static void LogD(String TAG, String msg) {
        if (flag) {
            Log.d(TAG, msg);
        }
    }

    public static void LogI(String TAG, String msg) {
        if (flag) {
            Log.i(TAG, msg);
        }
    }
}