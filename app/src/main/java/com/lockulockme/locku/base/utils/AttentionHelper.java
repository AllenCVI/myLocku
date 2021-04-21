package com.lockulockme.locku.base.utils;

import com.lockulockme.locku.base.beans.requestbean.AttentionRequestBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;

public class AttentionHelper {
    public AttentionHelper() {
    }

    static AttentionHelper attentionHelper;

    public static AttentionHelper getInstance() {
        if (attentionHelper == null) {
            attentionHelper = new AttentionHelper();
        }
        return attentionHelper;
    }

    public void attentionUser(Object tag, String userIdHash, NewJsonCallback<Void> callback) {
        OkGoUtils.getInstance().attention(tag, new AttentionRequestBean(userIdHash), callback);
    }

    public void unAttentionUser(Object tag, String userIdHash, NewJsonCallback<Void> callback) {
        OkGoUtils.getInstance().unAttention(tag, new AttentionRequestBean(userIdHash), callback);
    }
}
