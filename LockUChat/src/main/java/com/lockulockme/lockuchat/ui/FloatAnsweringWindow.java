package com.lockulockme.lockuchat.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.ChatAnswerUtils;
import com.lockulockme.lockuchat.aavg2.ui.ui.NERTCAudioCallActivity;
import com.lockulockme.lockuchat.aavg2.ui.ui.NERTCBaseActivity;
import com.lockulockme.lockuchat.aavg2.ui.ui.NERTCSmallScreenActivity;
import com.lockulockme.lockuchat.aavg2.ui.ui.NERTCVideoCallActivity;
import com.lockulockme.lockuchat.databinding.FloatWindowAudioBinding;
import com.lockulockme.lockuchat.databinding.FloatWindowAudioWindowBinding;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.lockulockme.lockuchat.utils.ScreenInfo;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.anim.AppFloatDefaultAnimator;
import com.lzf.easyfloat.anim.DefaultAnimator;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.lzf.easyfloat.interfaces.OnInvokeView;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FloatAnsweringWindow implements Application.ActivityLifecycleCallbacks {
    private Application application;
    private Activity topActivity;
    private List<Class<?>> filterActivitys = new ArrayList<>();

    public void addFilterActivity(Class<?> clazzs) {
        filterActivitys.add(clazzs);
    }

    public void addFilterActivity(Class<?>... clazzs) {
        List<Class<?>> list = Arrays.asList(clazzs);
        filterActivitys.addAll(list);
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public FloatAnsweringWindow() {
        addFilterActivity(NERTCVideoCallActivity.class);
        addFilterActivity(NERTCAudioCallActivity.class);
        addFilterActivity(NERTCSmallScreenActivity.class);
    }

    private static class InnnerHolder {
        private static FloatAnsweringWindow INSTANCE = new FloatAnsweringWindow();
    }

    public static FloatAnsweringWindow getInstance() {
        return FloatAnsweringWindow.InnnerHolder.INSTANCE;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        showOrHideFloatWindow(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        topActivity = activity;
        showOrHideFloatWindow(topActivity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    private boolean isShowFloatWindow = false;
    private Class<?> targetActivity;
    private NERTCBaseActivity chatActivity;
    private Intent reStartIntent;

//    public void showFirstFloatWindow(Intent intent) {
//        isShowFloatWindow = true;
//        reStartIntent = intent;
//    }

    public void showFirstFloatWindow(NERTCBaseActivity activity, Class<?> targetActivity) {
        chatActivity = activity;
        isShowFloatWindow = true;
        this.targetActivity = targetActivity;
    }

    public void hideFloatWindow() {
        isShowFloatWindow = false;
        if (topActivity != null) {
            FrameLayout contentView = topActivity.findViewById(android.R.id.content);
            View floatView = topActivity.findViewById(R.id.ll_out_float_window_out);
            if (floatView != null) {
                contentView.removeView(floatView);
            }
        }
    }

    private synchronized void showOrHideFloatWindow(Activity activity) {
        for (Class<?> filterActivity : filterActivitys) {
            if (filterActivity == activity.getClass()) {
                return;
            }
        }

        FrameLayout contentView = activity.findViewById(android.R.id.content);
        FrameLayout floatViewOut = contentView.findViewById(R.id.ll_out_float_window_out);
        View floatView = contentView.findViewById(R.id.ll_out_float_window);
        if (isShowFloatWindow) {
            if (floatViewOut == null) {
                final LayoutInflater inflater = LayoutInflater.from(activity);
                FloatWindowAudioWindowBinding binding = FloatWindowAudioWindowBinding.inflate(inflater, contentView, false);

                contentView.addView(binding.getRoot());

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) binding.llOutFloatWindow.llOutFloatWindow.getLayoutParams();
                layoutParams.leftMargin = (int) floatWindowX;
                layoutParams.topMargin = (int) floatWindowY;
                binding.llOutFloatWindow.llOutFloatWindow.setLayoutParams(layoutParams);

                binding.llOutFloatWindow.llOutFloatWindow.setOnTouchListener(new View.OnTouchListener() {
                    //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
                    private float mTouchStartX;
                    private float mTouchStartY;
                    private float mTouchCurrentX;
                    private float mTouchCurrentY;
                    //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
                    private float mStartX;
                    private float mStartY;
                    private float mStopX;
                    private float mStopY;
                    //判断悬浮窗口是否移动，这里做个标记，防止移动后松手触发了点击事件
                    private boolean isMove = false;

                    private long startTime;

                    FrameLayout.LayoutParams layoutParams = null;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                layoutParams = (FrameLayout.LayoutParams) binding.llOutFloatWindow.llOutFloatWindow.getLayoutParams();
                                isMove = false;
                                mTouchStartX = event.getRawX();
                                mTouchStartY = event.getRawY();

                                mStartX = mTouchStartX;
                                mStartY = mTouchStartY;
                                startTime = System.currentTimeMillis();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (layoutParams == null) return false;
                                mTouchCurrentX = event.getRawX();
                                mTouchCurrentY = event.getRawY();
                                int leftMargin = (int) (layoutParams.leftMargin + mTouchCurrentX - mTouchStartX);
                                int topMargin = (int) (layoutParams.topMargin + mTouchCurrentY - mTouchStartY);
                                if (leftMargin < 0) {
                                    leftMargin = 0;
                                }
                                if (topMargin < 0) {
                                    topMargin = 0;
                                }
                                if (leftMargin + layoutParams.width > ScreenInfo.getInstance().screenWidth) {
                                    leftMargin = ScreenInfo.getInstance().screenWidth - layoutParams.width;
                                }
                                if (topMargin + layoutParams.height > ScreenInfo.getInstance().screenHeight) {
                                    topMargin = ScreenInfo.getInstance().screenHeight - layoutParams.height;
                                }
                                layoutParams.leftMargin = leftMargin;
                                layoutParams.topMargin = topMargin;

                                binding.llOutFloatWindowOut.updateViewLayout(binding.llOutFloatWindow.llOutFloatWindow, layoutParams);

                                mTouchStartX = mTouchCurrentX;
                                mTouchStartY = mTouchCurrentY;

                                break;
                            case MotionEvent.ACTION_UP:
                                mStopX = event.getX();
                                mStopY = event.getY();
//                                binding.getRoot().setLayoutParams(layoutParams);
//                                layoutParams = (FrameLayout.LayoutParams) binding.getRoot().getLayoutParams();
//                                float moveX= 0;
                                if (layoutParams.leftMargin > ScreenInfo.getInstance().screenWidth / 2 - layoutParams.width / 2) {
//                                    moveX = ScreenInfo.getInstance().screenWidth - layoutParams.width - layoutParams.leftMargin;
                                    layoutParams.leftMargin = ScreenInfo.getInstance().screenWidth - layoutParams.width;
                                } else {
//                                    moveX = 0- layoutParams.leftMargin;
                                    layoutParams.leftMargin = 0;
                                }

                                floatWindowX = layoutParams.leftMargin;
                                floatWindowY = layoutParams.topMargin;

//                                ObjectAnimator animator = ObjectAnimator.ofFloat(binding.getRoot(), "translationX", 0, moveX)
//                                        .setDuration(500);
//                                animator.addListener(new AnimatorListenerAdapter() {
//                                    @Override
//                                    public void onAnimationEnd(Animator animation) {
//                                        super.onAnimationEnd(animation);
//                                        contentView.updateViewLayout(binding.getRoot(), layoutParams);
//                                    }
//                                });
//                                animator.start();

                                binding.llOutFloatWindowOut.updateViewLayout(binding.llOutFloatWindow.llOutFloatWindow, layoutParams);

                                long endTime = System.currentTimeMillis();
                                if (endTime - startTime < 500 && Math.sqrt(Math.pow(Math.abs(event.getRawX() - mStartX),2)+Math.pow(Math.abs(event.getRawY() - mStartY),2)) < 10){
                                    onClickEvent();
                                }
                                break;
                        }
                        return true;
                    }
                });

            } else {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) floatView.getLayoutParams();
                layoutParams.leftMargin = (int) floatWindowX;
                layoutParams.topMargin = (int) floatWindowY;
                floatViewOut.updateViewLayout(floatView, layoutParams);
            }
        } else {
            if (floatViewOut != null) {
                contentView.removeView(floatViewOut);
            }
        }
    }

    private void onClickEvent(){
        if (!moveToFront()){
            hideFloatWindow();
        }
    }

    private float floatWindowX = 0;
    private float floatWindowY = 200;

//    class OnFloatClickListener {
//        public void onClick(View v) {
//            if (!moveToFront()){
////                if (!ChatAnswerUtils.getInstance().isAnswering())
//                hideFloatWindow();
//            }
////            application.startActivity(reStartIntent);
//
////            Intent intent = getIntent();
////            if (targetActivity != null&&intent!=null) {
////                application.startActivity(intent);
////            }else {
////                hideFloatWindow();
////            }
//        }
//    };

//    private Intent getIntent(){
//        if (targetActivity!=null){
//            Intent intent=new Intent();
//            intent.setClass(application, targetActivity);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            return intent;
//        }
//        return null;
//    }

    //    private boolean moveToFront() {
//        return chatActivity.moveToFront(targetActivity);
//    }
    public boolean moveToFront() {
        ActivityManager manager = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            List<ActivityManager.RunningTaskInfo> recentTasks = manager.getRunningTasks(Integer.MAX_VALUE);
            if (recentTasks != null && !recentTasks.isEmpty()) {
                for (ActivityManager.RunningTaskInfo taskInfo : recentTasks) {
                    ComponentName cpn = taskInfo.baseActivity;
                    if (null != cpn && TextUtils.equals(targetActivity.getName(), cpn.getClassName())) {
                        manager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
