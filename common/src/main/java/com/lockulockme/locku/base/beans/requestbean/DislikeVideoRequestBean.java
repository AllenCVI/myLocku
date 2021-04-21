package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DislikeVideoRequestBean{
    @SerializedName("vpqicmdsuekeowfIafd")
    public int videoId;

    public DislikeVideoRequestBean(int videoId) {
        this.videoId = videoId;
    }
}
