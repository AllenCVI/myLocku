package com.lockulockme.locku.zlockeight.module.ui.activity;

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
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.FacebookRequestBean;
import com.lockulockme.locku.base.beans.responsebean.LoginResponseBean;
import com.lockulockme.locku.base.policy.PolicyUrls;
import com.lockulockme.locku.databinding.AcLoginIndexBinding;
import com.lockulockme.locku.zlockeight.application.MyActivityLifecycle;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlockeight.base.utils.AccountManager;
import com.lockulockme.locku.zlockeight.base.utils.OkGoUtils;
import com.lockulockme.locku.zlockeight.base.utils.VipManager;
import com.lockulockme.locku.zlockeight.common.BaseActivity;
import com.lockulockme.locku.zlockeight.thirdparty.FBManager;
import com.lockulockme.locku.zlockeight.thirdparty.OnFacebookLoginListener;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.lang.ref.WeakReference;

public class LoginIndexActivity extends BaseActivity<AcLoginIndexBinding> {

    private FacebookOnFacebookLoginListener onFacebookListener;

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
                Intent intent= new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(PolicyUrls.USER_AGREEMENT_URL);
                intent.setData(content_url);
                startActivity(intent);
            }
        });

        binding.tvPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
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
                FBManager.getInstance().loginWithPermission(LoginIndexActivity.this);
            }
        });
    }

    public class FacebookOnFacebookLoginListener implements OnFacebookLoginListener {
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
            if (result != null) {
                AccessToken accessToken = result.getAccessToken();
                if (accessToken != null) {
                    String access_token = accessToken.getToken();
                    if (!TextUtils.isEmpty(access_token)) {
                        FBManager.getInstance().faceBookLogOut();
                    }
                    if (weakReference != null) {
                        LoginIndexActivity activity = weakReference.get();
                        if (activity != null) {
                            showLoading();
                            FacebookRequestBean facebookRequestBean = new FacebookRequestBean(access_token, accessToken.getUserId());
                            OkGoUtils.getInstance().facebookLogin(LoginIndexActivity.this, facebookRequestBean, new NewJsonCallback<LoginResponseBean>() {
                                @Override
                                public void onSuc(LoginResponseBean response, String msg) {
                                    IMLoginHelper.getInstance().login(response.user.nimId, response.userNimAccessToken, new RequestCallback<LoginInfo>() {
                                        @Override
                                        public void onSuccess(LoginInfo loginInfo) {
                                            hideLoading();
                                            AccountManager.getInstance().saveAccountNoPwd(response);
                                            MyActivityLifecycle.getInstance().finishAllActivity();
                                            MainActivity.StartMe(mContext);
                                        }

                                        @Override
                                        public void onFailed(int i) {
                                            hideLoading();
                                            ToastUtils.toastShow(R.string.login_failed);
                                        }

                                        @Override
                                        public void onException(Throwable throwable) {
                                            hideLoading();
                                            ToastUtils.toastShow(R.string.login_failed);
                                        }
                                    });
                                }

                                @Override
                                public void onE(int httpCode, int apiCode, String msg, LoginResponseBean response) {
                                    hideLoading();
                                    ToastUtils.toastShow(R.string.login_failed);
                                }
                            });
                        }
                    }
                } else {
                    ToastUtils.toastShow(R.string.login_failed);
                }
            } else {
                ToastUtils.toastShow(R.string.login_failed);
            }
        }

        @Override
        public void onLoginCancel() {
            ToastUtils.toastShow(R.string.login_failed);
        }

        @Override
        public void onLoginError(FacebookException error) {
            ToastUtils.toastShow(R.string.login_failed);
            if (error instanceof FacebookAuthorizationException) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    FBManager.getInstance().faceBookLogOut();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        onFacebookListener.clearActivity();
        FBManager.getInstance().unregisterCallback();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FBManager.getInstance().setOnActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
