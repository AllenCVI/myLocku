package com.lockulockme.lockuchat.utils;

import android.content.Context;

public class ContextHelper {
    private Context context;

    private static class InstanceHolder{
        private static ContextHelper INSTANCE = new ContextHelper();
    }
    public static ContextHelper getInstance(){
        return ContextHelper.InstanceHolder.INSTANCE;
    }

    public void init(Context context){
        this.context=context;
    }

    public Context getContext() {
        return context;
    }
}
