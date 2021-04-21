package com.lockulockme.locku.module.ui.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;



public class ContentNormalPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments;
    private final String[] titles;

    public ContentNormalPagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] strings) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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