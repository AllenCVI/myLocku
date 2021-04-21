package com.lockulockme.lockuchat.attach;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

import org.json.JSONObject;

public abstract class SelfAttachment implements MsgAttachment {

    protected int type;

    public int getType() {
        return type;
    }

    public SelfAttachment(int type) {
        this.type = type;
    }

    public void unPaleJson(JSONObject data) {
        if (data != null) {
            unPaleData(data);
        }
    }

    @Override
    public String toJson(boolean send) {
        return SelfAttachParser.resolveData(type, paleData());
    }

    protected abstract void unPaleData(JSONObject data);

    protected abstract JSONObject paleData();
}
