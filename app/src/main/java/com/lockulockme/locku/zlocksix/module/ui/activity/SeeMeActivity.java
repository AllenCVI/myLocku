package com.lockulockme.locku.zlocksix.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.LookMeResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.base.eventbus.VipStatusRefreshEvent;
import com.lockulockme.locku.databinding.AcSeemeBinding;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlocksix.base.utils.AccountManager;
import com.lockulockme.locku.zlocksix.base.utils.OkGoUtils;
import com.lockulockme.locku.zlocksix.base.utils.VipManager;
import com.lockulockme.locku.zlocksix.common.BaseActivity;
import com.lockulockme.locku.zlocksix.module.ui.adapter.SeeMeAdapter;
import com.lockulockme.lockuchat.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class SeeMeActivity extends BaseActivity<AcSeemeBinding> {

    private SeeMeAdapter seeMeAdapter;

    public static void go(Context context) {
        Intent intent = new Intent(context, SeeMeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcSeemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvName.setText(String.format(getString(R.string.dear_name), AccountManager.getInstance().getCurrentUser().name));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        binding.rvRecentVisitors.setLayoutManager(gridLayoutManager);
        seeMeAdapter = new SeeMeAdapter(null);
        binding.rvRecentVisitors.setAdapter(seeMeAdapter);
        seeMeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                VipManager.getInstance().getVipState(null, new VipManager.OnVipListener() {
                    @Override
                    public void onVipSuccess(VipResponseBean vipResp) {
                        if (vipResp.isVip) {
                            SheDetailActivity.StartMe(SeeMeActivity.this, seeMeAdapter.getItem(position).stringId);
                        } else {
                            if (position <= 3) {
                                SheDetailActivity.StartMe(SeeMeActivity.this, seeMeAdapter.getItem(position).stringId);
                            } else {
                                VipGoodsListActivity.go(SeeMeActivity.this);
                            }
                        }
                    }

                    @Override
                    public void onVipFailed() {
                        ToastUtils.toastShow(R.string.get_vip_failed);
                    }
                });
            }
        });
        seeMeAdapter.setEmptyView(R.layout.layout_common_empty);
        binding.llNetwork.rltCommonNetworkError.setOnClickListener(v -> {
            loadData();
        });
        loadData();
        EventBus.getDefault().register(this);

    }

    private void showProgress() {
        binding.rlProgress.rltProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        binding.rlProgress.rltProgress.setVisibility(View.GONE);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getVipRecharge(VipStatusRefreshEvent event) {
        loadData();
    }

    private void loadData() {
        showProgress();
        VipManager.getInstance().getVipState(null, new VipManager.OnVipListener() {
            @Override
            public void onVipSuccess(VipResponseBean vipResp) {
                if (vipResp.isVip) {
                    binding.tvConfirm.setVisibility(View.GONE);
                } else {
                    binding.tvConfirm.setVisibility(View.VISIBLE);
                }
                OkGoUtils.getInstance().getLookmeList(this, new Object(), new NewJsonCallback<List<LookMeResponseBean>>() {
                    @Override
                    public void onSuc(List<LookMeResponseBean> response, String msg) {
                        hideProgress();
                        seeMeAdapter.setNewInstance(response);
                        binding.llContent.setVisibility(View.VISIBLE);
                        binding.llNetwork.rltCommonNetworkError.setVisibility(View.GONE);
                    }

                    @Override
                    public void onE(int httpCode, int apiCode, String msg, List<LookMeResponseBean> response) {
                        hideProgress();
                        binding.llContent.setVisibility(View.GONE);
                        binding.llNetwork.rltCommonNetworkError.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onVipFailed() {
                hideProgress();
                binding.llContent.setVisibility(View.GONE);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.VISIBLE);
            }
        });

    }

    public void back(View view) {
        finish();
    }

    public void becomingVip(View view) {
        VipGoodsListActivity.go(SeeMeActivity.this);
    }
}
