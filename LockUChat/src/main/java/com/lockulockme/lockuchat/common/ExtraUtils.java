package com.lockulockme.lockuchat.common;

import android.text.TextUtils;

import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.data.SelfDataUtils;
import com.lockulockme.lockuchat.utils.im.MsgDigest;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.HashMap;
import java.util.Map;

public class ExtraUtils {
    public static void msgAddExtension(IMMessage imMessage) {
        Map<String, Object> extensions = imMessage.getRemoteExtension();
        if (extensions == null) {
            extensions = new HashMap<>();
        }
        User localLoginInfo = SelfDataUtils.getInstance().getSelfData().getSelfUser();
        int isVip = SelfDataUtils.getInstance().getSelfData().getSelfIsVip4Sync();
        if (isVip == 1) {
            extensions.put("vip", "1");
        } else {
            extensions.put("vip", "2");
        }
        extensions.put("gender", localLoginInfo.userSex);
        extensions.put("userType", "1");
        imMessage.setRemoteExtension(extensions);


        String content = getPushContent(imMessage);
        Map<String, Object> payload = getPushPayload(imMessage);
        if (!TextUtils.isEmpty(content)) {
            imMessage.setPushContent(content);
        }
        if (payload != null) {
            imMessage.setPushPayload(payload);
        }
    }

    private static String getPushContent(IMMessage message) {
        return MsgDigest.getMsgDigest(message);
    }
    private static Map<String, Object> getPushPayload(IMMessage message) {
        if (message == null) {
            return null;
        }
        HashMap<String, Object> payload = new HashMap<>();
        int sessionType = message.getSessionType().getValue();
        payload.put(LockUMixPushMessageHandler.PAYLOAD_SESSION_TYPE, sessionType);
        payload.put(LockUMixPushMessageHandler.PAYLOAD_JUMP_TO, LockUMixPushMessageHandler.JUMPCHAT);
        String sessionId  = message.getFromAccount();
        if (!TextUtils.isEmpty(sessionId)) {
            payload.put(LockUMixPushMessageHandler.PAYLOAD_SESSION_ID, sessionId);
        }

        return payload;
    }
}
