package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class EnoughRequestBean {
    @SerializedName("tjmyljpule")
    public String type;
    @SerializedName("simtqgrkwijmnsqgttIkid")
    public String stringId;

    public EnoughRequestBean(String type, String stringId) {
        this.type = type;
        this.stringId = stringId;
    }
}
