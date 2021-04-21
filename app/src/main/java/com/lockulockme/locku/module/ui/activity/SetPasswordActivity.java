package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.SetPwbRequest;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.common.LoginoutManager;
import com.lockulockme.locku.databinding.AcSetPwbBinding;
import com.lockulockme.lockuchat.utils.ToastUtils;

public class SetPasswordActivity extends BaseActivity<AcSetPwbBinding> {

    public static void start(Context context) {
        Intent intent = new Intent(context, SetPasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcSetPwbBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.ivTitleBack.setOnClickListener(v->{
            finish();
        });

        binding.tvComplete.setOnClickListener(v->{
            setPwb();
        });
    }

    private void setPwb() {

        if (TextUtils.isEmpty(binding.etNewPwb.getText().toString().trim())) {
            ToastUtils.toastShow(R.string.please_input_new_password);
            return;
        }

        int length  = binding.etNewPwb.getText().toString().trim().length();
        if (length< 6|| length>15) {
            ToastUtils.toastShow(R.string.password_rules);
            return;
        }

        if (!binding.etNewPwb.getText().toString().trim().equals(binding.etConfirmPwb.getText().toString().trim())) {
            ToastUtils.toastShow(R.string.two_password_not_match);
            return;
        }

        showLoading();
        binding.tvComplete.setEnabled(false);
        SetPwbRequest setPwbRequest = new SetPwbRequest();
        setPwbRequest.password = binding.etConfirmPwb.getText().toString();
        OkGoUtils.getInstance().setPassword(SetPasswordActivity.this, setPwbRequest, new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                binding.tvComplete.setEnabled(true);
                hideLoading();
                ToastUtils.toastShow(R.string.set_pwb_success);
                AccountManager.getInstance().putPwd("");
                LoginoutManager.loginout(SetPasswordActivity.this);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                binding.tvComplete.setEnabled(true);
                hideLoading();
                ToastUtils.toastShow(R.string.network_error);
            }
        });

    }


}
