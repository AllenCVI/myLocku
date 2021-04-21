package com.lockulockme.locku.zlockthree.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.lockulockme.locku.R;
import com.lockulockme.locku.databinding.AcMySpaceBinding;
import com.lockulockme.locku.zlockthree.common.BaseActivity;
import com.lockulockme.locku.zlockthree.module.ui.adapter.ContentNormalPagerAdapter;
import com.lockulockme.locku.zlockthree.module.ui.fragment.AlbumFragment;

import java.util.ArrayList;
import java.util.List;

public class MySpaceActivity extends BaseActivity<AcMySpaceBinding> {

    public static void go(Context context) {
        Intent intent = new Intent(context, MySpaceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcMySpaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
    }

    private void initViews() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new AlbumFragment());
//        fragments.add(new VideoFragment());
        binding.vpSpace.setAdapter(new ContentNormalPagerAdapter(getSupportFragmentManager(), fragments, null));
        binding.vpSpace.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.tvAlbum.setBackgroundResource(R.drawable.bg_my_space_title_selected);
                    binding.tvShortvideo.setBackgroundResource(R.drawable.bg_my_space_title_default);
                } else {
                    binding.tvAlbum.setBackgroundResource(R.drawable.bg_my_space_title_default);
                    binding.tvShortvideo.setBackgroundResource(R.drawable.bg_my_space_title_selected);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.vpSpace.setCurrentItem(0);
    }

    public void back(View view) {
        finish();
    }

    public void goAlbum(View view) {
        binding.tvAlbum.setBackgroundResource(R.drawable.bg_my_space_title_selected);
        binding.tvShortvideo.setBackgroundResource(R.drawable.bg_my_space_title_default);
        binding.vpSpace.setCurrentItem(0);
    }

    public void goShortVideo(View view) {
        binding.tvAlbum.setBackgroundResource(R.drawable.bg_my_space_title_default);
        binding.tvShortvideo.setBackgroundResource(R.drawable.bg_my_space_title_selected);
        binding.vpSpace.setCurrentItem(1);
    }

}
