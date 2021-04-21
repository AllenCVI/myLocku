package com.lockulockme.lockuchat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lockulockme.lockuchat.adapter.ScanImage4StrAdapter;
import com.lockulockme.lockuchat.adapter.ScanImageAdapter;
import com.lockulockme.lockuchat.databinding.AcScanImageBinding;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.Serializable;
import java.util.Arrays;

public class ScanImageActivity extends BaseActivity {

    private AcScanImageBinding binding;
    public static final String MSG_DATA="msg";
    private BaseQuickAdapter scanImageAdapter;

    public static void startMe(Context context, IMMessage message){
        Intent intent=new Intent(context,ScanImageActivity.class);
        intent.putExtra(MSG_DATA,message);
        context.startActivity(intent);
    }
    public static void startMe(Context context, String url){
        Intent intent=new Intent(context,ScanImageActivity.class);
        intent.putExtra(MSG_DATA,url);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcScanImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Serializable data = getIntent().getSerializableExtra(MSG_DATA);
        if (data instanceof IMMessage){
            IMMessage message = (IMMessage) data;
            scanImageAdapter = new ScanImageAdapter();
            scanImageAdapter.setNewInstance(Arrays.asList(message));

            ((ScanImageAdapter)scanImageAdapter).setOnImageClickListener(()->{
                finish();
            });
        }else {
            String url = (String) data;
            scanImageAdapter = new ScanImage4StrAdapter();
            scanImageAdapter.setNewInstance(Arrays.asList(url));

            ((ScanImage4StrAdapter)scanImageAdapter).setOnImageClickListener(()->{
                finish();
            });
        }

        binding.viewPager.setAdapter(scanImageAdapter);
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

    }
}
