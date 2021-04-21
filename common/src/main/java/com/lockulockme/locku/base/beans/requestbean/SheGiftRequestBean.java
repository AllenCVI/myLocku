package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Locale;

public class SheGiftRequestBean {
    @SerializedName("uqnswyepgrteScntcerdaiwdnyfgiaIfvd")
    public String userStringId;
    @SerializedName("lmhadnngbg")
    public String lang;

    public SheGiftRequestBean(String userStringId) {
        this.userStringId = userStringId;
        this.lang = Locale.getDefault().getLanguage();
    }
}
