package com.lockulockme.locku.zlockseven.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.databinding.AcModifyPersonalSignatureBinding;
import com.lockulockme.locku.zlockseven.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlockseven.base.utils.AccountManager;
import com.lockulockme.locku.zlockseven.base.utils.OkGoUtils;
import com.lockulockme.locku.zlockseven.common.BaseActivity;
import com.lockulockme.lockuchat.utils.ToastUtils;

public class PersonalSignatureActivity extends BaseActivity<AcModifyPersonalSignatureBinding> implements TextWatcher{

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
        binding.etModifySignature.addTextChangedListener(this);
    }

    public void back(View view) {
        finish();
    }

    public void confirm(View view) {
        if (binding.etModifySignature.getText().toString().trim().equals(AccountManager.getInstance().getCurrentUser().userSign)) {
            return;
        }
        UserInfo userBean = new UserInfo();
        userBean.userSign = binding.etModifySignature.getText().toString().trim();
        showLoading();

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
            binding.tvPersonalInputCount.setText(binding.etModifySignature.getText().toString().trim());
        }
    }
}
