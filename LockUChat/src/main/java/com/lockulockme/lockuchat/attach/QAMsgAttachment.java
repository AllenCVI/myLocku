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

public class QAMsgAttachment extends SelfAttachment {

    public static final int TYPE = 100;

    private String question;
    private List<QAAnswer> answers;

    public QAMsgAttachment() {
        super(TYPE);
    }

    @Override
    protected void unPaleData(JSONObject data) {
        try {
            this.question = data.getString("question");
            JSONArray jsonArray=data.getJSONArray("answers");
            this.answers = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<QAAnswer>>() {}.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject paleData() {
        JSONObject data = new JSONObject();
        Map<String,Object> map=new HashMap<>();
        map.put("question",question);
        map.put("answers",answers);
        String string = new Gson().toJson(map);
        try {
            data=new JSONObject(string);
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return data;
        }
    }

    public String getQuestion() {
        return question;
    }

    public List<QAAnswer> getAnswers() {
        return answers;
    }

}
