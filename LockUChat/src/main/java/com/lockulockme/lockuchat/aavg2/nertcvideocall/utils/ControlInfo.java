package com.lockulockme.lockuchat.aavg2.nertcvideocall.utils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 自定义控制消息
 */
public class ControlInfo {
    @SerializedName("cid")
    public int cid;

    public ControlInfo(int cid){
        this.cid = cid;
    }
}
