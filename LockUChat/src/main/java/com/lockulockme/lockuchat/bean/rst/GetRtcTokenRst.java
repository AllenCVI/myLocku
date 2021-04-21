package com.lockulockme.lockuchat.bean.rst;

import com.google.gson.annotations.SerializedName;

public class GetRtcTokenRst {
    @SerializedName("nlsicsmntUohigfd")
    public long nimUid;

    public GetRtcTokenRst(long nimUid) {
        this.nimUid = nimUid;
    }
}
