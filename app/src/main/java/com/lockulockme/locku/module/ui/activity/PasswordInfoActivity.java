package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.requestbean.FacebookRequestBean;
import com.lockulockme.locku.base.beans.requestbean.GoogleSignRequest;
import com.lockulockme.locku.base.beans.requestbean.UnbindRequest;
import com.lockulockme.locku.base.beans.responsebean.BindData;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.common.LockUConstant;
import com.lockulockme.locku.databinding.AcPasswordInfoBinding;
import com.lockulockme.locku.thirdparty.FBManager;
import com.lockulockme.locku.thirdparty.GoogleLoginListener;
import com.lockulockme.locku.thirdparty.LockUGoogleLoginUtils;
import com.lockulockme.locku.thirdparty.OnFacebookLoginListener;
import com.lockulockme.lockuchat.utils.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.List;

public class PasswordInfoActivity extends BaseActivity<AcPasswordInfoBinding> {

    private FacebookOnFacebookLoginListener onFacebookListener;
    private GoogleSignInClient mGoogleSignInClient;
    private final int REQUEST_CODE = 100;

    public static void go(Context context) {
        Intent intent = new Intent(context, PasswordInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcPasswordInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UserInfo userBean = AccountManager.getInstance().getCurrentUser();
        binding.tvLoginAccount.setText(userBean.account);
        onFacebookListener = new PasswordInfoActivity.FacebookOnFacebookLoginListener(this);
        FBManager.getInstance().initRegisterCallback(onFacebookListener);
        initView();
        initData();
    }

    private void initView() {
        if(AccountManager.getInstance().getHasPwb()){
            binding.passwordState.setText(getResources().getString(R.string.change_password));
//            binding.tvLoginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.tvLoginPassword.setText("******");
        }else {
            binding.passwordState.setText(getResources().getString(R.string.set_pwb));
//            binding.tvLoginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
            binding.tvLoginPassword.setText(R.string.not_set);
        }

        binding.llPassword.setOnClickListener(v->{
            if (AccountManager.getInstance().getHasPwb()){
                ChangePasswordActivity.go(this);
            }else {
                SetPasswordActivity.start(this);
            }
        });


        binding.llFacebook.setOnClickListener(v->{
            String linkStr = binding.tvFacebookLink.getText().toString();
            if (TextUtils.isEmpty(linkStr)){
                ToastUtils.toastShow(R.string.network_error);
                return;
            }
            if (linkStr.equals(getResources().getString(R.string.link))){
                binding.llFacebook.setEnabled(false);
                FBManager.getInstance().loginWithPermission(this);
            }else if (linkStr.equals(getResources().getString(R.string.unlink))){
                unbindFacebook(LockUConstant.FACEBOOK);
            }
        });

        binding.llGoogle.setOnClickListener(v->{
            String linkStr = binding.tvGoogleLink.getText().toString();
            if (TextUtils.isEmpty(linkStr)){
                ToastUtils.toastShow(R.string.network_error);
                return;
            }
            if (getResources().getString(R.string.link).equals(linkStr)){
                binding.llGoogle.setEnabled(false);
                //绑定
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(LockUConstant.LOCKU_GOOGLE_ID)
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
                LockUGoogleLoginUtils.getInstance().LoginToGoogle(this,mGoogleSignInClient,REQUEST_CODE);
            }else if (getResources().getString(R.string.unlink).equals(linkStr)){
                unbindGoogle(LockUConstant.GOOGLE);
            }

        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.llGoogle.setEnabled(true);
    }

    private void unbindGoogle(String google) {
        binding.llGoogle.setEnabled(false);
        showLoading();
        UnbindRequest unbindRequest = new UnbindRequest();
        unbindRequest.thirdPartyName = google;
        OkGoUtils.getInstance().unbind(PasswordInfoActivity.this, unbindRequest, new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                binding.llGoogle.setEnabled(true);
                hideLoading();
                ToastUtils.toastShow(R.string.unbind_success);
                UpdateTextViewLink(binding.tvGoogleLink);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                binding.llGoogle.setEnabled(true);
                hideLoading();
                ToastUtils.toastShow(R.string.unbind_failed);
            }
        });
    }

    private void unbindFacebook(String facebook) {
        binding.llFacebook.setEnabled(false);
        showLoading();
        UnbindRequest unbindRequest = new UnbindRequest();
        unbindRequest.thirdPartyName = facebook;
        OkGoUtils.getInstance().unbind(PasswordInfoActivity.this, unbindRequest, new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                binding.llFacebook.setEnabled(true);
                hideLoading();
                ToastUtils.toastShow(R.string.unbind_success);
                UpdateTextViewLink(binding.tvFacebookLink);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                binding.llFacebook.setEnabled(true);
                hideLoading();
                ToastUtils.toastShow(R.string.unbind_failed);
            }
        });
    }

    private void initData() {
        showLoading();
        OkGoUtils.getInstance().getBind(PasswordInfoActivity.this, new NewJsonCallback<List<BindData>>() {
            @Override
            public void onSuc(List<BindData> response, String msg) {
                hideLoading();
                FillBindUI(response);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<BindData> response) {
                hideLoading();
                ToastUtils.toastShow(R.string.network_error);
            }
        });
    }

    private void FillBindUI(List<BindData> response) {
        for (int i=0;i<response.size();i++){
            if (response.get(i).ThirdPartyName.equals(LockUConstant.FACEBOOK)){
                if (response.get(i).IsLink){
                    UpdateTextViewUnlink(binding.tvFacebookLink);
                }else {
                    UpdateTextViewLink(binding.tvFacebookLink);
                }
            }
            if (response.get(i).ThirdPartyName.equals(LockUConstant.GOOGLE)){
                if (response.get(i).IsLink){
                    UpdateTextViewUnlink(binding.tvGoogleLink);
                }else {
                    UpdateTextViewLink(binding.tvGoogleLink);
                }
            }
        }
    }

    private void UpdateTextViewLink(TextView tv){
        tv.setText(getResources().getString(R.string.link));
        tv.setTextColor(getResources().getColor(R.color.color_CCFFFFFF));
    }

    private void UpdateTextViewUnlink(TextView tv){
        tv.setText(getResources().getString(R.string.unlink));
        tv.setTextColor(getResources().getColor(R.color.color_66ffffff));
    }

    public void back(View view) {
        finish();
    }

    public class FacebookOnFacebookLoginListener implements OnFacebookLoginListener {
        WeakReference<PasswordInfoActivity> weakReference;

        public FacebookOnFacebookLoginListener(PasswordInfoActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        public void clearActivity() {
            weakReference.clear();
            weakReference = null;
        }

        @Override
        public void onLoginSuccess(LoginResult result) {
            binding.llFacebook.setEnabled(true);
            if (result != null) {
                AccessToken accessToken = result.getAccessToken();
                if (accessToken != null) {
                    String access_token = accessToken.getToken();
                    if (!TextUtils.isEmpty(access_token)) {
                        FBManager.getInstance().faceBookLogOut();
                    }
                    if (weakReference != null) {
                        PasswordInfoActivity activity = weakReference.get();
                        if (activity != null) {
                            //绑定
                            bindFacebook(access_token, accessToken.getUserId());
                        }
                    }
                } else {
                    ToastUtils.toastShow(R.string.bind_failed);
                }
            } else {
                ToastUtils.toastShow(R.string.bind_failed);
            }
        }

        @Override
        public void onLoginCancel() {
            binding.llFacebook.setEnabled(true);
            ToastUtils.toastShow(R.string.bind_failed);
        }

        @Override
        public void onLoginError(FacebookException error) {
            binding.llFacebook.setEnabled(true);
            ToastUtils.toastShow(R.string.bind_failed);
            if (error instanceof FacebookAuthorizationException) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    FBManager.getInstance().faceBookLogOut();
                }
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FBManager.getInstance().setOnActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        LockUGoogleLoginUtils.getInstance().onResult(requestCode, data, REQUEST_CODE, new GoogleLoginListener() {
            @Override
            public void successful(String result) {
                if (result == null) {
                    ToastUtils.toastShow(getString(R.string.bind_failed));
                } else {
                    if (!TextUtils.isEmpty(result)) {
                        if (mGoogleSignInClient != null) {
                            LockUGoogleLoginUtils.getInstance().GoogleSingOut(PasswordInfoActivity.this,mGoogleSignInClient);
                        }
                        bindGoogle(result);
                    }
                }
            }

            @Override
            public void failed() {
                hideLoading();
                ToastUtils.toastShow(getString(R.string.bind_failed));
            }


        });
    }

    private void bindGoogle(String idToken) {
        showLoading();
        GoogleSignRequest googleSignRequest = new GoogleSignRequest();
        googleSignRequest.googleTokenId = idToken;
        OkGoUtils.getInstance().linkGoogle(PasswordInfoActivity.this, googleSignRequest, new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                hideLoading();
                ToastUtils.toastShow(R.string.bind_success);
                UpdateTextViewUnlink(binding.tvGoogleLink);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                hideLoading();
                ToastUtils.toastShow(R.string.bind_failed);
            }
        });

    }

    private void bindFacebook(String access_token, String userId) {
        showLoading();
        FacebookRequestBean facebookRequestBean = new FacebookRequestBean(access_token,userId);
        OkGoUtils.getInstance().linkFacebook(PasswordInfoActivity.this, facebookRequestBean, new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                hideLoading();
                ToastUtils.toastShow(R.string.bind_success);
                UpdateTextViewUnlink(binding.tvFacebookLink);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                hideLoading();
                ToastUtils.toastShow(R.string.bind_failed);
            }
        });
    }


    @Override
    protected void onDestroy() {
        onFacebookListener.clearActivity();
        FBManager.getInstance().unregisterCallback();
        super.onDestroy();
    }
}
