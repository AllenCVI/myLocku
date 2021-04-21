package com.lockulockme.locku.zlocksix.googlepay;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.List;

public class CustomSkuDetailsResponseCallback implements SkuDetailsResponseListener {
    private OnQueryCallback mOnQueryFinishedListener;

    private String mSkuType;

    public CustomSkuDetailsResponseCallback(OnQueryCallback onQueryFinishedListener, String skuType) {
        mOnQueryFinishedListener = onQueryFinishedListener;
        this.mSkuType = skuType;
    }

    @Override
    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
        if (mOnQueryFinishedListener == null) {
            return;
        }
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            mOnQueryFinishedListener.onQuerySuc(mSkuType, list);
        } else {
            mOnQueryFinishedListener.onQueryFail(billingResult.getResponseCode());
        }
    }

}