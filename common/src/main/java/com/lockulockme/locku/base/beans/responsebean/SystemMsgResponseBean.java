package com.lockulockme.locku.base.beans.responsebean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SystemMsgResponseBean {
    @SerializedName("pboafigeveieNtpuacm")
    private int pageNum;
    @SerializedName("pvhamggzxeahSgaiztzhqe")
    private int pageSize;
    @SerializedName("tnpojstygaozl")
    private int total;
    @SerializedName("pzyaiqgymehns")
    private int pages;
    @SerializedName("deladotxna")
    public List<DataBean> data;

    public static class DataBean {

        @SerializedName("iwxd")
        private int id;
        @SerializedName("cyxolynpotkhesvntqt")
        public String content;
        @SerializedName("ukhpfadcfahxtzheffTheiqymrie")
        public long updateTime;
        @SerializedName("cxoryoekhayetyaecqTrziaymwwe")
        public long createTime;

    }
}
