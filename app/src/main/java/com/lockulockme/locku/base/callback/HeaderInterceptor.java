package com.lockulockme.locku.base.callback;

import android.os.Build;
import android.text.TextUtils;

import com.lockulockme.locku.BuildConfig;
import com.lockulockme.locku.base.utils.AccountManager;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
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
