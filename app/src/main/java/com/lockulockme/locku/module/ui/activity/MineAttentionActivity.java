package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.lockulockme.locku.R;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.databinding.AcMineAttentionBinding;
import com.lockulockme.locku.module.ui.adapter.ContentNormalPagerAdapter;
import com.lockulockme.locku.module.ui.fragment.MineAttentionFragment;

import java.util.ArrayList;
import java.util.List;

public class MineAttentionActivity extends BaseActivity<AcMineAttentionBinding> {

    private List<Fragment> mFragmentList;
    public static final int MUT_INDEX = 0;
    public static final int FOLLOW_INDEX = 1;
    public static final int FANS_INDEX = 2;
    private int mIndex = 0;
    private static final String INDEX_KEY = "intent_key_index";

    public static void start(Context context, int curr) {
        Intent intent = new Intent(context, MineAttentionActivity.class);
        intent.putExtra(INDEX_KEY, curr);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcMineAttentionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mIndex = getIntent().getIntExtra(INDEX_KEY, 0);
        initView();
    }

    private void initView() {
        initFragment();
        initViewPager();
        initListener();
        selectNormal();
    }

    private void selectNormal() {
        binding.viewPager.setCurrentItem(mIndex);
        setSelectTitle(mIndex);
    }

    private void initListener() {
        binding.tvAttention.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(FOLLOW_INDEX);
            setSelectTitle(FOLLOW_INDEX);
        });
        binding.tvMut.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(MUT_INDEX);
            setSelectTitle(MUT_INDEX);
        });
        binding.tvFans.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(FANS_INDEX);
            setSelectTitle(FANS_INDEX);
        });
    }

    private void initViewPager() {
        ContentNormalPagerAdapter contentNormalPagerAdapter = new ContentNormalPagerAdapter(getSupportFragmentManager(), mFragmentList, null);
        binding.viewPager.setAdapter(contentNormalPagerAdapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelectTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initFragment() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(MineAttentionFragment.getInstance(MineAttentionFragment.FRIEND_TYPE));
        mFragmentList.add(MineAttentionFragment.getInstance(MineAttentionFragment.ATTENTION_TYPE));
        mFragmentList.add(MineAttentionFragment.getInstance(MineAttentionFragment.FANS_TYPE));
    }

    private void setSelectTitle(int position) {
        if (position == 0) {
            setNavTitle(getString(R.string.mutu_follow));
            binding.tvMut.setBackgroundResource(R.drawable.shape_cff4a52_c18);
            binding.tvAttention.setBackgroundResource(R.drawable.shape_c1d1a4d_c18);
            binding.tvFans.setBackgroundResource(R.drawable.shape_c1d1a4d_c18);
        } else if (position == 1) {
            setNavTitle(getString(R.string.follow));
            binding.tvMut.setBackgroundResource(R.drawable.shape_c1d1a4d_c18);
            binding.tvAttention.setBackgroundResource(R.drawable.shape_cff4a52_c18);
            binding.tvFans.setBackgroundResource(R.drawable.shape_c1d1a4d_c18);
        } else if (position == 2) {
            setNavTitle(getString(R.string.fans));
            binding.tvMut.setBackgroundResource(R.drawable.shape_c1d1a4d_c18);
            binding.tvAttention.setBackgroundResource(R.drawable.shape_c1d1a4d_c18);
            binding.tvFans.setBackgroundResource(R.drawable.shape_cff4a52_c18);
        }
    }
}
