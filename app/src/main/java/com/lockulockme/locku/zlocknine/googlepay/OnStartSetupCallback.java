package com.lockulockme.locku.zlocknine.googlepay;

public interface OnStartSetupCallback {
    void onSetupSuc();
    void onSetupFail(int responseCode);
    void onSetupError();
}
