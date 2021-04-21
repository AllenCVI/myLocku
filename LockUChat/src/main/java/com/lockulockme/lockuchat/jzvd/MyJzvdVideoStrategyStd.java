package com.lockulockme.lockuchat.jzvd;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lockulockme.lockuchat.R;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;

public class MyJzvdVideoStrategyStd extends Jzvd {


    public MyJzvdVideoStrategyStd(Context context) {
        super(context);
    }

    public MyJzvdVideoStrategyStd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP);
    }

    public void startVideo(String url) {
        if (TextUtils.isEmpty(url)) return;
        JZDataSource jzDataSource = new JZDataSource(url);
        setUp(jzDataSource, SCREEN_NORMAL, JZExo.class);
        startVideo();
    }

    @Override
    public void startVideo() {
        super.startVideo();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.my_jz_layout_std;
    }
}
