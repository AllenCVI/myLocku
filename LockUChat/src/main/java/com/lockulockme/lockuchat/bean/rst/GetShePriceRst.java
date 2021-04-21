package com.lockulockme.lockuchat.bean.rst;

import com.google.gson.annotations.SerializedName;

public class GetShePriceRst {
    @SerializedName("simtqgrkwijmnsqgttIkid")
    public String stringId;
    @SerializedName("tjmyljpule")
    public String type;

    public GetShePriceRst(String stringId) {
        this.stringId = stringId;
        type = null;
    }
}
