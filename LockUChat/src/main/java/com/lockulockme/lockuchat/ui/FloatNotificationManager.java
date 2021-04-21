package com.lockulockme.lockuchat.ui;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.attach.StrategyAVMsgAttachment;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.databinding.FloatNotificationBinding;
import com.lockulockme.lockuchat.http.UserBeanCacheUtils;
import com.lockulockme.lockuchat.http.VipDiamondsHelper;
import com.lockulockme.lockuchat.utils.im.MsgDigest;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FloatNotificationManager implements Application.ActivityLifecycleCallbacks {
    private Activity topActivity;
    private final Object mLock;
    private Class<?> splashActivity;
    private Application application;
    private NotificationManager notificationManager;
    private static final int INCOMING_MSG_NOTIFY_ID = 100221;
    private List<Class<?>> filterActivityList = new ArrayList<>();
    private int acStartedCount = 0;
    private boolean isBackground = false;
    private long lastShowTime = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public boolean isBackground() {
        return isBackground;
    }

    public void setFilterActivity(List<Class<?>> filterActivity) {
        this.filterActivityList.addAll(filterActivity);
    }

    public void setSplashActivity(Class<?> splashActivity) {
        this.splashActivity = splashActivity;
    }

    public void init(Application application) {
        this.application = application;
        notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void registerMsgObserver(boolean isReg){
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(messageReceiverObserver, isReg);
    }

    private final Observer<List<IMMessage>> messageReceiverObserver = new Observer<List<IMMessage>>() {

        @Override
        public void onEvent(List<IMMessage> imMessages) {
            if (imMessages != null) {
                int count = 0;
                for (IMMessage imMessage : imMessages) {
                    if (imMessage.getAttachment() instanceof StrategyAVMsgAttachment){
                        continue;
                    }
                    if (count == 0) {
                        onMsgNotification(imMessage);
                    }
                    count++;
                }

            }
        }
    };

    private void onMsgNotification(final IMMessage imMessage) {
        boolean isCanShow = isCanShow();
        if (!isCanShow){
            return;
        }
        VipDiamondsHelper.getInstance().getVipStatus(this.toString(), new VipDiamondsHelper.OnVipStatusListener() {
            @Override
            public void onSuccess(int status) {
                if (status != 1) {
                    onGetUserBean(imMessage);
                }
            }

            @Override
            public void onFailed() {

            }
        });


    }

    private void onGetUserBean(IMMessage message) {
        List<String> ids = Arrays.asList(message.getSessionId());
        UserBeanCacheUtils.getInstance().getCacheUsers(this,ids, new UserBeanCacheUtils.OnGetUsersLitener() {
            @Override
            public void onGetUsers(List<User> users) {
                if (users != null && users.size() > 0) {
                    onGetUserIcon(users.get(0), message);
                }
            }

            @Override
            public void onGetFailed() {

            }
        });
    }
    private void onGetUserIcon(User user, IMMessage message) {
        Glide.with(application).asFile().load(user.smallUserIcon).into(new CustomTarget<File>() {
            @Override
            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                onShowNotification(message, user.nick,
                        MsgDigest.getMsgDigest(message), resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

    }

    public void onShowNotification(IMMessage message, String title, String content, File headBitmap) {

        long now = System.currentTimeMillis();
        if (now - lastShowTime >= waitFloatNotifyTime) {
            if (isBackground) {
                showAppOutNotification(message, title, content, headBitmap);
            } else {
                if (topActivity == null || topActivity.isDestroyed() ){
                    return;
                }
                for (Class<?> aClass : filterActivityList) {
                    if (aClass == topActivity.getClass()) {
                        return;
                    }
                }
                showToastNotification(message, title, content, headBitmap);
            }
            lastShowTime = now;
        }
    }
    public boolean isCanShow() {
        long now = System.currentTimeMillis();
        return now - lastShowTime >= waitFloatNotifyTime;
    }
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        acStartedCount++;
        //数值从0 变到 1 说明是从后台切到前台
        if (acStartedCount == 1) {
            //从后台切到前台
            isBackground =false;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        topActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        acStartedCount--;
        //数值从1到0说明是从前台切到后台
        if (acStartedCount == 0) {
            //从前台切到后台
            isBackground =true;
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
    private final int showFloatNotifyTime = 5000;
    private final int waitFloatNotifyTime = 10*1000;

    public FloatNotificationManager() {
        mLock = new Object();
    }

    private static class InnnerHolder {
        private static final FloatNotificationManager INSTANCE = new FloatNotificationManager();
    }

    public static FloatNotificationManager getInstance() {
        return FloatNotificationManager.InnnerHolder.INSTANCE;
    }

    public void showToastNotification(IMMessage message, String title,String content, File headBitmap) {
        synchronized (mLock){
            final LayoutInflater inflater = LayoutInflater.from(topActivity);
            FrameLayout contentView = topActivity.findViewById(android.R.id.content);
            FloatNotificationBinding binding = FloatNotificationBinding.inflate(inflater, contentView, false);
            binding.tvContentFloatNotification.setText(content);
            binding.tvTitleFloatNotification.setText(title);
            Glide.with(topActivity).load(headBitmap).apply(new RequestOptions().transform(new CenterCrop(), new CircleCrop()))
                    .override(80, 80).into(binding.ivHeadFloatNotification);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                contentView.removeView(binding.getRoot());
                            }catch (Exception e){

                            }
                        }
                    });
                    Intent splashIntent = getSplashIntent(message);
                    application.startActivity(splashIntent);
                }
            });
            contentView.addView(binding.getRoot());

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) binding.getRoot().getLayoutParams();
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            layoutParams.leftMargin = 20;
            layoutParams.rightMargin = 20;
            binding.getRoot().setLayoutParams(layoutParams);

            Animation loadAnimation = AnimationUtils.loadAnimation(topActivity,
                    R.anim.toast_in);
            loadAnimation.setFillAfter(true);
            binding.getRoot().startAnimation(loadAnimation);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        binding.getRoot().setVisibility(View.GONE);
                        contentView.removeView(binding.getRoot());
                    }catch (Exception e){

                    }
                }
            },showFloatNotifyTime);
        }

    }





    private void showAppOutNotification(IMMessage imMessage, String title, String content, File headBitmap) {
        Intent splashIntent = getSplashIntent(imMessage);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                application,
                INCOMING_MSG_NOTIFY_ID,
                splashIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = getNotificationBuilder(pendingIntent, title, content, title, R.mipmap.app_icon, true, true);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        notificationBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        notificationBuilder.setAutoCancel(true);
        RemoteViews views = new RemoteViews(application.getPackageName(), R.layout.float_notification);
        views.setTextViewText(R.id.tv_title_float_notification, title);
        views.setTextViewText(R.id.tv_content_float_notification, content);

        Glide.with(application).asBitmap().load(headBitmap)
                .apply(new RequestOptions().transform(new CenterCrop(), new CircleCrop()))
                .override(80, 80).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                views.setImageViewBitmap(R.id.iv_head_float_notification, resource);
                notificationBuilder.setContent(views);
                Notification notification = notificationBuilder.build();
                notificationManager.notify(INCOMING_MSG_NOTIFY_ID, notification);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        notificationManager.cancel(INCOMING_MSG_NOTIFY_ID);
                    }
                }, showFloatNotifyTime);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

    }

    private Intent getSplashIntent(IMMessage imMessage) {
        ArrayList<IMMessage> list = new ArrayList<>();
        list.add(imMessage);
        Intent intent = new Intent();
        intent.setClass(application, splashActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, list);

        return intent;
    }

    private NotificationCompat.Builder getNotificationBuilder(PendingIntent pendingIntent, String title, String content, String tickerText,
                                                              int iconId, boolean ring, boolean vibrate) {

        // 唯一的通知通道的id.
        String incomingCallChannel = "msg_notification_007";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //用户可见的通道名称
            String channelName = "in_message_007";
            //通道的重要程度
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(incomingCallChannel, channelName, importance);
            notificationChannel.setDescription("Message");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(application, incomingCallChannel);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(tickerText)
                .setSmallIcon(iconId);
        int defaults = Notification.DEFAULT_LIGHTS;
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (ring) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        builder.setDefaults(defaults);

        return builder;
    }


}
