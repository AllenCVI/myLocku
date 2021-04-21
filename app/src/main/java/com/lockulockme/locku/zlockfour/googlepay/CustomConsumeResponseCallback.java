package com.lockulockme.locku.zlockfour.googlepay;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;

public class CustomConsumeResponseCallback implements ConsumeResponseListener {
    @Override
    public void onConsumeResponse(BillingResult billingResult, String s) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

        }
    }
}
