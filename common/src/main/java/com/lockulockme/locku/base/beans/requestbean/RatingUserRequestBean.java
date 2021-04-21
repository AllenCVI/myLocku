package com.lockulockme.locku.base.beans.requestbean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RatingUserRequestBean {

    @SerializedName("agwnrjcnjheuohxrjsIoydqfHpmajgsmsh")
    private final String userStringId;
    @SerializedName("syicszoturfge")
    private final int userScore;
    @SerializedName("depefkselc")
    private final String content;
    @SerializedName("tucatogxtIubdjis")
    private final List<Integer> tags;

    public RatingUserRequestBean(String anchorIdHash, int score, String desc, List<Integer> tagIds) {
        this.userStringId = anchorIdHash;
        this.userScore = score;
        this.content = desc;
        this.tags = tagIds;
    }
}
