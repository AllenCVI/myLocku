package com.lockulockme.lockuchat.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AskForGifts implements Serializable {
    @SerializedName("giftId")
    public long gId;
    @SerializedName("iconUrl")
    public String url;
    @SerializedName("diamondNumber")
    public int diamondNum;
    @SerializedName("integral")
    public long score;
    @SerializedName("anchorGiftName")
    public String sheGiftName;
    @SerializedName("anchorGiftDesc")
    public String sheGiftDesc;
    @SerializedName("userGiftName")
    public String meGiftName;
    @SerializedName("userGiftDesc")
    public String meGiftDesc;
}
