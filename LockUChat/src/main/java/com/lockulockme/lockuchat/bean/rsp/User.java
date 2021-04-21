package com.lockulockme.lockuchat.bean.rsp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class User {
    @SerializedName("uypsvoesmrulIokd")
    public long uid;
    @SerializedName("uqnswyepgrteScntcerdaiwdnyfgiaIfvd")
    public String stringId;
    @SerializedName("ircmsyIknd")
    public String accid;
    @SerializedName("nbcixqcztkhhnkmansmmre")
    public String nick;
    @SerializedName("axqcaeculorzuimnyut")
    public String loginAccount;
    @SerializedName("ifncydorwn")
    public String userIcon;
    @SerializedName("mpwidrdgadfqlgreimIrjcvjonun")
    public String middleUserIcon;
    @SerializedName("sgdmhvarqlbzlppIhrcwlouxn")
    public String smallUserIcon;
    @SerializedName("gudendnnqdqyemxr")
    public String userSex;
    @SerializedName("psqw")
    public String password;
    @SerializedName("aojgmge")
    public int birthdayAge;
    @SerializedName("cmyopeuvdniztmhrfly")
    public String ctry;
    @SerializedName("tjmyljpule")
    public String userType;
    @SerializedName("sgqiuaggdn")
    public String sign;
    @SerializedName("hodi")
    public boolean isSayHi;
    @SerializedName("neyaektimipeoirnbcaodlwzFwrlatamigbeUuerdgl")
    public String countryUrl;
    @SerializedName("oiqncqlvyihenyue")
    public boolean isOn;
    @SerializedName("blxukosdiy")
    public boolean bs;
    @SerializedName("pymrqlioucxiecfIrhtciepjmeos")
    public List<PriceItems> pItems;
    @SerializedName("lfseedvmxekcljyDzoayptqra")
    public LevelData myLevelData;

    @Override
    public String toString() {
        return "User{" +
                "userId=" + uid +
                ", userStringId='" + stringId + '\'' +
                ", imId='" + accid + '\'' +
                ", nickname='" + nick + '\'' +
                ", account='" + loginAccount + '\'' +
                ", icon='" + userIcon + '\'' +
                ", gender='" + userSex + '\'' +
                ", pw='" + password + '\'' +
                ", age=" + birthdayAge +
                ", country='" + ctry + '\'' +
                ", type='" + userType + '\'' +
                ", sign='" + sign + '\'' +
                ", hi=" + isSayHi +
                ", nationalFlagUrl='" + countryUrl + '\'' +
                ", online=" + isOn +
                ", busy=" + bs +
                ", priceItems=" + pItems +
                '}';
    }
}
