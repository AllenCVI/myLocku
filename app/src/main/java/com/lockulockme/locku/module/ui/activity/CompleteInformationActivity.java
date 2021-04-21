package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.CompleteInfoRequest;
import com.lockulockme.locku.base.beans.responsebean.LoginResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.VipManager;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.databinding.AcCompleteInformationBinding;
import com.lockulockme.lockuchat.utils.ToastUtils;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class CompleteInformationActivity extends BaseActivity<AcCompleteInformationBinding> {
    public static final String LOGIN_BUNDLE = "Login_Bundle";
    public static final String LOGIN_JSON = "Login_Json";
    private LoginResponseBean loginResponseBean;
    private final int MALE_TYPE = 1;
    private final int FEMALE_TYPE = 2;
    private final int MAX_AGE = 99;
    private final int MIN_AGE = 18;
    private final int CURR_AGE = 25;
    private int currAge = 0;
    private int currGender = MALE_TYPE;

    public static void StartMe(Context context, String LoginJson) {
        Intent intent = new Intent(context, CompleteInformationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(LOGIN_JSON, LoginJson);
        intent.putExtra(LOGIN_BUNDLE, bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcCompleteInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        VipManager.getInstance().cancelTimer();

        String json = getIntent().getBundleExtra(LOGIN_BUNDLE).getString(LOGIN_JSON);
        if (TextUtils.isEmpty(json)){
            ToastUtils.toastShow(R.string.network_error);
            finish();
            return;
        }
        try {
            Gson gson = new Gson();
            loginResponseBean = gson.fromJson(json, LoginResponseBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        binding.tvRegister.setOnClickListener(v -> {
            if (loginResponseBean == null) {
                ToastUtils.toastShow(R.string.network_error);
                return;
            }
            onComplete(currAge, String.valueOf(currGender));
        });

        binding.ivTitleBack.setOnClickListener(v -> {
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



    private void onComplete(int age, String type) {
        showLoading();
        CompleteInfoRequest completeInfoRequest = new CompleteInfoRequest();
        completeInfoRequest.UserAge = age;
        completeInfoRequest.sex = type;
        OkGoUtils.getInstance().completeInfo(CompleteInformationActivity.this, completeInfoRequest, new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                hideLoading();
                loginResponseBean.isCompleteUserInfo = true;
                loginResponseBean.user.age = age;
                loginResponseBean.user.userGender = type;
                AccountManager.getInstance().putIsComplete(loginResponseBean.isCompleteUserInfo);
                AccountManager.getInstance().putUser(loginResponseBean.user);
                Gson gson = new Gson();
                String loginJson = gson.toJson(loginResponseBean);
                SetHeaderImgActivity.start(CompleteInformationActivity.this, loginJson);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                hideLoading();
                ToastUtils.toastShow(R.string.network_error);
            }
        });
    }
}
