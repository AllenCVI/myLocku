package com.lockulockme.locku.zlocksix.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.databinding.AcImagePreviewBinding;
import com.lockulockme.locku.zlocksix.common.BaseActivity;
import com.lockulockme.locku.zlocksix.module.ui.adapter.ImagePreviewAdapter;

import java.io.Serializable;
import java.util.List;

public class ImagePreviewActivity extends BaseActivity<AcImagePreviewBinding> {


    private ImagePreviewAdapter imageAdapter;
    private int position;
    private List<String> list;

    public static void StartMe(Context context, List<String> list, int position) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra("list", (Serializable) list);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcImagePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        list = (List<String>) getIntent().getSerializableExtra("list");
        position = getIntent().getIntExtra("position", 0);
        initAdapter();
        initViewPager();
    }

    private void initViewPager() {
        binding.viewPager.setCurrentItem(position, false);
        binding.tvPosition.setText((position + 1) + "/" + imageAdapter.getItemCount());
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setPos(position);
            }
        });
    }

    private void initAdapter() {
        imageAdapter = new ImagePreviewAdapter();
        imageAdapter.addChildClickViewIds(R.id.iv_back);
        imageAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                finish();
            }
        });
        binding.viewPager.setAdapter(imageAdapter);
        imageAdapter.setNewInstance(list);
        imageAdapter.setSelectPos(position);
    }

    private void setPos(int position) {
        binding.tvPosition.setText((position + 1) + "/" + imageAdapter.getItemCount());
    }
}
