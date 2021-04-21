package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class AttentionRequestBean {
    @SerializedName("uspsgrenyrcyIjfdkqHetaqusouh")
    public String userStringId;

    public AttentionRequestBean(String userIdHash) {
        this.userStringId = userIdHash;
    }
}
