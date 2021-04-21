package com.lockulockme.lockuchat.attach;

import com.lockulockme.lockuchat.utils.LogHelper;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

import org.json.JSONException;
import org.json.JSONObject;

public class SelfAttachParser implements MsgAttachmentParser {

    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";

    @Override
    public MsgAttachment parse(String json) {
        SelfAttachment attachment = null;
        try {
            JSONObject data = new JSONObject(json);
            int type = data.getInt(KEY_TYPE);
            JSONObject messageData = data.getJSONObject(KEY_DATA);
            switch (type) {
                case QAMsgAttachment.TYPE:
                    attachment = new QAMsgAttachment();
                    break;
                case QAAnswerMsgAttachment.TYPE:
                    attachment = new QAAnswerMsgAttachment();
                    break;
                case StrategyAVMsgAttachment.TYPE:
                    attachment = new StrategyAVMsgAttachment();
                    break;
                case GiftMsgAttachment.TYPE:
                    attachment = new GiftMsgAttachment();
                    break;
                case StrategyImageAttachment.TYPE:
                    attachment = new StrategyImageAttachment();
                    break;
                case NotifyMsgAttachment.TYPE:
                    attachment = new NotifyMsgAttachment();
                    break;
                case AskGiftsAttachment.TYPE:
                    attachment = new AskGiftsAttachment();
                    break;
            }

            if (attachment != null) {
                attachment.unPaleJson(messageData);
            }
        } catch (Exception e) {

        }

        return attachment;
    }

    public static String resolveData(int type, JSONObject data) {
        JSONObject object = new JSONObject();
        try {
            object.put(KEY_TYPE,type);
            if (data != null) {
                object.put(KEY_DATA,data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
