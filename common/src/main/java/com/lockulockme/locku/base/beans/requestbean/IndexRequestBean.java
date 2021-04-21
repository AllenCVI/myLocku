package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IndexRequestBean {
    @SerializedName("pboafigeveieNtpuacm")
    public int pageNo;
    @SerializedName("pvhamggzxeahSgaiztzhqe")
    public int sizePerPage;
    @SerializedName("cmyopeuvdniztmhrfly")
    public String country;

    public IndexRequestBean(int page, int size, String country) {
        this.pageNo = page;
        this.sizePerPage = size;
        this.country = country;
    }
}
