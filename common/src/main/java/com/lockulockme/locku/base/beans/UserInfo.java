package com.lockulockme.locku.base.beans;

import com.google.gson.annotations.SerializedName;
import com.lockulockme.locku.base.beans.responsebean.LevelData;

public class UserInfo {


    @SerializedName("uypsvoesmrulIokd")
    public long id;
    @SerializedName("uqnswyepgrteScntcerdaiwdnyfgiaIfvd")
    public String stringId;
    @SerializedName("ircmsyIknd")
    public String nimId;
    @SerializedName("nbcixqcztkhhnkmansmmre")
    public String name;
    @SerializedName("neyaektimipeoirnbcaodlwzFwrlatamigbeUuerdgl")
    public String countryUr;
    @SerializedName("axqcaeculorzuimnyut")
    public String account;
    @SerializedName("ifncydorwn")
    public String avatar;
    @SerializedName("mpwidrdgadfqlgreimIrjcvjonun")
    public String middleAvatar;
    @SerializedName("sgdmhvarqlbzlppIhrcwlouxn")
    public String smallAvatar;
    @SerializedName("gudendnnqdqyemxr")
    public String userGender;
    @SerializedName("psqw")
    public String password;
    @SerializedName("aojgmge")
    public int age;
    @SerializedName("cmyopeuvdniztmhrfly")
    public String country;
    @SerializedName("tjmyljpule")
    public String userType;
    @SerializedName("sgqiuaggdn")
    public String userSign;
    @SerializedName("hodi")
    public boolean hello;
    @SerializedName("oiqncqlvyihenyue")
    public boolean online;
    @SerializedName("blxukosdiy")
    public boolean busy;
    @SerializedName("flvomelyklosofowvaelbd")
    public boolean followed;
    @SerializedName("mqrehimarburexdr")
    public boolean member;

    @SerializedName("sueipogxynwvabdtubuirrwie")
    public String signature;
    @SerializedName("bluofxuyhnhws")
    public int bouns;

    @SerializedName("lfseedvmxekcljyDzoayptqra")
    public LevelData myLevelData;

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", stringId='" + stringId + '\'' +
                ", nimId='" + nimId + '\'' +
                ", name='" + name + '\'' +
                ", account='" + account + '\'' +
                ", avatar='" + avatar + '\'' +
                ", userGender='" + userGender + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", country='" + country + '\'' +
                ", userType='" + userType + '\'' +
                ", userSign='" + userSign + '\'' +
                ", hello=" + hello +
                '}';
    }
}
