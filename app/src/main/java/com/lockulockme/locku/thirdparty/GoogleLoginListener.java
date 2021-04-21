package com.lockulockme.locku.thirdparty;

/**
 * 登录回调接口
 */
public interface GoogleLoginListener {

    void successful(String result);

    void failed();
}
