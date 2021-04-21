package com.lockulockme.lockuchat.utils.im;

import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.data.SelfDataUtils;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.HashMap;
import java.util.Map;

public class IMMsgHelper {
    private static final String VIP_TRUE="1";
    private static final String VIP_FALSE="2";
    public static void storeRemoteExtension(IMMessage imMessage) {
        Map<String, Object> remoteExtension = imMessage.getRemoteExtension();
        if (remoteExtension == null) {
            remoteExtension = new HashMap<>();
        }
        User user = SelfDataUtils.getInstance().getSelfData().getSelfUser();
        int isVip = SelfDataUtils.getInstance().getSelfData().getSelfIsVip4Sync();
        if (isVip == 1) {
            remoteExtension.put("vip", VIP_TRUE);
        } else {
            remoteExtension.put("vip", VIP_FALSE);
        }
        remoteExtension.put("gender", user.userSex);
        remoteExtension.put("userType", "1");
        imMessage.setRemoteExtension(remoteExtension);
    }
}
