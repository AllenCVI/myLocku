package com.lockulockme.lockuchat.http;

public interface NetDataListener {
    void onStart();
    void onSuccess(String response);
    void onFailed(String msg,int code);
}
