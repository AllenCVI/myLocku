package com.lockulockme.lockuchat.attach;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lockulockme.lockuchat.bean.AskForGifts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AskGiftsAttachment extends SelfAttachment {
    public static final int TYPE = 107;

    private List<AskForGifts> askForGifts;

    public AskGiftsAttachment() {
        super(TYPE);
    }

    @Override
    protected void unPaleData(JSONObject data) {
        try {
            JSONArray jsonArray=data.getJSONArray("giftList");
            this.askForGifts = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<AskForGifts>>() {}.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject paleData() {
        JSONObject data = new JSONObject();
        Map<String,Object> map=new HashMap<>();
        map.put("giftList",askForGifts);
        String string = new Gson().toJson(map);
        try {
            data=new JSONObject(string);
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return data;
        }
    }

    public List<AskForGifts> getAskForGifts() {
        return askForGifts;
    }
}
