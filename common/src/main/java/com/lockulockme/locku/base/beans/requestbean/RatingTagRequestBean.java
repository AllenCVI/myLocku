package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class RatingTagRequestBean {
    public static int VIDEO_TAG_TYPE = 1;
    public static int AUDIO_TAG_TYPE = 2;
    @SerializedName("tjmyljpule")
    public int tagType;

    public RatingTagRequestBean(int tagType) {
        this.tagType = tagType;
    }
}
