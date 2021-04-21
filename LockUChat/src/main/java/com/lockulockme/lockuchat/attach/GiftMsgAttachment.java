package com.lockulockme.lockuchat.attach;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiftMsgAttachment extends SelfAttachment {

    public static final int  TYPE = 102;

    private String giftUrl;
    private String giftName;
    private long giftId;
    private int giftPrice;

    private String giftDesc;
    private long integral;
    private String anchorGiftName;
    private String anchorGiftDesc;

    public GiftMsgAttachment() {
        super(TYPE);
    }

    public GiftMsgAttachment(String giftUrl, String giftName, long giftId, int giftPrice,
                             String giftDesc, long integral, String anchorGiftName, String anchorGiftDesc) {
        super(TYPE);
        this.giftUrl = giftUrl;
        this.giftName = giftName;
        this.giftId = giftId;
        this.giftPrice = giftPrice;
        this.giftDesc = giftDesc;
        this.integral = integral;
        this.anchorGiftName = anchorGiftName;
        this.anchorGiftDesc = anchorGiftDesc;
    }

    @Override
    protected void unPaleData(JSONObject data) {
        try {
            this.giftUrl = data.getString("giftUrl");
            this.giftName = data.getString("giftName");
            this.giftId = data.getLong("giftId");
            this.giftPrice = data.getInt("giftPrice");

            this.giftDesc = data.getString("giftDesc");
            this.integral = data.getLong("integral");
            this.anchorGiftName = data.getString("anchorGiftName");
            this.anchorGiftDesc = data.getString("anchorGiftDesc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject paleData() {
        JSONObject data = new JSONObject();
        Map<String,Object> map=new HashMap<>();
        map.put("giftUrl",giftUrl);
        map.put("giftName",giftName);
        map.put("giftId",giftId);
        map.put("giftPrice",giftPrice);

        map.put("giftDesc",giftDesc);
        map.put("integral",integral);
        map.put("anchorGiftName",anchorGiftName);
        map.put("anchorGiftDesc",anchorGiftDesc);
        String string = new Gson().toJson(map);
        try {
            data=new JSONObject(string);
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return data;
        }
    }

    public String getGiftUrl() {
        return giftUrl;
    }

    public String getGiftName() {
        return giftName;
    }

    public long getGiftId() {
        return giftId;
    }

    public int getGiftPrice() {
        return giftPrice;
    }

    public String getGiftDesc() {
        return giftDesc;
    }

    public long getIntegral() {
        return integral;
    }

    public String getAnchorGiftName() {
        return anchorGiftName;
    }

    public String getAnchorGiftDesc() {
        return anchorGiftDesc;
    }
}
