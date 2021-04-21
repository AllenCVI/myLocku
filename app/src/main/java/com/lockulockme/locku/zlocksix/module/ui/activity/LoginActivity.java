package com.lockulockme.locku.zlocksix.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.LoginRequestBean;
import com.lockulockme.locku.base.beans.responsebean.LoginResponseBean;
import com.lockulockme.locku.databinding.AcLoginBinding;
import com.lockulockme.locku.zlocksix.application.MyActivityLifecycle;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlocksix.base.utils.AccountManager;
import com.lockulockme.locku.zlocksix.base.utils.OkGoUtils;
import com.lockulockme.locku.zlocksix.base.utils.VipManager;
import com.lockulockme.locku.zlocksix.common.BaseActivity;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;

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
                OkGoUtils.getInstance().onLogin(this, new LoginRequestBean(binding.etAccount.getText().toString(), binding.etPwd.getText().toString()), new NewJsonCallback<LoginResponseBean>() {
                    @Override
                    public void onSuc(LoginResponseBean response, String msg) {
                        IMLoginHelper.getInstance().login(response.user.nimId, response.userNimAccessToken, new RequestCallback<LoginInfo>() {
                            @Override
                            public void onSuccess(LoginInfo loginInfo) {
                                hideLoading();
                                AccountManager.getInstance().saveAccountNoPwd(response);
                                AccountManager.getInstance().putPwd(response.pwModified?"":response.user.password);
                                MyActivityLifecycle.getInstance().finishAllActivity();
                                MainActivity.StartMe(mContext);
                            }

                            @Override
                            public void onFailed(int i) {
                                hideLoading();
                                ToastUtils.toastShow(R.string.login_failed);
                            }

                            @Override
                            public void onException(Throwable throwable) {
                                hideLoading();
                                ToastUtils.toastShow(R.string.login_failed);
                            }
                        });
                    }

                    @Override
                    public void onE(int httpCode, int apiCode, String msg, LoginResponseBean response) {
                        super.onE(httpCode, apiCode, msg, response);
                        hideLoading();
                        ToastUtils.toastShow(R.string.login_failed);
                    }
                });
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
