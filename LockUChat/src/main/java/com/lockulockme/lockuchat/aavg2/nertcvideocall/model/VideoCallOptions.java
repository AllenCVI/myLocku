package com.lockulockme.lockuchat.aavg2.nertcvideocall.model;

import com.netease.lava.nertc.sdk.NERtcOption;

import org.jetbrains.annotations.NotNull;

/**
 * 初始化options
 */
public class VideoCallOptions {
    //rtc Option
    private NERtcOption rtcOption;

    //rtc token
    private String token;

    //UI 注入
    private UIService uiService;

    private UserInfoInitCallBack userInfoInitCallBack;

    public VideoCallOptions(String token, NERtcOption rtcOption, @NotNull UIService uiService, UserInfoInitCallBack userInfoInitCallBack) {
        this.token = token;
        this.rtcOption = rtcOption;
        this.uiService = uiService;
        this.userInfoInitCallBack = userInfoInitCallBack;
    }

    public NERtcOption getRtcOption() {
        return rtcOption;
    }

    public String getToken() {
        return token;
    }

    public UIService getUiService() {
        return uiService;
    }

    public UserInfoInitCallBack getUserInfoInitCallBack() {
        return userInfoInitCallBack;
    }
}
