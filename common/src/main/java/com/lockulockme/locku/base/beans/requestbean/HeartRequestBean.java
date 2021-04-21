package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class HeartRequestBean {
    @SerializedName("poqauicwvkiqekcthgIiunestwoexwrahvqjarwl")
    public int packetInterval;

    public HeartRequestBean(int packetInterval) {
        this.packetInterval = packetInterval;
    }
}
