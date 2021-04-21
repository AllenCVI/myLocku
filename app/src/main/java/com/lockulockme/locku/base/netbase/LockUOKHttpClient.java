package com.lockulockme.locku.base.netbase;

import android.os.Build;
import android.text.TextUtils;

import com.lockulockme.locku.BuildConfig;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.EncryptUtil;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.base.utils.MyPasswordUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class LockUOKHttpClient {

    public static OkHttpClient client() {
        return PostJsonHolder.okHttpClient;
    }

//    public static OkHttpClient postFileClient(){
//        return PostFileHolder.okHttpClient;
//    }

    private static class PostJsonHolder {
        private static final OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .readTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .addInterceptor(new HeaderInterceptor())
                        .addInterceptor(new RequestSecretInterceptor())
                        .addInterceptor(new ResponseInterceptor())
                        .retryOnConnectionFailure(false)
                        .build();
    }

//    private static class PostFileHolder {
//        private static final OkHttpClient okHttpClient =
//                new OkHttpClient.Builder()
//                        .readTimeout(15, TimeUnit.SECONDS)
//                        .writeTimeout(15, TimeUnit.SECONDS)
//                        .connectTimeout(15, TimeUnit.SECONDS)
//                        .addInterceptor(new HeaderInterceptor())
//                        .addInterceptor(new RequestSecretInterceptor())
//                        .addInterceptor(new ResponseInterceptor())
//                        .retryOnConnectionFailure(false)
//                        .build();
//    }


    private static class HeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            StringBuffer stringBuffer = new StringBuffer();
            String sdk = Build.VERSION.RELEASE;
            stringBuffer.append("locku").append("/").append(BuildConfig.VERSION_NAME).append("/").append(BuildConfig.VERSION_CODE).append(" ")
                    .append("(Android ").append(sdk).append(")");
            Request.Builder builder = original.newBuilder()
                    .removeHeader("User-Agent");
            String token = AccountManager.getInstance().getToken();
            if (!TextUtils.isEmpty(token)) {
                builder.addHeader("Authorization", token);
            }
            builder.addHeader("User-Agent", stringBuffer.toString());
            builder.addHeader("lang", Locale.getDefault().getLanguage());
            Request request = builder.build();
            return chain.proceed(request);
        }
    }

    private static class RequestSecretInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();

            MediaType rqMediaType = request.body().contentType();
            boolean isFrom = false;
            if (rqMediaType != null) {
                String contentType = rqMediaType.toString();
                if (contentType != null && contentType.contains("multipart/form-data")) {
                    isFrom = true;
                }
            }
            byte[] newRequestBytes;
            RequestBody oldRequestBody = request.body();
            Buffer requestBuffer = new Buffer();
            oldRequestBody.writeTo(requestBuffer);
            String oldBodyStr = requestBuffer.readUtf8();
            requestBuffer.close();
            byte[] oldRequestBytes = oldBodyStr.getBytes();
            if (!isFrom) {
                newRequestBytes = EncryptUtil.encrypt(oldRequestBytes, MyPasswordUtils.getMyParamsPwd());
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), newRequestBytes);
                HttpUrl.Builder builder = request.url().newBuilder();
                HttpUrl httpUrl = builder.build();
                request = request.newBuilder().post(requestBody).url(httpUrl).build();
            }
            LogUtil.LogD("okHTTP", "{\"params\":\"" + request.url().toString() + "\",\"request\":" + new String(oldRequestBytes) + "}");


            Response response = chain.proceed(request);
            return response;

        }
    }

    private static class ResponseInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();

            Response response = chain.proceed(request);
            int code = response.code();
            if (code == 200) {
                MediaType mediaType = response.body().contentType();
                ResponseBody responseBody = response.body();
                byte[] responseByte = responseBody.bytes();
                byte[] decryptResponseByte = EncryptUtil.decrypt(responseByte,MyPasswordUtils.getMyParamsPwd());
                LogUtil.LogD("okHTTP", "{\"url\":\"" + request.url().toString() + "\",\"response\":" + new String(decryptResponseByte) + "}");
                return response.newBuilder().body(ResponseBody.create(mediaType, decryptResponseByte)).build();
            }
            return response;

        }
    }
}
