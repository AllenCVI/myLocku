package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lockulockme.locku.R;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.eventbus.AvatarModifiedEvent;
import com.lockulockme.locku.databinding.AcModifyBinding;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.GlideUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ModifyActivity extends BaseActivity<AcModifyBinding> {

    public static void go(Context context) {
        Intent intent = new Intent(context, ModifyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UserInfo userBean = AccountManager.getInstance().getCurrentUser();
        if ("1".equals(userBean.userGender)) {
            GlideUtils.loadCircleImg(this, userBean.avatar, binding.ivModifyAvatar, R.mipmap.ic_male_portrait_placeholder);
        } else {
            GlideUtils.loadCircleImg(this, userBean.avatar, binding.ivModifyAvatar, R.mipmap.ic_female_portrait_placeholder);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        UserInfo userBean = AccountManager.getInstance().getCurrentUser();
        binding.tvModifyNickname.setText(userBean.name);
        binding.tvModifyPersonalGingature.setText(userBean.userSign);
    }

    public void back(View view) {
        finish();
    }

    public void goUploadAvatar(View view) {
        UploadAvatarActivity.go(this);
    }

    public void goModifyNickname(View view) {
        ModifyNicknameActivity.go(this);
    }

    public void goModifySignature(View view) {
        PersonalSignatureActivity.go(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void avatarUpdateEvent(AvatarModifiedEvent event) {
        GlideUtils.loadCircleImg(this, event.filePath, binding.ivModifyAvatar);
    }
}
