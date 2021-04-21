package com.lockulockme.locku.module.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.responsebean.UploadPortraitResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.eventbus.AvatarModifiedEvent;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.GlideUtils;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.PictureSelectorHelper;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.databinding.AcUploadAvatarBinding;
import com.lockulockme.lockuchat.utils.MatisseHelper;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.tbruyelle.rxpermissions3.RxPermissions;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.functions.Consumer;

public class UploadAvatarActivity extends BaseActivity<AcUploadAvatarBinding> {

    public static final int CODE_CHOOSE_IMAGE = 1;
    List<String> imageList;

    final RxPermissions rxPermissions = new RxPermissions(this);

    UserInfo userBean;
    private MyConsumer myConsumer;

    public static void go(Context context) {
        Intent intent = new Intent(context, UploadAvatarActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcUploadAvatarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myConsumer = new MyConsumer(this);
        userBean = AccountManager.getInstance().getCurrentUser();
        if ("1".equals(userBean.userGender)) {
            GlideUtils.loadCircleImg(this, userBean.smallAvatar, binding.ivPortrait, R.mipmap.ic_male_portrait_placeholder);
        } else {
            GlideUtils.loadCircleImg(this, userBean.smallAvatar, binding.ivPortrait, R.mipmap.ic_female_portrait_placeholder);
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
                .subscribe(myConsumer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myConsumer != null){
            myConsumer.clear();
        }
    }

    private static class MyConsumer implements Consumer<Boolean> {
        private WeakReference<UploadAvatarActivity> weakReference;

        public MyConsumer(UploadAvatarActivity avatarActivity) {
            this.weakReference = new WeakReference<>(avatarActivity);
        }

        public void clear(){
            if (weakReference != null){
                weakReference.clear();
            }
        }

        @Override
        public void accept(Boolean granted) throws Throwable {
            UploadAvatarActivity avatarActivity = null;
            if (weakReference != null){
                avatarActivity = weakReference.get();
            }
            if (avatarActivity == null){
                return;
            }
            if (granted) {
                avatarActivity.selectPictures();
            } else {
                ToastUtils.toastShow(R.string.no_camera_or_external_storage_permission);
            }
        }
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

    private void compress(File file) {
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(file).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {
                if (isSuccess) {
                    showLoading();
                    OkGoUtils.getInstance().uploadPortrait(UploadAvatarActivity.this, "icon", new File(outfile), new NewJsonCallback<UploadPortraitResponseBean>() {
                        @Override
                        public void onSuc(UploadPortraitResponseBean response, String msg) {
                            hideLoading();
                            ToastUtils.toastShow(getString(R.string.portrait_modified_success));
                            if (!TextUtils.isEmpty(response.smallAvatar)) {
                                if ("1".equals(userBean.userGender)) {
                                    GlideUtils.loadCircleImg(UploadAvatarActivity.this,response.smallAvatar , binding.ivPortrait, R.mipmap.ic_male_portrait_placeholder);
                                } else {
                                    GlideUtils.loadCircleImg(UploadAvatarActivity.this,response.smallAvatar , binding.ivPortrait, R.mipmap.ic_female_portrait_placeholder);
                                }
                                EventBus.getDefault().post(new AvatarModifiedEvent(outfile));
                                userBean.avatar = response.avatar;
                                userBean.middleAvatar = response.middleAvatar;
                                userBean.smallAvatar = response.smallAvatar;
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
