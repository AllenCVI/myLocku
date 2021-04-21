package com.lockulockme.lockuchat.adapter.chat;

import com.lockulockme.lockuchat.attach.AskGiftsAttachment;
import com.lockulockme.lockuchat.attach.GiftMsgAttachment;
import com.lockulockme.lockuchat.attach.QAAnswerMsgAttachment;
import com.lockulockme.lockuchat.attach.QAMsgAttachment;
import com.lockulockme.lockuchat.attach.StrategyAVMsgAttachment;
import com.lockulockme.lockuchat.attach.StrategyImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public class MsgType {
    public static final int UNKOWN = 0;
    public static final int TEXT = 1;
    public static final int QA = 2;
    public static final int QA_ANSWER = 3;
    public static final int PICTURE = 4;
    public static final int AUDIO_VIDEO = 5;
    public static final int STRATEGY_AUDIO_VIDEO = 6;
    public static final int VOICE = 7;
    public static final int GIFT = 8;
    public static final int STRATEGY_IMAGE = 9;
    public static final int ASK_4_GIFTS = 10;
    public static final int VIDEO = 11;

    public static int getMsgType(IMMessage message) {
        int msgType = message.getMsgType().getValue();
        if (msgType == MsgTypeEnum.text.getValue()) {
            return TEXT;
        } else if (msgType == MsgTypeEnum.image.getValue()) {
            return PICTURE;
        } else if (msgType == MsgTypeEnum.nrtc_netcall.getValue()) {
            return AUDIO_VIDEO;
        } else if (msgType == MsgTypeEnum.audio.getValue()) {
            return VOICE;
        }else if (msgType == MsgTypeEnum.video.getValue()) {
            return VIDEO;
        }  else if (msgType == MsgTypeEnum.custom.getValue()) {
            int type = UNKOWN;
            MsgAttachment attachment = message.getAttachment();
            if (attachment instanceof QAMsgAttachment) {
                type = QA;
            } else if (attachment instanceof QAAnswerMsgAttachment) {
                type = QA_ANSWER;
            } else if (attachment instanceof StrategyAVMsgAttachment) {
                type = STRATEGY_AUDIO_VIDEO;
            } else if (attachment instanceof GiftMsgAttachment) {
                type = GIFT;
            } else if (attachment instanceof StrategyImageAttachment) {
                type = STRATEGY_IMAGE;
            } else if (attachment instanceof AskGiftsAttachment) {
                type = ASK_4_GIFTS;
            }
            return type;
        } else {
            return UNKOWN;
        }
    }
}
