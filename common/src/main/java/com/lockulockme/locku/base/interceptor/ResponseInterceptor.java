package com.lockulockme.locku.base.interceptor;


import com.lockulockme.locku.base.utils.EncryptUtil;
import com.lockulockme.locku.base.utils.MyPasswordUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class ResponseInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request original = chain.request();
        Response originalResponse = chain.proceed(original);
        int code = originalResponse.code();
        if (code == 200) {
            MediaType mediaType = originalResponse.body().contentType();
            ResponseBody responseBody = originalResponse.body();
            byte[] responseByte = responseBody.bytes();
            byte[] decryptResponseByte = EncryptUtil.decrypt(responseByte, MyPasswordUtils.getMyParamsPwd());
            return originalResponse.newBuilder().body(ResponseBody.create(mediaType, decryptResponseByte)).build();
        }
        return originalResponse;
    }
}
