package com.lockulockme.locku.zlockthree.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by ${xeh} on 2017/4/20 0020.
 */

public class BaseApplication extends Application {

    private static BaseApplication instance;
    private Set<Activity> allActivities;
    private Activity curActivity;


    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public void setCurActivity(Activity activity) {
        curActivity = activity;
    }

    public Activity getCurActivity() {
        return curActivity;
    }

    public void registerActivity(Activity act) {
        if (allActivities == null) {
            allActivities = new HashSet<Activity>();
        }
        allActivities.add(act);
    }

    public void unregisterActivity(Activity act) {
        if (allActivities != null) {
            allActivities.remove(act);
        }
    }

    public void exitApp() {
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    if (act != null && !act.isFinishing())
                        act.finish();
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
