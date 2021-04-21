package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class FacebookRequestBean {
    @SerializedName("axncdhcfnehssxdsjhTzuohbkwuejzn")
    public String accessToken;
    @SerializedName("favayacsgeacbkvohdoyhkmzIdhd")
    public String facebookId;

    public FacebookRequestBean(String accessToken, String facebookId) {
        this.accessToken = accessToken;
        this.facebookId = facebookId;
    }
}
