package com.lockulockme.lockuchat.utils;


import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TransformTextMsgUtils {
    private TransformTextMsgUtils() {
        country2LanguageMap = new HashMap<>();
        initLocalLanguage();
    }

    private static class InstanceHelper {
        private static TransformTextMsgUtils INSTANCE = new TransformTextMsgUtils();
    }

    public static TransformTextMsgUtils getInstance() {
        return TransformTextMsgUtils.InstanceHelper.INSTANCE;
    }

    private static final String translate_domain = "https://translate.google.cn/";
    private static final String head_agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
    private static final String auto = "auto";
    private final HashMap<String, String> country2LanguageMap;
    private String defaultLan = "en";

    private void initLocalLanguage() {
        List<String> countryList = Arrays.asList("VN", "TH", "ID", "MY", "PH", "IN");
        List<String> languageList = Arrays.asList("vi", "th", "id", "ms", "tl", "en");
        for (int i = 0; i < countryList.size(); i++) {
            country2LanguageMap.put(countryList.get(i), languageList.get(i));
        }
    }

    private String getLanguage(String countryCode) {
        String language = country2LanguageMap.get(countryCode);
        if (TextUtils.isEmpty(language)) {
            return defaultLan;
        }
        return language;
    }


    public void transform(String sourceLan, String targetLan, String content, OnTransformListener callback) {
        TransformTask task = new TransformTask(sourceLan, getLanguage(targetLan), content, callback);
        task.request();
    }

    class TransformTask {
        String sourceLan;
        String targetLan;
        String content;
        OnTransformListener callback;

        TransformTask(String sourceLan, String targetLan, String content, OnTransformListener callback) {
            this.content = content;
            this.callback = callback;
            this.sourceLan = sourceLan;
            this.targetLan = targetLan;
        }

        public void request() {
            OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
            Request request = new Request.Builder()
                    .url(makeTransformUrl(sourceLan, targetLan, content))//请求接口。如果需要传参拼接到接口后面。
                    .get()
                    .addHeader("User-Agent", head_agent)
                    .build();//创建Request 对象
            LogHelper.e("initOkHttpClient", "getTransformUrl(sourceLan, targetLan, content):" + makeTransformUrl(sourceLan, targetLan, content));

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    if (callback != null) {
                        LogHelper.e("TransformUtil", "onTransformFailed:11=" + e.getMessage());
                        callback.onFailed("statusCode" + e.getMessage());
                    }
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            ResponseBody body = response.body();
                            String requestResult = streamToJsonStr(body.byteStream());
                            String result = "";
                            // 处理返回结果，拼接
                            JSONArray jsonArray = new JSONArray(requestResult).getJSONArray(0);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                result += jsonArray.getJSONArray(i).getString(0);
                            }
                            if (callback != null) {
                                callback.onSuccess(result);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogHelper.e("TransformUtil", "翻译错误");
                        }
                    } else {
                        if (callback != null) {
                            LogHelper.e("TransformUtil", "onTransformFailed:statusCode=" + response.code());
                            callback.onFailed("statusCode" + response.code());
                        }
                    }
                }
            });
        }
    }

    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return 字符串
     */
    static String streamToJsonStr(InputStream is) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.close();
            is.close();
            byte[] byteArray = out.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            return null;
        }
    }

    public void transformTextMsg(String targetLan, String content, OnTransformListener callback) {
        transform(TextUtils.isEmpty(Locale.getDefault().getLanguage()) ? auto : Locale.getDefault().getLanguage(), targetLan, content, callback);
    }

    private static String makeTransformUrl(String sourceLan, String targetLan, String content) {
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(translate_domain).append("translate_a/single?client=gtx&sl=").append(sourceLan).append("&tl=")
                .append(targetLan).append("&dt=t&q=");
        try {
            urlBuffer.append(URLEncoder.encode(content, "UTF-8"));
        } catch (Exception e) {
            urlBuffer.append(content);
        }
        return urlBuffer.toString();
    }

    public interface OnTransformListener {
        void onSuccess(String result);

        void onFailed(String msg);
    }
}
