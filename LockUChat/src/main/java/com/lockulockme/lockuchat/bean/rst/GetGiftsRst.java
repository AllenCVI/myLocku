package com.lockulockme.lockuchat.bean.rst;

import com.google.gson.annotations.SerializedName;

public class GetGiftsRst {
    @SerializedName("lrlafrnxjgtoutvaiigtpe")
    public String language;

    public GetGiftsRst(String language) {
        this.language = language;
    }
}
