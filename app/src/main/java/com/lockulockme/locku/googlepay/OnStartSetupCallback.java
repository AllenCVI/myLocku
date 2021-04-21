package com.lockulockme.locku.googlepay;

public interface OnStartSetupCallback {
    void onSetupSuc();
    void onSetupFail(int responseCode);
    void onSetupError();
}
