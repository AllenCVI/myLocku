package com.lockulockme.lockuchat.utils.im;

import com.lockulockme.lockuchat.attach.SelfAttachParser;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;

public class SelfAttachmentUtils {
    public static void registerSelfAttachmentParser(){
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new SelfAttachParser());
    }
}
