package com.lockulockme.locku.base.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseEntity<T> {
    @SerializedName("coeosvdote")
    public int code;
    @SerializedName("mqfsahg")
    public String msg;
    @SerializedName("deladotxna")
    public T data;
}
