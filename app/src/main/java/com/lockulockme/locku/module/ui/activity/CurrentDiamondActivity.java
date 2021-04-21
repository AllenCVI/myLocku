package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.CreatePayOrderRequestBean;
import com.lockulockme.locku.base.beans.requestbean.ProductRequestBean;
import com.lockulockme.locku.base.beans.requestbean.VipInterceptRequestBean;
import com.lockulockme.locku.base.beans.responsebean.CreatePayOrderResponseBean;
import com.lockulockme.locku.base.beans.responsebean.MyStoneResponseBean;
import com.lockulockme.locku.base.beans.responsebean.PayCenterResponseBean;
import com.lockulockme.locku.base.beans.responsebean.ProductResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipInterceptResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.eventbus.WebPaySucEvent;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.PayUtils;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.databinding.AcCurrentDiamondBinding;
import com.lockulockme.locku.googlepay.GoogleInPayProxy;
import com.lockulockme.locku.module.ui.adapter.MyDiamondAdapter;
import com.lockulockme.locku.module.ui.pop.DiamondBlockPop;
import com.lockulockme.lockuchat.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CurrentDiamondActivity extends BaseActivity<AcCurrentDiamondBinding> {

    MyDiamondAdapter myDiamondAdapter;
    private DiamondBlockPop diamondBlockPop;
    private VipInterceptResponseBean vipInterceptResponseBean;
    private boolean isShowBlock = false;
    private int mDiamondNumber = -1;//未加载成功 1大于0 0等于0

    private GoogleInPayProxy googlePayUtils;
    List<PayCenterResponseBean> payCenterResponseBeanList;

    public static void go(Context context) {
        Intent intent = new Intent(context, CurrentDiamondActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        binding = AcCurrentDiamondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        PayUtils.getInstance().init(this);
        googlePayUtils = PayUtils.getInstance().getGooglePayUtils();
        initViews();
        requestProductList();
//        requestMyStone();
        loadInterceptLabel();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WebPaySucEvent event) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestMyStone();
    }

    private void showProgress() {
        binding.rlProgress.rltProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        binding.rlProgress.rltProgress.setVisibility(View.GONE);
    }

    private void requestProductList() {
        showProgress();
        ProductRequestBean req = new ProductRequestBean();
        req.goodsType = "1";
        OkGoUtils.getInstance().getProductList(CurrentDiamondActivity.this, req, new NewJsonCallback<List<ProductResponseBean>>() {
            @Override
            public void onSuc(List<ProductResponseBean> response, String msg) {
                getPayTypeList(response);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<ProductResponseBean> response) {
                hideProgress();
                ToastUtils.toastShow(R.string.load_diamond_failed);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.VISIBLE);
                binding.llContent.setVisibility(View.GONE);
            }
        });
    }

    private void getPayTypeList(List<ProductResponseBean> productResponseBeans) {
        OkGoUtils.getInstance().getPayCenterList(CurrentDiamondActivity.this, new Object(), new NewJsonCallback<List<PayCenterResponseBean>>() {
            @Override
            public void onSuc(List<PayCenterResponseBean> payCenterResponseBeans, String msg) {
                hideProgress();
                myDiamondAdapter.setNewInstance(productResponseBeans);
                if (productResponseBeans.size() > 1) {
                    myDiamondAdapter.setSelectedPosition(1);
                } else if (productResponseBeans.size() > 0) {
                    myDiamondAdapter.setSelectedPosition(0);
                }
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.GONE);
                binding.llContent.setVisibility(View.VISIBLE);

                payCenterResponseBeanList = payCenterResponseBeans;

                if (productResponseBeans != null) {
                    ArrayList<String> idList = new ArrayList<>();
                    for (int i = 0; i < productResponseBeans.size(); i++) {
                        idList.add(productResponseBeans.get(i).goodsId);
                    }
                    googlePayUtils.buildWithSKUList(idList);
                }

            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<PayCenterResponseBean> payCenterResponseBeans) {
                hideProgress();
                ToastUtils.toastShow(R.string.load_diamond_failed);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.VISIBLE);
                binding.llContent.setVisibility(View.GONE);

            }
        });
    }


    private void loadInterceptLabel() {
        OkGoUtils.getInstance().getInterceptLabel(CurrentDiamondActivity.this, new VipInterceptRequestBean(VipInterceptRequestBean.DIAMOND_INTERCEPT_TYPE), new NewJsonCallback<VipInterceptResponseBean>() {
            @Override
            public void onSuc(VipInterceptResponseBean response, String msg) {
                vipInterceptResponseBean = response;
                if (diamondBlockPop != null) {
                    diamondBlockPop.setLabel(response.retainingWords);
                }
            }
        });
    }

    private void requestMyStone() {
        mDiamondNumber = -1;
        OkGoUtils.getInstance().getMyStone(CurrentDiamondActivity.this, new NewJsonCallback<MyStoneResponseBean>() {
            @Override
            public void onSuc(MyStoneResponseBean response, String msg) {
                binding.tvMyDiamond.setText(response.stoneNum + "");
                mDiamondNumber = response.stoneNum > 0 ? 1 : 0;
            }
        });
    }

    private void initViews() {
        LinearLayoutManager rvManager = new LinearLayoutManager(mContext);
        rvManager.setOrientation(RecyclerView.VERTICAL);
        binding.rvMyDiamond.setLayoutManager(rvManager);
        myDiamondAdapter = new MyDiamondAdapter(null);
        binding.rvMyDiamond.setAdapter(myDiamondAdapter);
        myDiamondAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                myDiamondAdapter.setSelectedPosition(position);
                myDiamondAdapter.notifyDataSetChanged();
            }
        });
        myDiamondAdapter.setEmptyView(R.layout.layout_common_empty);
        diamondBlockPop = new DiamondBlockPop(this);
    }

    public void back(View v) {
        BackIntercept();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShowBlock || mDiamondNumber == -1 || mDiamondNumber > 0 || vipInterceptResponseBean == null) {
                return super.onKeyUp(keyCode, event);
            } else {
                BackIntercept();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void BackIntercept() {
        if (!isShowBlock && vipInterceptResponseBean != null && mDiamondNumber == 0) {
            isShowBlock = true;
            diamondBlockPop.showDialog();
        } else {
            finish();
        }
    }

    private boolean containsGooglePayOnly() {
        if (payCenterResponseBeanList != null && payCenterResponseBeanList.size() == 1 && payCenterResponseBeanList.get(0).payType.equalsIgnoreCase("googlepay")) {
            return true;
        }
        return false;
    }

    public void payForGoogle(String orderId, String goodId) {
        googlePayUtils.buyGoodsByGoogleInApp(orderId, goodId);
    }

    public void createOrder(String goodId, String type) {
        showLoading();
        CreatePayOrderRequestBean createOrderReq = new CreatePayOrderRequestBean();
        createOrderReq.id = goodId;
        createOrderReq.type = type;
        OkGoUtils.getInstance().createPayOrder(CurrentDiamondActivity.this, createOrderReq, new NewJsonCallback<CreatePayOrderResponseBean>() {
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


    public void open(View view) {
        if (containsGooglePayOnly()) {
            ProductResponseBean selectedResp = myDiamondAdapter.getItem(myDiamondAdapter.getSelectedPosition());
            createOrder(selectedResp.goodsId, "googlepay");
        } else {
            if (myDiamondAdapter != null && myDiamondAdapter.getSelectedPosition() >= 0) {
                ProductResponseBean selectedResp = myDiamondAdapter.getItem(myDiamondAdapter.getSelectedPosition());
                ArrayList<String> idList = new ArrayList<>();
                for (int i = 0; i < myDiamondAdapter.getItemCount(); i++) {
                    idList.add(myDiamondAdapter.getItem(i).goodsId);
                }
                PayCenterActivity.go(this, selectedResp, idList, "1");

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
