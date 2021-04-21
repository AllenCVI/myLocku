package com.lockulockme.lockuchat.aavg2.ui.model;

import android.text.TextUtils;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.UserInfoInitCallBack;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.uinfo.UserService;

public final class ProfileManager implements UserInfoInitCallBack {
    private static final ProfileManager instance = new ProfileManager();

    public static ProfileManager getInstance() {
        return instance;
    }

    private final static String PER_USER_MODEL = "per_user_model";
    private final static String PER_DATA = "per_profile_manager";
    private UserModel userModel;
    private String token;
    private boolean isLogin = false;

    private AbortableFuture<LoginInfo> loginRequest;

    private ProfileManager() {
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public UserModel getUserModel() {
        if (userModel == null) {
            loadUserModel();
        }
        return userModel;
    }

    /**
     * 是否是本用户
     *
     * @param imAccId
     * @return
     */
    public boolean isCurrentUser(String imAccId) {
        if (getUserModel() == null) {
            return false;
        }
        return TextUtils.equals(getUserModel().imAccid, imAccId);
    }

    public boolean isCurrentUser(long g2Uid) {
        if (getUserModel() == null) {
            return false;
        }
        return getUserModel().g2Uid == g2Uid;
    }

    /**
     * 是否是本用户(对比G2uid)
     *
     * @param g2Uid
     * @return
     */
    public boolean isCurrentUserForG2(long g2Uid) {
        if (getUserModel() == null) {
            return false;
        }
        return getUserModel().g2Uid != 0 && getUserModel().g2Uid == g2Uid;
    }

    public void setUserModel(UserModel model) {
        userModel = model;
        saveUserModel();
    }

    private void loadUserModel() {
        try {
            String json = SPUtils.getInstance(PER_DATA).getString(PER_USER_MODEL);
            userModel = GsonUtils.fromJson(json, UserModel.class);
        } catch (Exception e) {
        }
    }

    private void saveUserModel() {
        try {
            if (userModel != null) {
                SPUtils.getInstance(PER_DATA).put(PER_USER_MODEL, GsonUtils.toJson(userModel));
            } else {
                SPUtils.getInstance(PER_DATA).put(PER_USER_MODEL, "");
            }
        } catch (Exception e) {
        }
    }

    public void login(LoginInfo loginInfo, RequestCallback<LoginInfo> callback) {
        loginRequest = NIMClient.getService(AuthService.class).login(loginInfo);
        loginRequest.setCallback(callback);
    }


    public void logout() {
        NIMClient.getService(AuthService.class).logout();
    }

    @Override
    public void onUserLoginToIm(String imAccId, String imToken) {
        UserModel userModel = new UserModel();
        userModel.imAccid = imAccId;
        userModel.imToken = imToken;
//        userModel.nickname = NIMClient.getService(UserService.class).getUserInfo(imAccId).getName();
        userModel.nickname = "";
        setUserModel(userModel);
    }
}
