package com.lockulockme.lockuchat.ui;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.service.CallServiceInstance;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.ChatAnswerUtils;
import com.lockulockme.lockuchat.aavg2.ui.ui.AVChatSoundPlayer;
import com.lockulockme.lockuchat.bean.HangUp;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.bean.rsp.UserStrategyPermissionRsp;
import com.lockulockme.lockuchat.common.HangUpHelper;
import com.lockulockme.lockuchat.databinding.ActivityVideoStrategyBinding;
import com.lockulockme.lockuchat.event.EventSubscriber;
import com.lockulockme.lockuchat.http.DownloadListener;
import com.lockulockme.lockuchat.http.NetDataUtils;
import com.lockulockme.lockuchat.http.UserBeanCacheUtils;
import com.lockulockme.lockuchat.http.VipDiamondsHelper;
import com.lockulockme.lockuchat.router.GoNeedUIUtils;
import com.lockulockme.lockuchat.utils.ImageHelper;
import com.lockulockme.lockuchat.utils.LogHelper;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.Jzvd;

public class VideoCustomCallActivity extends BaseActivity {
    private SurfaceHolder surfaceHolder;
    private final String TAG = "VideoCustomCall";
    private boolean isSound = true;//是否有声音 默认有声音
    private ActivityVideoStrategyBinding binding;
    private CameraHelper cameraHelper;
    static String VIDEO_URL_KEY = "intent_key_video_url";
    static String VIDEO_DURATION_KEY = "intent_key_video_duration";
    static String USER_BEAN_KEY = "intent_key_user_bean";
    private String videoUrl = "";
    private int videoDuration = 0;
    private User user;
    private final int ANSWER_TYPE = 1;
    private final int VIDEO_TYPE = 2;
    private Timer answerTimer, videoTimer;
    private AnswerTimerTask answerTimerTask;
    private VideoTimerTask videoTimerTask;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Activity activity;
    private boolean check = false;

    public static void StartMe(Context context) {
        context.startActivity(new Intent(context, VideoCustomCallActivity.class));
    }

    public static void StartMe(String imId, Intent intent, String url, int duration, onIntentListener onIntentListener) {
        List<String> ids = Arrays.asList(imId);
        UserBeanCacheUtils.getInstance().getNetworkUsers(new Object(),ids, new UserBeanCacheUtils.OnGetUsersLitener() {
            @Override
            public void onGetUsers(List<User> userList) {
                if (userList != null && userList.size() > 0) {
                    NetDataUtils.getInstance().getNetData().downloadVideo(url, new DownloadListener() {
                        @Override
                        public void onSuccess(String path) {
                            intent.putExtra(USER_BEAN_KEY, new Gson().toJson(userList.get(0)));
                            intent.putExtra(VIDEO_URL_KEY, path);
                            intent.putExtra(VIDEO_DURATION_KEY, duration);
                            onIntentListener.onIntent(intent);
                        }

                        @Override
                        public void onProgress(float progress) {
                        }

                        @Override
                        public void onFailed() {
                            intent.putExtra(USER_BEAN_KEY, new Gson().toJson(userList.get(0)));
                            intent.putExtra(VIDEO_URL_KEY, url);
                            intent.putExtra(VIDEO_DURATION_KEY, duration);
                            onIntentListener.onIntent(intent);
                        }
                    });
                }
            }

            @Override
            public void onGetFailed() {
            }
        });
    }


    public interface onIntentListener {
        void onIntent(Intent it);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        CallServiceInstance.getInstance().cancelNotification();
        binding = ActivityVideoStrategyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        getIntentData();
        initView();
        initCamera();
        initUserData();
        setLayoutVisible(ANSWER_TYPE);
        ChatAnswerUtils.getInstance().setAnswering(true);
        observeHangUpEvent();
    }

    private void observeHangUpEvent() {
        HangUpHelper.getInstance().observeHangUp(new EventSubscriber<HangUp>() {
            @Override
            public void onEvent(HangUp hangUp) {
                onHangUpEvent(hangUp);
            }
        });
    }

