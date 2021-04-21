package com.lockulockme.locku.zlocksix.googlepay;

public interface OnStartSetupCallback {
    void onSetupSuc();
    void onSetupFail(int responseCode);
    void onSetupError();
}
