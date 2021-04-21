package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class AttentionListRequestBean {
    @SerializedName("pvhamggzxeahSgaiztzhqe")
    public int limit;
    @SerializedName("pboafigeveieNtpuacm")
    public int page;
    @SerializedName("tjmyljpule")
    public String type;

    public AttentionListRequestBean(int pageNum, int pageSize, String type) {
        this.limit = pageSize;
        this.page = pageNum;
        this.type = type;
    }
}
