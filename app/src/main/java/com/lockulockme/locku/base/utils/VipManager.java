package com.lockulockme.locku.base.utils;


import android.text.TextUtils;

import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;

import java.util.Timer;
import java.util.TimerTask;

public class VipManager {

    static VipManager vipManager;
    private Timer timer;
    private TimerTask task;
    private int timerTag = 0;
    public String TAG = "VipManager";
    /**
     * -1未知状态
     * 1是vip
     * 0 不是
     */
    private VipResponseBean vipResp = null;

    public static VipManager getInstance() {
        if (vipManager == null) {
            vipManager = new VipManager();
        }
        return vipManager;
    }

    public VipManager() {
    }

    public void getVipState(Object tag, final OnVipListener onVipListener) {
        if (TextUtils.isEmpty(AccountManager.getInstance().getToken())){
            if (onVipListener != null) {
                onVipListener.onVipFailed();
            }
            return;
        }
        LogUtil.LogD(TAG, "vipResp == null:" + (vipResp == null));
        if (vipResp == null) {
            OkGoUtils.getInstance().getVipState(tag, new NewJsonCallback<VipResponseBean>() {
                @Override
                public void onSuc(VipResponseBean response, String msg) {
                    resetMills();
                    vipResp = response;
                    if (onVipListener != null) onVipListener.onVipSuccess(vipResp);
                }

                @Override
                public void onE(int httpCode, int apiCode, String msg, VipResponseBean response) {
                    if (onVipListener != null) onVipListener.onVipFailed();
                }
            });
        } else {
            if (onVipListener != null) onVipListener.onVipSuccess(vipResp);
        }
    }

    public VipResponseBean getVipStateFromCache() {
        return vipResp;
    }

    public VipManager reset() {
        vipResp = null;
        return this;
    }

    private void resetMills() {
        this.timerTag = 0;
    }


    public interface OnVipListener {
        void onVipSuccess(VipResponseBean vipResp);

        void onVipFailed();
    }

    public VipResponseBean getVipResp() {
        return vipResp;
    }

    private void initTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                LogUtil.LogD("timerTask", "timerTag:" + timerTag);
                if (timerTag % 5 == 0) {
                    getVipState(null, null);
                }
                timerTag++;
            }
        };
    }


    public void startTimer() {
        timerTag = 0;
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }
        initTimer();
        timer.schedule(task, 0, 1000 * 60);
    }

    public void cancelTimer() {
        try {
            if (timer != null) {
                timerTag = 0;
                timer.cancel();
            }
            if (task != null) {
                task.cancel();
            }
        }catch (Exception e){

        }

    }

}
