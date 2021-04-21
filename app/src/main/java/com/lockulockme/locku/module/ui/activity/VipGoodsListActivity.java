package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.lockulockme.locku.base.beans.responsebean.PayCenterResponseBean;
import com.lockulockme.locku.base.beans.responsebean.ProductResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipBannerImageResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipInterceptResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.eventbus.WebPaySucEvent;
import com.lockulockme.locku.base.utils.GlideUtils;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.PayUtils;
import com.lockulockme.locku.base.utils.VipManager;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.databinding.AcVipBinding;
import com.lockulockme.locku.googlepay.GoogleInPayProxy;
import com.lockulockme.locku.module.ui.adapter.VipAdapter;
import com.lockulockme.locku.module.ui.pop.VipBlockPop;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import github.hellocsl.layoutmanager.gallery.GalleryLayoutManager;

public class VipGoodsListActivity extends BaseActivity<AcVipBinding> {

    VipAdapter vipAdapter;
    private final int CONTENT_TYPE = 1;
    private final int NETWORK_TYPE = 2;
    private VipBlockPop vipBlockPop;
    private VipInterceptResponseBean vipInterceptResponseBean;
    private boolean isShowBlock = false;
    private int mVipStatus = -1;//1是vip 0否

    private GoogleInPayProxy googlePayUtils;
    List<PayCenterResponseBean> payCenterResponseBeanList;

    public static void go(Context context) {
        Intent intent = new Intent(context, VipGoodsListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        binding = AcVipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        PayUtils.getInstance().init(this);
        googlePayUtils = PayUtils.getInstance().getGooglePayUtils();
        initViews();
        requestProductList();
        loadInterceptLabel();
        loadBanner();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WebPaySucEvent event) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initVipState();
        binding.vipBanner.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.vipBanner.stop();
    }

    private void initVipState() {
        mVipStatus = -1;
        VipManager.getInstance().getVipState(VipGoodsListActivity.this, new VipManager.OnVipListener() {
            @Override
            public void onVipSuccess(VipResponseBean vipResp) {
                mVipStatus = vipResp.isVip ? 1 : 0;
            }

            @Override
            public void onVipFailed() {
                mVipStatus = -1;
            }
        });
    }

    private void loadBanner() {
        OkGoUtils.getInstance().getVipImages(VipGoodsListActivity.this, new Object(), new NewJsonCallback<List<VipBannerImageResponseBean>>() {
            @Override
            public void onSuc(List<VipBannerImageResponseBean> response, String msg) {
                if (response != null) {
                    binding.vipBanner.setAdapter(new GalleryBannerAdapter<VipBannerImageResponseBean>(response) {
                        @Override
                        public void onBindView(BannerImageHolder holder, VipBannerImageResponseBean data, int position, int size) {
                            GlideUtils.loadImage(VipGoodsListActivity.this, data.image, holder.imageView, R.mipmap.ic_vip_banner_placeholder);
                        }
                    });
                }
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<VipBannerImageResponseBean> response) {
            }
        });
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
        req.goodsType = "2";
        OkGoUtils.getInstance().getProductList(VipGoodsListActivity.this, req, new NewJsonCallback<List<ProductResponseBean>>() {
            @Override
            public void onSuc(List<ProductResponseBean> response, String msg) {
                getPayTypeList(response);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<ProductResponseBean> response) {
                hideProgress();
                setLayoutVisible(NETWORK_TYPE);
            }
        });
    }

    private void getPayTypeList(List<ProductResponseBean> productResponseBeans) {
        OkGoUtils.getInstance().getPayCenterList(VipGoodsListActivity.this, new Object(), new NewJsonCallback<List<PayCenterResponseBean>>() {
            @Override
            public void onSuc(List<PayCenterResponseBean> payCenterResponseBeans, String msg) {
                hideProgress();
                vipAdapter.setNewInstance(productResponseBeans);
                if (productResponseBeans.size() > 1) {
                    vipAdapter.setSelectedPosition(1);
                } else if (productResponseBeans.size() > 0) {
                    vipAdapter.setSelectedPosition(0);
                }
                setLayoutVisible(CONTENT_TYPE);
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
                setLayoutVisible(NETWORK_TYPE);

            }
        });
    }

