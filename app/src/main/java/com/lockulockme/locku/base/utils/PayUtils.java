package com.lockulockme.locku.base.utils;


import android.app.Activity;
import android.text.TextUtils;

import com.android.billingclient.api.Purchase;
import com.lockulockme.locku.R;
import com.lockulockme.locku.application.MyApplication;
import com.lockulockme.locku.base.advertismentid.GoogleAdHelper;
import com.lockulockme.locku.base.beans.requestbean.UpdatePayOrderRequestBean;
import com.lockulockme.locku.base.beans.responsebean.UpdatePayOrderResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.eventbus.VipStatusRefreshEvent;
import com.lockulockme.locku.googlepay.CustomOnPurchaseCallback;
import com.lockulockme.locku.googlepay.GoogleInPayProxy;
import com.lockulockme.lockuchat.common.VipStatusListener;
import com.lockulockme.lockuchat.utils.ReportConstant;
import com.lockulockme.lockuchat.utils.ReportUtils;
import com.lockulockme.lockuchat.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class PayUtils {
    private GoogleInPayProxy googlePayUtils;
    public void init(Activity activity){
        googlePayUtils = new GoogleInPayProxy(activity, new GooglePurchaseListener());
    }

    public GoogleInPayProxy getGooglePayUtils() {
        return googlePayUtils;
    }

    public class GooglePurchaseListener implements CustomOnPurchaseCallback {
        @Override
        public void onPurchaseSuc(List<Purchase> list) {
            for (Purchase purchase : list) {
                UpdatePayOrderRequestBean req = new UpdatePayOrderRequestBean();
                req.token = purchase.getPurchaseToken();
                req.goodId = purchase.getSku();
                req.aaid = GoogleAdHelper.getGoogleAdId();
                if (purchase.getAccountIdentifiers() != null && !TextUtils.isEmpty(purchase.getAccountIdentifiers().getObfuscatedAccountId())) {
                    req.googleOrderId = purchase.getAccountIdentifiers().getObfuscatedAccountId();
                }
                ReportUtils.report("GooglePayUtils", "onPurchaseSuccess", ReportConstant.ORDER_ID_PAY_AFTER, "purchaseToken:" + req.token + ",req.goodsId:" + req.goodId + ",req.aaId:" + req.aaid + ",req.orderId:" + req.googleOrderId);
                updateOrder(purchase, req);
            }
        }

        @Override
        public void onPurchaseFail(int responseCode) {
            ToastUtils.toastShow(R.string.google_pay_failed);
        }


        @Override
        public void onPurchaseError() {
            ToastUtils.toastShow(R.string.google_pay_failed);
        }
    }
    private static class InstanceHelper {
        private static final PayUtils INSTANCE = new PayUtils();
    }

    public static PayUtils getInstance() {
        return PayUtils.InstanceHelper.INSTANCE;
    }

    public void updateOrder(Purchase purchase, UpdatePayOrderRequestBean req) {
        OkGoUtils.getInstance().updatePayOrder(this, req, new NewJsonCallback<UpdatePayOrderResponseBean>() {
            @Override
            public void onSuc(UpdatePayOrderResponseBean response, String msg) {
                int consume = response.updateOrderResult;
                if (consume == 1) {
                    googlePayUtils.consumePurchase(purchase);
                    VipManager.getInstance().reset();
                    VipStatusListener.getInstance().notifyVipStatusChange();
                    EventBus.getDefault().post(new VipStatusRefreshEvent());
                    ToastUtils.toastShow(R.string.google_pay_success);
                } else {
                }
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, UpdatePayOrderResponseBean response) {
                ToastUtils.toastShow(MyApplication.application.getApplicationContext().getString(R.string.network_error) + ":" + apiCode);
            }
        });
    }

}
