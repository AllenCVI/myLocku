package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.databinding.AcModifyPersonalSignatureBinding;
import com.lockulockme.lockuchat.utils.ToastUtils;

public class PersonalSignatureActivity extends BaseActivity<AcModifyPersonalSignatureBinding> implements TextWatcher {

    public static void go(Context context) {
        Intent intent = new Intent(context, PersonalSignatureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcModifyPersonalSignatureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
    }

    public void initViews() {
        binding.etModifySignature.setText(AccountManager.getInstance().getCurrentUser().userSign);
        binding.etModifySignature.setSelection(AccountManager.getInstance().getCurrentUser().userSign.length());
        if (AccountManager.getInstance().getCurrentUser() != null && !TextUtils.isEmpty(AccountManager.getInstance().getCurrentUser().userSign)) {
            binding.tvPersonalInputCount.setText(AccountManager.getInstance().getCurrentUser().userSign.length() + "");
        } else {
            binding.tvPersonalInputCount.setText("0");
        }
        binding.etModifySignature.addTextChangedListener(this);
    }

    public void back(View view) {
        finish();
    }

    public void confirm(View view) {
        if (binding.etModifySignature.getText().toString().trim().equals(AccountManager.getInstance().getCurrentUser().userSign)) {
            finish();
            return;
        }
        UserInfo userBean = new UserInfo();
        userBean.userSign = binding.etModifySignature.getText().toString().trim();
        showLoading();
        OkGoUtils.getInstance().modifyPersonalInfo(PersonalSignatureActivity.this, userBean, new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                hideLoading();
                UserInfo user = AccountManager.getInstance().getCurrentUser();
                user.userSign = userBean.userSign;
                AccountManager.getInstance().putUser(user);
                ToastUtils.toastShow(getString(R.string.modify_success));
                finish();
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                hideLoading();
                if (apiCode==46){
                    ToastUtils.toastShow(getString(R.string.PersonalSignatureIllegal));
                }else {
                    ToastUtils.toastShow(getString(R.string.modify_error));
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(binding.etModifySignature.getText().toString().trim())) {
            binding.tvPersonalInputCount.setText("0");
        } else {
            binding.tvPersonalInputCount.setText(binding.etModifySignature.getText().toString().length() + "");
        }
    }
}
