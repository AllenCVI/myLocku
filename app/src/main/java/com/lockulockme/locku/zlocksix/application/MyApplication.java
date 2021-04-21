package com.lockulockme.locku.zlocksix.application;

import android.content.Context;
import android.graphics.Color;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.advertismentid.GoogleAdHelper;
import com.lockulockme.locku.base.interceptor.ResponseInterceptor;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.zlocksix.base.utils.HeartManager;
import com.lockulockme.locku.zlocksix.common.BaseApplication;
import com.lockulockme.locku.zlocksix.im.IMInitHelper;
import com.lockulockme.lockuchat.utils.ResouseUtils;
import com.lockulockme.lockuchat.utils.ScreenInfo;
import com.scwang.smart.refresh.footer.BallPulseFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.zxy.tiny.Tiny;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import io.branch.referral.Branch;
import okhttp3.OkHttpClient;


/**
 * Created by xuenhao on 2018/3/15.
 */

public class MyApplication extends BaseApplication {

    public static Context appContext;
    public static MyApplication application;

    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new MaterialHeader(context);
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                BallPulseFooter ballPulseFooter = new BallPulseFooter(context);
                ballPulseFooter.setMinimumHeight(80);
                return ballPulseFooter.setAnimatingColor(Color.WHITE);
            }
        });
    }

    public static Context getContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        application = this;
        boolean isOfficial = getResources().getBoolean(R.bool.official);
        boolean isPrint = getResources().getBoolean(R.bool.print);
//        UriConstant.isDebug = !isOfficial;
        LogUtil.flag = isPrint;
        ResouseUtils.getInstance().init(this);
        Tiny.getInstance().init(this);
        GoogleAdHelper.initAAId(this);
        registerActivityLifecycleCallbacks(MyActivityLifecycle.getInstance());
        ScreenInfo.getInstance().init(this);
        IMInitHelper.getInstance().init(this);
        HeartManager.getInstance().startTimer();
        if (!isOfficial)
        Branch.enableLogging();
        Branch.getAutoInstance(this);
    }




}
