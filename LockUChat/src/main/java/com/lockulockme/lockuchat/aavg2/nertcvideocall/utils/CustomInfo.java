package com.lockulockme.lockuchat.aavg2.nertcvideocall.utils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CustomInfo {
    /**
     * 呼叫类型{@link Utils}
     */
    @SerializedName("callType")
    public int callType;

    /**
     * 房间所有用户Id，不包含自己
     */
    @SerializedName("callUserList")
    public ArrayList<String> callUserList;

    /**
     * 是否来自群呼
     */
    @SerializedName("isFromGroup")
    public boolean isFromGroup;

    /**
     * 群呼Id
     */
    @SerializedName("groupID")
    public String groupID;

    public CustomInfo(int callType, ArrayList<String> callUserList, boolean isFromGroup, String groupID) {
        this.callType = callType;
        this.callUserList = callUserList;
        this.isFromGroup = isFromGroup;
        this.groupID = groupID;
    }
}
