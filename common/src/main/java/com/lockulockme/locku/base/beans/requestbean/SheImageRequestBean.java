package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SheImageRequestBean {
    @SerializedName("uqnswyepgrteScntcerdaiwdnyfgiaIfvd")
    public String userId;
    @SerializedName("pboafigeveieNtpuacm")
    public int pageNum;
    @SerializedName("pvhamggzxeahSgaiztzhqe")
    public int pageSize;

    public SheImageRequestBean(String userId, int pageNum, int pageSize) {
        this.userId = userId;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
