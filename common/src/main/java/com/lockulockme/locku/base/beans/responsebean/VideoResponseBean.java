package com.lockulockme.locku.base.beans.responsebean;

import com.google.gson.annotations.SerializedName;
import com.lockulockme.locku.base.beans.UserInfo;

import java.io.Serializable;
import java.util.List;

public class VideoResponseBean {
    @SerializedName("vpqicmdsuekeowfIafd")
    public int videoId;
    @SerializedName("vrlinadarentoipUoorjkl")
    public String videoUrl;
    @SerializedName("ceeojhvhmetwrfmUmgruhl")
    public String coverUrl;
    @SerializedName("depefkselc")
    public String desc;
    @SerializedName("uafsucegcr")
    public UserInfo user;
    @SerializedName("pymrqlioucxiecfIrhtciepjmeos")
    public List<AudioAndVideoPriceResponseBean> priceItems;
}
