package com.lockulockme.lockuchat.common;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.lockulockme.lockuchat.aavg2.nertcvideocall.push.SignalingPushHandler;
import com.lockulockme.lockuchat.utils.ContextHelper;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.mixpush.MixPushMessageHandler;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.Map;

public class LockUMixPushMessageHandler implements MixPushMessageHandler {

    public static final String PAYLOAD_SESSION_ID = "sId";
    public static final String PAYLOAD_SESSION_TYPE = "sType";
    public static final String PAYLOAD_JUMP_TO = "jump";
    public static final String JUMPCHAT="chat";

    private MixPushMessageHandler mixPushMessageHandler = new SignalingPushHandler();

    @Override
    public boolean onNotificationClicked(Context context, Map<String, String> payload) {
        if (mixPushMessageHandler.onNotificationClicked(context, payload)) {
            return true;
        }

        String sessionId = payload.get(PAYLOAD_SESSION_ID);
        String type = payload.get(PAYLOAD_SESSION_TYPE);
        //
        if (sessionId != null && type != null) {
            int typeValue = Integer.valueOf(type);
            ArrayList<IMMessage> imMessages = new ArrayList<>();
            IMMessage imMessage = MessageBuilder.createEmptyMessage(sessionId, SessionTypeEnum.typeOfValue(typeValue), 0);
            imMessages.add(imMessage);
            Intent notifyIntent = new Intent();
            notifyIntent.setComponent(initLaunchComponent(context));
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notifyIntent.setAction(Intent.ACTION_VIEW);
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ??????
            notifyIntent.putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, imMessages);

            context.startActivity(notifyIntent);
            return true;
        } else {
            return false;
        }
    }

    private ComponentName initLaunchComponent(Context context) {
        ComponentName launchComponent;
        StatusBarNotificationConfig config = IMLoginHelper.getNotificationConfig();
        Class<? extends Activity> entrance = config.notificationEntrance;
        if (entrance == null) {
            launchComponent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent();
        } else {
            launchComponent = new ComponentName(context, entrance);
        }
        return launchComponent;
    }

    // ?????????????????? Notification ?????????????????????????????????????????? Notification??????????????????????????????????????????????????????????????????????????????
    @Override
    public boolean cleanMixPushNotifications(int pushType) {
        Context context = ContextHelper.getInstance().getContext();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
        }
        return true;
    }
}