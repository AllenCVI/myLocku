package com.lockulockme.locku.zlockfive.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.SystemMsgRequestBean;
import com.lockulockme.locku.base.beans.responsebean.SystemMsgResponseBean;
import com.lockulockme.locku.databinding.AcSystemNotificationBinding;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlockfive.base.utils.OkGoUtils;
import com.lockulockme.locku.zlockfive.common.BaseActivity;
import com.lockulockme.locku.zlockfive.module.ui.adapter.SystemMsgAdapter;
import com.lockulockme.lockuchat.utils.ToastUtils;


public class SystemMsgActivity extends BaseActivity<AcSystemNotificationBinding> {
    private final int page = 1;
    private final int size = 50;
    private SystemMsgAdapter mAdapter;

    public static void StartMe(Context context) {
        context.startActivity(new Intent(context, SystemMsgActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcSystemNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setNavTitle(getString(R.string.system_msg_title));
        binding.rvMsg.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new SystemMsgAdapter();
        binding.rvMsg.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_common_empty);
        binding.llNetwork.rltCommonNetworkError.setOnClickListener(v -> {
            getNotificationMsg(page, size);
        });
        getNotificationMsg(page, size);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void getNotificationMsg(int pg, int sz) {
        showLoading();
        OkGoUtils.getInstance().getNotificationMsg(this, new SystemMsgRequestBean(pg, sz), new NewJsonCallback<SystemMsgResponseBean>() {
            @Override
            public void onSuc(SystemMsgResponseBean response, String msg) {
                hideLoading();
                mAdapter.setNewInstance(response.data);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.GONE);
                binding.rvMsg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, SystemMsgResponseBean response) {
                super.onE(httpCode, apiCode, msg, response);
                hideLoading();
                ToastUtils.toastShow(R.string.load_system_msg_failed);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.VISIBLE);
                binding.rvMsg.setVisibility(View.GONE);
            }
        });
    }
}
