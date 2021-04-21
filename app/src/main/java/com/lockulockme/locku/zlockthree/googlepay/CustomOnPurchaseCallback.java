package com.lockulockme.locku.zlockthree.googlepay;

import com.android.billingclient.api.Purchase;

import java.util.List;

public interface CustomOnPurchaseCallback {
    void onPurchaseSuc(List<Purchase> list);
    void onPurchaseFail(int responseCode);
    void onPurchaseError();

}