package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class RegisterRequestBean {
    @SerializedName("aojgmge")
    public int registerAge;
    @SerializedName("gudendnnqdqyemxr")
    public String sex;

    public RegisterRequestBean(int age, String gender) {
        this.registerAge = age;
        this.sex = gender;
    }

}