    public void onHangUpEvent(HangUp event) {
        AVChatSoundPlayer.instance().stop();
        finish();
    }

    private void getIntentData() {
        videoUrl = getIntent().getStringExtra(VIDEO_URL_KEY);
        videoDuration = getIntent().getIntExtra(VIDEO_DURATION_KEY, 15);
        if (!TextUtils.isEmpty(getIntent().getStringExtra(USER_BEAN_KEY))) {
            user = new Gson().fromJson(getIntent().getStringExtra(USER_BEAN_KEY), User.class);
        }
        if (videoDuration > 15) {
            videoDuration = 15;
        }
        binding.tvLimitTime.setText(String.format(getString(R.string.video_strategy_right), videoDuration + "S"));
        binding.tvWatchTime.setText(String.valueOf(videoDuration));
    }

    private void initCamera() {
        surfaceHolder = binding.surfaceView.getHolder();
        cameraHelper = new CameraHelper();
        cameraHelper.setPreviewDisplay(surfaceHolder);
    }

    private void initUserData() {
        if (user == null)
            return;
        ImageHelper.intoIV4Circle(binding.ivAvatar, user.smallUserIcon, R.mipmap.icon_placeholder_user, R.mipmap.icon_placeholder_user);
        ImageHelper.intoIV4Circle(binding.ivCallUser, user.middleUserIcon, R.mipmap.icon_placeholder_user, R.mipmap.icon_placeholder_user);
        ImageHelper.intoIV4Circle(binding.ivCountry, user.countryUrl, R.mipmap.country_placeholder, R.mipmap.country_placeholder);
        ImageHelper.intoIV4Circle(binding.ivCountry2, user.countryUrl, R.mipmap.country_placeholder, R.mipmap.country_placeholder);
        binding.tvName.setText(user.nick);
        binding.tvName2.setText(user.nick);

    }

    private void initView() {
        binding.ivAudioControl.setOnClickListener(v -> {
            if (isSound) {
                binding.ivAudioControl.setImageResource(R.drawable.img_voice_off);
                isSound = false;
            } else {
                binding.ivAudioControl.setImageResource(R.drawable.img_voice_on);
                isSound = true;
            }
        });
        binding.ivCameraSwitch.setOnClickListener(v -> {
            cameraHelper.switchCamera();
        });
        //挂断
        binding.ivCancel.setOnClickListener(v -> {
            finish();
        });
        //接受视频
        binding.ivVideoAccept.setOnClickListener(v -> {
            checkUserPermission();
//            setLayoutVisible(VIDEO_TYPE);
        });
        //拒绝视频
        binding.ivVideoCancel.setOnClickListener(v -> {
            finish();
        });
        //vip
        binding.ivVip.setOnClickListener(v -> {
            GoNeedUIUtils.getInstance().goVipPage(activity);
        });
    }

