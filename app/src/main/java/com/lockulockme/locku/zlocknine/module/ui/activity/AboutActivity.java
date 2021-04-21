package com.lockulockme.locku.zlocknine.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.lockulockme.locku.BuildConfig;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.policy.PolicyUrls;
import com.lockulockme.locku.databinding.AcAboutBinding;
import com.lockulockme.locku.zlocknine.common.BaseActivity;

public class AboutActivity extends BaseActivity<AcAboutBinding> {

    public static void go(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    long lastClick;
    int clickNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvVersionNumber.setText(getString(R.string.version_number, BuildConfig.VERSION_NAME));


        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                if (now - lastClick < 200) {
                    clickNum++;
                    if (clickNum > 6) {
                        binding.tvAppName.setText(BuildConfig.VERSION_NAME + "_" + BuildConfig.FLAVOR + "_" + BuildConfig.BUILD_TYPE + "_" + BuildConfig.outputTime);

                        binding.tvAppName.setVisibility(View.VISIBLE);
                    }
                } else {
                    clickNum = 0;
                }
                lastClick = now;
            }
        });
    }

    public void back(View view) {
        finish();
    }

    public void goPrivacy(View view) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(PolicyUrls.PRIVACY_POLICY_URL);
        intent.setData(content_url);
        startActivity(intent);
    }

    public void goUserAgreement(View view) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(PolicyUrls.USER_AGREEMENT_URL);
        intent.setData(content_url);
        startActivity(intent);
    }

}
