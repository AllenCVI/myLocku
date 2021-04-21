package com.lockulockme.locku.zlockeight.module.ui.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;


public class ContentNormalPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private String[] titles;

    public ContentNormalPagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] strings) {
        super(fm);
        this.fragments = fragments;
        this.titles = strings;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}