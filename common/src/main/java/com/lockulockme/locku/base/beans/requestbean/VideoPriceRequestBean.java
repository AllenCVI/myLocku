package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VideoPriceRequestBean {
    @SerializedName("simtqgrkwijmnsqgttIkid")
    public String id;
    @SerializedName("tjmyljpule")
    public String priceType;

    public VideoPriceRequestBean(String stringId) {
        this.id = stringId;
        priceType = null;
    }
}
