package com.lockulockme.lockuchat.common;

import com.lockulockme.lockuchat.utils.TransformTextMsgUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.NIMAntiSpamOption;

import java.util.HashMap;
import java.util.Map;

public class SayHiUtils {
    private static void sendHiMsg(IMMessage imMessage, boolean resend, RequestCallback<Void> callback) {
        ExtraUtils.msgAddExtension(imMessage);
        Map<String, Object> extensions = imMessage.getRemoteExtension();
        if (extensions == null) {
            extensions = new HashMap<>();
        }
        extensions.put("sayHi", "1");
        NIMAntiSpamOption spamOption = new NIMAntiSpamOption();
        spamOption.enable = false;
        imMessage.setNIMAntiSpamOption(spamOption);
        imMessage.setRemoteExtension(extensions);
        NIMClient.getService(MsgService.class).sendMessage(imMessage, resend).setCallback(callback);
    }

    public static void sendHiMsg(String sendAccount,String text,String targetLan, boolean resend,  RequestCallback<Void> callback) {
        IMMessage imMessage = MessageBuilder.createTextMessage(sendAccount, SessionTypeEnum.P2P, text);

        TransformTextMsgUtils.getInstance().transformTextMsg(targetLan, imMessage.getContent(), new TransformTextMsgUtils.OnTransformListener() {
            @Override
            public void onSuccess(String result) {
                Map<String, Object> extensions = imMessage.getRemoteExtension();
                if (extensions == null) {
                    extensions = new HashMap<>();
                }
                extensions.put(Extra.TRANSFORM_KEY, result);
                imMessage.setRemoteExtension(extensions);
                sendHiMsg(imMessage, resend, callback);
            }

            @Override
            public void onFailed(String msg) {
                sendHiMsg(imMessage, resend, callback);
            }

        });
    }
}
