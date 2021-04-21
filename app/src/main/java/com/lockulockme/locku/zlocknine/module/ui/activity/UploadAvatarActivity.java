package com.lockulockme.locku.zlocknine.module.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.responsebean.UploadPortraitResponseBean;
import com.lockulockme.locku.base.eventbus.AvatarModifiedEvent;
import com.lockulockme.locku.databinding.AcUploadAvatarBinding;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlocknine.base.utils.AccountManager;
import com.lockulockme.locku.zlocknine.base.utils.GlideUtils;
import com.lockulockme.locku.zlocknine.base.utils.OkGoUtils;
import com.lockulockme.locku.zlocknine.common.BaseActivity;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadAvatarActivity extends BaseActivity<AcUploadAvatarBinding> {

    public static final int CODE_CHOOSE_IMAGE = 1;
    List<String> imageList;

    final RxPermissions rxPermissions = new RxPermissions(this);

    UserInfo userBean;

    public static void go(Context context) {
        Intent intent = new Intent(context, UploadAvatarActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcUploadAvatarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userBean = AccountManager.getInstance().getCurrentUser();
        if ("1".equals(userBean.userGender)) {
            GlideUtils.loadCircleImg(this, userBean.avatar, binding.ivPortrait, R.mipmap.ic_male_portrait_placeholder);
        } else {
            GlideUtils.loadCircleImg(this, userBean.avatar, binding.ivPortrait, R.mipmap.ic_female_portrait_placeholder);
        }
        binding.ivPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = new ArrayList<>();
                if (!TextUtils.isEmpty(userBean.avatar)) {
                    list.add(userBean.avatar);
                    ImagePreviewActivity.StartMe(mContext, list, 0);

                }
            }
        });
    }

    public void back(View view) {
        finish();
    }

    public void confirm(View view) {
        rxPermissions
                .request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        selectPictures();
                    } else {
                        ToastUtils.toastShow(getString(R.string.no_camera_or_external_storage_permission));
                    }
                });
    }

    private void selectPictures() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_CHOOSE_IMAGE && resultCode == RESULT_OK) {
        }
    }

    private void compress(String filePath) {
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(new File(filePath)).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {
                if (isSuccess) {
                    showLoading();
                    OkGoUtils.getInstance().uploadPortrait(this, "icon", new File(outfile), new NewJsonCallback<UploadPortraitResponseBean>() {
                        @Override
                        public void onSuc(UploadPortraitResponseBean response, String msg) {
                            hideLoading();
                            ToastUtils.toastShow(getString(R.string.portrait_modified_success));
                            if (!TextUtils.isEmpty(response.avatar)) {
                                GlideUtils.loadCircleImg(UploadAvatarActivity.this, outfile, binding.ivPortrait);
                                EventBus.getDefault().post(new AvatarModifiedEvent(outfile));
                                userBean.avatar = response.avatar;
                                AccountManager.getInstance().putUser(userBean);
                            }
                        }

                        @Override
                        public void onE(int httpCode, int apiCode, String msg, UploadPortraitResponseBean response) {
                            hideLoading();
                            ToastUtils.toastShow(getString(R.string.upload_image_error));
                        }
                    });
                } else {
                    t.printStackTrace();
                    ToastUtils.toastShow(getString(R.string.upload_image_error));
                }

            }
        });
    }

}
