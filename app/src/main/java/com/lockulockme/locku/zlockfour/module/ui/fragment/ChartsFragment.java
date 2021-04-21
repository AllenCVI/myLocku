package com.lockulockme.locku.zlockfour.module.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.lockulockme.locku.databinding.FgChartsBinding;
import com.lockulockme.locku.zlockfour.common.BaseFragment;
import com.lockulockme.locku.zlockfour.module.ui.adapter.ContentNormalPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChartsFragment extends BaseFragment<FgChartsBinding> {

    private List<Fragment> fragments;
    private final int[] typeArr = {ChartsChildFragment.GOD_TYPE, ChartsChildFragment.MALE_TYPE};

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container) {
        binding = FgChartsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewPager();
        initListener();
        binding.viewPager.setCurrentItem(0);
        setSelect(ChartsChildFragment.GOD_TYPE);
    }

    private void initListener() {
        binding.tvGoddess.setOnClickListener(v -> {
            setSelect(ChartsChildFragment.GOD_TYPE);
            binding.viewPager.setCurrentItem(0);
        });
        binding.tvRich.setOnClickListener(v -> {
            setSelect(ChartsChildFragment.MALE_TYPE);
            binding.viewPager.setCurrentItem(1);
        });
        binding.tvRule.setOnClickListener(v -> {

        });
    }

    private void initViewPager() {
        fragments = new ArrayList<>();
        fragments.add(ChartsChildFragment.getInstance(ChartsChildFragment.GOD_TYPE));
        fragments.add(ChartsChildFragment.getInstance(ChartsChildFragment.MALE_TYPE));
        ContentNormalPagerAdapter pagerAdapter = new ContentNormalPagerAdapter(getChildFragmentManager(), fragments, null);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (fragments != null) {
                    ((ChartsChildFragment) fragments.get(position)).updateData();
                }
                setSelect(typeArr[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void setSelect(int type) {
        if (type == ChartsChildFragment.GOD_TYPE) {
            binding.tvGoddess.setSelected(true);
            binding.tvRich.setSelected(false);
        } else {
            binding.tvGoddess.setSelected(false);
            binding.tvRich.setSelected(true);
        }
    }
}
