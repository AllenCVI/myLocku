package com.lockulockme.lockuchat.attach;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lockulockme.lockuchat.bean.QAAnswer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrategyAVMsgAttachment extends SelfAttachment {

    public static final int  TYPE = 103;

    public static final int AUDIO_CALL_TYPE = 1;
    public static final int VIDEO_CALL_TYPE = 2;

    private int duration;
    private int callType;
    private String audioUrl;
    private String videoUrl;
    private int contentDuration;
    public StrategyAVMsgAttachment() {
        super(TYPE);
    }

    @Override
    protected void unPaleData(JSONObject data) {
        try {
            this.duration = data.getInt("duration");
            this.callType = data.getInt("callType");
            if (data.has("audioUrl"))
            this.audioUrl = data.getString("audioUrl");
            if (data.has("videoUrl"))
            this.videoUrl = data.getString("videoUrl");
            if (data.has("contentDuration"))
            this.contentDuration = data.getInt("contentDuration");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject paleData() {
        JSONObject data = new JSONObject();
        Map<String,Object> map=new HashMap<>();
        map.put("duration",duration);
        map.put("callType",callType);
        map.put("audioUrl",audioUrl);
        map.put("videoUrl",videoUrl);
        map.put("contentDuration",contentDuration);
        String string = new Gson().toJson(map);
        try {
            data=new JSONObject(string);
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return data;
        }
    }

    public int getDuration() {
        return duration;
    }

    public int getCallType() {
        return callType;
    }


    public String getAudioUrl() {
        return audioUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public int getContentDuration() {
        return contentDuration;
    }

    public boolean isDelayShow(){
        if (!TextUtils.isEmpty(this.videoUrl)){
            return true;
        }else {
            return false;
        }
    }
}