    private void setLayoutVisible(int type) {
        if (type == VIDEO_TYPE) {
            binding.rlVideo.setVisibility(View.VISIBLE);
            binding.rlAnswer.setVisibility(View.GONE);
            AVChatSoundPlayer.instance().stop();
            releaseAnswerTimer();
            startVideoTimer();
            startVipAnimator();
            if (!TextUtils.isEmpty(videoUrl)) {
                binding.player.startVideo(videoUrl);
            }
            LogHelper.e(TAG, "------------------- videoUrl:" + videoUrl);
        } else if (type == ANSWER_TYPE) {
            binding.rlVideo.setVisibility(View.GONE);
            binding.rlAnswer.setVisibility(View.VISIBLE);
            AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.RING);
            startAnswerTimer();
        }
    }

    private void checkUserPermission() {
        if (check) return;
        check = true;
        VipDiamondsHelper.getInstance().userAcceptVideoStrategy(VideoCustomCallActivity.this, new VipDiamondsHelper.onUserPermission() {
            @Override
            public void onSuccess(UserStrategyPermissionRsp rsp) {
                LogHelper.e(TAG, "userAcceptVideoStrategy onSuccess: rsp.flag：" + rsp.flag);
                check = false;
                if (!rsp.flag) {
                    GoNeedUIUtils.getInstance().goVipPage(activity);
                    return;
                }
                setVideoLayoutAndAddStrategyNumber();
            }

            @Override
            public void onFailed(int code) {
                check = false;
            }
        });
    }

    private void setVideoLayoutAndAddStrategyNumber() {
        setLayoutVisible(VIDEO_TYPE);
        VipDiamondsHelper.getInstance().userAddVideoStrategyNum(VideoCustomCallActivity.this, new VipDiamondsHelper.OnReduceListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailed(int code) {
            }
        });
    }

    private void startVipAnimator() {
        ObjectAnimator nope = nope(binding.ivVip);
        nope.setRepeatCount(ValueAnimator.INFINITE);
        nope.setInterpolator(new LinearInterpolator());
        nope.start();
    }

    private void releaseAllTimer() {
        if (answerTimer != null) {
            answerTimer.cancel();
            answerTimer = null;
        }
        if (answerTimerTask != null) {
            answerTimerTask.cancel();
            answerTimerTask = null;
        }
        if (videoTimer != null) {
            videoTimer.cancel();
            videoTimer = null;
        }
        if (videoTimerTask != null) {
            videoTimerTask.cancel();
            videoTimerTask = null;
        }
    }

    private void releaseAnswerTimer() {
        if (answerTimer != null) {
            answerTimer.cancel();
        }
        if (answerTimerTask != null) {
            answerTimerTask.cancel();
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void deleteFile() {
        try {
            File file = new File(videoUrl);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        HangUpHelper.getInstance().clearHungup();
        handler.removeCallbacksAndMessages(null);
        CallServiceInstance.getInstance().cancelNotification();
        ChatAnswerUtils.getInstance().setAnswering(false);
        Jzvd.releaseAllVideos();
        if (cameraHelper != null) {
            cameraHelper.release();
        }
        AVChatSoundPlayer.instance().stop();
        releaseAllTimer();
        deleteFile();
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    public void startAnswerTimer() {
        if (answerTimer != null) {
            answerTimer.cancel();
        }
        if (answerTimerTask != null) {
            answerTimerTask.cancel();
        }
        answerTimer = new Timer();
        answerTimerTask = new AnswerTimerTask();
        answerTimer.schedule(answerTimerTask, 0, 1000);
    }

    public void startVideoTimer() {
        if (videoTimer != null) {
            videoTimer.cancel();
        }
        if (videoTimerTask != null) {
            videoTimerTask.cancel();
        }
        videoTimer = new Timer();
        videoTimerTask = new VideoTimerTask();
        videoTimer.schedule(videoTimerTask, 1000, 1000);
    }

    class AnswerTimerTask extends TimerTask {

        private int answerTime = 15;

        @Override
        public void run() {
            if (answerTime == 0) {
                AVChatSoundPlayer.instance().stop();
                finish();
            }
            answerTime--;
        }
    }

    class VideoTimerTask extends TimerTask {

        private int videoTime = 0;

        @Override
        public void run() {
            videoDuration--;
            videoTime++;
            if (videoDuration == 0) {
                GoNeedUIUtils.getInstance().goVipPage(activity);
                AVChatSoundPlayer.instance().stop();
                finish();
                return;
            }
            handler.post(() -> {
                binding.tvVideoTime.setText(timeParse(videoTime) + "");
                binding.tvWatchTime.setText(String.valueOf(videoDuration));
            });
        }
    }

    private String timeParse(long duration) {
        String time = "";
        long minute = duration / 60;
        long seconds = duration % 60;
        long second = Math.round((float) seconds);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }

    private ObjectAnimator nope(View view) {
        int delta = 8;
        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.10f, -delta),
                Keyframe.ofFloat(.26f, delta),
                Keyframe.ofFloat(.42f, -delta),
                Keyframe.ofFloat(.58f, delta),
                Keyframe.ofFloat(.74f, -delta),
                Keyframe.ofFloat(.90f, delta),
                Keyframe.ofFloat(1f, 0f)
        );
        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).
                setDuration(500);
    }


}
