package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class MyAlbumRequestBean {
    @SerializedName("pvhamggzxeahSgaiztzhqe")
    public int SizePerPage;
    @SerializedName("pboafigeveieNtpuacm")
    public int pageNo;

    public MyAlbumRequestBean(int pageSize, int pageNum) {
        this.SizePerPage = pageSize;
        this.pageNo = pageNum;
    }
}
