package com.lockulockme.locku.zlockeight.common;

import android.app.Activity;
import android.content.Intent;

import com.lockulockme.locku.zlockeight.application.MyActivityLifecycle;
import com.lockulockme.locku.zlockeight.application.MyApplication;
import com.lockulockme.locku.zlockeight.base.utils.AccountManager;
import com.lockulockme.locku.zlockeight.base.utils.VipManager;
import com.lockulockme.locku.zlockeight.module.ui.activity.LoginActivity;
import com.lockulockme.lockuchat.bean.HangUp;
import com.lockulockme.lockuchat.common.HangUpHelper;
import com.lockulockme.lockuchat.http.VipDiamondsHelper;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;

public class LoginoutManager {
    public static void loginout(Activity activity) {
//        挂断电话
        HangUpHelper.getInstance().updateHangUpLiveData(new HangUp());
//        清理token（自己的和云信的）
        AccountManager.getInstance().clearToken();
//        清理vip状态
        VipDiamondsHelper.getInstance().resetVipStatus();
        VipManager.getInstance().reset();
        VipManager.getInstance().cancelTimer();
//        退出云信
        IMLoginHelper.getInstance().logout();

//        关闭页面并跳转登陆页面
        MyActivityLifecycle.getInstance().finishAllActivity();

        if (activity != null && !activity.isDestroyed()) {
            LoginActivity.StartMe(activity);
        } else {
            Intent intent = new Intent(MyApplication.getInstance(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            MyApplication.getInstance().startActivity(intent);
        }
    }
}
