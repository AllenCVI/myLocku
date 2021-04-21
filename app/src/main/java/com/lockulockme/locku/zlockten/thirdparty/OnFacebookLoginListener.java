package com.lockulockme.locku.zlockten.thirdparty;

import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

public interface OnFacebookLoginListener {

    void onLoginSuccess(LoginResult result);

    void onLoginCancel();

    void onLoginError(FacebookException error);
}
