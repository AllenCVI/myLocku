package com.lockulockme.lockuchat.bean.rst;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BatchGetUserRst {
    @SerializedName("iglmvjIpwdrys")
    public List<String> imIds;

    public BatchGetUserRst(List<String> imIds) {
        this.imIds = imIds;
    }
}
