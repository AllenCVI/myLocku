package com.lockulockme.lockuchat.utils.im;

import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.attach.AskGiftsAttachment;
import com.lockulockme.lockuchat.attach.GiftMsgAttachment;
import com.lockulockme.lockuchat.attach.QAAnswerMsgAttachment;
import com.lockulockme.lockuchat.attach.QAMsgAttachment;
import com.lockulockme.lockuchat.attach.StrategyAVMsgAttachment;
import com.lockulockme.lockuchat.attach.StrategyImageAttachment;
import com.lockulockme.lockuchat.utils.ResouseUtils;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.NetCallAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public class MsgDigest {
    public static String getMsgDigest(IMMessage msg) {
        switch (msg.getMsgType()) {
            case text:
            case tip:
                return msg.getContent();
            case image:
                return ResouseUtils.getResouseString(com.lockulockme.lockuchat.R.string.msg_picture_message);
            case video:
                return ResouseUtils.getResouseString(com.lockulockme.lockuchat.R.string.msg_video_message);
            case audio:
                return ResouseUtils.getResouseString(com.lockulockme.lockuchat.R.string.msg_audio_message);
            case notification:
                return "";
            default:
                String digest = ResouseUtils.getResouseString(com.lockulockme.lockuchat.R.string.msg_unknown);
                MsgAttachment attachment=msg.getAttachment();
                if (attachment instanceof QAMsgAttachment) {
                    return ((QAMsgAttachment) attachment).getQuestion();
                } else if (attachment instanceof QAAnswerMsgAttachment) {
                    return ((QAAnswerMsgAttachment) attachment).getAnswer();
                } else if (attachment instanceof GiftMsgAttachment) {
                    return (ResouseUtils.getResouseString(com.lockulockme.lockuchat.R.string.msg_gift));
                } else if (attachment instanceof AskGiftsAttachment) {
                    return (ResouseUtils.getResouseString(R.string.str_msg_ask_gift));
                } else if (attachment instanceof StrategyAVMsgAttachment) {
                    StrategyAVMsgAttachment customAVChatCallMsgAttachment = (StrategyAVMsgAttachment) attachment;
                    if (customAVChatCallMsgAttachment.getCallType() == StrategyAVMsgAttachment.AUDIO_CALL_TYPE) {
                        return (ResouseUtils.getResouseString(com.lockulockme.lockuchat.R.string.msg_voice_chat));
                    } else {
                        return (ResouseUtils.getResouseString(com.lockulockme.lockuchat.R.string.msg_video_chat));
                    }
                } else if (attachment instanceof StrategyImageAttachment) {
                    return ResouseUtils.getResouseString(com.lockulockme.lockuchat.R.string.msg_picture_message);
                } else if (attachment instanceof NetCallAttachment) {
                    NetCallAttachment netCallAttachment = (NetCallAttachment) attachment;
                    if (netCallAttachment.getType() == ChannelType.AUDIO.getValue()) {
                        return (ResouseUtils.getResouseString(com.lockulockme.lockuchat.R.string.msg_voice_chat));
                    } else {
                        return (ResouseUtils.getResouseString(com.lockulockme.lockuchat.R.string.msg_video_chat));
                    }
                } else if (msg.getSessionType() == SessionTypeEnum.Ysf) {
                    return msg.getContent();
                }
                return digest;
        }
    }
}
