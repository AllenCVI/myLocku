package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class HasRatingRequestBean {
    @SerializedName("atmnjvclxhraobprmmIxdmazIadd")
    public String anchorImId;

    public HasRatingRequestBean(String anchorImId) {
        this.anchorImId = anchorImId;
    }
}
