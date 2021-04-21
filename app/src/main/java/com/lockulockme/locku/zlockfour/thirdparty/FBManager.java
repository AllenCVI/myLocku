

package com.lockulockme.locku.zlockfour.thirdparty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

public class FBManager {

    public static final String PUBLIC_PROFILE = "public_profile";
    public static final String USER_GENDER = "user_gender";

    private CallbackManager callbackManager;

    private static FBManager sInstance = new FBManager();

    public synchronized static FBManager getInstance() {
        return sInstance;
    }

    private FBManager() {

    }

    public void loginWithPermission(Context context) {
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
           LoginManager.getInstance().logInWithReadPermissions((Activity) context,
                        Arrays.asList(PUBLIC_PROFILE, USER_GENDER));
    }


    public void faceBookLogOut() {
        LoginManager.getInstance().logOut();
    }

    public void initRegisterCallback(
                             final OnFacebookLoginListener mListener) {
        callbackManager = CallbackManager.Factory.create();
        if (callbackManager != null) {
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallbackImpl(mListener));

        }
    }

    public static class FacebookCallbackImpl implements FacebookCallback<LoginResult> {

        public OnFacebookLoginListener mOnFacebookLoginListener;

        public FacebookCallbackImpl(OnFacebookLoginListener listener) {
            this.mOnFacebookLoginListener = listener;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            if (loginResult != null) {
                if (mOnFacebookLoginListener != null) {
                    mOnFacebookLoginListener.onLoginSuccess(loginResult);
                }
            }
        }

        @Override
        public void onCancel() {
            if (mOnFacebookLoginListener != null) {
                mOnFacebookLoginListener.onLoginCancel();
                LoginManager.getInstance().logOut();
            }
        }

        @Override
        public void onError(FacebookException error) {
            error.fillInStackTrace();
            if (mOnFacebookLoginListener != null) {
                mOnFacebookLoginListener.onLoginError(error);
            }
        }
    }

    public void unregisterCallback() {
        if (callbackManager != null) {
            LoginManager.getInstance().unregisterCallback(callbackManager);
        }
    }

    public void setOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

}
