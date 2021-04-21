package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PageRequestBean {
    @SerializedName("pcuajtgbce")
    public int pageNo;
    @SerializedName("sheikbztqe")
    public int sizePerPage;

    public PageRequestBean(int page, int size) {
        this.pageNo = page;
        this.sizePerPage = size;
    }
}
