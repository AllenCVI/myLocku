package com.lockulockme.locku.zlocknine.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.ProductRequestBean;
import com.lockulockme.locku.base.beans.responsebean.ProductResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipBannerImageResponseBean;
import com.lockulockme.locku.databinding.AcVipBinding;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlocknine.base.utils.OkGoUtils;
import com.lockulockme.locku.zlocknine.common.BaseActivity;
import com.lockulockme.locku.zlocknine.module.ui.adapter.VipAdapter;
import com.lockulockme.locku.zlocknine.module.ui.adapter.VipBannerAdapter;

import java.util.ArrayList;
import java.util.List;

import github.hellocsl.layoutmanager.gallery.GalleryLayoutManager;

public class VipGoodsListActivity extends BaseActivity<AcVipBinding> {

    VipBannerAdapter vipBannerAdapter;
    VipAdapter vipAdapter;
    private final int CONTENT_TYPE = 1;
    private final int NETWORK_TYPE = 2;

    public static void go(Context context) {
        Intent intent = new Intent(context, VipGoodsListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcVipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
        requestProductList();
        OkGoUtils.getInstance().getVipImages(this, new Object(), new NewJsonCallback<List<VipBannerImageResponseBean>>() {
            @Override
            public void onSuc(List<VipBannerImageResponseBean> response, String msg) {
                if (response != null) {
                    vipBannerAdapter.setNewInstance(response);
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
        OkGoUtils.getInstance().getProductList(this, req, new NewJsonCallback<List<ProductResponseBean>>() {
            @Override
            public void onSuc(List<ProductResponseBean> response, String msg) {
                hideProgress();
                vipAdapter.setNewInstance(response);
                if (response.size() > 0) {
                    vipAdapter.setSelectedPosition(0);
                }
                setLayoutVisible(CONTENT_TYPE);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<ProductResponseBean> response) {
                hideProgress();
                setLayoutVisible(NETWORK_TYPE);
            }
        });
    }

    public void setLayoutVisible(int type) {
        binding.llNetwork.rltCommonNetworkError.setVisibility(type == CONTENT_TYPE ? View.GONE : View.VISIBLE);
        binding.llContent.setVisibility(type == CONTENT_TYPE ? View.VISIBLE : View.GONE);
    }


    public void initViews() {
//        GalleryLayoutManager layoutManager = new GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL);
//        layoutManager.attach(binding.rvVipBanner, 1);
//        vipBannerAdapter = new VipBannerAdapter();
//        binding.rvVipBanner.setAdapter(vipBannerAdapter);
//        layoutManager.setItemTransformer(new ScaleTransformer());

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
            requestProductList();
        });
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

    public void back(View view) {
        finish();
    }

    public void open(View view) {
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
