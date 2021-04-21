package com.lockulockme.locku.zlockfour.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.requestbean.ChangePasswordRequestBean;
import com.lockulockme.locku.databinding.AcModifyPasswordBinding;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlockfour.base.utils.AccountManager;
import com.lockulockme.locku.zlockfour.base.utils.OkGoUtils;
import com.lockulockme.locku.zlockfour.common.BaseActivity;
import com.lockulockme.locku.zlockfour.common.LoginoutManager;
import com.lockulockme.lockuchat.utils.ToastUtils;

public class ChangePasswordActivity extends BaseActivity<AcModifyPasswordBinding> {

    public static void go(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcModifyPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UserInfo userBean = AccountManager.getInstance().getCurrentUser();
        binding.etOldPassword.setText(AccountManager.getInstance().getPwd());
    }

    public void back(View view) {
        finish();
    }

    public void changePassword(View view) {
        if (TextUtils.isEmpty(binding.etOldPassword.getText().toString().trim())) {
            ToastUtils.toastShow(R.string.please_input_old_password);
            return;
        }
        if (TextUtils.isEmpty(binding.etNewPassword.getText().toString().trim())) {
            ToastUtils.toastShow(R.string.please_input_new_password);
            return;
        }
        if (binding.etNewPassword.getText().toString().trim().length()< 6) {
            ToastUtils.toastShow(R.string.password_rules);
            return;
        }
        if (!binding.etNewPassword.getText().toString().trim().equals(binding.etConfirmPassword.getText().toString().trim())) {
            ToastUtils.toastShow(R.string.two_password_not_match);
            return;
        }
        ChangePasswordRequestBean req = new ChangePasswordRequestBean();
        req.newPassword = binding.etNewPassword.getText().toString().trim();
        req.oldPassword = binding.etOldPassword.getText().toString().trim();
        OkGoUtils.getInstance().modifyPassword(this, req, new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                AccountManager.getInstance().putPwd("");
                ToastUtils.toastShow(getString(R.string.modify_pwd_success));

                LoginoutManager.loginout(ChangePasswordActivity.this);
            }
        });
    }
}
