package com.lockulockme.lockuchat.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class QAAnswer implements Serializable{
    @SerializedName("answer")
    public String answer;
    @SerializedName("ids")
    public List<Integer> ids;
}
