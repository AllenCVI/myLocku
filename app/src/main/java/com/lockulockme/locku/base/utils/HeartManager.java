package com.lockulockme.locku.base.utils;


import com.lockulockme.locku.base.beans.requestbean.HeartRequestBean;
import com.lockulockme.locku.base.callback.StringCall;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;

public class HeartManager {

    static HeartManager heartManager;
    private Timer timer;
    private TimerTask task;
    private final String TAG = "HeartManager";

    public static HeartManager getInstance() {
        if (heartManager == null) {
            heartManager = new HeartManager();
        }
        return heartManager;
    }

    public HeartManager() {
    }

    private void startHeart() {
        if ("".equals(AccountManager.getInstance().getToken())){
            return;
        }
        OkGoUtils.getInstance().updateOnline(new HeartRequestBean(60), new StringCall() {
            @Override
            public void onSuccess(Response response) {

            }
        });
    }

    private void initTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                startHeart();
            }
        };
    }


    public void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }
        initTimer();
        timer.schedule(task, 0, 1000 * 60);
    }
}
