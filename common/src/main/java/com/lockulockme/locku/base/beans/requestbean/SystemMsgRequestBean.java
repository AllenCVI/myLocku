package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class SystemMsgRequestBean {
    @SerializedName("pcuajtgbce")
    int page;
    @SerializedName("sheikbztqe")
    int size;

    public SystemMsgRequestBean(int page, int size) {
        this.page = page;
        this.size = size;
    }
}
