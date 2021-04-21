package com.lockulockme.lockuchat.aavg2.nertcvideocall.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
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

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.ui.FloatNotificationManager;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.NERTCCallingDelegate;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.NERTCVideoCall;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.UIService;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.CallParams;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.CustomInfo;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CallServiceInstance implements Application.ActivityLifecycleCallbacks {
    private boolean isSmallScreenQuested;
    private Activity topActivity;
    private List<Class<?>> filterActivityList = new ArrayList<>();
    public void setFilterActivity(List<Class<?>> filterActivity) {
        this.filterActivityList.addAll(filterActivity);
    }
    private CallServiceInstance() {
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

    private static class InstanceHolder {
        private static CallServiceInstance INSTANCE = new CallServiceInstance();
    }
    public static CallServiceInstance getInstance() {
        return CallServiceInstance.InstanceHolder.INSTANCE;
    }

    public void setContext(Context context, UIService uiService) {
        this.context = context;
        this.uiService = uiService;
        init();
    }

    public void setSmallScreenQuested(boolean smallScreenQuested) {
        isSmallScreenQuested = smallScreenQuested;
    }

    public boolean isSmallScreenQuested() {
        return isSmallScreenQuested;
    }

    private Context context;
    public static final int INCOMING_CALL_NOTIFY_ID = 1025;

    //UI相关注册
    private static UIService uiService;

    private NotificationManager notificationManager;

    private Notification incomingCallNotification;

    private NERTCVideoCall nertcVideoCall;

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    private final NERTCCallingDelegate callingDelegate = new NERTCCallingDelegate() {

        @Override
        public void onError(int errorCode, String errorMsg) {
        }

        @Override
        public void onInvited(InvitedEvent invitedEvent) {
            if (topActivity != null){
                if (!FloatNotificationManager.getInstance().isBackground()){
                    for (Class<?> aClass : filterActivityList) {
                        if (aClass == topActivity.getClass()) {
                            return;
                        }
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && FloatNotificationManager.getInstance().isBackground()) {
                Intent intent=buildIncomingCallingNotification(invitedEvent);
//                if (!isSmallScreenQuested){
                    notificationManager.notify(INCOMING_CALL_NOTIFY_ID, incomingCallNotification);
//                }
                if (FloatNotificationManager.getInstance().isBackground())
                moveToFront();
                if (intent != null){
                    context.startActivity(intent);
                }
            } else {
                //直接呼起
                Intent intent = initIntent(invitedEvent);
                if (intent != null){
                    context.startActivity(intent);
                }
            }

        }


        @Override
        public void onUserEnter(long uid, String accId) {
        }

        @Override
        public void onCallEnd(String userId) {
        }

        @Override
        public void onUserLeave(String accountId) {
            cancelNotification();
        }


        @Override
        public void onRejectByUserId(String userId) {
        }


        @Override
        public void onUserBusy(String userId) {
        }

        @Override
        public void onCancelByUserId(String userId) {
            cancelNotification();
        }


        @Override
        public void onCameraAvailable(long userId, boolean isVideoAvailable) {
        }

        @Override
        public void onAudioAvailable(long userId, boolean isVideoAvailable) {
        }

        @Override
        public void timeOut() {
        }

        @Override
        public void onGetChannelId(long channelId) {

        }

        @Override
        public void onGetIMChannelId(String channelId) {

        }

        @Override
        public void onUserDisconnect(String userId) {

        }

        @Override
        public void onUserNetworkQuality(NERtcNetworkQualityInfo[] stats) {

        }

    };

    public void cancelNotification() {
        if (incomingCallNotification != null) {
            notificationManager.cancel(INCOMING_CALL_NOTIFY_ID);
        }
    }

    /**
     * 跳转到接通页面的intent
     *
     * @param invitedEvent
     * @return
     */
    private Intent initIntent(InvitedEvent invitedEvent) {
        CustomInfo customInfo = GsonUtils.fromJson(invitedEvent.getCustomInfo(), CustomInfo.class);
        if (customInfo != null && uiService != null) {
            if (customInfo.callType == Utils.ONE_TO_ONE_CALL) {
                Intent intent = new Intent();

                if (invitedEvent.getChannelBaseInfo().getType() == ChannelType.VIDEO) {
                    intent.setClassName(context, uiService.getOneToOneVideoChat());
                } else if (invitedEvent.getChannelBaseInfo().getType() == ChannelType.AUDIO) {
                    intent.setClassName(context, uiService.getOneToOneAudioChat());
                } else {
                    return null;
                }

                intent.putExtra(CallParams.INVENT_REQUEST_ID, invitedEvent.getRequestId());
                intent.putExtra(CallParams.INVENT_CHANNEL_ID, invitedEvent.getChannelBaseInfo().getChannelId());
                intent.putExtra(CallParams.INVENT_FROM_ACCOUNT_ID, invitedEvent.getFromAccountId());
                intent.putExtra(CallParams.INVENT_CALL_RECEIVED, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                return intent;
            } else if (customInfo.callType == Utils.GROUP_CALL) {
                Intent intentTeam = new Intent();
                customInfo.callUserList.add(invitedEvent.getFromAccountId());
                intentTeam.setClassName(context, uiService.getGroupVideoChat());
                intentTeam.putExtra(CallParams.INVENT_USER_IDS, customInfo.callUserList);
                intentTeam.putExtra(CallParams.INVENT_REQUEST_ID, invitedEvent.getRequestId());
                intentTeam.putExtra(CallParams.INVENT_CHANNEL_ID, invitedEvent.getChannelBaseInfo().getChannelId());
                intentTeam.putExtra(CallParams.INVENT_FROM_ACCOUNT_ID, invitedEvent.getFromAccountId());
                intentTeam.putExtra(CallParams.INVENT_CALL_RECEIVED, true);
                intentTeam.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                return intentTeam;
            }
        }
        return null;
    }

    /**
     * 删除通知的intent
     *
     * @param invitedEvent
     * @return
     */
    private PendingIntent getDeleteIntent(InvitedEvent invitedEvent) {
        CustomInfo customInfo = GsonUtils.fromJson(invitedEvent.getCustomInfo(), CustomInfo.class);
        if (customInfo != null && uiService != null) {
            Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
            intent.setAction("notification_cancelled");
            intent.putExtra(CallParams.INVENT_REQUEST_ID, invitedEvent.getRequestId());
            intent.putExtra(CallParams.INVENT_CHANNEL_ID, invitedEvent.getChannelBaseInfo().getChannelId());
            intent.putExtra(CallParams.INVENT_FROM_ACCOUNT_ID, invitedEvent.getFromAccountId());
            PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, 0,
                    intent, PendingIntent.FLAG_ONE_SHOT);
            return pendingIntentCancel;

        }
        return null;
    }

    private Intent buildIncomingCallingNotification(InvitedEvent invitedEvent) {
        String displayName = invitedEvent.getRequestId();
        Intent notifyIntent = initIntent(invitedEvent);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                INCOMING_CALL_NOTIFY_ID,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String title = context.getString(R.string.new_call);
        String content = displayName + ":" + context.getString(R.string.net_call);
        String tickerText = displayName + " " + title;
        int iconId = R.mipmap.app_icon;

        NotificationCompat.Builder incomingCallNotificationBuilder = makeIncomingCallNotificationBuilder(pendingIntent, title, content, tickerText, iconId, true, true);
        incomingCallNotificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        incomingCallNotificationBuilder.setCategory(NotificationCompat.CATEGORY_CALL);
        incomingCallNotificationBuilder.setFullScreenIntent(pendingIntent, true);
        incomingCallNotificationBuilder.setAutoCancel(true);
        incomingCallNotificationBuilder.setDeleteIntent(getDeleteIntent(invitedEvent));
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.layout_call);
        incomingCallNotificationBuilder.setContent(views);

        incomingCallNotification = incomingCallNotificationBuilder.build();
        return notifyIntent;
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
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, incomingCallChannel);
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

    public static void start(Context context, UIService uiService) {
        if (ServiceUtils.isServiceRunning(CallServiceInstance.class)) {
            return;
        }
        CallServiceInstance.uiService = uiService;
        Intent starter = new Intent(context, CallServiceInstance.class);
        context.startService(starter);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, CallServiceInstance.class);
        context.stopService(intent);
    }

    public void init() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        initNERTCCall();
    }

    private void initNERTCCall() {
        nertcVideoCall = NERTCVideoCall.sharedInstance();
        nertcVideoCall.addDelegate(callingDelegate);
    }

    public void onDestroy() {
        if (nertcVideoCall != null) {
            nertcVideoCall.removeDelegate(callingDelegate);
        }
        NERTCVideoCall.destroySharedInstance();
    }

    public boolean moveToFront() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            List<ActivityManager.RunningTaskInfo> recentTasks = manager.getRunningTasks(Integer.MAX_VALUE);
            if (recentTasks != null && !recentTasks.isEmpty()) {
                for (ActivityManager.RunningTaskInfo taskInfo : recentTasks) {
                    ComponentName cpn = taskInfo.baseActivity;
                    if (null != cpn && TextUtils.equals(context.getPackageName(), taskInfo.baseActivity.getPackageName())) {
                        manager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
