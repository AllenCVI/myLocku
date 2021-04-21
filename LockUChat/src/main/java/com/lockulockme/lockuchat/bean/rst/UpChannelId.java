package com.lockulockme.lockuchat.bean.rst;

import com.google.gson.annotations.SerializedName;

public class UpChannelId {
    @SerializedName("sbhinrgvdnyyaiulneCpxheeallnfnniaeoxlutItrd")
    public String imChannelId;

    @SerializedName("vhfiexdptesjodgClchluaagnsjnzxefvlgiIqsd")
    public String channelId;

    public UpChannelId(String imChannelId, String channelId) {
        this.imChannelId = imChannelId;
        this.channelId = channelId;
    }
}
