package com.lockulockme.lockuchat.bean.rsp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PriceItems {
    @SerializedName("tjmyljpule")
    public String type;
    @SerializedName("cbnohqsuft")
    public int cost;
    @SerializedName("uppnrmirit")
    public String unit;

    @Override
    public String toString() {
        return "PriceItems{" +
                "type='" + type + '\'' +
                ", cost=" + cost +
                ", unit='" + unit + '\'' +
                '}';
    }
}
