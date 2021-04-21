package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.lockulockme.locku.R;
import com.lockulockme.locku.application.MyActivityLifecycle;
import com.lockulockme.locku.base.beans.requestbean.FacebookRequestBean;
import com.lockulockme.locku.base.beans.requestbean.GoogleSignRequest;
import com.lockulockme.locku.base.beans.responsebean.LoginResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.policy.PolicyUrls;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.VipManager;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.common.LockUConstant;
import com.lockulockme.locku.databinding.AcLoginIndexBinding;
import com.lockulockme.locku.thirdparty.FBManager;
import com.lockulockme.locku.thirdparty.GoogleLoginListener;
import com.lockulockme.locku.thirdparty.LockUGoogleLoginUtils;
import com.lockulockme.locku.thirdparty.OnFacebookLoginListener;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.lang.ref.WeakReference;

public class LoginIndexActivity extends BaseActivity<AcLoginIndexBinding> {

    private FacebookOnFacebookLoginListener onFacebookListener;
    private GoogleSignInClient mGoogleSignInClient;
    private final int REQUEST_CODE = 100;
    private LoginIMCallback mLoginIMCallback;

    public static void StartMe(Context context) {
        Intent intent = new Intent(context, LoginIndexActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcLoginIndexBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initPickerView();
        VipManager.getInstance().cancelTimer();
        onFacebookListener = new FacebookOnFacebookLoginListener(this);
        FBManager.getInstance().initRegisterCallback(onFacebookListener);
    }

    private void initPickerView() {
    }

    private void initView() {
        binding.tvAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(PolicyUrls.USER_AGREEMENT_URL);
                intent.setData(content_url);
                startActivity(intent);
            }
        });

        binding.tvPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(PolicyUrls.PRIVACY_POLICY_URL);
                intent.setData(content_url);
                startActivity(intent);
            }
        });

        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.StartMe(mContext);
            }
        });

        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.StartMe(mContext);
            }
        });

        binding.ivFaceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ivFaceBook.setEnabled(false);
                FBManager.getInstance().loginWithPermission(LoginIndexActivity.this);
            }
        });


        binding.ivGoogle.setOnClickListener(v -> {
            binding.ivGoogle.setEnabled(false);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(LockUConstant.LOCKU_GOOGLE_ID)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            LockUGoogleLoginUtils.getInstance().LoginToGoogle(this, mGoogleSignInClient, REQUEST_CODE);

        });
    }


    private static class LoginIMCallback implements RequestCallback<LoginInfo> {
        WeakReference<LoginIndexActivity> weakReference;
        LoginResponseBean loginResponseBean;

        public LoginIMCallback(LoginIndexActivity activity, LoginResponseBean loginResponseBean) {
            weakReference = new WeakReference<>(activity);
            this.loginResponseBean = loginResponseBean;
        }

        public void clear() {
            weakReference.clear();
            weakReference = null;
        }

        @Override
        public void onSuccess(LoginInfo loginInfo) {
            if (weakReference != null) {
                LoginIndexActivity activity = weakReference.get();
                if (activity != null && loginResponseBean != null) {
                    activity.hideLoading();
                    AccountManager.getInstance().saveAccountNoPwd(loginResponseBean);
                    MyActivityLifecycle.getInstance().finishAllActivity();
                    MainActivity.StartMe(activity);
                }
            }
        }

        @Override
        public void onFailed(int i) {
            if (weakReference != null) {
                LoginIndexActivity activity = weakReference.get();
                if (activity != null) {
                    activity.hideLoading();
                    ToastUtils.toastShow(R.string.login_failed);
                }
            }

        }

        @Override
        public void onException(Throwable throwable) {
            if (weakReference != null) {
                LoginIndexActivity activity = weakReference.get();
                if (activity != null) {
                    activity.hideLoading();
                    ToastUtils.toastShow(R.string.login_failed);
                }
            }

        }
    }


    public static class FacebookOnFacebookLoginListener implements OnFacebookLoginListener {
        WeakReference<LoginIndexActivity> weakReference;

        public FacebookOnFacebookLoginListener(LoginIndexActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        public void clearActivity() {
            weakReference.clear();
            weakReference = null;
        }

        @Override
        public void onLoginSuccess(LoginResult result) {
            if (weakReference != null) {
                LoginIndexActivity activity = weakReference.get();
                if (activity != null) {
                    activity.binding.ivFaceBook.setEnabled(true);
                    if (result != null) {
                        AccessToken accessToken = result.getAccessToken();
                        if (accessToken != null) {
                            String access_token = accessToken.getToken();
                            if (!TextUtils.isEmpty(access_token)) {
                                FBManager.getInstance().faceBookLogOut();
                            }
                            activity.showLoading();
                            FacebookRequestBean facebookRequestBean = new FacebookRequestBean(access_token, accessToken.getUserId());
                            OkGoUtils.getInstance().facebookLogin(activity, facebookRequestBean, new NewJsonCallback<LoginResponseBean>() {
                                @Override
                                public void onSuc(LoginResponseBean response, String msg) {
                                    if (response == null) {
                                        ToastUtils.toastShow(R.string.login_failed);
                                        return;
                                    }
                                    activity.goNext(response);
                                }

                                @Override
                                public void onE(int httpCode, int apiCode, String msg, LoginResponseBean response) {
                                    activity.hideLoading();
                                    ToastUtils.toastShow(R.string.login_failed);
                                }
                            });
                        } else {
                            ToastUtils.toastShow(R.string.login_failed);
                        }
                    } else {
                        ToastUtils.toastShow(R.string.login_failed);
                    }
                }
            }
        }

        @Override
        public void onLoginCancel() {
            if (weakReference != null) {
                LoginIndexActivity activity = weakReference.get();
                if (activity != null) {
                    activity.binding.ivFaceBook.setEnabled(true);
                    ToastUtils.toastShow(R.string.login_failed);
                }
            }
        }

        @Override
        public void onLoginError(FacebookException error) {
            if (weakReference != null) {
                LoginIndexActivity activity = weakReference.get();
                if (activity != null) {
                    activity.binding.ivFaceBook.setEnabled(true);
                    ToastUtils.toastShow(R.string.login_failed);
                    if (error instanceof FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            FBManager.getInstance().faceBookLogOut();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mLoginIMCallback != null) {
            mLoginIMCallback.clear();
        }
        onFacebookListener.clearActivity();
        FBManager.getInstance().unregisterCallback();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.ivGoogle.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FBManager.getInstance().setOnActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        LockUGoogleLoginUtils.getInstance().onResult(requestCode, data, REQUEST_CODE, new GoogleLoginListener() {
            @Override
            public void successful(String result) {
                if (result == null) {
                    ToastUtils.toastShow(getString(R.string.login_failed));
                } else {
                    if (!TextUtils.isEmpty(result)) {
                        if (mGoogleSignInClient != null) {
                            LockUGoogleLoginUtils.getInstance().GoogleSingOut(LoginIndexActivity.this, mGoogleSignInClient);
                        }
                        googleLogin(result);
                    }
                }
            }

            @Override
            public void failed() {
                ToastUtils.toastShow(getString(R.string.login_failed));
            }
        });
    }

    private void googleLogin(String idToken) {
        showLoading();
        GoogleSignRequest googleSignRequest = new GoogleSignRequest();
        googleSignRequest.googleTokenId = idToken;
        OkGoUtils.getInstance().googleLogin(LoginIndexActivity.this, googleSignRequest, new NewJsonCallback<LoginResponseBean>() {
            @Override
            public void onSuc(LoginResponseBean response, String msg) {
                if (response == null) {
                    ToastUtils.toastShow(R.string.login_failed);
                    return;
                }
                goNext(response);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, LoginResponseBean response) {
                hideLoading();
                ToastUtils.toastShow(R.string.login_failed);
            }
        });
    }


    private void goNext(LoginResponseBean response) {
        if (response.isCompleteUserInfo) {
            if (mLoginIMCallback != null) {
                mLoginIMCallback.clear();
            }
            mLoginIMCallback = new LoginIMCallback(this, response);
            IMLoginHelper.getInstance().login(response.user.nimId, response.userNimAccessToken, mLoginIMCallback);
        } else {
            hideLoading();
            AccountManager.getInstance().putToken(response.userToken);
            Gson gson = new Gson();
            String loginJson = gson.toJson(response);
            CompleteInformationActivity.StartMe(LoginIndexActivity.this, loginJson);
        }
    }


}
