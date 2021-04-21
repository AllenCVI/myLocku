package com.lockulockme.locku.zlocksix.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lockulockme.locku.databinding.AcPlayBinding;
import com.lockulockme.locku.zlocksix.common.BaseActivity;
import com.lockulockme.lockuchat.utils.ToastUtils;

import cn.jzvd.Jzvd;

public class PlayActivity extends BaseActivity<AcPlayBinding> {

    static final String URL_KEY = "intent_key_url";
    static final String COVER_URL_KEY = "intent_key_cover_url";
    private String videoUrl;
    private String coverUrl;

    public static void StartMe(Context context, String url) {
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(URL_KEY, url);
        context.startActivity(intent);
    }

    public static void StartMe(Context context, String url, String coverUrl) {
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(URL_KEY, url);
        intent.putExtra(COVER_URL_KEY, coverUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
    }

    private void initData() {
        videoUrl = getIntent().getStringExtra(URL_KEY);
        coverUrl = getIntent().getStringExtra(COVER_URL_KEY);
        if (TextUtils.isEmpty(videoUrl)) {
            ToastUtils.toastShow("url is empty！");
            return;
        }
        binding.jzvd.setUpVideo(videoUrl, coverUrl);
        binding.jzvd.startPreloading();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }
}
