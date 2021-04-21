package com.lockulockme.lockuchat.attach;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotifyMsgAttachment extends SelfAttachment {
    public static final int TYPE = 105;
    private String systemType;
    private String isShow;
    private String msgContent;
    private String jumpToImId;
    private String goPage;
    private String smallAvatar;
    public static final String SHOW_TYPE_NO_SHOW = "2";
    public static final String OPEN_PAGE = "chat";

    private static final String KEY_SYSTYPE = "sysType";
    private static final String KEY_SHOWTYPE = "showType";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_BUDDYIMID = "buddyImId";
    private static final String KEY_JUMPTO = "jumpTo";
    private static final String KEY_ICON = "icon";


    NotifyMsgAttachment() {
        super(TYPE);
    }

    @Override
    protected void unPaleData(JSONObject data) {
        try {
            this.systemType = data.getString(KEY_SYSTYPE);
            this.isShow = data.getString(KEY_SHOWTYPE);
            this.msgContent = data.getString(KEY_CONTENT);
            this.jumpToImId = data.getString(KEY_BUDDYIMID);
            this.goPage = data.getString(KEY_JUMPTO);
            this.smallAvatar = data.getString(KEY_ICON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected JSONObject paleData() {
        JSONObject data = new JSONObject();
        Map<String,Object> map=new HashMap<>();
        map.put(KEY_SYSTYPE, systemType);
        map.put(KEY_SHOWTYPE, isShow);
        map.put(KEY_CONTENT, msgContent);
        map.put(KEY_BUDDYIMID, jumpToImId);
        map.put(KEY_JUMPTO, goPage);
        map.put(KEY_ICON, smallAvatar);
        String string = new Gson().toJson(map);
        try {
            data=new JSONObject(string);
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return data;
        }
    }

    public String getSystemType() {
        return systemType;
    }

    public String getIsShow() {
        return isShow;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public String getJumpToImId() {
        return jumpToImId;
    }

    public String getGoPage() {
        return goPage;
    }

    public String getSmallAvatar() {
        return smallAvatar;
    }


}