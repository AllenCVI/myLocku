package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class VipInterceptRequestBean {

    public static final int VIP_INTERCEPT_TYPE = 2;
    public static final int DIAMOND_INTERCEPT_TYPE = 1;
    @SerializedName("gfuodlocadyhsxbTaoyegphie")
    public int type;

    public VipInterceptRequestBean(int type) {
        this.type = type;
    }
}
