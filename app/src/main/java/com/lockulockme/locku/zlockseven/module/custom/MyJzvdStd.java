package com.lockulockme.locku.zlockseven.module.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.lockulockme.locku.R;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class MyJzvdStd extends JzvdStd {

    private final int radius = 10;
    private ImageView ivCover;

    public MyJzvdStd(Context context) {
        super(context);
    }

    public MyJzvdStd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP);
        bottomContainer.setVisibility(GONE);
        ivCover = findViewById(R.id.iv_cover);
    }

    public void setUpVideo(String url, String coverUrl) {
        Glide.with(ivCover).load(coverUrl).into(ivCover);
        setUpVideo(url);
    }

    public void setUpVideo(String url) {
        JZDataSource jzDataSource = new JZDataSource(url);
        jzDataSource.looping = true;
        setUp(jzDataSource, SCREEN_NORMAL, JZMediaExo.class);
    }

    public void startVideo(String url) {
        setUpVideo(url, "");
        startPreloading();
    }


    @Override
    public void startPreloading() {
        super.startPreloading();
        startButton.setVisibility(INVISIBLE);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (radius > 0) {
            Path path = new Path();
            path.addRoundRect(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()),
                    radius, radius, Path.Direction.CW);
            canvas.clipPath(path);
        }
        super.dispatchDraw(canvas);
    }

    @Override
    public void onClick(View v) {
//        super.onClick(v);
        int i = v.getId();
        if (i == cn.jzvd.R.id.fullscreen) {
            Log.i(TAG, "onClick: fullscreen button");
        } else if (i == R.id.surface_container || i == R.id.start) {
            Log.i(TAG, "onClick: surface_container button");
            if (state == STATE_NORMAL) {
                startVideo();
            } else if (state == STATE_PLAYING) {
                Log.d(TAG, "pauseVideo [" + this.hashCode() + "] ");
                mediaInterface.pause();
                onStatePause();
            } else if (state == STATE_PAUSE) {
                mediaInterface.start();
                onStatePlaying();
            } else if (state == STATE_AUTO_COMPLETE) {
                startVideo();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        super.onTouch(v, event);
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.jz_layout_std;
    }

    @Override
    public void startVideo() {
        super.startVideo();
        Log.i(TAG, "startVideo");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        Log.i(TAG, "Seek position ");
    }

    @Override
    public void gotoFullscreen() {
        super.gotoFullscreen();
        Log.i(TAG, "goto Fullscreen");
    }

    @Override
    public void gotoNormalScreen() {
        super.gotoNormalScreen();
        Log.i(TAG, "quit Fullscreen");
    }

    @Override
    public void autoFullscreen(float x) {
        super.autoFullscreen(x);
        Log.i(TAG, "auto Fullscreen");
    }

    @Override
    public void onClickUiToggle() {
        super.onClickUiToggle();
        Log.i(TAG, "click blank");
    }

    //onState 代表了播放器引擎的回调，播放视频各个过程的状态的回调
    @Override
    public void onStateNormal() {
        super.onStateNormal();
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        if (state != STATE_NORMAL
                && state != STATE_ERROR
                && state != STATE_AUTO_COMPLETE) {
            post(() -> {
                bottomContainer.setVisibility(View.INVISIBLE);
                topContainer.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
                cancelProgressTimer();
            });
        }
    }

    @Override
    public void onStateError() {
        super.onStateError();
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        Log.i(TAG, "Auto complete");
    }

    //changeUiTo 真能能修改ui的方法
    @Override
    public void changeUiToNormal() {
        super.changeUiToNormal();
        startButton.setVisibility(INVISIBLE);
    }

    @Override
    public void changeUiToPreparing() {
        super.changeUiToPreparing();
        startButton.setVisibility(INVISIBLE);
    }

    @Override
    public void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        startButton.setVisibility(INVISIBLE);
    }

    @Override
    public void changeUiToPlayingClear() {
        super.changeUiToPlayingClear();
    }

    @Override
    public void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        startButton.setVisibility(VISIBLE);
    }

    @Override
    public void changeUiToPauseClear() {
        super.changeUiToPauseClear();
        startButton.setVisibility(INVISIBLE);
    }

    @Override
    public void changeUiToComplete() {
        super.changeUiToComplete();
    }

    @Override
    public void changeUiToError() {
        super.changeUiToError();
        startButton.setVisibility(INVISIBLE);
    }

    @Override
    public void onInfo(int what, int extra) {
        super.onInfo(what, extra);
    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
    }

}
