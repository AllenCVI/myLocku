package com.lockulockme.locku.zlocksix.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.databinding.AcPasswordInfoBinding;
import com.lockulockme.locku.zlocksix.base.utils.AccountManager;
import com.lockulockme.locku.zlocksix.common.BaseActivity;

public class PasswordInfoActivity extends BaseActivity<AcPasswordInfoBinding> {

    public static void go(Context context) {
        Intent intent = new Intent(context, PasswordInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcPasswordInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UserInfo userBean = AccountManager.getInstance().getCurrentUser();
        binding.tvLoginAccount.setText(userBean.id+"");
        binding.tvLoginPassword.setText(AccountManager.getInstance().getPwd());
    }

    public void back(View view) {
        finish();
    }

    public void changePassword(View view) {
        ChangePasswordActivity.go(this);
    }
}
