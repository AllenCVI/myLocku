package com.lockulockme.locku.zlockfive.base.utils;

import com.lockulockme.locku.base.beans.requestbean.EnoughRequestBean;
import com.lockulockme.locku.base.beans.responsebean.EnoughResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;

public class EnoughManager {
    static EnoughManager manager;
    public static final String VOICE_TYPE = "voiceChat";
    public static final String VIDEO_TYPE = "videoChat";

    public static EnoughManager getInstance() {
        if (manager == null) {
            manager = new EnoughManager();
        }
        return manager;
    }

    public void isEnough(String type, String id, Object tag, EnoughListener listener) {
        OkGoUtils.getInstance().isEnough(tag, new EnoughRequestBean(type, id), new NewJsonCallback<EnoughResponseBean>() {
            @Override
            public void onSuc(EnoughResponseBean response, String msg) {
                if (listener != null) {
                    listener.onEnoughSuc(response);
                }
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, EnoughResponseBean response) {
                super.onE(httpCode, apiCode, msg, response);
                if (listener != null) {
                    listener.onEnoughFailed();
                }
            }
        });
    }


    public interface EnoughListener {
        void onEnoughSuc(EnoughResponseBean bean);

        void onEnoughFailed();
    }
}
