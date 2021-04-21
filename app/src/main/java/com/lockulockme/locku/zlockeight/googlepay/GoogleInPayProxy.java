package com.lockulockme.locku.zlockeight.googlepay;

import android.app.Activity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.lockulockme.locku.R;
import com.lockulockme.lockuchat.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class GoogleInPayProxy {

    private final Activity activity;
    private GoogleInAppClient googleInAppClient;
    private List<SkuDetails> skuDetailList;

    public CustomOnPurchaseCallback onPurchaseCallback;

    public GoogleInPayProxy(Activity activity, CustomOnPurchaseCallback listener) {
        this.activity = activity;
        onPurchaseCallback = listener;
        initGoogleInAppClient();
    }

    public void initGoogleInAppClient() {
        googleInAppClient = GoogleInAppClient.getInstance()
                .setOnPurchaseFinishedListener(onPurchaseCallback)
                .setOnQueryFinishedListener(new GooglePayOnQueryCallback())
                .setOnStartSetupFinishedListener(new GooglePayOnStartSetupCallback());
    }

    public void buildWithSKUList(ArrayList<String> skuList) {
        googleInAppClient.setSKUList(skuList).build(activity);
    }

    public void buyGoodsByGoogleInApp(String id) {
        if (!GoogleInAppClient.isGooglePlayServicesAvailable(activity)) {
            ToastUtils.toastShow(R.string.google_pay_unavailable);
            return;
        }
        if (skuDetailList == null) {
            ToastUtils.toastShow(R.string.google_pay_failed);
            return;
        }
        SkuDetails skuDetail = null;
        for (int i = 0; i < skuDetailList.size(); i++) {
            SkuDetails localSkuDetail = skuDetailList.get(i);
            if (id.equals(localSkuDetail.getSku())) {
                skuDetail = localSkuDetail;
                break;
            }
        }
        if (skuDetail == null) {
            ToastUtils.toastShow(R.string.google_pay_failed);
            return;
        }
        googleInAppClient.purchaseInApp(activity, skuDetail);
    }

    public void consumePurchase(Purchase purchase) {
        googleInAppClient.consumeAsync(purchase);
    }

    private class GooglePayOnStartSetupCallback implements OnStartSetupCallback {
        @Override
        public void onSetupSuc() {
        }

        @Override
        public void onSetupFail(int responseCode) {
        }

        @Override
        public void onSetupError() {
        }
    }

    private class GooglePayOnQueryCallback implements OnQueryCallback {
        @Override
        public void onQuerySuc(String skuType, List<SkuDetails> list) {
            if (BillingClient.SkuType.INAPP.equals(skuType)) {
                skuDetailList = list;
            }
        }

        @Override
        public void onQueryFail(int responseCode) {
        }

        @Override
        public void onQueryError() {
        }
    }



}
