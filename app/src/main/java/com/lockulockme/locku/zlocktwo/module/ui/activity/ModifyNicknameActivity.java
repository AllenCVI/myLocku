package com.lockulockme.locku.zlocktwo.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.databinding.AcModifyNicknameBinding;
import com.lockulockme.locku.zlocktwo.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlocktwo.base.utils.AccountManager;
import com.lockulockme.locku.zlocktwo.base.utils.OkGoUtils;
import com.lockulockme.locku.zlocktwo.common.BaseActivity;
import com.lockulockme.lockuchat.utils.ToastUtils;

public class ModifyNicknameActivity extends BaseActivity<AcModifyNicknameBinding> implements TextWatcher{

    public static void go(Context context) {
        Intent intent = new Intent(context, ModifyNicknameActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcModifyNicknameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
    }

    public void initViews() {
        binding.etModifyNickname.setText(AccountManager.getInstance().getCurrentUser().name);
        binding.etModifyNickname.addTextChangedListener(this);
    }

    public void back(View view) {
        finish();
    }

    public void confirm(View view) {
        if (TextUtils.isEmpty(binding.etModifyNickname.getText().toString().trim())) {
            ToastUtils.toastShow(getString(R.string.nickname_is_empty));
            return;
        }
        if (binding.etModifyNickname.getText().toString().trim().equals(AccountManager.getInstance().getCurrentUser().name)) {
            return;
        }
        UserInfo userBean = new UserInfo();
        userBean.name = binding.etModifyNickname.getText().toString().trim();
        showLoading();

    }

    public void deleteNickname(View View) {
        binding.etModifyNickname.setText("");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(binding.etModifyNickname.getText().toString().trim())) {
            binding.ivModifyNicknameDelete.setVisibility(View.GONE);
        } else {
            binding.ivModifyNicknameDelete.setVisibility(View.VISIBLE);
        }
    }
}