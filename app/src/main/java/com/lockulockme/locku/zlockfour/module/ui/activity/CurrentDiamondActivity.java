package com.lockulockme.locku.zlockfour.module.ui.activity;

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
import com.lockulockme.locku.base.beans.responsebean.MyStoneResponseBean;
import com.lockulockme.locku.base.beans.responsebean.ProductResponseBean;
import com.lockulockme.locku.databinding.AcCurrentDiamondBinding;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlockfour.base.utils.OkGoUtils;
import com.lockulockme.locku.zlockfour.common.BaseActivity;
import com.lockulockme.locku.zlockfour.module.ui.adapter.MyDiamondAdapter;
import com.lockulockme.lockuchat.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class CurrentDiamondActivity extends BaseActivity<AcCurrentDiamondBinding> {

    MyDiamondAdapter myDiamondAdapter;

    public static void go(Context context) {
        Intent intent = new Intent(context, CurrentDiamondActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcCurrentDiamondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
        requestProductList();
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
        OkGoUtils.getInstance().getProductList(this, req, new NewJsonCallback<List<ProductResponseBean>>() {
            @Override
            public void onSuc(List<ProductResponseBean> response, String msg) {
                hideProgress();
                myDiamondAdapter.setNewInstance(response);
                if (response.size() > 0) {
                    myDiamondAdapter.setSelectedPosition(0);
                }
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.GONE);
                binding.llContent.setVisibility(View.VISIBLE);
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

    private void requestMyStone() {
        OkGoUtils.getInstance().getMyStone(this, new NewJsonCallback<MyStoneResponseBean>() {
            @Override
            public void onSuc(MyStoneResponseBean response, String msg) {
                binding.tvMyDiamond.setText(response.stoneNum + "");
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
    }

    public void back(View view) {
        finish();
    }

    public void open(View view) {
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