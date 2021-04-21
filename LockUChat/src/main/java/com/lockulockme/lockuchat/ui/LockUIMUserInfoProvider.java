package com.lockulockme.lockuchat.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.bean.rsp.BaseBean;
import com.lockulockme.lockuchat.bean.rsp.PriceItems;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.http.GsonUtils;
import com.lockulockme.lockuchat.http.NetDataListener;
import com.lockulockme.lockuchat.http.NetDataUtils;
import com.lockulockme.lockuchat.http.UserBeanCacheUtils;
import com.lockulockme.lockuchat.http.VipDiamondsHelper;
import com.lockulockme.lockuchat.utils.ContextHelper;
import com.lockulockme.lockuchat.utils.ImageHelper;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.lockulockme.lockuchat.utils.ResouseUtils;
import com.lockulockme.lockuchat.utils.ScreenInfo;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class LockUIMUserInfoProvider implements UserInfoProvider {

    private Context context;

    public LockUIMUserInfoProvider(Context context) {
        this.context = context;
    }

    @Override
    public UserInfo getUserInfo(String account) {
        return null;
    }

    @Override
    public Bitmap getAvatarForMessageNotifier(SessionTypeEnum sessionType, String sessionId) {
        /*
         * 注意：这里最好从缓存里拿，如果加载时间过长会导致通知栏延迟弹出！该函数在后台线程执行！
         */
        final Bitmap[] bm = {null};
        Bitmap bitmap=null;
        int defResId = R.mipmap.app_icon;

        CountDownLatch countDownLatch = new CountDownLatch(1);

        List<String> userAccount = new ArrayList<>();
        userAccount.add(sessionId);

        UserBeanCacheUtils.getInstance().getCacheUsers(new Object(),userAccount, new UserBeanCacheUtils.OnGetUsersLitener() {
            @Override
            public void onGetUsers(List<User> userList) {
                if (userList!=null&&userList.size()>0){
                    User userBean=userList.get(0);
                    Glide.with(ContextHelper.getInstance().getContext()).asBitmap().load(userBean.smallUserIcon)
                            .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(ScreenInfo.getInstance().dip2px(16))))
                            .override(80,80).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            bm[0] =resource;
                            countDownLatch.countDown();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            countDownLatch.countDown();
                        }
                    });
                }
            }

            @Override
            public void onGetFailed() {
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await(10*1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bitmap=bm[0];
        if (bitmap == null) {
            Drawable drawable = context.getResources().getDrawable(defResId);
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            }
        }
        return bitmap;
    }

    @Override
    public String getDisplayNameForMessageNotifier(String account, String sessionId,
                                                   SessionTypeEnum sessionType) {
        String nick = null;
        if (TextUtils.isEmpty(nick)) {
            return null; // 返回null，交给sdk处理。如果对方有设置nick，sdk会显示nick
        }
        return nick;
    }
}
