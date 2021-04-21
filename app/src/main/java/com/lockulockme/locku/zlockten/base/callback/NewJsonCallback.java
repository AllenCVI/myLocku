package com.lockulockme.locku.zlockten.base.callback;


import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lockulockme.locku.base.beans.BaseEntity;
import com.lockulockme.locku.zlockten.common.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by xuenhao on 2018/3/15.
 */

public abstract class NewJsonCallback<T>{

    public NewJsonCallback() {
    }

    public void onStart() {

    }

    public final void onError() {

    }

    public final void onSuccess() {
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    public BaseEntity<T> convertResponse(Response response) {

        //详细自定义的原理和文档，看这里： https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        /*
         * 一般直接 new JsonCallback 会直接用无参构造器，但是无参构造器不能带有Bean类类型，
         * 无参的Bean类类型在泛型T中已传入，所以在这里先判断一下，如果为空，就获取一下。
         */
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
        }
        return null;
    }

    public T dealJSONConvertedResult(T t) {
        return t;
    }

    public boolean needShowError() {
        return false;
    }

    public abstract void onSuc(T response, String msg);

    public void onE(int httpCode, int apiCode, String msg, T response) {

    }

}