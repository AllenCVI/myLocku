package com.lockulockme.locku.zlockthree.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.Purchase;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.advertismentid.GoogleAdHelper;
import com.lockulockme.locku.base.beans.requestbean.CodaRequestBean;
import com.lockulockme.locku.base.beans.requestbean.CreatePayOrderRequestBean;
import com.lockulockme.locku.base.beans.requestbean.UpdatePayOrderRequestBean;
import com.lockulockme.locku.base.beans.responsebean.CreatePayOrderResponseBean;
import com.lockulockme.locku.base.beans.responsebean.PayCenterResponseBean;
import com.lockulockme.locku.base.beans.responsebean.ProductResponseBean;
import com.lockulockme.locku.base.beans.responsebean.UpdatePayOrderResponseBean;
import com.lockulockme.locku.base.eventbus.VipStatusRefreshEvent;
import com.lockulockme.locku.databinding.AcPayCenterBinding;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlockthree.base.utils.OkGoUtils;
import com.lockulockme.locku.zlockthree.base.utils.VipManager;
import com.lockulockme.locku.zlockthree.common.BaseActivity;
import com.lockulockme.locku.zlockthree.googlepay.CustomOnPurchaseCallback;
import com.lockulockme.locku.zlockthree.googlepay.GoogleInPayProxy;
import com.lockulockme.locku.zlockthree.module.ui.adapter.PayAdapter;
import com.lockulockme.lockuchat.common.VipStatusListener;
import com.lockulockme.lockuchat.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PayCenterActivity extends BaseActivity<AcPayCenterBinding> {

    PayAdapter payAdapter;

    ProductResponseBean productResp;
    ArrayList<String> ids;
    String type;

    private GoogleInPayProxy googlePayUtils;
    private final String CODAPAY_TYPE = "codapay";
    private final String PAYMAX_TYPE = "payermax";
    private final String GOOGLE_TYPE = "googlepay";

    public static void go(Context context, ProductResponseBean productResp, ArrayList<String> ids, String type) {
        Intent intent = new Intent(context, PayCenterActivity.class);
//        intent.putExtra("productResp", productResp);
        intent.putExtra("ids", ids);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcPayCenterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        googlePayUtils = new GoogleInPayProxy(this, new GooglePurchaseListener());
        initData();
        initViews();
        binding.getRoot().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPayData();
            }
        }, 200);
    }

    private void loadPayData() {
        showLoading();
        OkGoUtils.getInstance().getPayCenterList(this, new Object(), new NewJsonCallback<List<PayCenterResponseBean>>() {
            @Override
            public void onSuc(List<PayCenterResponseBean> response, String msg) {
                payAdapter.setNewInstance(response);
                if (response.size() > 0) {
                    payAdapter.setSelectedPosition(0);
                }
                binding.llContent.setVisibility(View.VISIBLE);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.GONE);
                hideLoading();
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<PayCenterResponseBean> response) {
                ToastUtils.toastShow(R.string.load_pay_failed);
                binding.llContent.setVisibility(View.GONE);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.VISIBLE);
                hideLoading();
            }
        });
    }

    private void initData() {
        productResp = (ProductResponseBean) getIntent().getSerializableExtra("productResp");
        ids = getIntent().getStringArrayListExtra("ids");
        type = getIntent().getStringExtra("type");
        googlePayUtils.buildWithSKUList(ids);
    }

    private void initViews() {
        LinearLayoutManager rvManager = new LinearLayoutManager(mContext);
        rvManager.setOrientation(RecyclerView.VERTICAL);
        binding.rvPay.setLayoutManager(rvManager);
        payAdapter = new PayAdapter(null);
        binding.rvPay.setAdapter(payAdapter);
        payAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                payAdapter.setSelectedPosition(position);
                payAdapter.notifyDataSetChanged();
            }
        });

        if ("2".equals(type)) {
            binding.rltVipTop.setVisibility(View.VISIBLE);
            binding.rltDiamondTop.setVisibility(View.GONE);
            binding.tvPayMonthVip.setText(getString(R.string.month_vip, productResp.num));
        } else {
            binding.rltVipTop.setVisibility(View.GONE);
            binding.rltDiamondTop.setVisibility(View.VISIBLE);
            binding.tvPayBuyDiamondNum.setText(getString(R.string.buy_diamond, productResp.num));
        }
        if (TextUtils.isEmpty(productResp.goodsPrice) || TextUtils.isEmpty(productResp.currency)) {
            binding.tvConfirmPayment.setText(getString(R.string.confirm_pay));
        } else {
            binding.tvConfirmPayment.setText(getString(R.string.confirm_pay_with_price, productResp.currency + productResp.goodsPrice));
        }

        binding.llNetwork.rltCommonNetworkError.setOnClickListener(v -> {
            loadPayData();
        });
    }

    private void updateOrder(Purchase purchase, UpdatePayOrderRequestBean req) {
        OkGoUtils.getInstance().updatePayOrder(this, req, new NewJsonCallback<UpdatePayOrderResponseBean>() {
            @Override
            public void onSuc(UpdatePayOrderResponseBean response, String msg) {
                Integer consume = response.updateOrderResult;
                if (consume != null && consume == 1) {
                    googlePayUtils.consumePurchase(purchase);
                    VipManager.getInstance().reset();
                    VipStatusListener.getInstance().notifyVipStatusChange();
                    EventBus.getDefault().post(new VipStatusRefreshEvent());
                    ToastUtils.toastShow(R.string.google_pay_success);
                }
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, UpdatePayOrderResponseBean response) {
            }
        });
    }

    public void confirm(View view) {
        if (payAdapter != null && payAdapter.getSelectedPosition() >= 0) {
            PayCenterResponseBean selectedResp = payAdapter.getItem(payAdapter.getSelectedPosition());
            String selectedPayType = selectedResp.payType;
            if (GOOGLE_TYPE.equalsIgnoreCase(selectedPayType)) {
                payForGoogle(productResp.goodsId);
            } else if (CODAPAY_TYPE.equalsIgnoreCase(selectedPayType)) {
                payForPay(productResp.goodsId, CODAPAY_TYPE);
            } else if (PAYMAX_TYPE.equalsIgnoreCase(selectedPayType)) {
                payForPay(productResp.goodsId, PAYMAX_TYPE);
            }
        }
    }

    public void payForGoogle(String goodId) {
        googlePayUtils.buyGoodsByGoogleInApp(goodId);
    }

    public void payForPay(String goodId, String type) {
        showLoading();
        CreatePayOrderRequestBean createOrderReq = new CreatePayOrderRequestBean();
        createOrderReq.id = goodId;
        createOrderReq.type = type;
        OkGoUtils.getInstance().createPayOrder(this, createOrderReq, new NewJsonCallback<CreatePayOrderResponseBean>() {
            @Override
            public void onSuc(CreatePayOrderResponseBean response, String msg) {
                hideLoading();
                getPayTypeUrl(response, type);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, CreatePayOrderResponseBean response) {
                hideLoading();
                ToastUtils.toastShow(R.string.pay_failed);
            }
        });
    }

    private void getPayTypeUrl(CreatePayOrderResponseBean response, String type) {
        CodaRequestBean codaReq = new CodaRequestBean();
        codaReq.id = response.id;
        codaReq.aaid = GoogleAdHelper.getGoogleAdId();
        if (type.equals(PAYMAX_TYPE)) {
            showLoading();
            OkGoUtils.getInstance().getPayerMaxUrl(this, codaReq, new NewJsonCallback<String>() {
                @Override
                public void onSuc(String response, String msg) {
                    hideLoading();
                    H5Activity.go(PayCenterActivity.this, response, true);
                }

                @Override
                public void onE(int httpCode, int apiCode, String msg, String response) {
                    hideLoading();
                    ToastUtils.toastShow(R.string.load_pay_url_failed);
                }
            });
        } else if (type.equals(CODAPAY_TYPE)) {
            showLoading();
            codaReq.type = 0;
            OkGoUtils.getInstance().getCodaPayUrl(this, codaReq, new NewJsonCallback<String>() {
                @Override
                public void onSuc(String response, String msg) {
                    hideLoading();
                    H5Activity.go(PayCenterActivity.this, response, true);
                }

                @Override
                public void onE(int httpCode, int apiCode, String msg, String response) {
                    hideLoading();
                    ToastUtils.toastShow(R.string.load_pay_url_failed);
                }
            });
        }

    }

    public void back(View view) {
        finish();
    }

    public class GooglePurchaseListener implements CustomOnPurchaseCallback {
        @Override
        public void onPurchaseSuc(List<Purchase> list) {
            for (Purchase purchase : list) {
                UpdatePayOrderRequestBean req = new UpdatePayOrderRequestBean();
                req.token = purchase.getPurchaseToken();
                req.goodId = purchase.getSku();
                req.aaid = GoogleAdHelper.getGoogleAdId();
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

}
