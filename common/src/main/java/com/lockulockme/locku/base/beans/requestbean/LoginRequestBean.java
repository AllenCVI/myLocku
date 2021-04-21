package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class LoginRequestBean {
    @SerializedName("axqcaeculorzuimnyut")
    public String number;
    @SerializedName("psqw")
    public String password;

    public LoginRequestBean(String account, String pw) {
        this.number = account;
        this.password = pw;
    }
}
