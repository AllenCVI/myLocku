package com.lockulockme.locku.zlockten.googlepay;

public interface OnStartSetupCallback {
    void onSetupSuc();
    void onSetupFail(int responseCode);
    void onSetupError();
}
