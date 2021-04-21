package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class GetUrlV2RequestBean {
    @SerializedName("guyofooipdwyslsIqhd")
    public String gId;

    @SerializedName("pvwaxfyxmThmybgpote")
    public long pType;

    @SerializedName("aupadwIjnd")
    public String googleId;

    public GetUrlV2RequestBean(String gId, long pType, String googleId) {
        this.gId = gId;
        this.pType = pType;
        this.googleId = googleId;
    }
}
