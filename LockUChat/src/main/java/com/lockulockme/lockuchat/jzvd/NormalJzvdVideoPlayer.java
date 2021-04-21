package com.lockulockme.lockuchat.jzvd;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.lockulockme.lockuchat.R;

import cn.jzvd.JzvdStd;

public class NormalJzvdVideoPlayer extends JzvdStd {


    public NormalJzvdVideoPlayer(Context context) {
        super(context);
    }

    public NormalJzvdVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
//        setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP);
    }

//    public void startVideo(String url) {
//        if (TextUtils.isEmpty(url)) return;
//        JZDataSource jzDataSource = new JZDataSource(url);
//        setUp(jzDataSource, SCREEN_NORMAL, JZExo.class);
//        startVideo();
//    }

    @Override
    public void startVideo() {
        super.startVideo();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == cn.jzvd.R.id.poster &&
                (state == STATE_PLAYING ||
                        state == STATE_PAUSE)) {
            onClickUiToggle();
        } else if (v.getId() == R.id.fullscreen) {

        } else {
            super.onClick(v);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.my_jz_normal;
    }


}
