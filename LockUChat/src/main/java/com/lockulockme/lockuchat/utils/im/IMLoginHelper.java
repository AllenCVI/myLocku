package com.lockulockme.lockuchat.utils.im;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;

import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.impl.UIServiceManager;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.service.CallServiceInstance;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.service.NotificationBroadcastReceiver;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.CallParams;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.ChatAnswerUtils;
import com.lockulockme.lockuchat.attach.StrategyAVMsgAttachment;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.common.EventHelper;
import com.lockulockme.lockuchat.common.HangUpHelper;
import com.lockulockme.lockuchat.bean.HangUpWithChannelId;
import com.lockulockme.lockuchat.common.SingleLiveData;
import com.lockulockme.lockuchat.data.SelfDataUtils;
import com.lockulockme.lockuchat.ui.FloatNotificationManager;
import com.lockulockme.lockuchat.ui.VideoCustomCallActivity;
import com.lockulockme.lockuchat.utils.ContextHelper;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.lockulockme.lockuchat.utils.ResouseUtils;
import com.netease.nimlib.app.AppForegroundWatcherCompat;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class IMLoginHelper implements Application.ActivityLifecycleCallbacks{
    private static final String TAG = "IMLoginHelper";
    private static Handler handler = new Handler(Looper.getMainLooper());
    private boolean isLogined = false;
    private String account = null;
    private SingleLiveData<Integer> unreadMsgLiveData = new SingleLiveData<>();
    private SingleLiveData<StatusCode> onlineStatusLiveData = new SingleLiveData<>();
    private SingleLiveData<LoginSyncStatus> loginSyncDataStatusLiveData = new SingleLiveData<>();
    private Activity topActivity;
    private List<Class<?>> filterActivityList = new ArrayList<>();
    public void setFilterActivity(List<Class<?>> filterActivity) {
        this.filterActivityList.addAll(filterActivity);
    }

    private IMLoginHelper() {

    }

    public void init() {
        observeOnline();
        observeLoginSyncData();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

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

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    private static class InstanceHelper {
        private static IMLoginHelper INSTANCE = new IMLoginHelper();
    }

    public static IMLoginHelper getInstance() {
        return IMLoginHelper.InstanceHelper.INSTANCE;
    }

    public void login(String imId, String token, RequestCallback<LoginInfo> callback) {
        LoginInfo info = new LoginInfo(imId, token, null, 0);
        NIMClient.getService(AuthService.class).login(info).setCallback(callback);
    }

    public void logout() {
        setUnreadNum(0);
        NIMClient.getService(AuthService.class).logout();
    }

    private void observeOnline() {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
                (Observer<StatusCode>) status -> {
                    onlineStatusLiveData.setValue(status);
                    String desc = status.getDesc();
                    LogHelper.e(TAG, "" + status);
                }, true);
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    private void observeLoginSyncData() {
        NIMClient.getService(AuthServiceObserver.class).observeLoginSyncDataStatus((Observer<LoginSyncStatus>) status -> {
            loginSyncDataStatusLiveData.setValue(status);
            if (status == LoginSyncStatus.BEGIN_SYNC) {
                LogHelper.e(TAG, "login sync data begin");
            } else if (status == LoginSyncStatus.SYNC_COMPLETED) {
                LogHelper.e(TAG, "login sync data completed");
                isLogined = true;
            }
        }, true);
    }

    public boolean isLogined() {
        return isLogined;
    }

    public void observeOnlineStatus(@NonNull LifecycleOwner owner, @NonNull androidx.lifecycle.Observer<StatusCode> observer) {
        onlineStatusLiveData.observe(owner, observer);
    }

    public void observeLoginSyncDataStatus(@NonNull LifecycleOwner owner, @NonNull androidx.lifecycle.Observer<LoginSyncStatus> observer) {
        loginSyncDataStatusLiveData.observe(owner, observer);
    }

    public void observeReceiveMessage(boolean isReg) {
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(msgComeInObserver, isReg);//接受观察
    }

    Observer<List<IMMessage>> msgComeInObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            try {
                if (messages != null && !messages.isEmpty()) {
                    handleStrategyAVMsg(messages);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public void handleStrategyAVMsg(List<IMMessage> messages) {
        if (topActivity == null){
            return;
        }
        if (!FloatNotificationManager.getInstance().isBackground()){
            for (Class<?> aClass : filterActivityList) {
                if (aClass == topActivity.getClass()) {
                    return;
                }
            }
        }

        for (IMMessage message : messages) {
            MsgAttachment msgAttachment = message.getAttachment();
            if (msgAttachment != null) {
                if (msgAttachment instanceof StrategyAVMsgAttachment) {
                    StrategyAVMsgAttachment strategyAVMsgAttachment = (StrategyAVMsgAttachment) msgAttachment;
                    if (strategyAVMsgAttachment.getCallType() == StrategyAVMsgAttachment.AUDIO_CALL_TYPE) {
                        startStrategyCall(message, message.getFromAccount(), String.valueOf(ChannelType.AUDIO.getValue()), System.currentTimeMillis() + new Random().nextInt(1000) + "", strategyAVMsgAttachment.getDuration());
                    } else {
                        startStrategyCall(message, message.getFromAccount(), String.valueOf(ChannelType.VIDEO.getValue()), System.currentTimeMillis() + new Random().nextInt(1000) + "", strategyAVMsgAttachment.getDuration());
                    }
                }
            }
        }
    }

    public void startStrategyCall(IMMessage message, String accId, String chanelType, final String channelId, int duration) {
        jumpVideoAudioChat(message, accId, chanelType, channelId);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> CallServiceInstance.getInstance().cancelNotification());
                HangUpHelper.getInstance().updateHangUpWithChannelIdLiveData(new HangUpWithChannelId(channelId));
            }
        }, duration * 1000);
    }

    public void jumpVideoAudioChat(IMMessage msg, String accId, String channelType, String channelId) {
        if (ChatAnswerUtils.getInstance().isAnswering()) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    EventHelper.getInstance().updateStrategyMsg(msg);
                }
            }, 1000);
            return;
        }
        Intent avChatIntent = new Intent();
        String requestId = System.currentTimeMillis() + new Random().nextInt(1000) + "custom_call";
        avChatIntent.putExtra(CallParams.INVENT_REQUEST_ID, requestId);
        avChatIntent.putExtra(CallParams.INVENT_CHANNEL_ID, channelId);
        avChatIntent.putExtra(CallParams.INVENT_FROM_ACCOUNT_ID, accId);
        avChatIntent.putExtra(CallParams.INVENT_STRATEGY, true);
        avChatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean isHasVideo = false;
        StrategyAVMsgAttachment customAVChatCallMsgAttachment = (StrategyAVMsgAttachment) msg.getAttachment();
        if (!TextUtils.isEmpty(customAVChatCallMsgAttachment.getVideoUrl())) {
            isHasVideo = true;
        }
        if (isHasVideo) {
            avChatIntent.setClass(ContextHelper.getInstance().getContext(), VideoCustomCallActivity.class);
        } else {
            if (TextUtils.equals(String.valueOf(ChannelType.AUDIO.getValue()), channelType)) {
                avChatIntent.setClassName(ContextHelper.getInstance().getContext(), UIServiceManager.getInstance().getUiService().getOneToOneAudioChat());
            } else {
                avChatIntent.setClassName(ContextHelper.getInstance().getContext(), UIServiceManager.getInstance().getUiService().getOneToOneVideoChat());
            }
        }

        avChatIntent.putExtra(CallParams.INVENT_CALL_RECEIVED, true);
        if (isHasVideo) {
            if (TextUtils.equals(String.valueOf(ChannelType.VIDEO.getValue()), channelType)) {


                User user = SelfDataUtils.getInstance().getSelfData().getSelfUser();
                final String myImid = user != null ? user.accid : "";
                VideoCustomCallActivity.StartMe(accId, avChatIntent, customAVChatCallMsgAttachment.getVideoUrl(), customAVChatCallMsgAttachment.getContentDuration(), new VideoCustomCallActivity.onIntentListener() {
                    @Override
                    public void onIntent(Intent it) {
                        User user = SelfDataUtils.getInstance().getSelfData().getSelfUser();
                        if (user == null || !TextUtils.equals(myImid, user.accid)) {
                            return;
                        }
                        if (ChatAnswerUtils.getInstance().isAnswering()) {
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    EventHelper.getInstance().updateStrategyMsg(msg);
                                }
                            }, 1000);
                            return;
                        }
                        ChatAnswerUtils.getInstance().setAnswering(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && AppForegroundWatcherCompat.isBackground()) {
                            if (!CallServiceInstance.getInstance().isSmallScreenQuested()) {
                                Notification notification = buildIncomingCallingNotification(avChatIntent, requestId, accId, channelType, channelId);
                                if (CallServiceInstance.getInstance().getNotificationManager() != null)
                                    CallServiceInstance.getInstance().getNotificationManager().notify(CallServiceInstance.INCOMING_CALL_NOTIFY_ID, notification);
                            }
                            ContextHelper.getInstance().getContext().startActivity(avChatIntent);
                        } else {
                            //直接呼起
                            ContextHelper.getInstance().getContext().startActivity(avChatIntent);
                        }
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                EventHelper.getInstance().updateStrategyMsg(msg);
                            }
                        }, 2000);
                    }
                });
            }
        } else {
            ChatAnswerUtils.getInstance().setAnswering(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && AppForegroundWatcherCompat.isBackground()) {
                if (!CallServiceInstance.getInstance().isSmallScreenQuested()) {
                    Notification notification = buildIncomingCallingNotification(avChatIntent, requestId, accId, channelType, channelId);
                    if (CallServiceInstance.getInstance().getNotificationManager() != null)
                        CallServiceInstance.getInstance().getNotificationManager().notify(CallServiceInstance.INCOMING_CALL_NOTIFY_ID, notification);
                }
                ContextHelper.getInstance().getContext().startActivity(avChatIntent);
            } else {
                //直接呼起
                ContextHelper.getInstance().getContext().startActivity(avChatIntent);
            }
        }
    }


    private PendingIntent getDeleteIntent(String requestId, String accId, String channelType, String channelId) {
        Intent intent = new Intent(ContextHelper.getInstance().getContext(), NotificationBroadcastReceiver.class);
        intent.setAction("notification_cancelled");
        intent.putExtra(CallParams.INVENT_REQUEST_ID, requestId);
        intent.putExtra(CallParams.INVENT_CHANNEL_ID, channelId);
        intent.putExtra(CallParams.INVENT_FROM_ACCOUNT_ID, accId);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(ContextHelper.getInstance().getContext(), 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        return pendingIntentCancel;
    }

    private Notification buildIncomingCallingNotification(Intent notifyIntent, String requestId, String accId, String channelType, String channelId) {
        String displayName = requestId;
        PendingIntent pendingIntent = PendingIntent.getActivity(
                ContextHelper.getInstance().getContext(),
                CallServiceInstance.INCOMING_CALL_NOTIFY_ID,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String title = ResouseUtils.getResouseString(R.string.new_call);
        String content = displayName + ":" + ResouseUtils.getResouseString(R.string.net_call);
        String tickerText = displayName + " " + title;
        int iconId = R.mipmap.app_icon;

        NotificationCompat.Builder incomingCallNotificationBuilder = makeIncomingCallNotificationBuilder(pendingIntent, title, content, tickerText, iconId, true, true);
        incomingCallNotificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        incomingCallNotificationBuilder.setCategory(NotificationCompat.CATEGORY_CALL);
        incomingCallNotificationBuilder.setFullScreenIntent(pendingIntent, true);
        incomingCallNotificationBuilder.setAutoCancel(true);
        incomingCallNotificationBuilder.setDeleteIntent(getDeleteIntent(requestId, accId, channelType, channelId));
        RemoteViews views = new RemoteViews(ContextHelper.getInstance().getContext().getPackageName(), R.layout.layout_call);
        incomingCallNotificationBuilder.setContent(views);

        return incomingCallNotificationBuilder.build();
    }

    private NotificationCompat.Builder makeIncomingCallNotificationBuilder(PendingIntent pendingIntent, String title, String content, String tickerText,
                                                                           int iconId, boolean ring, boolean vibrate) {

        // 唯一的通知通道的id.
        String incomingCallChannel = "incoming_call_notification_channel_id_02";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //用户可见的通道名称
            String channelName = "incall_call_channel";
            //通道的重要程度
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(incomingCallChannel, channelName, importance);
            notificationChannel.setDescription("Channel description");
            if (CallServiceInstance.getInstance().getNotificationManager() != null) {
                CallServiceInstance.getInstance().getNotificationManager().createNotificationChannel(notificationChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ContextHelper.getInstance().getContext(), incomingCallChannel);
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

    public void setUnreadNum(int unreadNum) {
        unreadMsgLiveData.setValue(unreadNum);
    }

    public void observeUnreadMsgNum(@NonNull LifecycleOwner owner, @NonNull androidx.lifecycle.Observer<Integer> observer) {
        unreadMsgLiveData.observe(owner, observer);
    }

    private static StatusBarNotificationConfig notificationConfig;

    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        IMLoginHelper.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }
}
