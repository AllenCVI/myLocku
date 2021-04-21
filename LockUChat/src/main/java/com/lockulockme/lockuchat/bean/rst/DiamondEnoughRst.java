package com.lockulockme.lockuchat.bean.rst;

import com.google.gson.annotations.SerializedName;

public class DiamondEnoughRst {
    @SerializedName("tjmyljpule")
    public String type;
    @SerializedName("simtqgrkwijmnsqgttIkid")
    public String stringId;

    public DiamondEnoughRst(String type, String stringId) {
        this.type = type;
        this.stringId = stringId;
    }
}
