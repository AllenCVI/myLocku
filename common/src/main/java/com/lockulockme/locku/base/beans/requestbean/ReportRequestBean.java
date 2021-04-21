package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

public class ReportRequestBean {

    public static final int PERSONAL_TYPE = 1;
    public static final int CHAT_TYPE = 2;
    public static final int VIDEO_TYPE = 3;

    @SerializedName("uqnswyepgrteScntcerdaiwdnyfgiaIfvd")
    public String userId;
    @SerializedName("rjmeodauxskhohkn")
    public String content;
    @SerializedName("padodfswvilmtehiuhoeon")
    public String position;//举报位置
    @SerializedName("tjmyljpule")
    public String type;//举报类型

    public ReportRequestBean(String userId, String content, String position, String type) {
        this.userId = userId;
        this.content = content;
        this.position = position;
        this.type = type;
    }
}
