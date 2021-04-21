package com.lockulockme.locku.zlocktwo.base.utils;

import com.lockulockme.locku.base.beans.requestbean.EnoughRequestBean;
import com.lockulockme.locku.base.beans.responsebean.EnoughResponseBean;
import com.lockulockme.locku.zlocktwo.base.callback.NewJsonCallback;

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

    }


    public interface EnoughListener {
        void onEnoughSuc(EnoughResponseBean bean);

        void onEnoughFailed();
    }
}
