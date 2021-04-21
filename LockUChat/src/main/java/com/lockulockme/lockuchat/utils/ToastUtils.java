package com.lockulockme.lockuchat.utils;

import android.widget.Toast;

import androidx.annotation.StringRes;

public class ToastUtils {
    public static void toastShow(@StringRes int resId, int duration){
        Toast.makeText(ContextHelper.getInstance().getContext(),resId,duration).show();
    }
    public static void toastShow(@StringRes int resId){
        toastShow(resId,Toast.LENGTH_SHORT);
    }
    public static void toastShow(String str, int duration){
        Toast.makeText(ContextHelper.getInstance().getContext(),str,duration).show();
    }
    public static void toastShow(String str){
        toastShow(str,Toast.LENGTH_SHORT);
    }
}
