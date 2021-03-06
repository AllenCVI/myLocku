package com.lockulockme.locku.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.lockuchat.http.HttpReqCacheClear;

import java.util.LinkedList;

public class MyActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    private static MyActivityLifecycle instance = new MyActivityLifecycle();
    private LinkedList<Activity> activityLinkedList = new LinkedList<>();

    public synchronized static MyActivityLifecycle getInstance() {
        return instance;
    }


    private void add(Activity activity) {
        activityLinkedList.remove(activity);
        activityLinkedList.add(activity);
    }

    private void remove(Activity activity) {
        activityLinkedList.remove(activity);
    }


    public void finishAllActivity(){
        for (Activity activity : activityLinkedList) {
            activity.finish();
        }
        activityLinkedList.clear();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        add(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        HttpReqCacheClear.clearHttp(activity.toString());
        remove(activity);
    }

}
