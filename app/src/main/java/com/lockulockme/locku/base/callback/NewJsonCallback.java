package com.lockulockme.locku.base.callback;


import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lockulockme.locku.base.beans.BaseEntity;
import com.lockulockme.locku.common.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class NewJsonCallback<T> {

    public NewJsonCallback() {
    }

    public abstract void onSuc(T response, String msg);

    public void onE(int httpCode, int apiCode, String msg, T response) {

    }



    public BaseEntity<T> convertResponse(Response response) {
        try {
            if (response.code() != 200) {
                return null;
            }

            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }

            Type tClass = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            Type type = new ParameterizedTypeImpl(BaseEntity.class, new Type[]{tClass});

            Gson gson = new Gson();
            JsonReader jsonReader = new JsonReader(body.charStream());
            BaseEntity<T> data = gson.fromJson(jsonReader, type);
            if (data != null) {
                data.data = dealJSONConvertedResult(data.data);
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            response.close();
        }
        return null;
    }



    public T dealJSONConvertedResult(T t) {
        return t;
    }
}