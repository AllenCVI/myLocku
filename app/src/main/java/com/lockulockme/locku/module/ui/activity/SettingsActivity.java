package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.lockulockme.locku.R;
import com.lockulockme.locku.application.MyActivityLifecycle;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.common.LoginoutManager;
import com.lockulockme.locku.common.PopupWindowBuilder;
import com.lockulockme.locku.databinding.AcSettingsBinding;
import com.lockulockme.locku.databinding.LayoutSwitchUserBinding;
import com.lockulockme.locku.databinding.PopRateBinding;
import com.lockulockme.locku.databinding.PopRateReviewBinding;
import com.lockulockme.locku.base.utils.SharedPreferencesUtil;
import com.lockulockme.locku.module.ui.pop.CommentAppPop;

public class SettingsActivity extends BaseActivity<AcSettingsBinding> {

    private CommentAppPop commentAppPop;

    public static void go(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        boolean isRated = SharedPreferencesUtil.getBooleanValue(this,"rate");
        if (isRated) {
            binding.lltRate.setVisibility(View.GONE);
        } else {
            binding.lltRate.setVisibility(View.VISIBLE);
        }

        commentAppPop = new CommentAppPop(this);
    }

    public void back(View view) {
        finish();
    }

    public void goUserInformation(View view) {
        PasswordInfoActivity.go(this);
    }

    public void goRateUs(View view) {
        commentAppPop.goRateUs();
    }

    public void goAbout(View view) {
        AboutActivity.go(this);
    }

    public void switchUser(View view) {
        final PopupWindowBuilder popBuilder=PopupWindowBuilder.newBuilder(this)
                .setContentView(R.layout.layout_switch_user)
                .setFocusable(true)
                .setOutsideTouchable(true)
                .build();
        final LayoutSwitchUserBinding popRateBing = LayoutSwitchUserBinding.bind(popBuilder.getRootView());
        popBuilder.show(binding.getRoot(), Gravity.CENTER,0,0);
        popRateBing.tvSwitchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBuilder.dismiss();
            }
        });
        popRateBing.tvSwitchConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBuilder.dismiss();
                LoginoutManager.loginout(SettingsActivity.this);
            }
        });

    }
}
