package com.lockulockme.locku.module.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lockulockme.locku.R;
import com.lockulockme.locku.application.MyActivityLifecycle;
import com.lockulockme.locku.base.beans.responsebean.LoginResponseBean;
import com.lockulockme.locku.base.beans.responsebean.UploadPortraitResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.GlideUtils;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.PictureSelectorHelper;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.common.LockUConstant;
import com.lockulockme.locku.databinding.AcSetAvatarBinding;
import com.lockulockme.lockuchat.utils.MatisseHelper;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.tbruyelle.rxpermissions3.RxPermissions;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class SetHeaderImgActivity extends BaseActivity<AcSetAvatarBinding> {
    public static final String LOGIN_BUNDLE = "Login_Bundle";
    public static final String LOGIN_JSON = "Login_Json";
    public static final int CODE_CHOOSE_IMAGE = 1;
    private LoginResponseBean loginResponseBean;
    private final RxPermissions rxPermissions = new RxPermissions(this);
    private LoginIMCallback mLoginIMCallback;
    private List<String> imageList;


    public static void start(Context context,String loginJson) {
        Intent intent = new Intent(context, SetHeaderImgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(LOGIN_JSON,loginJson);
        intent.putExtra(LOGIN_BUNDLE,bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcSetAvatarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String loginJson = getIntent().getBundleExtra(LOGIN_BUNDLE).getString(LOGIN_JSON);
        if (TextUtils.isEmpty(loginJson)){
            ToastUtils.toastShow(R.string.network_error);
            MyActivityLifecycle.getInstance().finishAllActivity();
            LoginIndexActivity.StartMe(this);
            return;
        }

        try {
            Gson gson = new Gson();
            loginResponseBean = gson.fromJson(loginJson, LoginResponseBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
    }

    private void initView() {
        binding.ivTitleBack.setOnClickListener(v->{
            finish();
        });

        if (loginResponseBean==null){
            ToastUtils.toastShow(R.string.network_error);
            MyActivityLifecycle.getInstance().finishAllActivity();
            LoginIndexActivity.StartMe(this);
            return;
        }
        String loginAccount = loginResponseBean.user.account;

        binding.tvUniqueId.setText(getResources().getString(R.string.uniqueId)+loginAccount);

        binding.tvSkip.setOnClickListener(v->{
            if (loginResponseBean==null){
                ToastUtils.toastShow(R.string.network_error);
                return;
            }
            showLoading();
            loginIM();
        });

        binding.ivAvatar.setOnClickListener(v->{
            rxPermissions
                    .request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {
                            selectPictures();
                        } else {
                            ToastUtils.toastShow(getString(R.string.no_camera_or_external_storage_permission));
                        }
                    });
        });

        binding.tvNextStep.setOnClickListener(v->{
            if (loginResponseBean==null){
                ToastUtils.toastShow(R.string.network_error);
                return;
            }
            if (TextUtils.isEmpty(imagePath)){
                ToastUtils.toastShow(R.string.please_select_picture);
                return;
            }
            showLoading();
            OkGoUtils.getInstance().uploadPortrait(SetHeaderImgActivity.this, LockUConstant.UPLOAD_AVATAR, new File(imagePath), new NewJsonCallback<UploadPortraitResponseBean>() {
                @Override
                public void onSuc(UploadPortraitResponseBean response, String msg) {
                    if (!TextUtils.isEmpty(response.avatar)&&loginResponseBean!=null) {
                        loginResponseBean.user.avatar = response.avatar;
                    }
                    loginIM();
                }

                @Override
                public void onE(int httpCode, int apiCode, String msg, UploadPortraitResponseBean response) {
                    hideLoading();
                    ToastUtils.toastShow(R.string.network_error);
                }
            });
        });
    }

    private static class LoginIMCallback implements RequestCallback<LoginInfo> {
        WeakReference<SetHeaderImgActivity> weakReference;
        LoginResponseBean loginBean;

        public LoginIMCallback(SetHeaderImgActivity activity, LoginResponseBean loginResponseBean) {
            weakReference = new WeakReference<>(activity);
            loginBean = loginResponseBean;
        }

        public void clear() {
            weakReference.clear();
            weakReference = null;
        }

        @Override
        public void onSuccess(LoginInfo loginInfo) {
            if (weakReference != null) {
                SetHeaderImgActivity activity = weakReference.get();
                if (activity != null && loginBean != null) {
                    activity.hideLoading();
                    AccountManager.getInstance().saveAccountNoPwd(loginBean);
                    MyActivityLifecycle.getInstance().finishAllActivity();
                    MainActivity.StartMe(activity);
                }
            }
        }

        @Override
        public void onFailed(int i) {
            if (weakReference != null) {
                SetHeaderImgActivity activity = weakReference.get();
                if (activity != null) {
                    activity.hideLoading();
                    ToastUtils.toastShow(R.string.network_error);
                }
            }

        }

        @Override
        public void onException(Throwable throwable) {
            if (weakReference != null) {
                SetHeaderImgActivity activity = weakReference.get();
                if (activity != null) {
                    activity.hideLoading();
                    ToastUtils.toastShow(R.string.network_error);
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoginIMCallback!=null){
            mLoginIMCallback.clear();
        }
    }

    private void loginIM() {
        if (loginResponseBean==null){
            ToastUtils.toastShow(R.string.network_error);
            return;
        }

        mLoginIMCallback = new LoginIMCallback(SetHeaderImgActivity.this,loginResponseBean);
        IMLoginHelper.getInstance().login(loginResponseBean.user.nimId, loginResponseBean.userNimAccessToken,mLoginIMCallback);
    }


    private void selectPictures() {
        PictureSelectorHelper.getLocalImage(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            List<File> fileList= MatisseHelper.getFile(selectList,getContentResolver());
            if (fileList != null && fileList.size()>0) {
                compress(fileList.get(0));
            }else {
                ToastUtils.toastShow(getString(R.string.upload_image_error));
            }
        }
    }

    private String imagePath;
    private void compress(File file) {
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(file).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {
                if (isSuccess) {
                    if (TextUtils.isEmpty(outfile)){
                        ToastUtils.toastShow(getString(R.string.get_photo_failed));
                        return;
                    }
                    imagePath = outfile;
                    GlideUtils.loadCircleImg(SetHeaderImgActivity.this,outfile,binding.ivAvatar);
                } else {
                    t.printStackTrace();
                    ToastUtils.toastShow(getString(R.string.get_photo_failed));
                }

            }
        });
    }
}
