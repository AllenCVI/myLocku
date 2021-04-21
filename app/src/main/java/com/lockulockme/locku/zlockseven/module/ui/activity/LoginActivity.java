package com.lockulockme.locku.zlockseven.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.lockulockme.locku.databinding.AcLoginBinding;
import com.lockulockme.locku.zlockseven.base.utils.AccountManager;
import com.lockulockme.locku.zlockseven.base.utils.VipManager;
import com.lockulockme.locku.zlockseven.common.BaseActivity;

public class LoginActivity extends BaseActivity<AcLoginBinding> {

    public static void StartMe(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        VipManager.getInstance().cancelTimer();
    }

    private void initView() {
        binding.etAccount.setText(AccountManager.getInstance().getAccount());
        binding.etPwd.setText(AccountManager.getInstance().getPwd());
        binding.tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();

            }
        });
        binding.ivBack.setOnClickListener(v -> {
            LoginIndexActivity.StartMe(this);
            finish();
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LoginIndexActivity.StartMe(this);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
