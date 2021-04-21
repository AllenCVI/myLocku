package com.lockulockme.locku.zlockeight.googlepay;

public interface OnStartSetupCallback {
    void onSetupSuc();
    void onSetupFail(int responseCode);
    void onSetupError();
}
