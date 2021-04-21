package com.lockulockme.locku.base.beans.responsebean;


import com.google.gson.annotations.SerializedName;
import com.lockulockme.locku.base.beans.UserInfo;

import java.io.Serializable;

public class LoginResponseBean {
    @SerializedName("axncdhcfnehssxdsjhTzuohbkwuejzn")
    public String userToken;
    @SerializedName("iogmpcAdicvjcmhesnslysrfTlooklkifelun")
    public String userNimAccessToken;
    @SerializedName("pcpwkgMztodtduxizufliitferpd")
    public boolean pwModified;
    @SerializedName("uafsucegcr")
    public UserInfo user;
    @SerializedName("dcqatytwaalhCjlotsmylpfpllleeuteqe")
    public boolean isCompleteUserInfo;
    @SerializedName("hihahmseaPylw")
    public boolean IsHavePwb;


    @Override
    public String toString() {
        return "LoginResponseBean{" +
                "userToken='" + userToken + '\'' +
                ", userNimAccessToken='" + userNimAccessToken + '\'' +
                ", user=" + user +
                '}';
    }
}
