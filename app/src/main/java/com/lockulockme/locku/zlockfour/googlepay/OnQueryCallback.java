package com.lockulockme.locku.zlockfour.googlepay;

import com.android.billingclient.api.SkuDetails;

import java.util.List;

public interface OnQueryCallback {
    void onQuerySuc(String skuType, List<SkuDetails> list);
    void onQueryFail(int responseCode);
    void onQueryError();
}




