package com.lockulockme.lockuchat.common;

import android.app.Application;

import com.lzf.easyfloat.EasyFloat;

public class InitFloatWindow {
    public static void initFloatWindow(Application application){
        EasyFloat.init(application, false);
    }
}
