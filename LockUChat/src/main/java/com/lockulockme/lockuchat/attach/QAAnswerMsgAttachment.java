package com.lockulockme.lockuchat.attach;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lockulockme.lockuchat.bean.QAAnswer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QAAnswerMsgAttachment extends SelfAttachment {

    public static final int TYPE = 101;

    private String answer;
    private List<Integer> ids;

    public QAAnswerMsgAttachment() {
        super(TYPE);
    }

    @Override
    protected void unPaleData(JSONObject data) {
        try {
            this.answer = data.getString("answer");
            JSONArray jsonArray=data.getJSONArray("ids");
            this.ids = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<Integer>>() {}.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject paleData() {
        JSONObject data = new JSONObject();
        Map<String,Object> map=new HashMap<>();
        map.put("answer",answer);
        map.put("ids",ids);
        String string = new Gson().toJson(map);
        try {
            data=new JSONObject(string);
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return data;
        }
    }

    public String getAnswer() {
        return answer;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }
}
