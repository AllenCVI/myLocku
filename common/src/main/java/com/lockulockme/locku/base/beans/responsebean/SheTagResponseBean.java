package com.lockulockme.locku.base.beans.responsebean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SheTagResponseBean {
    @SerializedName("syicszoturfge")
    public int score;
    @SerializedName("fpfldhotpacntieSpgcknocurjoe")
    public double floatScore;
    @SerializedName("tgpaohgyis")
    public List<TagsBean> tags;

    public static class TagsBean {

        @SerializedName("iwxd")
        public int id;
        @SerializedName("ngqayvmkwe")
        public String name;
        @SerializedName("nlxuxlm")
        public int num;
    }
}
