package com.lockulockme.lockuchat.ui;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.attach.NotifyMsgAttachment;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

public class NotifyMsgManager {
    private Class<?> splashActivity;
    private Application app;
    private NotificationManager notificationManager;
    private static final int INCOMING_MSG_NOTIFY_ID = 1002201;
    protected int notificationId;

    public void init(Application app,Class<?> splashActivity){
        this.app = app;
        this.splashActivity = splashActivity;
        notificationManager = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static class InnnerHolder {
        private static final NotifyMsgManager INSTANCE = new NotifyMsgManager();
    }

    public static NotifyMsgManager getInstance() {
        return NotifyMsgManager.InnnerHolder.INSTANCE;
    }

    public void showNotification(IMMessage message) {
        MsgAttachment attachment = message.getAttachment();
        if (attachment != null && attachment instanceof NotifyMsgAttachment){
            NotifyMsgAttachment notifyMsgAttachment = (NotifyMsgAttachment) message.getAttachment();
            Glide.with(app).asBitmap().load(notifyMsgAttachment.getSmallAvatar())
                    .override(80, 80).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    String title = message.getFromNick();
                    String content = message.getPushContent();
                    title = title != null?title: "";
                    content = content != null?content: "";
                    showNotification(message, title, content, resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }

    }

    private void showNotification(IMMessage imMessage, String title, String content, Bitmap icon) {
        Intent splashIntent = getSplashIntent(imMessage);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                app,
                INCOMING_MSG_NOTIFY_ID,
                splashIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = getNotificationBuilder(pendingIntent, title, content, title, R.mipmap.app_icon, true, true);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        notificationBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setLargeIcon(icon);
        Notification notification = notificationBuilder.build();
        notificationId++;
        notificationManager.notify(notificationId, notification);

    }

    private Intent getSplashIntent(IMMessage imMessage) {
        Intent intent = new Intent();
        NotifyMsgAttachment notifyMsgAttachment = (NotifyMsgAttachment) imMessage.getAttachment();
        ArrayList<IMMessage> list = new ArrayList<>();
        if (NotifyMsgAttachment.OPEN_PAGE.equals(notifyMsgAttachment.getGoPage())){
            IMMessage message = MessageBuilder.createEmptyMessage(notifyMsgAttachment.getJumpToImId(), SessionTypeEnum.P2P, System.currentTimeMillis());
            message.setStatus(MsgStatusEnum.read);
            list.add(message);
            intent.putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, list);
        }else {
            list.add(imMessage);
            intent.putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, list);
        }

        intent.setClass(app, splashActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(app, incomingCallChannel);
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