    private void loadInterceptLabel() {
        OkGoUtils.getInstance().getInterceptLabel(VipGoodsListActivity.this, new VipInterceptRequestBean(VipInterceptRequestBean.VIP_INTERCEPT_TYPE), new NewJsonCallback<VipInterceptResponseBean>() {
            @Override
            public void onSuc(VipInterceptResponseBean response, String msg) {
                vipInterceptResponseBean = response;
                if (vipBlockPop != null) {
                    vipBlockPop.setLabel(response.retainingWords);
                }
            }
        });
    }

    public void setLayoutVisible(int type) {
        binding.llNetwork.rltCommonNetworkError.setVisibility(type == CONTENT_TYPE ? View.GONE : View.VISIBLE);
        binding.llContent.setVisibility(type == CONTENT_TYPE ? View.VISIBLE : View.GONE);
    }


    public void initViews() {
        binding.vipBanner.setIndicator(new CircleIndicator(this));
        binding.vipBanner.setBannerGalleryEffect(18, 10);
        LinearLayoutManager rvManager = new LinearLayoutManager(mContext);
        rvManager.setOrientation(RecyclerView.VERTICAL);
        binding.rvVip.setLayoutManager(rvManager);
        vipAdapter = new VipAdapter(null);
        binding.rvVip.setAdapter(vipAdapter);
        vipAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                vipAdapter.setSelectedPosition(position);
                vipAdapter.notifyDataSetChanged();
            }
        });
        vipAdapter.setEmptyView(R.layout.layout_common_empty);
        binding.llNetwork.rltCommonNetworkError.setOnClickListener(v -> {
            loadBanner();
            requestProductList();
            loadInterceptLabel();
            initVipState();
        });
        vipBlockPop = new VipBlockPop(this);
    }


    public void back(View v) {
        checkBack();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShowBlock || mVipStatus == -1 || vipInterceptResponseBean == null) {
                return super.onKeyUp(keyCode, event);
            } else {
                checkBack();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void checkBack() {
        if (!isShowBlock && vipInterceptResponseBean != null && mVipStatus == 0) {
            isShowBlock = true;
            vipBlockPop.showDialog();
        } else {
            finish();
        }
    }

    public class ScaleTransformer implements GalleryLayoutManager.ItemTransformer {

        @Override
        public void transformItem(GalleryLayoutManager layoutManager, View item, float fraction) {
            item.setPivotX(item.getWidth() / 2.f);
            item.setPivotY(item.getHeight() / 2.0f);
            float scale = 1 - 0.3f * Math.abs(fraction);
            item.setScaleX(scale);
            item.setScaleY(scale);
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
        OkGoUtils.getInstance().createPayOrder(VipGoodsListActivity.this, createOrderReq, new NewJsonCallback<CreatePayOrderResponseBean>() {
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
            ProductResponseBean selectedResp = vipAdapter.getItem(vipAdapter.getSelectedPosition());
            createOrder(selectedResp.goodsId, "googlepay");
        } else {
            if (vipAdapter != null && vipAdapter.getSelectedPosition() >= 0) {
                ProductResponseBean selectedResp = vipAdapter.getItem(vipAdapter.getSelectedPosition());
                ArrayList<String> idList = new ArrayList<>();
                for (int i = 0; i < vipAdapter.getItemCount(); i++) {
                    idList.add(vipAdapter.getItem(i).goodsId);
                }
                PayCenterActivity.go(this, selectedResp, idList, "2");
            }
        }

    }

    private abstract class GalleryBannerAdapter<T> extends BannerAdapter<T, BannerImageHolder> {

        public GalleryBannerAdapter(List<T> mData) {
            super(mData);
        }

        @Override
        public BannerImageHolder onCreateHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            //注意，必须设置为match_parent，这个是viewpager2强制要求的
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            return new BannerImageHolder(imageView);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
