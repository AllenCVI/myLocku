package com.lockulockme.locku.zlocknine.googlepay;

import android.app.Activity;
import android.content.Context;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

public class GoogleInAppClient {

    private ArrayList<String> mSkuList = new ArrayList<>();

    private static BillingClient mBillingClient;
    private static BillingClient.Builder builder;
    private static CustomOnPurchaseCallback mOnPurchaseCallback;
    private static OnStartSetupCallback mOnStartSetupCallback;
    private static OnQueryCallback mOnQueryCallback;

    private static final GoogleInAppClient googleInAppClient = new GoogleInAppClient();


    private GoogleInAppClient() {

    }

    public static GoogleInAppClient getInstance() {
        cleanListener();
        return googleInAppClient;
    }

    public GoogleInAppClient build(Activity activity) {
        if (mBillingClient == null) {
            synchronized (googleInAppClient) {
                if (mBillingClient == null) {
                    if (isGooglePlayServicesAvailable(activity)) {
                        builder = BillingClient.newBuilder(activity);
                        mBillingClient = builder.setListener(new MyPurchasesUpdatedListener())
                                .enablePendingPurchases().build();
                    } else {
                        if (mOnStartSetupCallback != null) {
                            mOnStartSetupCallback.onSetupError();
                        }
                    }
                } else {
                    builder.setListener(new MyPurchasesUpdatedListener());
                }
            }
        } else {
            builder.setListener(new MyPurchasesUpdatedListener());
        }
        synchronized (googleInAppClient) {
            if (connectBillingService()) {
                queryInventoryInApp(mSkuList);
                List<Purchase> list = queryPurchasesInApp();
                if (mOnPurchaseCallback != null && list != null) {
                    mOnPurchaseCallback.onPurchaseSuc(list);
                }
            }
        }
        return googleInAppClient;
    }

    public boolean connectBillingService() {
        if (mBillingClient == null) {
            return false;
        }
        if (!mBillingClient.isReady()) {
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (mOnStartSetupCallback != null) {
                            mOnStartSetupCallback.onSetupSuc();
                        }
                        queryInventoryInApp(mSkuList);
                        List<Purchase> list = queryPurchasesInApp();
                        if (mOnPurchaseCallback != null && list != null) {
                            mOnPurchaseCallback.onPurchaseSuc(list);
                        }

                    } else {
                        if (mOnStartSetupCallback != null) {
                            mOnStartSetupCallback.onSetupFail(billingResult.getResponseCode());
                        }
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    if (mOnStartSetupCallback != null) {
                        mOnStartSetupCallback.onSetupError();
                    }
                }
            });
            return false;
        } else {
            return true;
        }
    }

    public void queryInventoryInApp(ArrayList<String> skuList) {
        queryInventory(BillingClient.SkuType.INAPP, skuList);
    }


    private void queryInventory(final String skuType, ArrayList<String> skuList) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBillingClient == null) {
                    if (mOnQueryCallback != null) {
                        mOnQueryCallback.onQueryError();
                    }
                    return;
                }
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(skuType);
                mBillingClient.querySkuDetailsAsync(params.build(), new CustomSkuDetailsResponseCallback(mOnQueryCallback, skuType));
            }
        };
        executeServiceRequest(runnable);
    }

    public void purchaseInApp(Activity activity, SkuDetails skuDetails) {
        purchase(activity, skuDetails);
    }


    private void purchase(Activity activity, final SkuDetails skuDetails) {
        if (mBillingClient == null) {
            if (mOnPurchaseCallback != null) {
                mOnPurchaseCallback.onPurchaseError();
            }
            return;
        }
        if (connectBillingService()) {
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();
            mBillingClient.launchBillingFlow(activity, flowParams);
        } else {
            if (mOnPurchaseCallback != null) {
                mOnPurchaseCallback.onPurchaseError();
            }
        }
    }

    public void consumeAsync(Purchase purchase) {
        if (mBillingClient == null) {
            return;
        }
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
        mBillingClient.consumeAsync(consumeParams, new CustomConsumeResponseCallback());
    }

    public List<Purchase> queryPurchasesInApp() {
        return queryPurchases(BillingClient.SkuType.INAPP);
    }


    private List<Purchase> queryPurchases(String skuType) {
        if (mBillingClient == null) {
            return null;
        }
        if (!mBillingClient.isReady()) {
            connectBillingService();
        } else {
            Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(skuType);
            if (purchasesResult != null) {
                if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    List<Purchase> purchaseList = purchasesResult.getPurchasesList();
                    return purchaseList;
                }
            }

        }
        return null;
    }


    private void executeServiceRequest(final Runnable runnable) {
        if (connectBillingService()) {
            runnable.run();
        }
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        if (googleApiAvailability != null) {
            int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
            return resultCode == ConnectionResult.SUCCESS;
        }
        return false;
    }

    public GoogleInAppClient setSKUList(ArrayList<String> skuList) {
        mSkuList = skuList;
        return googleInAppClient;
    }

    public boolean isReady() {
        return mBillingClient != null && mBillingClient.isReady();
    }

    public static void cleanListener() {
        mOnPurchaseCallback = null;
        mOnQueryCallback = null;
        mOnStartSetupCallback = null;
        if (builder != null) {
            builder.setListener(null);
        }
    }

    public static void endConnection() {
        if (mBillingClient != null) {
            if (mBillingClient.isReady()) {
                mBillingClient.endConnection();
                mBillingClient = null;
            }
        }
    }

    public GoogleInAppClient setOnQueryFinishedListener(OnQueryCallback onQueryFinishedListener) {
        mOnQueryCallback = onQueryFinishedListener;
        return googleInAppClient;
    }

    public GoogleInAppClient setOnPurchaseFinishedListener(CustomOnPurchaseCallback onPurchaseFinishedListener) {
        mOnPurchaseCallback = onPurchaseFinishedListener;
        return googleInAppClient;
    }

    public GoogleInAppClient setOnStartSetupFinishedListener(OnStartSetupCallback onStartSetupFinishedListener) {
        mOnStartSetupCallback = onStartSetupFinishedListener;
        return googleInAppClient;
    }

    private class MyPurchasesUpdatedListener implements PurchasesUpdatedListener {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
            if (mOnPurchaseCallback == null) {
                return;
            }
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                mOnPurchaseCallback.onPurchaseSuc(list);
            } else {
                mOnPurchaseCallback.onPurchaseFail(billingResult.getResponseCode());
            }
        }
    }


}