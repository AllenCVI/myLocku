package com.lockulockme.locku.zlockfour.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.zlockfour.application.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2016/6/4.
 */
public class SharedPreferencesUtil {

    private static final String TABLE_NAME = "auto";
    public static String SP_KEY_MARKET = "sp_key_market";
    public static String SP_KEY_STUDY_CIRCLE_DOT_TIME = "sp_key_study_circle_dot_time";
    static Gson gson = new Gson();

    private static void paraCheck(SharedPreferences sp, String key) {
        if (sp == null) {
            throw new IllegalArgumentException();
        }
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 存放布尔型信息
     *
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(TABLE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 存放整型数值信息
     *
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, int value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(TABLE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, value);
            editor.commit();
        } else {

        }

    }

    /**
     * 存放long型数值信息
     *
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, long value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(TABLE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(key, value);
            editor.commit();
        }
    }

    /**
     * 存放字符串信息
     *
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(TABLE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 读取布尔信息
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBooleanValue(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(TABLE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    /**
     * 读取整型信息
     *
     * @param context
     * @param key
     * @return
     */
    public static int getIntValue(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(TABLE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    /**
     * 读取long信息
     *
     * @param context
     * @param key
     * @return
     */
    public static long getLongValue(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(TABLE_NAME, Context.MODE_PRIVATE);
        return preferences.getLong(key, 0l);
    }

    /**
     * 读取字符串信息
     *
     * @param context
     * @param key
     * @return
     */
    public static String getStringValue(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(TABLE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    /**
     * 存放bitmap
     */
    public static boolean putBitmap(Context context, String key, Bitmap bitmap) {
        SharedPreferences sp = context.getSharedPreferences(TABLE_NAME, Context.MODE_PRIVATE);
        paraCheck(sp, key);
        if (bitmap == null || bitmap.isRecycled()) {
            return false;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            String imageBase64 = new String(Base64.encode(baos.toByteArray(),
                    Base64.DEFAULT));
            SharedPreferences.Editor e = sp.edit();
            e.putString(key, imageBase64);
            return e.commit();
        }
    }

    /**
     * 取bitmap
     */
    public static Bitmap getBitmap(Context context, String key, Bitmap defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(TABLE_NAME,
                Context.MODE_PRIVATE);
        paraCheck(sp, key);
        String imageBase64 = sp.getString(key, "");
        if (TextUtils.isEmpty(imageBase64)) {
            return defaultValue;
        }
        byte[] base64Bytes = Base64.decode(imageBase64.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        Bitmap ret = BitmapFactory.decodeStream(bais);
        if (ret != null) {
            return ret;
        } else {
            return defaultValue;
        }
    }

    public static String getUserToken(String token) {
        String tokenStr = getStringValue(MyApplication.application, AccountManager.TOKEN_KEY);
        return TextUtils.isEmpty(tokenStr) ? "" : tokenStr;
    }

    public static void putUser(UserInfo userBean) {
        put(MyApplication.application, AccountManager.USER_KEY, gson.toJson(userBean));
    }

    public static UserInfo getUser() {
        String userStr = getStringValue(MyApplication.application, AccountManager.USER_KEY);
        UserInfo userBean = gson.fromJson(userStr, UserInfo.class);
        return userBean;
    }


}
