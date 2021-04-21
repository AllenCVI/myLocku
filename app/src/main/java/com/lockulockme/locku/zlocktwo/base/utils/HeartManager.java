package com.lockulockme.locku.zlocktwo.base.utils;



import java.util.Timer;
import java.util.TimerTask;

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
