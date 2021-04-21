package com.lockulockme.locku.base.callback;



import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by xuenhao on 2018/3/15.
 */

public abstract class StringCall{

    public StringCall() {
    }

    public String convertResponse(Response response) {
        try {
            if (response.code() != 200) {
                return null;
            }

            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            return body.string();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            response.close();
        }
        return null;
    }

    public void onError(Response response){

    }

    public void onSuccess(Response response){

    }




}