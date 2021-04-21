package com.lockulockme.lockuchat.bean.rsp;

import com.google.gson.annotations.SerializedName;

public class BaseBean {
    @SerializedName("coeosvdote")
    public int code;
    @SerializedName("mqfsahg")
    public String msg;
    @SerializedName("deladotxna")
    public Object data;

    @Override
    public String toString() {
        return "BaseBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
