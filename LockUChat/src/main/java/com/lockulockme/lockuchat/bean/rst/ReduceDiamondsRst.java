package com.lockulockme.lockuchat.bean.rst;

import com.google.gson.annotations.SerializedName;

public class ReduceDiamondsRst {
    @SerializedName("tjmyljpule")
    public String type;
    @SerializedName("tseabernngjcenzthfIifmcaIhgd")
    public String targetImId;
    @SerializedName("czhhhcagvnvjnfmevylncIiad")
    public String channelId;

    public ReduceDiamondsRst(String type, String targetImId) {
        this.type = type;
        this.targetImId = targetImId;
    }

    public ReduceDiamondsRst(String type, String targetImId, String channelId) {
        this.type = type;
        this.targetImId = targetImId;
        this.channelId = channelId;
    }
}
