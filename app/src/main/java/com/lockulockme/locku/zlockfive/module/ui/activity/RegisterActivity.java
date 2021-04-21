package com.lockulockme.locku.zlockfive.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.RegisterRequestBean;
import com.lockulockme.locku.base.beans.responsebean.LoginResponseBean;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.databinding.AcRegisterBinding;
import com.lockulockme.locku.zlockfive.application.MyActivityLifecycle;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlockfive.base.utils.AccountManager;
import com.lockulockme.locku.zlockfive.base.utils.OkGoUtils;
import com.lockulockme.locku.zlockfive.base.utils.VipManager;
import com.lockulockme.locku.zlockfive.common.BaseActivity;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class RegisterActivity extends BaseActivity<AcRegisterBinding> {


    private final int MALE_TYPE = 1;
    private final int FEMALE_TYPE = 2;
    private final int MAX_AGE = 99;
    private final int MIN_AGE = 18;
    private final int CURR_AGE = 25;
    private int currAge = 0;
    private int currGender = MALE_TYPE;

    public static void StartMe(Context context) {
        Intent it = new Intent(context, RegisterActivity.class);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        VipManager.getInstance().cancelTimer();
    }

    private void initView() {
        String[] displayContent = new String[82];
        for (int i = 0; i < displayContent.length; i++) {
            displayContent[i] = 18 + i + "";
        }
        binding.picker.setDisplayedValues(displayContent);
        binding.picker.setMinValue(MIN_AGE);
        binding.picker.setMaxValue(MAX_AGE);
        binding.picker.setValue(CURR_AGE);
        binding.picker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                currAge = newVal;
            }
        });
        currAge = CURR_AGE;

        binding.rlMale.setOnClickListener(v -> {
            setCurrentGender(MALE_TYPE);
        });
        binding.rlFemale.setOnClickListener(v -> {
            setCurrentGender(FEMALE_TYPE);
        });

        binding.tvLogin.setOnClickListener(v -> {
            //startLogin
            LoginActivity.StartMe(mContext);
        });

        binding.tvRegister.setOnClickListener(v -> {
            onRegister(currAge, String.valueOf(currGender));
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });
    }


    private void setCurrentGender(int type) {
        if (type == MALE_TYPE) {
            binding.rlMale.setBackgroundResource(R.drawable.shape_cffffd545_oval);
            binding.rlFemale.setBackgroundColor(Color.TRANSPARENT);
            binding.ivMale.setImageResource(R.mipmap.male_press);
            binding.ivFemale.setImageResource(R.mipmap.female_normal);
            binding.tvMale.setTextColor(Color.WHITE);
            binding.tvFemale.setTextColor(getResources().getColor(R.color.color_66ffffff));
        } else if (type == FEMALE_TYPE) {
            binding.rlFemale.setBackgroundResource(R.drawable.shape_cffffd545_oval);
            binding.rlMale.setBackgroundColor(Color.TRANSPARENT);
            binding.ivMale.setImageResource(R.mipmap.male_normal);
            binding.ivFemale.setImageResource(R.mipmap.female_press);
            binding.tvMale.setTextColor(getResources().getColor(R.color.color_66ffffff));
            binding.tvFemale.setTextColor(Color.WHITE);
        }
        currGender = type;
    }

    private void onRegister(int age, String type) {
        showLoading();
        OkGoUtils.getInstance().onRegister(this, new RegisterRequestBean(age, type), new NewJsonCallback<LoginResponseBean>() {
            @Override
            public void onSuc(LoginResponseBean response, String msg) {
                LogUtil.LogE("onRegister=",""+response);
                IMLoginHelper.getInstance().login(response.user.nimId, response.userNimAccessToken, new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
                        hideLoading();
                        AccountManager.getInstance().saveAccount(response);
                        MyActivityLifecycle.getInstance().finishAllActivity();
                        MainActivity.StartMe(mContext);
                    }

                    @Override
                    public void onFailed(int i) {
                        hideLoading();
                        ToastUtils.toastShow(R.string.login_failed);
                        LogUtil.LogE("REGISTER", i + "");
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        throwable.printStackTrace();
                        ToastUtils.toastShow(R.string.login_failed);
                        hideLoading();
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
}
