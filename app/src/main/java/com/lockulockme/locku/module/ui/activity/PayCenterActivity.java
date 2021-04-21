package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.advertismentid.GoogleAdHelper;
import com.lockulockme.locku.base.beans.requestbean.CodaRequestBean;
import com.lockulockme.locku.base.beans.requestbean.CreatePayOrderRequestBean;
import com.lockulockme.locku.base.beans.requestbean.GetUrlV2RequestBean;
import com.lockulockme.locku.base.beans.responsebean.CreatePayOrderResponseBean;
import com.lockulockme.locku.base.beans.responsebean.GetUrlV2ResponseBean;
import com.lockulockme.locku.base.beans.responsebean.PayCenterResponseBean;
import com.lockulockme.locku.base.beans.responsebean.ProductResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.eventbus.WebPaySucEvent;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.PayUtils;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.databinding.AcPayCenterBinding;
import com.lockulockme.locku.googlepay.GoogleInPayProxy;
import com.lockulockme.locku.module.ui.adapter.PayAdapter;
import com.lockulockme.lockuchat.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        intent.putExtra("productResp", new Gson().toJson(productResp));
        intent.putExtra("ids", ids);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        binding = AcPayCenterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        PayUtils.getInstance().init(this);
        googlePayUtils = PayUtils.getInstance().getGooglePayUtils();
        initData();
        initViews();
        binding.getRoot().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPayData();
            }
        }, 200);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WebPaySucEvent event) {
        finish();
    }

    private void loadPayData() {
        showLoading();
        OkGoUtils.getInstance().getPayCenterList(PayCenterActivity.this, new Object(), new NewJsonCallback<List<PayCenterResponseBean>>() {
            @Override
            public void onSuc(List<PayCenterResponseBean> response, String msg) {
                payAdapter.setNewInstance(response);
                if (response.size() > 0) {
                    int selectPosition = 0;
                    for (int i = 0; i< response.size();i++) {
                        if (!GOOGLE_TYPE.equalsIgnoreCase(response.get(i).payType)) {
                            selectPosition = i;
                            break;
                        }
                    }
                    payAdapter.setSelectedPosition(selectPosition);
                    payAdapter.notifyDataSetChanged();
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
        productResp = new Gson().fromJson(getIntent().getStringExtra("productResp"), ProductResponseBean.class);
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

    public void confirm(View view) {
        if (payAdapter != null && payAdapter.getSelectedPosition() >= 0) {
            PayCenterResponseBean selectedResp = payAdapter.getItem(payAdapter.getSelectedPosition());
            String selectedPayType = selectedResp.payType;
            if (GOOGLE_TYPE.equalsIgnoreCase(selectedPayType)) {
                createOrder(productResp.goodsId, GOOGLE_TYPE);
            } else {
                getPayUrlV2(productResp.goodsId,selectedResp.id);
            }

        }
    }

    public void payForGoogle(String orderId, String goodId) {
        googlePayUtils.buyGoodsByGoogleInApp(orderId, goodId);
    }

    public void createOrder(String goodId, String type) {
        showLoading();
        CreatePayOrderRequestBean createOrderReq = new CreatePayOrderRequestBean();
        createOrderReq.id = goodId;
        createOrderReq.type = type;
        OkGoUtils.getInstance().createPayOrder(PayCenterActivity.this, createOrderReq, new NewJsonCallback<CreatePayOrderResponseBean>() {
            @Override
            public void onSuc(CreatePayOrderResponseBean response, String msg) {
                hideLoading();
                payForGoogle(response.id, goodId);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, CreatePayOrderResponseBean response) {
                hideLoading();
                ToastUtils.toastShow(R.string.pay_failed);
            }
        });
    }

    public void getPayUrlV2(String goodId, long id) {
        showLoading();
        GetUrlV2RequestBean urlV2RequestBean = new GetUrlV2RequestBean(goodId,id,GoogleAdHelper.getGoogleAdId());
        OkGoUtils.getInstance().getPayUrlV2(PayCenterActivity.this, urlV2RequestBean, new NewJsonCallback<GetUrlV2ResponseBean>() {
            @Override
            public void onSuc(GetUrlV2ResponseBean response, String msg) {
                hideLoading();
                H5Activity.go(PayCenterActivity.this, response.payUrl, true);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, GetUrlV2ResponseBean response) {
                hideLoading();
                ToastUtils.toastShow(R.string.pay_failed);

            }
        });
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
