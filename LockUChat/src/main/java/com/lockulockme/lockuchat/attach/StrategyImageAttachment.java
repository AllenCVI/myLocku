package com.lockulockme.lockuchat.attach;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StrategyImageAttachment extends SelfAttachment {

    public static final int  TYPE = 104;

    private String image;

    public StrategyImageAttachment() {
        super(TYPE);
    }

    @Override
    protected void unPaleData(JSONObject data) {
        try {
            this.image = data.getString("image");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject paleData() {
        JSONObject data = new JSONObject();
        Map<String,Object> map=new HashMap<>();
        map.put("image",image);
        String string = new Gson().toJson(map);
        try {
            data=new JSONObject(string);
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return data;
        }
    }

    public String getImage() {
        return image;
    }
}
