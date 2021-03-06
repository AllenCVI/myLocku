package com.lockulockme.locku.base.utils;

import android.content.Context;

import com.lockulockme.locku.application.MyApplication;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.responsebean.LoginResponseBean;

public class AccountManager {

    static AccountManager accountManager;
    public static final String TOKEN_KEY = "sp_token_key";
    public static final String USER_KEY = "sp_user_key";
    public static final String NIM_TOKEN_KEY = "sp_nim_token_key";
    public static final String PWD_KEY = "sp_pwd_key";
    public static final String ACCOUNT_KEY = "sp_account_key";
    public static final String IS_COMPLETE_INFO = "isCompleteInfo";
    public static final String HAS_PWB = "Has_PWB";
    private Context mContext;

    public AccountManager() {
        mContext = MyApplication.appContext;
    }

    public static AccountManager getInstance() {
        if (accountManager == null) {
            accountManager = new AccountManager();
        }
        return accountManager;
    }

    public void putUser(UserInfo userBean) {
        SharedPreferencesUtil.putUser(userBean);
    }

    public void putIsComplete(boolean isComplete){
        SharedPreferencesUtil.put(mContext,IS_COMPLETE_INFO,isComplete);
    }

    public boolean getIsComplete(){
        return SharedPreferencesUtil.getBooleanValue(mContext,IS_COMPLETE_INFO);
    }

    public void putNimToken(String nimToken) {
        SharedPreferencesUtil.put(mContext, NIM_TOKEN_KEY, nimToken);
    }

    public String getNimToken() {
        return SharedPreferencesUtil.getStringValue(mContext, NIM_TOKEN_KEY);
    }

    public void putToken(String token) {
        SharedPreferencesUtil.put(mContext, TOKEN_KEY, token);
    }

    public String getToken() {
        return SharedPreferencesUtil.getStringValue(mContext, TOKEN_KEY);
    }

    public void putPwd(String pwd) {
        SharedPreferencesUtil.put(mContext, PWD_KEY, pwd);
    }

    public String getPwd() {
        return SharedPreferencesUtil.getStringValue(mContext, PWD_KEY);
    }

    public void putAccount(String account) {
        SharedPreferencesUtil.put(mContext, ACCOUNT_KEY, account);
    }

    public String getAccount() {
        return SharedPreferencesUtil.getStringValue(mContext, ACCOUNT_KEY);
    }

    public UserInfo getCurrentUser() {
        return SharedPreferencesUtil.getUser();
    }


    public void clearUser() {
        putUser(null);
        putToken("");
        putNimToken("");
    }
    public void clearToken() {
        putToken("");
        putNimToken("");
    }

    public void putHasPwb(boolean hasPwb){
        SharedPreferencesUtil.put(mContext,HAS_PWB,hasPwb);
    }

    public boolean getHasPwb(){
        return SharedPreferencesUtil.getBooleanValue(mContext,HAS_PWB);
    }

    public void saveAccount(LoginResponseBean response) {
        putAccount(response.user.account);
        putPwd(response.user.password);
        putNimToken(response.userNimAccessToken);
        putToken(response.userToken);
        putUser(response.user);
        putIsComplete(response.isCompleteUserInfo);
        putHasPwb(response.IsHavePwb);
    }

    public void saveAccountNoPwd(LoginResponseBean response) {
        putAccount(response.user.account);
        putNimToken(response.userNimAccessToken);
        putToken(response.userToken);
        putUser(response.user);
        putIsComplete(response.isCompleteUserInfo);
        putHasPwb(response.IsHavePwb);
    }
}
